package com.hkid.remotecamera.adapter;

import android.content.Context;
import android.support.wearable.view.CircledImageView;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hkid.remotecamera.R;
import com.hkid.remotecamera.object.ModeObject;
import com.hkid.remotecamera.ui.home.HomePresenter;

import java.util.ArrayList;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/27/16
 */
public class WearableAdapter extends WearableListView.Adapter {
    private ArrayList<ModeObject> mItems;
    private final LayoutInflater mInflater;
    private HomePresenter.View view;
    public WearableAdapter(Context context, ArrayList<ModeObject> items, HomePresenter.View view) {
        mInflater = LayoutInflater.from(context);
        mItems = items;
        this.view = view;
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(
            ViewGroup viewGroup, int i) {
        return new ItemViewHolder(mInflater.inflate(R.layout.item_mode, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder viewHolder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) viewHolder;
        TextView textView = itemViewHolder.mItemTextView;
        ModeObject modeObject = mItems.get(position);
        textView.setText(modeObject.getName());

        switch (modeObject.getModeType()){
            case HIDDEN_PICTURE:
                itemViewHolder.imgTitle.setBackgroundResource(R.drawable.hidden_camera_icon);
                break;
            case HIDDEN_VIDEO:
                itemViewHolder.imgTitle.setBackgroundResource(R.drawable.hidden_video_icon);
                break;
            case RECORD_AUDIO:
                itemViewHolder.imgTitle.setBackgroundResource(R.drawable.record_audio_icon);
                break;
            case PREVIEW_CAMERA:
                itemViewHolder.imgTitle.setBackgroundResource(R.drawable.remote_control_icon);
                break;
        }

        ((ViewGroup)textView.getParent()).setOnClickListener(v -> {
            switch (modeObject.getModeType()){
                case HIDDEN_PICTURE:
                    view.openHiddenPictureView();
                    break;
                case HIDDEN_VIDEO:
                    view.openHiddenVideoView();
                    break;
                case RECORD_AUDIO:
                    view.openRecordAudioView();
                    break;
                case PREVIEW_CAMERA:
                    view.openPreviewCameraView();
                    break;
            }
        });

//        circledView.setOnClickListener(view -> {
//            WearableListItemLayout parentView = (WearableListItemLayout) circledView.getParent();
////                if(parentView != null){
////                    if(!parentView.isCenter){
////                        parentView.onCenterPosition(true);
////                    }else {
////                        Toast.makeText(circledView.getContext().getApplicationContext(), "position:" + position, Toast.LENGTH_SHORT).show();
////                    }
////                }
//        });

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private static class ItemViewHolder extends WearableListView.ViewHolder {
        CircledImageView mCircledImageView;
        TextView mItemTextView;
        ImageView imgTitle;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mCircledImageView = (CircledImageView) itemView.findViewById(R.id.circle);
            mItemTextView = (TextView) itemView.findViewById(R.id.name);
            imgTitle = (ImageView) itemView.findViewById(R.id.imgTitle);
        }
    }
}
