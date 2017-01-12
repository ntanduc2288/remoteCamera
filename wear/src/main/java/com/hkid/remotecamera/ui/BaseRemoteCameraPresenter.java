package com.hkid.remotecamera.ui;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;

import rx.Observable;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/12/17
 */
public interface BaseRemoteCameraPresenter {
    Observable<GoogleApiClient> initGoogleApiClient(Context context);
    Observable<Node> findPhoneNode(GoogleApiClient googleApiClient);
    void onMessageResult(MessageEvent messageEvents);
    void release();
}
