package com.hkid.remotecamera.ui.recordAudio;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/12/17
 */
public class RecordAudioActivity extends BaseActivity implements RecordAudioPresenter.View {


    @BindView(R.id.lblRecordingStatus)
    TextView lblRecordingStatus;
    @BindView(R.id.btnRecord)
    Button btnRecord;

    RecordAudioPresenter.Presenter presenter;
    @BindView(R.id.prbLoading)
    ProgressBar prbLoading;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_record_audio;
    }

    @Override
    protected void setupViews() {
        presenter = new RecordAudioPresenterImpl(this);
        presenter.initPhoneNode(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
        runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void switchToRecordingMode() {
        runOnUiThread(() -> {
            btnRecord.setText("Stop");
            btnRecord.setCompoundDrawablesWithIntrinsicBounds(R.drawable.stop_icon, 0, 0, 0);
            btnRecord.setCompoundDrawablePadding(10);
            lblRecordingStatus.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void switchToNormalMode() {
        runOnUiThread(() -> {
            btnRecord.setText("Start");
            btnRecord.setCompoundDrawablesWithIntrinsicBounds(R.drawable.start_icon, 0, 0, 0);
            btnRecord.setCompoundDrawablePadding(10);
            lblRecordingStatus.setVisibility(View.GONE);
        });
    }

    @OnClick(R.id.btnRecord)
    public void onClick() {
        presenter.performRecordAudio();
    }

    @Override
    protected void onDestroy() {
        presenter.release();
        super.onDestroy();
    }
}
