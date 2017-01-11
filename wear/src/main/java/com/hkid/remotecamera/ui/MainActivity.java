package com.hkid.remotecamera.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.ImageView;

import com.data.SharedObject;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.hkid.remotecamera.R;

import butterknife.OnClick;

public class MainActivity extends Activity {


    GoogleApiClient mGoogleApiClient;
    Node mPhoneNode;
    ImageView imgPreview;
    Button btnTakePicture;
    Button btnStartPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgPreview = (ImageView) findViewById(R.id.imgPreview);
        btnTakePicture = (Button) findViewById(R.id.btnRecordBackgroundVideo);
        btnStartPreview = (Button) findViewById(R.id.btnStartPreview);


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
                .addOnConnectionFailedListener(connectionResult -> {
                })
                .addApi(Wearable.API)
                .build();

        mGoogleApiClient.connect();

        btnTakePicture.setOnClickListener(view -> clickOnTakePicture());
        btnStartPreview.setOnClickListener(view -> startPreviewBackground(mPhoneNode));

    }

    @OnClick(R.id.btnRecordBackgroundVideo)
    public void clickOnTakePicture() {
        startRecordBackgroundVideo(mPhoneNode);
    }

    @Override
    protected void onDestroy() {
        stopRecordBackgroundVideo(mPhoneNode);
        stopPreviewBackground(mPhoneNode);
        Wearable.MessageApi.removeListener(mGoogleApiClient, messageListener);
        super.onDestroy();
    }

    // Listen messages
    MessageApi.MessageListener messageListener = messageEvent -> onMessageResult(messageEvent);

    private void onMessageResult(MessageEvent messageEvents) {
        runOnUiThread(() -> {
            String path = messageEvents.getPath();
            if(path.equals(SharedObject.START_PREVIEW_CAMERA_BACKGROUND)){
                byte[] data = messageEvents.getData();
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                imgPreview.setImageBitmap(bitmap);
            }
        });
    }

    private void findPhoneNode() {
        PendingResult<NodeApi.GetConnectedNodesResult> pendingResult = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
        pendingResult.setResultCallback(getConnectedNodesResult -> {
            if (getConnectedNodesResult.getNodes().size() > 0) {
                mPhoneNode = getConnectedNodesResult.getNodes().get(0);
//                startRecordBackgroundVideo(mPhoneNode);
            }
        });
    }

    private void startRecordBackgroundVideo(Node phoneNode) {
        if (phoneNode != null && mGoogleApiClient != null) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, phoneNode.getId(), SharedObject.START_RECORD_VIDEO_BACKGROUND, null);
        }
    }

    private void stopRecordBackgroundVideo(Node phoneNode) {
        if (phoneNode != null && mGoogleApiClient != null) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, phoneNode.getId(), SharedObject.STOP_RECORD_VIDEO_BACKGROUND, null);
        }
    }

    private void startPreviewBackground(Node phoneNode) {
        if (phoneNode != null && mGoogleApiClient != null) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, phoneNode.getId(), SharedObject.START_PREVIEW_CAMERA_BACKGROUND, null);
        }
    }

    private void stopPreviewBackground(Node phoneNode) {
        if (phoneNode != null && mGoogleApiClient != null) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, phoneNode.getId(), SharedObject.STOP_PREVIEW_CAMERA_BACKGROUND, null);
        }
    }

}
