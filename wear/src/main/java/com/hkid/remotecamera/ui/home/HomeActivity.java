package com.hkid.remotecamera.ui.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.adapter.WearableAdapter;
import com.hkid.remotecamera.object.ModeObject;

import java.util.ArrayList;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/27/16
 */
public class HomeActivity extends FragmentActivity implements HomePresenter.View {

    private static ArrayList<ModeObject> modes;
    private TextView lblHeader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initModes();
        // This is our list header
        lblHeader = (TextView) findViewById(R.id.lblHeader);

        WearableListView wearableListView = (WearableListView) findViewById(R.id.wearable_list);
        wearableListView.setAdapter(new WearableAdapter(this, modes, this));
        wearableListView.addOnScrollListener(mOnScrollListener);
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


    private void initModes(){
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

    }

    @Override
    public void openHiddenVideoView() {

    }

    @Override
    public void openRecordAudioView() {

    }

    @Override
    public void openPreviewCameraView() {

    }
}
