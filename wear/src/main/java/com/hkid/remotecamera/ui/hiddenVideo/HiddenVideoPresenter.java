package com.hkid.remotecamera.ui.hiddenVideo;

import android.content.Context;

import com.google.android.gms.wearable.Node;

import rx.Observable;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/12/17
 */
public interface HiddenVideoPresenter {
    interface View{
        void showLoading();
        void hideLoading();
        void showMessage(String message);
        void switchToRecordingMode();
        void switchToNormalMode();
    }

    interface Presenter{
        void initPhoneNode(Context context);
        void performSwitchCamera(boolean isSwitchToFrontCamera);
        void performRecordVideo();
        Observable<Node> connectToNode(Node node);
        void stopRecordVideo();
        void release();
    }
}
