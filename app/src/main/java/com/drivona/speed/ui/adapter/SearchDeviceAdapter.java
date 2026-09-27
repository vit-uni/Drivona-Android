package com.drivona.speed.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drivona.speed.R;
import com.drivona.speed.api.BleRssiDevice;
import com.youth.banner.adapter.BannerAdapter;

import java.util.List;

public class SearchDeviceAdapter extends BannerAdapter<BleRssiDevice, SearchDeviceAdapter.BannerViewHolder> {

    public SearchDeviceAdapter(List<BleRssiDevice> mDatas) {
        //设置数据，也可以调用banner提供的方法,或者自己在adapter中实现
        super(mDatas);
    }


    //创建ViewHolder，可以用viewType这个字段来区分不同的ViewHolder
    @Override
    public BannerViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item_layout, null);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        inflate.setLayoutParams(params);
        return new BannerViewHolder(inflate);
    }

    @Override
    public void onBindView(BannerViewHolder holder, BleRssiDevice data, int position, int size) {
//        Glide.with(holder.imageView.getContext()).load(getImageUrl(data)).error(com.lalifa.base.R.drawable.mx_common_divider).thumbnail(0.1f).into(holder.imageView);
        holder.tv_device_name.setText(data.getBleName());
    }


    class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tv_device_name;

        public BannerViewHolder(@NonNull View view) {
            super(view);
            this.imageView = view.findViewById(R.id.imageView);
            this.tv_device_name = view.findViewById(R.id.tv_device_name);
        }
    }
}
