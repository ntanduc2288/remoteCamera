package com.hkid.remotecamera.presenter.gallery;

import android.content.Context;

import com.hkid.remotecamera.presenter.objects.ImageItem;
import com.hkid.remotecamera.util.Constants;
import com.hkid.remotecamera.util.Ulti;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/22/16
 */
public class GalleryPresenterImpl implements GalleryPresenter.Presenter {

    GalleryPresenter.View view;
    Context context;
    public GalleryPresenterImpl(Context context, GalleryPresenter.View view) {
        this.view = view;
        this.context = context;
    }

    @Override
    public void performGalleryItemSelected(ImageItem item) {
        int mediaType = item.getMediaType();

//        if (mediaType == Constants.MEDIA_TYPE_VIDEO) { //Open preview video view
//            view.openPreviewVideoView(mediaType, item.getPath());
//        } else { //Open preview photo view
//            view.openPreviewPhotoView(mediaType, item.getPath());
//        }
    }

    @Override
    public void refreshData() {
        ArrayList<ImageItem> imageItems = getAllItemsData();
        view.bindMediaOnView(imageItems);
        view.changeToEditingMode(false);
        view.finishRefresh();
    }

    @Override
    public ArrayList<ImageItem> getAllItemsData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        // get list of files from Apollo folders
        try {
            File videofiles[] = new File(Constants.VIDEO_FOLDER).listFiles();
            File photofiles[] = new File(Constants.IMAGE_FOLDER).listFiles();
            File audioFiles[] = new File(Constants.AUDIO_FOLDER).listFiles();

            if (videofiles != null) {
                Arrays.sort(videofiles, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return -Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                    }
                });
                for (File file : videofiles) {
                    imageItems.add(new ImageItem(file.getAbsolutePath(), Constants.MEDIA_TYPE_VIDEO, file, false, Ulti.getTimeFromMedia(context, file)));
                }
            }

            if (audioFiles != null){
                Arrays.sort(audioFiles, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return -Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                    }
                });

                for (File file : audioFiles) {
                    imageItems.add(new ImageItem(file.getAbsolutePath(), Constants.MEDIA_TYPE_AUDIO, file, false, Ulti.getTimeFromMedia(context, file)));
                }

            }

            if (photofiles != null) {
                for (File file : photofiles) {
                    imageItems.add(new ImageItem(file.getAbsolutePath(), Constants.MEDIA_TYPE_PHOTO, file, false));
                }
            }

            // sort imageItems
            Collections.sort(imageItems);
        } catch (Exception exception) {
            //something went wrong. return empty list.
            exception.printStackTrace();
        }

        return imageItems;
    }

    @Override
    public void deleteMultipleFiles(ArrayList<ImageItem> files) {
        if (files != null && files.size() > 0){
            try {
                for(ImageItem item:files){
                    File file = item.getFile();
                    file.delete();
                }
                refreshData();
                view.changeToEditingMode(false);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void release() {
        view = null;
    }
}
