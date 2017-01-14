package com.hkid.remotecamera.domain.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import com.data.SharedObject;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.hkid.remotecamera.util.Constants;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static com.hkid.remotecamera.presenter.splash.SplashActivity.mPreviewRunning;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/26/16
 */
public class BackgroundPictureService extends BaseRemoteCameraService implements SurfaceHolder.Callback {

    // Camera variables
// a surface holder
// a variable to control the camera
    private Camera mCamera;
    // the camera parameters
    private Camera.Parameters parameters;
    private Bitmap bmp;
    FileOutputStream fo;
    private String FLASH_MODE;
    private int QUALITY_MODE = 0;
    private boolean isFrontCamRequest = false;
    private Camera.Size pictureSize;
    SurfaceView sv;
    private SurfaceHolder sHolder;
    private WindowManager windowManager;
    WindowManager.LayoutParams params;
    public Intent cameraIntent;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int width = 0, height = 0;
    private static int currentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
    public int mCameraOrientation;
    private String TAG = BackgroundPictureService.class.getSimpleName();


    private Camera openFrontFacingCameraGingerbread() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
        }
        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("Camera",
                            "Camera failed to open: " + e.getLocalizedMessage());
                /*
                 * Toast.makeText(getApplicationContext(),
                 * "Front Camera failed to open", Toast.LENGTH_LONG)
                 * .show();
                 */
                }
            }
        }
        return cam;
    }

    private void setBesttPictureResolution() {
        // get biggest picture size
        width = pref.getInt("Picture_Width", 0);
        height = pref.getInt("Picture_height", 0);

        if (width == 0 | height == 0) {
            pictureSize = getBiggesttPictureSize(parameters);
            if (pictureSize != null)
                parameters
                        .setPictureSize(pictureSize.width, pictureSize.height);
            // save width and height in sharedprefrences
            width = pictureSize.width;
            height = pictureSize.height;
            editor.putInt("Picture_Width", width);
            editor.putInt("Picture_height", height);
            editor.commit();

        } else {
            // if (pictureSize != null)
            parameters.setPictureSize(width, height);
        }
    }

    private Camera.Size getBiggesttPictureSize(Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result = size;
            } else {
                int resultArea = result.width * result.height;
                int newArea = size.width * size.height;

                if (newArea > resultArea) {
                    result = size;
                }
            }
        }

        return (result);
    }

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Check if this device has front camera
     */
    private boolean checkFrontCamera(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_FRONT)) {
            // this device has front camera
            return true;
        } else {
            // no front camera on this device
            return false;
        }
    }

    Handler handler = new Handler();

    public void doSnap() {
        if (mCamera == null || !mPreviewRunning) {
            Log.d(TAG, "tried to snap when camera was inactive");
            return;
        }
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> sizes = params.getSupportedPictureSizes();
        Camera.Size size = sizes.get(0);
        for (int i = 0; i < sizes.size(); i++) {
            if (sizes.get(i).width > size.width)
                size = sizes.get(i);
        }
        params.setPictureSize(size.width, size.height);
        mCamera.setParameters(params);
        Camera.PictureCallback jpegCallback = (data, camera) -> {
            notifyScanMedia(data);
            sendToWearable(SharedObject.TAKE_PICTURE, reduceByteArray(data), null);
            mCamera.startPreview();
        };
        mCamera.takePicture(null, null, jpegCallback);
    }

    private byte[] reduceByteArray(byte[] data) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 4;
        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, opts);
        int smallWidth, smallHeight;
        int dimension = 280;
        if (bmp.getWidth() > bmp.getHeight()) {
            smallWidth = dimension;
            smallHeight = dimension * bmp.getHeight() / bmp.getWidth();
        } else {
            smallHeight = dimension;
            smallWidth = dimension * bmp.getWidth() / bmp.getHeight();
        }
        Bitmap bmpSmall = Bitmap.createScaledBitmap(bmp, smallWidth, smallHeight, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmpSmall.compress(Bitmap.CompressFormat.WEBP, 50, baos);
        return baos.toByteArray();
    }

    private void notifyScanMedia(byte[] data) {
        try {
            FileOutputStream outStream = null;
            String filename = String.format(Constants.IMAGE_FOLDER + "img_wear_%d.jpg", System.currentTimeMillis());
            outStream = new FileOutputStream(filename);
            outStream.write(data);
            outStream.close();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + filename)));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @SuppressWarnings("deprecation")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // sv = new SurfaceView(getApplicationContext());
        super.onStartCommand(intent, flags, startId);


        if (isSwitchToFrontCamera) {
            currentCamera = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            currentCamera = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        cameraIntent = intent;
        Log.d("ImageTakin", "StartCommand()");
        pref = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.width = 1;
        params.height = 1;
        params.x = 0;
        params.y = 0;
        sv = new SurfaceView(getApplicationContext());

        windowManager.addView(sv, params);
        sHolder = sv.getHolder();
        sHolder.addCallback(this);

        // tells Android that this surface will have its data constantly
        // replaced
        if (Build.VERSION.SDK_INT < 11)
            sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        return Service.START_STICKY;
    }

    @Override
    protected void messageReceived(MessageEvent m) {
        String path = m.getPath();

        SharedObject sharedObject = gson.fromJson(path, SharedObject.class);
        switch (sharedObject.getCommand()) {
            case TAKE_PICTURE:
                doSnap();
                break;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d("BackgroundPictureServic", "onDestroy");
        mPreviewRunning = false;
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
        if (sv != null)
            windowManager.removeView(sv);
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);


        super.onDestroy();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d("BackgroundPictureServic", "surfaceChanged");
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                setCameraDisplayOrientation();
                mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                    public void onPreviewFrame(byte[] data, Camera arg1) {
//                        if (mWearableNode != null && readyToProcessImage && mPreviewRunning && displayFrameLag<6 && displayTimeLag<2000
//                                && System.currentTimeMillis() - lastMessageTime < 4000) {
//                    readyToProcessImage = false;
                        if (!mPreviewRunning) {
                            return;
                        }
                        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();

                        int[] rgb = decodeYUV420SP(data, previewSize.width, previewSize.height);
                        Bitmap bmp = Bitmap.createBitmap(rgb, previewSize.width, previewSize.height, Bitmap.Config.ARGB_8888);
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                        int smallWidth, smallHeight;
                        int dimension = 200;
                        // stream is lagging, cut resolution and catch up
//                    if(displayTimeLag > 1500) {
//                        dimension = 50;
//                    } else if(displayTimeLag > 500) {
//                        dimension = 100;
//                    } else {
//                        dimension = 200;
//                    }
                        dimension = 150;
                        if (previewSize.width > previewSize.height) {
                            smallWidth = dimension;
                            smallHeight = dimension * previewSize.height / previewSize.width;
                        } else {
                            smallHeight = dimension;
                            smallWidth = dimension * previewSize.width / previewSize.height;
                        }

                        Matrix matrix = new Matrix();
                        matrix.postRotate(mCameraOrientation);
                        Bitmap bmpSmall = Bitmap.createScaledBitmap(bmp, smallWidth, smallHeight, false);
                        Bitmap bmpSmallRotated = Bitmap.createBitmap(bmpSmall, 0, 0, smallWidth, smallHeight, matrix, false);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bmpSmallRotated.compress(Bitmap.CompressFormat.WEBP, 100, baos);
//                        displayFrameLag++;
                        sendToWearable(SharedObject.START_PREVIEW_CAMERA_BACKGROUND, baos.toByteArray(), new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(MessageApi.SendMessageResult result) {
//                                if(displayFrameLag>0) displayFrameLag--;
                            }
                        });
                        bmp.recycle();
                        bmpSmall.recycle();
                        bmpSmallRotated.recycle();
//                        readyToProcessImage = true;
//                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
        mPreviewRunning = true;

    }

    private void sendToWearable(String path, byte[] data, final ResultCallback<MessageApi.SendMessageResult> callback) {
        if (mWearableNode != null) {
            PendingResult<MessageApi.SendMessageResult> pending = Wearable.MessageApi.sendMessage(mGoogleApiClient, mWearableNode.getId(), path, data);
            pending.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult result) {
                    if (callback != null) {
                        callback.onResult(result);
                    }
                    if (!result.getStatus().isSuccess()) {
                        Log.d(TAG, "ERROR: failed to send Message: " + result.getStatus());
                    }
                }
            });
        } else {
            Log.d(TAG, "ERROR: tried to send message before device was found");
        }
    }

    public int[] decodeYUV420SP(byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int rgb[] = new int[width * height];
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0) r = 0;
                else if (r > 262143) r = 262143;
                if (g < 0) g = 0;
                else if (g > 262143) g = 262143;
                if (b < 0) b = 0;
                else if (b > 262143) b = 262143;
                rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        if (cameraIntent != null)
//            new TakeImage().execute(cameraIntent);
        mCamera = Camera.open(currentCamera);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (sHolder != null) {
            sHolder.removeCallback(this);
        }
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    public void setCameraDisplayOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        mCamera.getCameraInfo(currentCamera, info);
        int rotation = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int resultA = 0, resultB = 0;
        if (currentCamera == Camera.CameraInfo.CAMERA_FACING_BACK) {
            resultA = (info.orientation - degrees + 360) % 360;
            resultB = (info.orientation - degrees + 360) % 360;
            mCamera.setDisplayOrientation(resultA);
        } else {
            resultA = (360 + 360 - info.orientation - degrees) % 360;
            resultB = (info.orientation + degrees) % 360;
            mCamera.setDisplayOrientation(resultA);
        }
        Camera.Parameters params = mCamera.getParameters();
        params.setRotation(resultB);
        mCamera.setParameters(params);
        mCameraOrientation = resultB;
    }
}
