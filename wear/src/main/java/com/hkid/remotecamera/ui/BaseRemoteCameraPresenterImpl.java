package com.hkid.remotecamera.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.ChannelApi;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import rx.Observable;
import rx.Subscriber;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/12/17
 */
public class BaseRemoteCameraPresenterImpl implements BaseRemoteCameraPresenter{
    private String TAG = BaseRemoteCameraPresenterImpl.class.getSimpleName();
    protected GoogleApiClient googleApiClient;
    protected Node phoneNode;
    protected MessageApi.MessageListener messageListener = messageEvent -> onMessageResult(messageEvent);
    protected Gson gson;

    public BaseRemoteCameraPresenterImpl() {
        gson = new Gson();
    }

    @Override
    public Observable<GoogleApiClient> initGoogleApiClient(Context context) {
        return Observable.create(new Observable.OnSubscribe<GoogleApiClient>() {
            @Override
            public void call(Subscriber<? super GoogleApiClient> subscriber) {
                googleApiClient = new GoogleApiClient.Builder(context)
                        .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                subscriber.onNext(googleApiClient);
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                subscriber.onError(new Throwable("onConnectionSuspended"));
                            }
                        })
                        .addOnConnectionFailedListener(connectionResult -> {
                            subscriber.onError(new Throwable(connectionResult.toString()));
                        })
                        .addApi(Wearable.API)
                        .build();

                googleApiClient.connect();
            }
        });
    }

    @Override
    public Observable<Node> findPhoneNode(GoogleApiClient googleApiClient) {
        return Observable.create(new Observable.OnSubscribe<Node>() {
            @Override
            public void call(Subscriber<? super Node> subscriber) {
                PendingResult<NodeApi.GetConnectedNodesResult> pendingResult = Wearable.NodeApi.getConnectedNodes(BaseRemoteCameraPresenterImpl.this.googleApiClient);
                pendingResult.setResultCallback(getConnectedNodesResult -> {
                    if (getConnectedNodesResult.getNodes().size() > 0) {
                        Node note = getConnectedNodesResult.getNodes().get(0);
                        subscriber.onNext(note);
                    } else {
                        subscriber.onError(new Throwable("Could not find phone node"));
                    }
                });
            }
        });
    }

    @Override
    public void onMessageResult(MessageEvent messageEvents) {

    }

    @Override
    public void release() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            Wearable.MessageApi.removeListener(googleApiClient, messageListener);
            Wearable.ChannelApi.removeListener(googleApiClient, channelListener);
            googleApiClient.disconnect();
            googleApiClient = null;
        }
    }

    protected Observable<Node> getPhoneNode(Context context){
        return initGoogleApiClient(context)
                .doOnNext(googleApiClient -> Wearable.MessageApi.addListener(googleApiClient, messageListener))
                .doOnNext(googleApiClient1 -> Wearable.ChannelApi.addListener(googleApiClient1, channelListener))
                .flatMap(googleApiClient -> findPhoneNode(googleApiClient))
                .doOnNext(node -> phoneNode = node);
    }


    protected ChannelApi.ChannelListener channelListener = new ChannelApi.ChannelListener() {
        @Override
        public void onChannelOpened(Channel channel) {
            Log.d(TAG, "onChannelOpened: A new channel to this device was just opened.\n" +
                    "From Node ID" + channel.getNodeId() + "\n" +
                    "Path: " + channel.getPath());
            channel.getInputStream(googleApiClient).setResultCallback(new ResultCallback<Channel.GetInputStreamResult>() {
                @Override
                public void onResult(@NonNull Channel.GetInputStreamResult getInputStreamResult) {
                    Log.d(TAG, "onChannelOpened: onResult");

                    InputStream inputStream = null;
                    BufferedReader bufferedReader = null;
                    try {
                        inputStream = getInputStreamResult.getInputStream();
                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        // You may need to read more lines, depending on what you send.
                        final String message = bufferedReader.readLine();
                        Log.d(TAG, "onChannelOpened: onResult: Received the following message: " + message);
                    }
                    catch (final IOException ioexception) {
                        Log.w(TAG, "Could not read message from smartwatch to given node.\n" +
                                "Node ID: " + channel.getNodeId() + "\n" +
                                "Path: " + channel.getPath() + "\n" +
                                "Error message: " + ioexception.getMessage() + "\n" +
                                "Error cause: " + ioexception.getCause());
                    }
                    finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                            if (bufferedReader != null) {
                                bufferedReader.close();
                            }
                        }
                        catch (final IOException ioexception) {
                            Log.d(TAG, "onChannelOpened: onResult: Could not close buffered reader.\n" +
                                    "Node ID: " + channel.getNodeId() + "\n" +
                                    "Path: " + channel.getPath() + "\n" +
                                    "Error message: " + ioexception.getMessage() + "\n" +
                                    "Error cause: " + ioexception.getCause());
                        }
                    }
                }
            });
        }

        @Override
        public void onChannelClosed(Channel channel, int closeReason, int appSpecificErrorCode) {
            switch (closeReason) {
                case CLOSE_REASON_NORMAL:
                    Log.d(TAG, "onChannelClosed: Channel closed. Reason: normal close (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
                case CLOSE_REASON_DISCONNECTED:
                    Log.d(TAG, "onChannelClosed: Channel closed. Reason: disconnected (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
                case CLOSE_REASON_REMOTE_CLOSE:
                    Log.d(TAG, "onChannelClosed: Channel closed. Reason: closed by remote (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
                case CLOSE_REASON_LOCAL_CLOSE:
                    Log.d(TAG, "onChannelClosed: Channel closed. Reason: closed locally (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
            }
        }

        @Override
        public void onInputClosed(Channel channel, final int closeReason, final int appSpecificErrorCode) {
            switch (closeReason) {
                case CLOSE_REASON_NORMAL:
                    Log.d(TAG, "onInputClosed: Channel input side closed. Reason: normal close (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
                case CLOSE_REASON_DISCONNECTED:
                    Log.d(TAG, "onInputClosed: Channel input side closed. Reason: disconnected (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
                case CLOSE_REASON_REMOTE_CLOSE:
                    Log.d(TAG, "onInputClosed: Channel input side closed. Reason: closed by remote (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
                case CLOSE_REASON_LOCAL_CLOSE:
                    Log.d(TAG, "onInputClosed: Channel input side closed. Reason: closed locally (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
            }

        }

        public void onOutputClosed(final Channel channel, final int closeReason, final int appSpecificErrorCode) {
            switch (closeReason) {
                case CLOSE_REASON_NORMAL:
                    Log.d(TAG, "onOutputClosed: Channel output side closed. Reason: normal close (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
                case CLOSE_REASON_DISCONNECTED:
                    Log.d(TAG, "onOutputClosed: Channel output side closed. Reason: disconnected (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
                case CLOSE_REASON_REMOTE_CLOSE:
                    Log.d(TAG, "onOutputClosed: Channel output side closed. Reason: closed by remote (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
                case CLOSE_REASON_LOCAL_CLOSE:
                    Log.d(TAG, "onOutputClosed: Channel output side closed. Reason: closed locally (" + closeReason + ") Error code: " + appSpecificErrorCode + "\n" +
                            "From Node ID" + channel.getNodeId() + "\n" +
                            "Path: " + channel.getPath());
                    break;
            }
        }
    };
}
