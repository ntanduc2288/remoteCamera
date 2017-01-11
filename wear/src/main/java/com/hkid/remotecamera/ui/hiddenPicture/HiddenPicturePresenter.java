package com.hkid.remotecamera.ui.hiddenPicture;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;

import rx.Observable;

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
        void showError(String message);
        void bindImageView(Bitmap bitmap);
    }

    interface Presenter{
        void performPreviewPicture(Context context);
        Observable<GoogleApiClient> initGoogleApiClient(Context context);
        Observable<Node> findPhoneNode(GoogleApiClient googleApiClient);
        void startPreviewBackground();
        void stopPreviewBackground();
        void onMessageResult(MessageEvent messageEvents);
        void performSwitchCamera();
        void release();
    }
}
