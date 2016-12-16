package com.hkid.remotecamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {


    GoogleApiClient mGoogleApiClient;
    Node mPhoneNode;
    @BindView(R.id.imgPreview)
    ImageView imgPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        findPhoneNode();
                        Wearable.MessageApi.addListener(mGoogleApiClient, messageListener);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();

    }

    @Override
    protected void onDestroy() {
        Wearable.MessageApi.removeListener(mGoogleApiClient, messageListener);
        super.onDestroy();
    }

    MessageApi.MessageListener messageListener = new MessageApi.MessageListener() {
        @Override
        public void onMessageReceived(MessageEvent messageEvent) {
            onMessageResult(messageEvent.getData());
        }
    };

    private void onMessageResult(byte[] data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                if(imgPreview == null){
                    imgPreview = (ImageView) findViewById(R.id.imgPreview);
                }
                imgPreview.setImageBitmap(bitmap);
            }
        });
    }

    private void findPhoneNode() {
        PendingResult<NodeApi.GetConnectedNodesResult> pendingResult = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        pendingResult.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
            @Override
            public void onResult(@NonNull NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                if (getConnectedNodesResult.getNodes().size() > 0) {
                    mPhoneNode = getConnectedNodesResult.getNodes().get(0);
                }
            }
        });
    }
}
