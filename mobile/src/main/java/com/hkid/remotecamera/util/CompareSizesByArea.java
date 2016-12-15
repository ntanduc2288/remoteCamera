package com.hkid.remotecamera.util;

import android.util.Size;

import java.util.Comparator;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/14/16
 */
public class CompareSizesByArea implements Comparator<Size> {

    @Override
    public int compare(Size lhs, Size rhs) {
        // We cast here to ensure the multiplications won't overflow
        return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                (long) rhs.getWidth() * rhs.getHeight());
    }

}
