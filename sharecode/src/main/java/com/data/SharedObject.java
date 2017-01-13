package com.data;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/11/17
 */
public class SharedObject {
    public static final String START_RECORD_VIDEO_BACKGROUND = "START_RECORD_VIDEO_BACKGROUND";
    public static final String STOP_RECORD_VIDEO_BACKGROUND = "STOP_RECORD_VIDEO_BACKGROUND";
    public static final String START_PREVIEW_CAMERA_BACKGROUND = "START_PREVIEW_CAMERA_BACKGROUND";
    public static final String STOP_PREVIEW_CAMERA_BACKGROUND = "STOP_PREVIEW_CAMERA_BACKGROUND";
    public static final String TAKE_PICTURE = "TAKE_PICTURE";

    public enum COMMAND{
        START_RECORD_VIDEO_BACKGROUND, STOP_RECORD_VIDEO_BACKGROUND,
        START_PREVIEW_CAMERA_BACKGROUND, STOP_PREVIEW_CAMERA_BACKGROUND,
        TAKE_PICTURE, START_RECORD_AUDIO, STOP_RECORD_AUDIO
    }

    COMMAND command;
    boolean switchToFrontCamera;
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public COMMAND getCommand() {
        return command;
    }

    public void setCommand(COMMAND command) {
        this.command = command;
    }

    public boolean isSwitchToFrontCamera() {
        return switchToFrontCamera;
    }

    public void setSwitchToFrontCamera(boolean switchCamera) {
        this.switchToFrontCamera = switchCamera;
    }
}
