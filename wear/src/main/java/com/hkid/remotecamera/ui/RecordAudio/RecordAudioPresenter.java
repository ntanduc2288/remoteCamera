package com.hkid.remotecamera.ui.recordAudio;

import android.content.Context;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/13/17
 */
public interface RecordAudioPresenter {
    interface View {
        void showLoading();
        void hideLoading();
        void showMessage(String message);
        void switchToRecordingMode();
        void switchToNormalMode();
    }

    interface Presenter {
        void initPhoneNode(Context context);
        void performRecordAudio();
        void startRecordAudio();
        void stopRecordAudio();
        void release();
    }
}
