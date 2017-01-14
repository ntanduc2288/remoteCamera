package com.hkid.remotecamera.presenter.gallery;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hkid.remotecamera.R;
import com.hkid.remotecamera.customView.GridViewItem;
import com.hkid.remotecamera.presenter.objects.ImageItem;
import com.hkid.remotecamera.util.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Duc Nguyen
 * @version 1.0
 * @since 12/22/16
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder>{

    public boolean isEditing;
    ArrayList<ImageItem> data;
    GalleryItemSelectedListener itemSelectedListener;
    Context context;
    public interface GalleryItemSelectedListener{
        void selectedItem(ImageItem imageItem);
        void onLongClickItem(ImageItem imageItem);
    }

    public GalleryAdapter(Context context, GalleryItemSelectedListener galleryItemSelectedListener) {
        this.context = context;
        this.itemSelectedListener = galleryItemSelectedListener;
    }

    public void setData(ArrayList<ImageItem> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void resetToNormalMode(){
        isEditing = false;
        for (ImageItem imageItem : data){
            imageItem.setSelected(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageItem item = data.get(position);

        Glide.with(context).load(item.getPath()).into(holder.imgItem);

        if (isEditing) {
            holder.imgIndicator.setVisibility(View.VISIBLE);
            if (item.isSelected()) {
                holder.imgIndicator.setImageResource(R.drawable.icon_select);
            } else {
                holder.imgIndicator.setImageResource(R.drawable.icon_unselect);
            }
        }else {
            holder.imgIndicator.setVisibility(View.GONE);
        }
        holder.imgItem.setOnClickListener(view -> {
            if(itemSelectedListener != null){
                if(isEditing){
                    item.setSelected(!item.isSelected());
                    notifyDataSetChanged();
                }else {
                    itemSelectedListener.selectedItem(item);
                }
            }
        });

        holder.imgItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!isEditing) {
                    if (itemSelectedListener != null) {
                        isEditing = !isEditing;
                        item.setSelected(!item.isSelected());
                        itemSelectedListener.onLongClickItem(item);
                        notifyDataSetChanged();
                    }
                }
                return true;
            }
        });


        switch (item.getMediaType()){
            case Constants.MEDIA_TYPE_PHOTO:
                holder.lnMediaType.setVisibility(View.GONE);
                holder.imgIconMediaType.setVisibility(View.GONE);
                break;
            case Constants.MEDIA_TYPE_VIDEO:
                holder.lnMediaType.setVisibility(View.VISIBLE);
                holder.imgIconMediaType.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.play_icon).into(holder.imgIconMediaType);
                holder.lblTime.setText(item.getTimeDuration());
                break;
            case Constants.MEDIA_TYPE_AUDIO:
                holder.lnMediaType.setVisibility(View.VISIBLE);
                holder.imgIconMediaType.setVisibility(View.VISIBLE);
                Glide.with(context).load(R.drawable.audio_icon).into(holder.imgIconMediaType);
                holder.lblTime.setText(item.getTimeDuration());
                break;
        }
    }

    @Override
    public int getItemCount() {
        if(data != null){
            return data.size();
        }
        return 0;
    }

    public ArrayList<ImageItem> getSelectedItems(){
        ArrayList<ImageItem> selecteds = new ArrayList<>();
        if (data != null && data.size() > 0){
            for(ImageItem item: data){
                if (item.isSelected()){
                    selecteds.add(item);
                }
            }
        }
        return selecteds;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.image_item)
        GridViewItem imgItem;
        @BindView(R.id.indicator_icon)
        ImageView imgIndicator;
        @BindView(R.id.imgIconMediaType)
        ImageView imgIconMediaType;
        @BindView(R.id.lnMediaType)
        LinearLayout lnMediaType;
        @BindView(R.id.lblTime)
        TextView lblTime;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
