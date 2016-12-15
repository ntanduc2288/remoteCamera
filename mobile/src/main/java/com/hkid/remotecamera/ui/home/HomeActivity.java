package com.hkid.remotecamera.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraDevice.StateCallback;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.customView.AutoFitTextureView;
import com.hkid.remotecamera.util.CompareSizesByArea;

import java.util.Arrays;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class HomeActivity extends FragmentActivity implements HomePresenter.View, TextureView.SurfaceTextureListener, ImageReader.OnImageAvailableListener {


    Unbinder unbinder;
    @BindView(R.id.btn_takepicture)
    Button btnTakepicture;
    @BindView(R.id.texture)
    AutoFitTextureView texture;

    private CameraDevice cameraDevice;
    private String cameraId = "0";
    private Size imageDimension;
    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private CaptureRequest captureRequest;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraCaptureSession cameraCaptureSession;
    private ImageReader mImageReader;
    private Surface previewSurface;
    private CameraManager cameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);
        keepScreenOn();
        initSurfaceView();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void initSurfaceView() {
        texture.setSurfaceTextureListener(this);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    public void clickOnSurfaceView() {

    }

    @OnClick(R.id.btn_takepicture)
    @Override
    public void clickOnTakePicture() {

        closeCamera();
        try {
            if(cameraId.endsWith("0")){
                cameraId = cameraManager.getCameraIdList()[1];
            }else {
                cameraId = cameraManager.getCameraIdList()[0];
            }
            openCamera(cameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void openCamera(String cameraId) {

        Log.d("HomeActivity", "is camera open");

        try {
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            assert map != null;
            imageDimension = map.getOutputSizes(SurfaceTexture.class)[0];

//            texture.setLayoutParams(new RelativeLayout.LayoutParams(imageDimension.getWidth(), imageDimension.getHeight()));


            // For still image captures, we use the largest available size.
            Size largest = Collections.max(
                    Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                    new CompareSizesByArea());

            int width = largest.getWidth();
            int height = largest.getHeight();
//            width = 320;
//            height = 420;
            mImageReader = ImageReader.newInstance(width, height,
                    ImageFormat.YUV_420_888, /*maxImages*/2);
            mImageReader.setOnImageAvailableListener(this, null);

            // Add permission for camera and let user grant the permission
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CAMERA_PERMISSION);
                return;
            }
            cameraManager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }

        if (null != mImageReader) {
            mImageReader.close();
            mImageReader = null;
        }

    }

    @Override
    public void createCameraPreview() {
        Log.d("HomeActivity", "createCameraPreview");
        SurfaceTexture surfaceTexture = texture.getSurfaceTexture();
        assert texture != null;
        surfaceTexture.setDefaultBufferSize(imageDimension.getWidth(), imageDimension.getHeight());
        texture.setAspectRatio(imageDimension.getHeight(),imageDimension.getWidth());
        previewSurface = new Surface(surfaceTexture);
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            captureRequestBuilder.addTarget(previewSurface);
            captureRequestBuilder.addTarget(mImageReader.getSurface());
            cameraDevice.createCaptureSession(Arrays.asList(previewSurface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    Log.d("HomeActivity", "onConfigured");
                    // The camera is already closed
                    if (null == cameraDevice) {
                        return;
                    }

                    // When the session is ready, we start displaying the preview
                    HomeActivity.this.cameraCaptureSession = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Log.e("HomeActivity", "onConfigureFailed");
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updatePreview() {
        if (null == cameraDevice) {
            Log.d("HomeActivity", "updatePreview error, return");
        }
        captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        try {
            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);

//            // Auto focus should be continuous for camera preview. [...code...]
//            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
//                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//            // Flash is automatically enabled when necessary.
//            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
//                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
//
//            // Finally, we start displaying the camera preview.
//            CaptureRequest mPreviewRequest = captureRequestBuilder.build();
//            cameraCaptureSession.setRepeatingRequest(mPreviewRequest,
//                    new CameraCaptureSession.CaptureCallback() {
//                        @Override
//                        public void onCaptureStarted(CameraCaptureSession session, CaptureRequest request, long timestamp, long frameNumber) {
//                            super.onCaptureStarted(session, request, timestamp, frameNumber);
//                        }
//                    }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();

        }
    }


    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        openCamera(cameraId);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    CameraDevice.StateCallback stateCallback = new StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.d("HomeActivity", "onOpened");
            cameraDevice = camera;
            createCameraPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            cameraDevice.close();
        }

        @Override
        public void onError(CameraDevice camera, int i) {
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(this, "Sorry!!!, you can't use this app without granting permission", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (texture.isAvailable()) {
            openCamera(cameraId);
        } else {
            texture.setSurfaceTextureListener(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
    }

    @Override
    public void onImageAvailable(ImageReader imageReader) {
        Log.d("HomeActivity", "onImageAvailable was called");
        if(imageReader != null){
            Image image = imageReader.acquireLatestImage();
            if(image != null){
                image.close();
            }
        }
    }
}
