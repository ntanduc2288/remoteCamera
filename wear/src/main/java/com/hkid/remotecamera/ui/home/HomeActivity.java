package com.hkid.remotecamera.ui.home;

import android.content.Intent;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.adapter.WearableAdapter;
import com.hkid.remotecamera.object.ModeObject;
import com.hkid.remotecamera.ui.BaseActivity;
import com.hkid.remotecamera.ui.hiddenPicture.HiddenPictureActivity;
import com.hkid.remotecamera.ui.hiddenVideo.HiddenVideoActivity;
import com.hkid.remotecamera.ui.previewCamera.PreviewCameraActivity;
import com.hkid.remotecamera.ui.recordAudio.RecordAudioActivity;

import java.util.ArrayList;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/27/16
 */
public class HomeActivity extends BaseActivity implements HomePresenter.View {

    private static ArrayList<ModeObject> modes;
    private TextView lblHeader;
    private HomePresenter.Presenter presenter;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_home;
    }

    @Override
    protected void setupViews() {
        presenter = new HomePresenterImpl(this);
        initModes();
        // This is our list header
        lblHeader = (TextView) findViewById(R.id.lblHeader);

        WearableListView wearableListView = (WearableListView) findViewById(R.id.wearable_list);
        wearableListView.setAdapter(new WearableAdapter(this, modes, this));
        wearableListView.addOnScrollListener(mOnScrollListener);
        wearableListView.setClickListener(new WearableListView.ClickListener() {
            @Override
            public void onClick(WearableListView.ViewHolder viewHolder) {
                WearableAdapter.ItemViewHolder itemViewHolder = (WearableAdapter.ItemViewHolder) viewHolder;
                if (itemViewHolder != null) {
                    ModeObject modeObject = itemViewHolder.getModeObject();

                    if (modeObject != null) {
                        switch (modeObject.getModeType()) {
                            case HIDDEN_PICTURE:
                                openHiddenPictureView();
                                break;
                            case HIDDEN_VIDEO:
                                openHiddenVideoView();
                                break;
                            case RECORD_AUDIO:
                                openRecordAudioView();
                                break;
                            case PREVIEW_CAMERA:
                                openPreviewCameraView();
                                break;
                        }
                    }
                }
            }

            @Override
            public void onTopEmptyRegionClick() {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.initPhoneNode(this);
    }

    // The following code ensures that the title scrolls as the user scrolls up
    // or down the list
    private WearableListView.OnScrollListener mOnScrollListener =
            new WearableListView.OnScrollListener() {
                @Override
                public void onAbsoluteScrollChange(int i) {
                    // Only scroll the title up from its original base position
                    // and not down.
                    if (i > 0) {
                        lblHeader.setY(-i);
                    }
                }

                @Override
                public void onScroll(int i) {
                    // Placeholder
                }

                @Override
                public void onScrollStateChanged(int i) {
                    // Placeholder
                }

                @Override
                public void onCentralPositionChanged(int i) {
                    // Placeholder
                }
            };


    private void initModes() {
        modes = new ArrayList<>();

        ModeObject modeHiddenVideo = new ModeObject(1, "Hidden Video", ModeObject.MODE_TYPE.HIDDEN_VIDEO);
        ModeObject modeHiddenPicture = new ModeObject(2, "Hidden Picture", ModeObject.MODE_TYPE.HIDDEN_PICTURE);
        ModeObject modeRecordAudio = new ModeObject(3, "Record Audio", ModeObject.MODE_TYPE.RECORD_AUDIO);
        ModeObject modePreviewCamera = new ModeObject(4, "Preview Camera", ModeObject.MODE_TYPE.PREVIEW_CAMERA);
        modes.add(modeHiddenPicture);
        modes.add(modeHiddenVideo);
        modes.add(modeRecordAudio);
        modes.add(modePreviewCamera);
    }

    @Override
    public void openHiddenPictureView() {
        openActivity(HiddenPictureActivity.class);
    }

    @Override
    public void openHiddenVideoView() {
        openActivity(HiddenVideoActivity.class);
    }

    @Override
    public void openRecordAudioView() {
        openActivity(RecordAudioActivity.class);
    }

    @Override
    public void openPreviewCameraView() {
        openActivity(PreviewCameraActivity.class);
    }

    private void openActivity(Class aClass){
        Intent intent = new Intent(this, aClass);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_right_to_left, R.anim.fade_out);
    }

    @Override
    public void showNode(String nodeName) {
        lblHeader.setText(nodeName);
    }
}
