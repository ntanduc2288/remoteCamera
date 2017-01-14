package com.hkid.remotecamera.ui.hiddenPicture;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.ui.BaseActivity;
import com.skyfishjy.library.RippleBackground;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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
    @BindView(R.id.rippleBg)
    RippleBackground rippleBg;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;

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
        runOnUiThread(() -> {
            rippleBg.startRippleAnimation();
            prbLoading.setVisibility(View.VISIBLE);
        });

    }

    @Override
    public void hideLoading() {
        runOnUiThread(() -> {
            rippleBg.stopRippleAnimation();
            prbLoading.setVisibility(View.GONE);
        });

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
        showSlideError(slidingLayout, message);
//        runOnUiThread(() -> showSlideError(slidingLayout, message));
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
