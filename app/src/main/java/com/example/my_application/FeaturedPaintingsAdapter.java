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

public class FeaturedPaintingsAdapter extends RecyclerView.Adapter<FeaturedPaintingsAdapter.PaintingViewHolder> {
    private final Context context;
    private final List<Painting> paintings;

    public FeaturedPaintingsAdapter(Context context, List<Painting> paintings) {
        this.context = context;
        this.paintings = paintings;
    }

    @NonNull
    @Override
    public PaintingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_featured_painting, parent, false);
        return new PaintingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaintingViewHolder holder, int position) {
        Painting painting = paintings.get(position);
        
        holder.titleTextView.setText(painting.getTitle());
        holder.artistTextView.setText(painting.getArtist());
        holder.priceTextView.setText(painting.getPrice());
        holder.mediumTextView.setText(painting.getMedium());

        // Load image using Glide
        Glide.with(context)
                .load(painting.getImageUrl())
                .centerCrop()
                .into(holder.imageView);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaintingDetailActivity.class);
            // Make sure to use consistent key names
            intent.putExtra("painting_name", painting.getTitle());
            intent.putExtra("artist_name", painting.getArtist());
            intent.putExtra("painting_price", "Rs. " + painting.getPrice());
            intent.putExtra("painting_medium", painting.getMedium());
            intent.putExtra("painting_image", painting.getImageUrl());
            // Don't set paintingId since this is from featured paintings
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return paintings.size();
    }

    static class PaintingViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleTextView;
        TextView artistTextView;
        TextView priceTextView;
        TextView mediumTextView;

        PaintingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.paintingImageView);
            titleTextView = itemView.findViewById(R.id.paintingTitleTextView);
            artistTextView = itemView.findViewById(R.id.artistNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            mediumTextView = itemView.findViewById(R.id.mediumTextView);
        }
    }
} 