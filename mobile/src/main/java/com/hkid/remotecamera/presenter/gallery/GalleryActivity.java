package com.hkid.remotecamera.presenter.gallery;

import android.os.Bundle;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.presenter.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/13/17
 */
public class GalleryActivity extends BaseActivity {
//    @BindView(R.id.btn_settings)
//    AppCompatButton btnSettings;
//    @BindView(R.id.btn_delete)
//    AppCompatButton btnDelete;
//    @BindView(R.id.rlOptionMenu)
//    RelativeLayout rlOptionMenu;
//    @BindView(R.id.recylerViewGallery)
//    android.support.v7.widget.RecyclerView recylerViewGallery;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_gallery;
    }

    @Override
    protected void setupViews() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
