package com.hkid.remotecamera.ui.previewCamera;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/13/17
 */
public interface PreviewCameraPresenter {
    interface View{
        void releaseResource();
        void showLoading();
        void hideLoading();
        void showControlView();
        void hideControlView();
        void showError(String message);
        void bindPreviewImageView(Bitmap bitmap);
        void bindTakePictureImageView(Bitmap bitmap);
        void setBackgroundForSwitchCameraButton(int res);
        void openTimerScreen();
        void updateTimer(String timer);
        void updateCounterView(String time);
        void showCounterView();
        void hideCounterView();
    }

    interface Presenter{
        void performPreviewPicture(Context context);
        void startPreviewBackground(boolean switchCamera);
        void stopPreviewBackground();
        void takePicture();
        void performSwitchCamera();
        void performTakePicture();
        void release();
        void getTimerTime();
        void startCounter(int currentTimer);
        void stopCounter();
    }
}
