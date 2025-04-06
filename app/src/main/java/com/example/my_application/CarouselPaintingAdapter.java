package com.example.my_application;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class CarouselPaintingAdapter extends RecyclerView.Adapter<CarouselPaintingAdapter.PaintingViewHolder> {
    private List<CarouselPainting> paintingList;
    private OnPaintingClickListener listener;

    public interface OnPaintingClickListener {
        void onPaintingClick(CarouselPainting painting);
    }

    public CarouselPaintingAdapter(List<CarouselPainting> paintingList, OnPaintingClickListener listener) {
        this.paintingList = paintingList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PaintingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_carousel_painting, parent, false);
        return new PaintingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaintingViewHolder holder, int position) {
        CarouselPainting painting = paintingList.get(position);
        holder.name.setText(painting.getTitle());
        holder.artist.setText(painting.getArtist());
        holder.price.setText(String.format("$%.2f", painting.getPrice()));
        
        Glide.with(holder.itemView.getContext())
                .load(painting.getImageResourceId())
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPaintingClick(painting);
            }
        });
    }

    @Override
    public int getItemCount() {
        return paintingList.size();
    }

    static class PaintingViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;
        TextView artist;
        TextView price;

        PaintingViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.paintingImage);
            name = itemView.findViewById(R.id.paintingName);
            artist = itemView.findViewById(R.id.paintingArtist);
            price = itemView.findViewById(R.id.paintingPrice);
        }
    }
}
