package com.hkid.remotecamera;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import butterknife.OnClick;

public class MainActivity extends Activity {


    GoogleApiClient mGoogleApiClient;
    Node mPhoneNode;
    ImageView imgPreview;
    Button btnTakePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        btnTakePicture = (Button) findViewById(R.id.btnTakePicture);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        findPhoneNode();
                        Wearable.MessageApi.addListener(mGoogleApiClient, messageListener);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {}
                })
                .addOnConnectionFailedListener(connectionResult -> {})
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();

        btnTakePicture.setOnClickListener(view -> clickOnTakePicture());

    }

    @OnClick(R.id.btnTakePicture)
    public void clickOnTakePicture(){
        openPhoneApp(mPhoneNode);
    }

    @Override
    protected void onDestroy() {
        closePhoneApp(mPhoneNode);
        Wearable.MessageApi.removeListener(mGoogleApiClient, messageListener);
        super.onDestroy();
    }

    MessageApi.MessageListener messageListener = messageEvent -> onMessageResult(messageEvent.getData());

    private void onMessageResult(byte[] data) {
        runOnUiThread(() -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

            imgPreview.setImageBitmap(bitmap);
        });
    }

    private void findPhoneNode() {
        PendingResult<NodeApi.GetConnectedNodesResult> pendingResult = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        pendingResult.setResultCallback(getConnectedNodesResult -> {
            if (getConnectedNodesResult.getNodes().size() > 0) {
                mPhoneNode = getConnectedNodesResult.getNodes().get(0);
                openPhoneApp(mPhoneNode);
            }
        });
    }

    private void openPhoneApp(Node phoneNode){
        if(phoneNode != null && mGoogleApiClient != null){
            Wearable.MessageApi.sendMessage(mGoogleApiClient, phoneNode.getId(), "start", null);
        }
    }

    private void closePhoneApp(Node phoneNode){
        if(phoneNode != null && mGoogleApiClient != null){
            Wearable.MessageApi.sendMessage(mGoogleApiClient, phoneNode.getId(), "close", null);
        }
    }
}
