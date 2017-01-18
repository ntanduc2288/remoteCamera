package com.hkid.remotecamera.domain.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;
import com.hkid.remotecamera.util.Constants;

import java.util.Scanner;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/12/17
 */
public abstract class BaseRemoteCameraService extends Service {
    protected Gson gson;
    protected Node mWearableNode = null;
    protected GoogleApiClient mGoogleApiClient;
    protected boolean isSwitchToFrontCamera;
    private String TAG = BaseRemoteCameraService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        gson = new Gson();
        isSwitchToFrontCamera = intent.getBooleanExtra(Constants.SWITCH_CAMERA, false);

        intiGoogleApiClient();
        return super.onStartCommand(intent, flags, startId);
    }

    private void intiGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        Log.d(TAG, "onConnected: " + connectionHint);
                        findWearableNode();
                        Wearable.MessageApi.addListener(mGoogleApiClient, mMessageListener);
                    }
                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
    }

    private MessageApi.MessageListener mMessageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived (MessageEvent m){
            Log.d(TAG, "onMessageReceived: " + m.getPath());
            messageReceived(m);
            long lastMessageTime = System.currentTimeMillis();
            Scanner s = new Scanner(m.getPath());
            String command = s.next();

//            if(command.equals("snap")) {
//                doSnap();
//            } else if(command.equals("switch")) {
//                int arg0 = 0;
//                if (s.hasNextInt()) arg0 = s.nextInt();
//                doSwitch(arg0);
//            } else if(command.equals("flash")) {
//                int arg0 = 0;
//                if (s.hasNextInt()) arg0 = s.nextInt();
//                doFlash(arg0);
//            } else if(command.equals("received")) {
//                long arg0 = 0;
//                if(s.hasNextLong()) arg0 = s.nextLong();
//                displayTimeLag = System.currentTimeMillis() - arg0;
//                if(D) Log.d(TAG, String.format("frame lag time: %d ms", displayTimeLag));
//            } else if(command.equals("stop")) {
//                moveTaskToBack(true);
//            }
        }
    };

    protected abstract void messageReceived (MessageEvent m);


    protected void findWearableNode() {
        PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        nodes.setResultCallback(result -> {
            if(result.getNodes().size()>0) {
                mWearableNode = result.getNodes().get(0);
                readyToRun();
                Log.d(TAG, "Found wearable: name=" + mWearableNode.getDisplayName() + ", id=" + mWearableNode.getId());
            } else {
                mWearableNode = null;
            }
        });
    }

    @Override
    public void onDestroy() {
        Wearable.MessageApi.removeListener(mGoogleApiClient, mMessageListener);
        super.onDestroy();
    }

    protected void readyToRun(){

    }
}
