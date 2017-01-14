package com.hkid.remotecamera.presenter.gallery;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.presenter.BaseActivity;
import com.hkid.remotecamera.presenter.objects.ImageItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/13/17
 */
public class GalleryActivity extends BaseActivity implements GalleryAdapter.GalleryItemSelectedListener, GalleryPresenter.View{
    @BindView(R.id.recylerViewGallery)
    android.support.v7.widget.RecyclerView recylerViewGallery;
    private GalleryAdapter galleryAdapter;
    private GalleryPresenter.Presenter galleryPresenter;

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_gallery;
    }

    @Override
    protected void setupViews() {
        galleryPresenter = new GalleryPresenterImpl(this);
        galleryAdapter = new GalleryAdapter(this, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recylerViewGallery.setLayoutManager(gridLayoutManager);
        recylerViewGallery.setAdapter(galleryAdapter);

        galleryPresenter.refreshData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void selectedItem(ImageItem imageItem) {

    }

    @Override
    public void onLongClickItem(ImageItem imageItem) {

    }

    @Override
    public void initOptionMenuDialog() {

    }

    @Override
    public void openTakePhotoView() {

    }

    @Override
    public void openStreamView() {

    }

    @Override
    public void openRecordVideoView() {

    }

    @Override
    public void openSettingsView() {

    }

    @Override
    public void openPreviewPhotoView(int mediaType, String path) {

    }

    @Override
    public void openPreviewVideoView(int mediaType, String path) {

    }

    @Override
    public void bindMediaOnView(ArrayList<ImageItem> imageItems) {
        if (galleryAdapter != null) {
            galleryAdapter.setData(imageItems);
        }
    }

    @Override
    public void changeToEditingMode(boolean editing) {

    }

    @Override
    public void showOptionMenuDialog() {

    }

    @Override
    public void hideOptionMenuDialog() {

    }

    @Override
    public void openShareDialog(ArrayList<ImageItem> selecteds) {

    }
}
