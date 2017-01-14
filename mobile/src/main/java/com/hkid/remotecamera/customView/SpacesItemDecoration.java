package com.hkid.remotecamera.customView;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 1/15/17
 */
public class SpacesItemDecoration extends RecyclerView.ItemDecoration {

    private int mSpacing;

    public SpacesItemDecoration(int spacing) {
        mSpacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView recyclerView, RecyclerView.State state) {
        outRect.left = mSpacing;
        outRect.top = mSpacing;
        outRect.right = mSpacing;
        outRect.bottom = mSpacing;
    }
}

