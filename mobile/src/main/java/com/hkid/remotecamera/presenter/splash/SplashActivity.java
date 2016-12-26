package com.hkid.remotecamera.presenter.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.domain.service.BackgroundVideoRecorder;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/20/16
 */
public class SplashActivity extends FragmentActivity implements SurfaceHolder.Callback {



    public static SurfaceView mSurfaceView;
    public static SurfaceHolder mSurfaceHolder;
    public static boolean mPreviewRunning;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);


        findViewById(R.id.btnRecord).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRecordingInBackgroundThread();
            }
        });

        findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecordingInBackgroundThread();
            }
        });
    }


    protected void startRecordingInBackgroundThread(){
        Intent intent = new Intent(getApplicationContext(), BackgroundVideoRecorder.class);
        startService(intent);
//        Intent intent = new Intent(this, RecorderService.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startService(intent);

    }

    protected void stopRecordingInBackgroundThread(){
        Intent intent = new Intent(getApplicationContext(), BackgroundVideoRecorder.class);
        stopService(intent);
//        stopService(new Intent(this, RecorderService.class));
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
