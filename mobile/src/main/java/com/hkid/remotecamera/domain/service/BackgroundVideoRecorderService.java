package com.hkid.remotecamera.domain.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.data.SharedObject;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.hkid.remotecamera.util.Constants;
import com.hkid.remotecamera.util.Ulti;

import java.util.Calendar;

public class BackgroundVideoRecorderService extends BaseRemoteCameraService implements SurfaceHolder.Callback {
    public static final String TAG = BackgroundVideoRecorderService.class.getName();
    private WindowManager windowManager;
    private SurfaceView surfaceView;
    private Camera camera = null;
    private MediaRecorder mediaRecorder = null;
    private String fileName = Constants.EMPTY_STRING;
    public static final int BACK_CAMERA_ID = 0;
    public static final int FRONT_CAMERA_ID = 1;
    private int cameraId = BACK_CAMERA_ID;

    @Override
    public void onCreate() {
        super.onCreate();
    }



    private void initSurface(){
        // Create new SurfaceView, set its size to 1x1, move it to the top left corner and set this service as a callback
        windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        surfaceView = new SurfaceView(this);
        LayoutParams layoutParams = new LayoutParams(
                1, 1,
                LayoutParams.TYPE_SYSTEM_OVERLAY,
                LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        windowManager.addView(surfaceView, layoutParams);
        surfaceView.getHolder().addCallback(this);
    }

//    int startId;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        try{
            boolean isSwitchToFrontCamera = intent.getBooleanExtra(Constants.SWITCH_CAMERA, false);
//            cameraId = intent.getIntExtra(Constants.CAMERA_ID, BACK_CAMERA_ID);
            if(isSwitchToFrontCamera){
                cameraId = FRONT_CAMERA_ID;
            }else {
                cameraId = BACK_CAMERA_ID;
            }
//            this.startId = startId;
        }catch (Exception e){
            e.printStackTrace();
            //Hardcode here if can not get camera id from intent
            cameraId = BACK_CAMERA_ID;
        }

        initSurface();

        return START_STICKY;
    }

    @Override
    protected void messageReceived(MessageEvent m) {
        String path = m.getPath();

        SharedObject sharedObject = gson.fromJson(path, SharedObject.class);
        switch (sharedObject.getCommand()) {
            case STOP_RECORD_VIDEO_BACKGROUND:
                notifyVideoIsStopped();
                break;
        }
    }

    private void notifyVideoIsRecording(){
        if(mWearableNode != null && mGoogleApiClient != null){
            SharedObject sharedObject = new SharedObject();
            sharedObject.setCommand(SharedObject.COMMAND.START_RECORD_VIDEO_BACKGROUND);
            String obTmp = gson.toJson(sharedObject);
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mWearableNode.getId(), obTmp, null);
        }
    }

    private void notifyVideoIsStopped(){
        if(mWearableNode != null && mGoogleApiClient != null){
            SharedObject sharedObjectStopRecord = new SharedObject();
            sharedObjectStopRecord.setCommand(SharedObject.COMMAND.STOP_RECORD_VIDEO_BACKGROUND);
            sharedObjectStopRecord.setSwitchToFrontCamera(isSwitchToFrontCamera);
            String message = "File is saved in " + fileName;
            sharedObjectStopRecord.setMessage(message);
            String obTmp = gson.toJson(sharedObjectStopRecord);
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mWearableNode.getId(), obTmp, null);
        }
    }

    @Override
    protected void findWearableNode() {
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(result -> {
            if(result.getNodes().size()>0) {
                mWearableNode = result.getNodes().get(0);
                Log.d(TAG, "Found wearable: name=" + mWearableNode.getDisplayName() + ", id=" + mWearableNode.getId());
                notifyVideoIsRecording();
            } else {
                mWearableNode = null;
            }
        });
    }

    // Method called right after Surface created (initializing and starting MediaRecorder)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated");
        Ulti.createFolder(Constants.VIDEO_FOLDER);
        long time = Calendar.getInstance().getTimeInMillis();
        fileName = Constants.VIDEO_FOLDER + time + Constants.VIDEO_TYPE;
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                if (Camera.getNumberOfCameras() <= cameraId) {
                    cameraId = Constants.DEFAULT_CAMERA;
                }

            } else {
                cameraId = Constants.DEFAULT_CAMERA;
            }
            camera = Camera.open(cameraId);

            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    Log.d(TAG, "onPreviewFrame was called");
                }
            });


            Camera.Size sizeOfCamera = null;
            for (int i = 0; i < camera.getParameters().getSupportedPreviewSizes().size(); i++) {
                Camera.Size previewSize = camera.getParameters().getSupportedPreviewSizes().get(i);
                boolean foundEqualSize = false;
                for (int j = 0; j < camera.getParameters().getSupportedPictureSizes().size(); j++) {
                    Camera.Size pictureSize = camera.getParameters().getSupportedPictureSizes().get(j);
                    if(previewSize.width == pictureSize.width && previewSize.height == pictureSize.height){
                        sizeOfCamera = previewSize;
                        foundEqualSize = true;
                        break;
                    }
                }

                if(foundEqualSize) break;
            }
            mediaRecorder = new MediaRecorder();

            Ulti.initRecorder(mediaRecorder, surfaceHolder, camera, cameraId, fileName, sizeOfCamera);

            mediaRecorder.prepare();
            mediaRecorder.start();


            camera.reconnect();
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] bytes, Camera camera) {
                    Log.d(TAG, "onPreviewFrame");
                }
            });
            surfaceView.getHolder().addCallback(this );

            notifyVideoIsRecording();
        } catch (Exception e) {
            e.printStackTrace();
            Ulti.deleteFile(fileName, getBaseContext());
            stopSelf();
        }


    }

    // Stop recording and remove SurfaceView 
    @Override
    public void onDestroy() {

        try {
            if (camera != null && mediaRecorder != null) {
                surfaceView.getHolder().removeCallback(this );
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();

                camera.stopPreview();
                camera.setPreviewCallback(null);
                camera.lock();
                camera.release();

                windowManager.removeView(surfaceView);


            }
//            stopSelf(startId);



        } catch (Exception e) {
            e.printStackTrace();
            Ulti.deleteFile(fileName, getBaseContext());
//            stopSelf(startId);
        }

        notifyVideoIsStopped();



        super.onDestroy();


    }



    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {

        Log.d(TAG, "surfaceChanged");
        camera.setPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
                Log.d(TAG, "onPreviewFrame was called");
            }
        });
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceDestroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
