package com.hkid.remotecamera.ui.hiddenPicture;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/11/17
 */
public interface HiddenPicturePresenter {
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
//        Observable<GoogleApiClient> initGoogleApiClient(Context context);
//        Observable<Node> findPhoneNode(GoogleApiClient googleApiClient);
        void startPreviewBackground(boolean switchCamera);
        void stopPreviewBackground();
        void takePicture();
//        void onMessageResult(MessageEvent messageEvents);
        void performSwitchCamera();
        void performTakePicture();
        void release();
        void getTimerTime();
        void startCounter(int currentTimer);
        void stopCounter();

    }
}
