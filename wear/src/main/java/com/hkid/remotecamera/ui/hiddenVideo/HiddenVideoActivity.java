package com.hkid.remotecamera.ui.hiddenVideo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

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
 * @since 1/12/17
 */
public class HiddenVideoActivity extends BaseActivity implements HiddenVideoPresenter.View {
    @BindView(R.id.rippleBg)
    RippleBackground rippleBg;
    @BindView(R.id.prbLoading)
    ProgressBar prbLoading;
    @BindView(R.id.switchCameraOption)
    Switch switchCameraOption;
    @BindView(R.id.btnRecordVideo)
    Button btnRecordVideo;
    @BindView(R.id.rlSwitchCameraContainer)
    RelativeLayout rlSwitchCameraContainer;

    HiddenVideoPresenter.Presenter presenter;
    @BindView(R.id.lblRecordingStatus)
    TextView lblRecordingStatus;
    @BindView(R.id.sliding_layout)
    SlidingUpPanelLayout slidingLayout;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_hidden_video;
    }

    @Override
    protected void setupViews() {
//        rippleBg.startRippleAnimation();
        presenter = new HiddenVideoPresenterImpl(this);
        presenter.initPhoneNode(this);
        switchCameraOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                presenter.performSwitchCamera(isChecked);
            }
        });

    }

    @Override
    public void showLoading() {
        runOnUiThread(() -> prbLoading.setVisibility(View.VISIBLE));
    }

    @Override
    public void hideLoading() {
        runOnUiThread(() -> prbLoading.setVisibility(View.GONE));

    }

    @Override
    public void showMessage(String message) {
        runOnUiThread(() -> showSlideError(slidingLayout, message));
    }

    @Override
    public void switchToRecordingMode() {
        runOnUiThread(() -> {
            btnRecordVideo.setText("Stop");
            btnRecordVideo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stop_icon, 0, 0, 0);
            btnRecordVideo.setCompoundDrawablePadding(10);
            rlSwitchCameraContainer.setVisibility(View.GONE);
            lblRecordingStatus.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void switchToNormalMode() {
        runOnUiThread(() -> {
            btnRecordVideo.setText("Start");
            btnRecordVideo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.start_icon, 0, 0, 0);
            btnRecordVideo.setCompoundDrawablePadding(10);
            rlSwitchCameraContainer.setVisibility(View.VISIBLE);
            lblRecordingStatus.setVisibility(View.GONE);
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btnRecordVideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRecordVideo:
                presenter.performRecordVideo();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        presenter.release();
        super.onDestroy();

    }
}
