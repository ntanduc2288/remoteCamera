package com.hkid.remotecamera.domain.service;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;
import com.hkid.remotecamera.presenter.home.HomeActivity;

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
        if(D) Log.d(TAG, "onMessageReceived: " + m.getPath());
        if(m.getPath().equals("start")) {
            Intent startIntent = new Intent(this, HomeActivity.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
    }
    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        // i don't care
    }
}
