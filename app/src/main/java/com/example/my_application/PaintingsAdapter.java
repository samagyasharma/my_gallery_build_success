package com.example.my_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class PaintingsAdapter extends BaseAdapter {

    private Context context;
    private int[] paintingImages;

    public PaintingsAdapter(Context context, int[] paintingImages) {
        this.context = context;
        this.paintingImages = paintingImages;
    }

    @Override
    public int getCount() {
        return paintingImages.length;
    }

    @Override
    public Object getItem(int position) {
        return paintingImages[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.grid_item, parent, false);
        }

        ImageView paintingImage = convertView.findViewById(R.id.paintingImage);
        paintingImage.setImageResource(paintingImages[position]);

        return convertView;
    }
}
