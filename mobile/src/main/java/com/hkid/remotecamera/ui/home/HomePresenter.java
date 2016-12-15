package com.hkid.remotecamera.ui.home;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/14/16
 */
public interface HomePresenter {
    interface View{
        void keepScreenOn();
        void initSurfaceView();
        void clickOnSurfaceView();
        void openCamera();
        void closeCamera();
        void createCameraPreview();
        void updatePreview();
    }

    interface Presenter{

    }
}
