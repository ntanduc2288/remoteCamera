package com.hkid.remotecamera.domain.service;

import android.content.Intent;
import android.util.Log;

import com.data.SharedData;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/16/16
 */
public class WearDataLayerListenerService extends WearableListenerService {

    private static final String TAG = WearDataLayerListenerService.class.getSimpleName();
    private static final boolean D = true;

    @Override
    public void onCreate() {
        if(D) Log.d(TAG, "onCreate");
        super.onCreate();
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
        
        switch (path){
            //Start record background video
            case SharedData.START_RECORD_VIDEO_BACKGROUND:
                startService(new Intent(this, BackgroundVideoRecorder.class));
                break;
            //Stop record background video
            case SharedData.STTOP_RECORD_VIDEO_BACKGROUND:
                stopService(new Intent(this, BackgroundVideoRecorder.class));
                break;
        }
    }
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        // i don't care
    }
}
