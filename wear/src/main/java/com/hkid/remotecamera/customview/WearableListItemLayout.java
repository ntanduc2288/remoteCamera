package com.hkid.remotecamera.customview;

import android.content.Context;
import android.graphics.Color;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hkid.remotecamera.R;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/27/16
 */
public class WearableListItemLayout extends LinearLayout implements WearableListView.OnCenterProximityListener{


    private static final float NO_ALPHA = 1f, PARTIAL_ALPHA = 0.40f;
    private static final float NO_X_TRANSLATION = 0f, X_TRANSLATION = 20f;

    private CircledImageView mCircle;
    private ImageView imgTitle;
    private final int mUnselectedCircleColor, mSelectedCircleColor;
    private float mBigCircleRadius;
    private float mSmallCircleRadius;
    private LayoutParams bigLayoutParams;
    private LayoutParams smallLayoutParams;

    public WearableListItemLayout(Context context) {
        this(context, null);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);

        mUnselectedCircleColor = Color.parseColor("#434343");
        mSelectedCircleColor = Color.parseColor("#434343");
        mSmallCircleRadius = getResources().getDimensionPixelSize(R.dimen.small_circle_radius);
        mBigCircleRadius = getResources().getDimensionPixelSize(R.dimen.big_circle_radius);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCircle = (CircledImageView) findViewById(R.id.circle);
        mCircle.setVisibility(GONE);
        imgTitle = (ImageView) findViewById(R.id.imgTitle);

        int size = getResources().getDimensionPixelOffset(R.dimen.big_circle_radius);
        bigLayoutParams = new LayoutParams(size, size);

        size = getResources().getDimensionPixelOffset(R.dimen.small_circle_radius);
        smallLayoutParams = new LayoutParams(size, size);
    }

    @Override
    public void onCenterPosition(boolean animate) {
        if (animate) {
            animate().alpha(NO_ALPHA).translationX(X_TRANSLATION).start();
        }


//        imgTitle.setLayoutParams(bigLayoutParams);
//        imgTitle.invalidate();
        imgTitle.setScaleX(1.4f);
        imgTitle.setScaleY(1.4f);
//        mCircle.setCircleColor(mSelectedCircleColor);
//        mCircle.setCircleRadius(mBigCircleRadius);
        Log.d("WearableListItemLayout", "onCenterPosition");
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        if (animate) {
            animate().alpha(PARTIAL_ALPHA).translationX(NO_X_TRANSLATION).start();
        }
        imgTitle.setScaleX(1);
        imgTitle.setScaleY(1);
//        mCircle.setCircleColor(mUnselectedCircleColor);
//        mCircle.setCircleRadius(mSmallCircleRadius);


    }
}
