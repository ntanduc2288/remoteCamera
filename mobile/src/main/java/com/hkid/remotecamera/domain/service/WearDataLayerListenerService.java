package com.hkid.remotecamera.domain.service;

import android.content.Intent;
import android.util.Log;

import com.data.SharedObject;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;
import com.google.gson.Gson;
import com.hkid.remotecamera.util.Constants;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/16/16
 */
public class WearDataLayerListenerService extends WearableListenerService {

    private static final String TAG = WearDataLayerListenerService.class.getSimpleName();
    private static final boolean D = true;
    private Gson gson;
    @Override
    public void onCreate() {
        if(D) Log.d(TAG, "onCreate");
        super.onCreate();
        gson = new Gson();
    }

    @Override
    public void onDestroy() {
        if(D) Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
    @Override
    public void onPeerConnected(Node peer) {
        if(D) Log.d(TAG, "onPeerConnected");
        super.onPeerConnected(peer);
        if(D) Log.d(TAG, "Connected: name=" + peer.getDisplayName() + ", id=" + peer.getId());
    }
    @Override
    public void onMessageReceived(MessageEvent m) {
        String path = m.getPath();
        Log.d(TAG, "onMessageReceived: " + path);

        try{
            SharedObject sharedObject = gson.fromJson(path, SharedObject.class);
            if(sharedObject != null){
                boolean switchToFrontCamera = sharedObject.isSwitchToFrontCamera();
                switch (sharedObject.getCommand()){
                    case START_PREVIEW_CAMERA_BACKGROUND:
                        Intent intent = new Intent(this, BackgroundPictureService.class);
                        intent.putExtra(Constants.SWITCH_CAMERA, switchToFrontCamera);
                        startService(intent);
                        break;
                    case STOP_PREVIEW_CAMERA_BACKGROUND:
                        stopService(new Intent(this, BackgroundPictureService.class));
                        break;
                    case START_RECORD_VIDEO_BACKGROUND:
                        Intent intentBg = new Intent(this, BackgroundVideoRecorderService.class);
                        intentBg.putExtra(Constants.SWITCH_CAMERA, switchToFrontCamera);
                        startService(intentBg);
                        break;
                    case STOP_RECORD_VIDEO_BACKGROUND:
                        stopService(new Intent(this, BackgroundVideoRecorderService.class));
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();

        }

    }
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        // i don't care
    }
}
