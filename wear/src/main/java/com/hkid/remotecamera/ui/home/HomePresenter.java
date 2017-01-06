package com.hkid.remotecamera.ui.home;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/6/17
 */
public interface HomePresenter {

    interface View{
        void openHiddenPictureView();
        void openHiddenVideoView();
        void openRecordAudioView();
        void openPreviewCameraView();
    }
}
