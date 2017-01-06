package com.hkid.remotecamera.object;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/27/16
 */
public class ModeObject {
    public enum MODE_TYPE{
        HIDDEN_VIDEO, HIDDEN_PICTURE, RECORD_AUDIO, PREVIEW_CAMERA
    }
    int id;
    String name;
    MODE_TYPE modeType;

    public ModeObject(int id, String name, MODE_TYPE modeType) {
        this.id = id;
        this.name = name;
        this.modeType = modeType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public MODE_TYPE getModeType() {
        return modeType;
    }

    public void setModeType(MODE_TYPE modeType) {
        this.modeType = modeType;
    }
}
