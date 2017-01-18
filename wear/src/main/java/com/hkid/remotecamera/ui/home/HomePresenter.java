package com.hkid.remotecamera.ui.home;

import android.content.Context;


/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/6/17
 */
public interface HomePresenter {

    interface View{
        void showNode(String nodeName);
        void openHiddenPictureView();
        void openHiddenVideoView();
        void openRecordAudioView();
        void openPreviewCameraView();
    }

    interface Presenter{
        void initPhoneNode(Context context);
    }
}
