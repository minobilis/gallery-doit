package com.minobi.gallerydoit.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.minobi.gallerydoit.R;
import com.minobi.gallerydoit.data.Image;

import java.util.ArrayList;

class GridAdapter extends RecyclerView.Adapter {
    private ArrayList<Image> dataset;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView gridItemPicture;
        TextView gridItemWeather;

        public ViewHolder(View itemView) {
            super(itemView);
            this.gridItemPicture = itemView.findViewById(R.id.gridItemPicture);
            this.gridItemWeather = itemView.findViewById(R.id.gridItemWeather);
        }
    }

    public GridAdapter(ArrayList<Image> dataset, Context context) {
        this.context = context;
        this.dataset = dataset;
    }

    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(
                LayoutInflater.from(
                        parent.getContext()).inflate(
                        R.layout.grid_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder) holder;

        Image item = dataset.get(position);
        Glide.with(context)
                .load(item.getSmallImagePath())
                .into(myHolder.gridItemPicture);

        myHolder.gridItemWeather.setText(item.getParameters().getWeather());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
