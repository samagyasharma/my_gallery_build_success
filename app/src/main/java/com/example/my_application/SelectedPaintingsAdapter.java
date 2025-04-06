package com.example.my_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SelectedPaintingsAdapter extends RecyclerView.Adapter<SelectedPaintingsAdapter.ViewHolder> {
    private List<Painting> paintings;
    private Context context;

    public SelectedPaintingsAdapter(Context context, List<Painting> paintings) {
        this.context = context;
        this.paintings = paintings;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_painting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Painting painting = paintings.get(position);
        holder.titleTextView.setText(painting.getTitle());
        holder.priceTextView.setText(painting.getPrice());

        // Load image based on type (URL or resource)
        if (painting.isUrlBased()) {
            // Load URL-based image using Glide
            Glide.with(context)
                .load(painting.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.paintingImageView);
        } else {
            // Load resource-based image directly
            holder.paintingImageView.setImageResource(painting.getImageResId());
        }
    }

    @Override
    public int getItemCount() {
        return paintings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView paintingImageView;
        public TextView titleTextView;
        public TextView priceTextView;

        public ViewHolder(View view) {
            super(view);
            paintingImageView = view.findViewById(R.id.paintingPreview);
            titleTextView = view.findViewById(R.id.paintingName);
            priceTextView = view.findViewById(R.id.paintingPrice);
        }
    }
} 