package com.hkid.remotecamera.ui.recordAudio;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.widget.FrameLayout;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.ui.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/12/17
 */
public class RecordAudioActivity extends BaseActivity {
    @BindView(R.id.frContainer)
    FrameLayout frContainer;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_record_audio;
    }

    @Override
    protected void setupViews() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        CardFragment cardFragment = CardFragment.create("Title",
                "Description",
                R.drawable.cicle_icon);
        fragmentTransaction.add(R.id.frContainer, cardFragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
