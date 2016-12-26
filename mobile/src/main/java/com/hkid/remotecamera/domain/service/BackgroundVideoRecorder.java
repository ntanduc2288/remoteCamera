package com.hkid.remotecamera.domain.service;

import android.app.Service;
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

import com.hkid.remotecamera.util.Constants;
import com.hkid.remotecamera.util.Ulti;

import java.util.Calendar;

public class BackgroundVideoRecorder extends Service implements SurfaceHolder.Callback {
    public static final String TAG = BackgroundVideoRecorder.class.getName();
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

        // Start foreground service to avoid unexpected kill 
//        Notification notification = new Notification.Builder(this)
//            .setContentTitle("Background Video Recorder")
//            .setContentText("")
//            .setSmallIcon(R.drawable.lockicon)
//            .build();
//        startForeground(1234, notification);
//        initNotificaiton();
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
            cameraId = intent.getIntExtra(Constants.CAMERA_ID, BACK_CAMERA_ID);
//            this.startId = startId;
        }catch (Exception e){
            e.printStackTrace();
            //Hardcode here if can not get camera id from intent
            cameraId = 0;
        }

        initSurface();

        return START_STICKY;
    }

    // Method called right after Surface created (initializing and starting MediaRecorder)
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d(TAG, "surfaceCreated");
        Ulti.createFolder(Constants.VIDEO_FOLDER);
        long time = Calendar.getInstance().getTimeInMillis();
        fileName = Constants.VIDEO_FOLDER + Constants.PREFIX_VIDEO_NAME + time + Constants.VIDEO_TYPE;
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

        } catch (Exception e) {
            e.printStackTrace();
            Ulti.deleteFile(fileName, getBaseContext());
//            stopSelf(startId);
        }


    }

    // Stop recording and remove SurfaceView 
    @Override
    public void onDestroy() {

        try {
            if (camera != null && mediaRecorder != null) {
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();

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
