package com.hkid.remotecamera.ui.hiddenPicture;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.data.SharedObject;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.hkid.remotecamera.R;
import com.hkid.remotecamera.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/11/17
 */
public class HiddenPictureActivity extends BaseActivity implements HiddenPicturePresenter.View {
    @BindView(R.id.imgPreview)
    ImageView imgPreview;
    @BindView(R.id.prbLoading)
    ProgressBar prbLoading;
    @BindView(R.id.btnSwitchCamera)
    Button btnSwitchCamera;

    HiddenPicturePresenter.Presenter presenter;
    @BindView(R.id.rlControl)
    RelativeLayout rlControl;
    @BindView(R.id.imgTakePicture)
    ImageView imgTakePicture;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_hidden_picture;
    }

    @Override
    protected void setupViews() {
        presenter = new HiddenPicturePresenterImpl(this);
        presenter.performPreviewPicture(this);
    }

    @Override
    public void bindPreviewImageView(Bitmap bitmap) {
        runOnUiThread(() -> {
            imgPreview.setImageBitmap(bitmap);
        });
    }


    private void startRecordBackgroundVideo(Node phoneNode, GoogleApiClient mGoogleApiClient) {
        if (phoneNode != null && mGoogleApiClient != null) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, phoneNode.getId(), SharedObject.START_RECORD_VIDEO_BACKGROUND, null);
        }
    }

    private void stopRecordBackgroundVideo(Node phoneNode, GoogleApiClient mGoogleApiClient) {
        if (phoneNode != null && mGoogleApiClient != null) {
            Wearable.MessageApi.sendMessage(mGoogleApiClient, phoneNode.getId(), SharedObject.STOP_RECORD_VIDEO_BACKGROUND, null);
        }
    }

    @Override
    protected void onDestroy() {
        releaseResource();
        super.onDestroy();
    }

    @Override
    public void releaseResource() {
        if (presenter != null) {
            presenter.release();
        }
    }

    @Override
    public void showLoading() {
        prbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoading() {
        prbLoading.setVisibility(View.GONE);
    }

    @Override
    public void showControlView() {
//        rlControl.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideControlView() {
//        rlControl.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnSwitchCamera, R.id.btnTakePicture})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSwitchCamera:
                presenter.performSwitchCamera();
                break;
            case R.id.btnTakePicture:
                presenter.performTakePicture();
                break;
        }
    }

    @Override
    public void bindTakePictureImageView(Bitmap bitmap) {
        runOnUiThread(() -> {
            imgTakePicture.setTranslationX(0);
            imgTakePicture.setRotation(0);
            imgTakePicture.setVisibility(View.VISIBLE);
            imgTakePicture.setImageBitmap(bitmap);
            imgTakePicture.animate().translationX(imgTakePicture.getWidth())
                    .rotation(40)
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            imgTakePicture.setVisibility(View.GONE);
                        }
                    }).start();
        });

    }

    @Override
    public void setBackgroundForSwitchCameraButton(int res) {
        btnSwitchCamera.setBackgroundResource(res);
    }
}
