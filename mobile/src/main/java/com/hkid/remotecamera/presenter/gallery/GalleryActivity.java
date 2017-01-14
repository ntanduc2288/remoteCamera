package com.hkid.remotecamera.presenter.gallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.customView.DeleteDialogFragment;
import com.hkid.remotecamera.customView.SpacesItemDecoration;
import com.hkid.remotecamera.presenter.BaseActivity;
import com.hkid.remotecamera.presenter.objects.ImageItem;
import com.hkid.remotecamera.util.Constants;
import com.michael.easydialog.EasyDialog;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/13/17
 */
public class GalleryActivity extends BaseActivity implements GalleryAdapter.GalleryItemSelectedListener, GalleryPresenter.View, DeleteDialogFragment.OnDeleteListener {
    @BindView(R.id.recylerViewGallery)
    RecyclerView recylerViewGallery;
    @BindView(R.id.swipeRefreshview)
    SwipeRefreshLayout swipeRefreshview;
    private GalleryAdapter galleryAdapter;
    private GalleryPresenter.Presenter galleryPresenter;
    @BindView(R.id.rlOptionMenu)
    RelativeLayout rlOptionMenu;
    private EasyDialog optionMenuDialog;
    private LinearLayout lnPopupContent;

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
        initOptionMenuDialog();

        swipeRefreshview.setColorSchemeColors(ContextCompat.getColor(this, R.color.colorPrimary));
        swipeRefreshview.setOnRefreshListener(() -> {
            galleryPresenter.refreshData();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void finishRefresh() {
        swipeRefreshview.setRefreshing(false);
    }

    @Override
    public void selectedItem(ImageItem imageItem) {
        switch (imageItem.getMediaType()) {
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
        changeToEditingMode(true);
    }

    @Override
    public void initOptionMenuDialog() {
        lnPopupContent = new LinearLayout(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelOffset(R.dimen.layout_width_popup_menu), ViewGroup.LayoutParams.WRAP_CONTENT);
        lnPopupContent.setLayoutParams(layoutParams);
        lnPopupContent.setPadding(20, 50, 20, 50);
        lnPopupContent.setGravity(Gravity.CENTER);
        lnPopupContent.setOrientation(LinearLayout.VERTICAL);

        AppCompatTextView lblDelete = new AppCompatTextView(this);
        lblDelete.setLayoutParams(layoutParams);
        lblDelete.setGravity(Gravity.CENTER);
        lblDelete.setText(getString(R.string.delete));
        lblDelete.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        lblDelete.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        lblDelete.setPadding(0, 0, 0, 100);
        lblDelete.setTextSize(18);
        lblDelete.setOnClickListener(v -> {
            hideOptionMenuDialog();
            deleteMultipleFiles();
        });

        AppCompatTextView lblUpload = new AppCompatTextView(this);
        lblUpload.setLayoutParams(layoutParams);
        lblUpload.setGravity(Gravity.CENTER);
        lblUpload.setText(R.string.upload);
        lblUpload.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        lblUpload.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        lblUpload.setTextSize(18);
        lblUpload.setOnClickListener(v -> {
            hideOptionMenuDialog();
            openShareDialog(galleryAdapter.getSelectedItems());
        });

        lnPopupContent.addView(lblDelete);
        lnPopupContent.addView(lblUpload);

        optionMenuDialog = new EasyDialog(this);
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
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageItem.getFile());
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
    }

    @Override
    public void openPreviewVideoView(ImageItem imageItem) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = Uri.fromFile(imageItem.getFile());
        intent.setDataAndType(uri, "video/*");
        startActivity(intent);

    }

    @Override
    public void openPreviewAudioView(ImageItem imageItem) {
        Intent intent = new Intent();
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
        if (editing) {
            rlOptionMenu.setVisibility(View.VISIBLE);
        } else {
            rlOptionMenu.setVisibility(View.GONE);
            galleryAdapter.resetToNormalMode();

        }
    }

    @Override
    public void hasNoSelectedItem() {
        changeToEditingMode(false);
    }

    @Override
    public void showOptionMenuDialog() {
        if (optionMenuDialog != null) {
            int[] attachedViewLocation = new int[2];
            rlOptionMenu.getLocationOnScreen(attachedViewLocation);
            attachedViewLocation[0] = attachedViewLocation[0] + rlOptionMenu.getWidth();
            attachedViewLocation[1] = attachedViewLocation[1] + rlOptionMenu.getHeight();
            optionMenuDialog.setLayout(lnPopupContent)
                    .setGravity(EasyDialog.GRAVITY_BOTTOM)
                    .setLocation(attachedViewLocation)
                    .setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                    .setMatchParent(false);
            optionMenuDialog.show();
        }
    }

    @Override
    public void hideOptionMenuDialog() {
        if (optionMenuDialog != null) {
            optionMenuDialog.dismiss();
        }
    }

    @Override
    public void openShareDialog(ArrayList<ImageItem> selecteds) {
        if (selecteds.size() == 0) return;
        Intent shareIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        ArrayList<Uri> uris = new ArrayList<>();
        boolean hasImage = false;
        boolean hasVideo = false;
        boolean hasAudio = false;


        for (int i = 0; i < selecteds.size(); i++) {
            File file = selecteds.get(i).getFile();
            Uri uri = Uri.fromFile(file);
            uris.add(uri);

            String extension = FilenameUtils.getExtension(file.getName());
            if (extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg")) {
                hasImage = true;
            }

            if (extension.equalsIgnoreCase("mp4")) {
                hasVideo = true;
            }

            if (extension.equalsIgnoreCase("3gpp")) {
                hasAudio = true;
            }
        }

        String type = "*/*";

        if (hasImage && hasVideo && hasAudio) {
            type = "*/*";
        } else if (hasImage) {
            type = "image/*";
        } else if (hasVideo) {
            type = "video/*";
        } else if (hasAudio) {
            type = "audio/*";
        }

        shareIntent.setType(type);

        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

        startActivity(Intent.createChooser(shareIntent, getString(R.string.upload)));
    }

    @Override
    public void onBackPressed() {
        if (galleryAdapter.isEditing) {
            // cancel delete
            changeToEditingMode(false);
        } else {
            super.onBackPressed();
        }
    }

    void deleteMultipleFiles() {
        ArrayList<ImageItem> selecteds = galleryAdapter.getSelectedItems();
        int size = selecteds.size();
        if (size > 0) {
            String title = String.format((size == 1) ? getString(R.string.delete_one_file_title) : getString(R.string.delete_multiple_files_title), size);
            DeleteDialogFragment dialog = DeleteDialogFragment.newInstance(title);
            dialog.mCallback = this;
            dialog.show(this.getFragmentManager(), "DeleteDialogFragment");
        }
    }

    @Override
    public void onDoDelete() {
        ArrayList<ImageItem> selecteds = galleryAdapter.getSelectedItems();
        if (selecteds.size() > 0) {
            if (galleryPresenter != null) {
                galleryPresenter.deleteMultipleFiles(selecteds);
            }
        }
    }

    @Override
    public void onCancelDelete() {
        // do nothing
    }

    @OnClick(R.id.rlOptionMenu)
    public void onClick() {
        showOptionMenuDialog();
    }
}
