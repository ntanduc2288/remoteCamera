package com.hkid.remotecamera.presenter.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.customView.SpacesItemDecoration;
import com.hkid.remotecamera.presenter.BaseActivity;
import com.hkid.remotecamera.presenter.objects.ImageItem;
import com.hkid.remotecamera.util.Constants;

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
        galleryPresenter = new GalleryPresenterImpl(this, this);
        galleryAdapter = new GalleryAdapter(this, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recylerViewGallery.setLayoutManager(gridLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.space_items);
        recylerViewGallery.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
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
        switch (imageItem.getMediaType()){
            case Constants.MEDIA_TYPE_PHOTO:
                openPreviewPhotoView(imageItem);
                break;
            case Constants.MEDIA_TYPE_VIDEO:
                openPreviewVideoView(imageItem);
                break;
            case Constants.MEDIA_TYPE_AUDIO:
                openPreviewAudioView(imageItem);
                break;
        }
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
    public void openPreviewPhotoView(ImageItem imageItem) {
        Intent intent = new Intent ();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageItem.getFile());
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    @Override
    public void openPreviewVideoView(ImageItem imageItem) {
        Intent intent = new Intent ();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageItem.getFile());
        intent.setDataAndType(uri, "video/*");
        startActivity(intent);

    }

    @Override
    public void openPreviewAudioView(ImageItem imageItem) {
        Intent intent = new Intent ();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageItem.getFile());
        intent.setDataAndType(uri, "audio/*");
        startActivity(intent);
    }

    @Override
    public void bindMediaOnView(ArrayList<ImageItem> imageItems) {
        if (galleryAdapter != null) {
            galleryAdapter.setData(imageItems);
        }
    }

    @Override
    public void changeToEditingMode(boolean editing) {
        galleryAdapter.resetToNormalMode();
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

    @Override
    public void onBackPressed() {
        if (galleryAdapter.isEditing) {
            // cancel delete
            changeToEditingMode(false);
        }  else {
            super.onBackPressed();
        }
    }
}
