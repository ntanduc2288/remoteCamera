package com.hkid.remotecamera.presenter.objects;

/**
 * Created by mking on 12/20/16.
 */
import java.io.File;
import java.util.Comparator;

public class ImageItem implements Comparator<ImageItem>, Comparable<ImageItem> {
    private String title;
    private String path;
    private int mediaType;
    private File mFile;
    private boolean isSelected;

    public ImageItem(String path, int mediaType, File file, boolean isSelected) {
        super();
        this.isSelected = isSelected;
        this.title = "";
        this.path = path;
        this.mediaType = mediaType;
        this.mFile = file;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }
    public int getMediaType() {
        return mediaType;
    }

    public File getFile() {return mFile;}

    // Overriding the compareTo method
    public int compareTo(ImageItem o2) {
        File f1 = this.getFile();
        File f2 = o2.getFile();
        if (f1==null || f2==null) {
            return 1;
        }
        // negate result so we get most recent first
        return -Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
    }

    // Overriding the compare method to sort the age
    public int compare(ImageItem o1, ImageItem o2) {
        File f1 = o1.getFile();
        File f2 = o2.getFile();
        if (f1==null || f2==null) {
            return 1;
        }
        // negate result so we get most recent first
        return (int) -(f1.lastModified() - f2.lastModified());
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}


