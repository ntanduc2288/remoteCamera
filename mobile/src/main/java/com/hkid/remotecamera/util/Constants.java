package com.hkid.remotecamera.util;

import android.media.CamcorderProfile;
import android.os.Environment;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/20/16
 */
public class Constants {

    public static final String CAMERA_ID = "CAMERA_ID";
    public static final String EMPTY_STRING = "";
    public static final int CAMERA_QUALITY = CamcorderProfile.QUALITY_480P;
    public static final int POSITIVE_90_DEGREE = 90;
    public static final int DEGREE_270 = 270;
    public static final int BACK_CAMERA_BIT_RATE = 1 * 1000 * 1000;
    public static final int FRONT_CAMERA_BIT_RATE = 1 * 1000 * 1000;
    public static final int DEFAULT_TIME_TO_RECORDING = 3 * 60 * 1000;
    public static final int DEFAULT_CAMERA = 0;
    public final static File SDROOT = Environment.getExternalStorageDirectory().getAbsoluteFile();
    public final static String SAFEAPP_FOLDER = SDROOT + File.separator + "SafeApp";
    public final static String VIDEO_FOLDER = SAFEAPP_FOLDER + File.separator + "Videos" + File.separator;
    public final static String IMAGE_FOLDER = SAFEAPP_FOLDER + File.separator + "Images" + File.separator;
    public final static String PREFIX_VIDEO_NAME = "SafeApp_";
    public final static String VIDEO_TYPE = ".mp4";
    public final static String PREFIX_VIDEO_ID = "Video_";
    public final static String PREFIX_LOCAL_FILE_URL = "file://";
    public static final LinkedHashMap<String, Integer> TIME_INTERVAL_LIST = new LinkedHashMap<String, Integer>() {{
        put("3 min", DEFAULT_TIME_TO_RECORDING);
        put("1 min", 60 * 1000);
        put("45 sec", 45 * 1000);
        put("30 sec", 30 * 1000);
    }};
}