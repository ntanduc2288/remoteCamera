package com.hkid.remotecamera.presenter.gallery;


import com.hkid.remotecamera.presenter.objects.ImageItem;

import java.util.ArrayList;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/22/16
 */
public interface GalleryPresenter {
    interface View {
        void finishRefresh();
        void initOptionMenuDialog();
        void openTakePhotoView();
        void openStreamView();
        void openRecordVideoView();
        void openSettingsView();
        void openPreviewPhotoView(ImageItem imageItem);
        void openPreviewVideoView(ImageItem imageItem);
        void openPreviewAudioView(ImageItem imageItem);
        void bindMediaOnView(ArrayList<ImageItem> imageItems);
        void changeToEditingMode(boolean editing);
        void showOptionMenuDialog();
        void hideOptionMenuDialog();
        void openShareDialog(ArrayList<ImageItem> selecteds);
    }

    interface Presenter {
        // TODO Implement business logic
        void performGalleryItemSelected(ImageItem item);
        void refreshData();
        ArrayList<ImageItem> getAllItemsData();
        void deleteMultipleFiles(ArrayList<ImageItem> files);
        void release();
    }
}
