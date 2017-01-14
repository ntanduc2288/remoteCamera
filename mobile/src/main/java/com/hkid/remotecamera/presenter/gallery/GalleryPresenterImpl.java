package com.hkid.remotecamera.presenter.gallery;

import com.hkid.remotecamera.presenter.objects.ImageItem;
import com.hkid.remotecamera.util.Constants;

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

    public GalleryPresenterImpl(GalleryPresenter.View view) {
        this.view = view;
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
    }

    @Override
    public ArrayList<ImageItem> getAllItemsData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();

        // get list of files from Apollo folders
        try {
            File videofiles[] = new File(Constants.VIDEO_FOLDER).listFiles();
            File photofiles[] = new File(Constants.IMAGE_FOLDER).listFiles();

            Arrays.sort(videofiles, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return -Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                }
            });

            if (videofiles != null) {
                for (File file : videofiles) {
                    imageItems.add(new ImageItem(file.getAbsolutePath(), Constants.MEDIA_TYPE_VIDEO, file, false));
                }
            }

            if (photofiles != null) {
                for (File file : photofiles) {
                    imageItems.add(new ImageItem(file.getAbsolutePath(), Constants.MEDIA_TYPE_PHOTO, file, false));
                }
            }

            /******* fake entries for testing *******/
//            BitmapFactory.Options o=new BitmapFactory.Options();
//            o.inSampleSize = 4;
//            o.outHeight = BITMAP_SIZE;
//            o.outWidth = BITMAP_SIZE;
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
//                    R.drawable.test360,o);
//            Bitmap thumbnail = ThumbnailUtils.extractThumbnail(bitmap, BITMAP_SIZE, BITMAP_SIZE);
//            bitmap.recycle();
//            bitmap = null;
//            for (int i = 0; i < 50; i++) {
//                // media type is constant int 0 or 1, so use mod to assign types to our dummy data
//                int mytype = i % 2;
//                imageItems.add(new ImageItem("/pretend/path/" + i, mytype, null));
//            }

            // sort imageItems
            Collections.sort(imageItems);
        } catch (Exception exception) {
            //something went wrong. return empty list.
            exception.printStackTrace();
        }
        System.gc();

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
