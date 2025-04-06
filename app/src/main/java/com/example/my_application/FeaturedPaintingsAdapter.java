package com.example.my_application;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
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
        
        // Add logging to debug data binding
        Log.d("ADAPTER_BINDING", "Binding painting at position " + position);
        Log.d("ADAPTER_BINDING", "Title: " + painting.getTitle());
        Log.d("ADAPTER_BINDING", "ImageUrl: " + painting.getImageUrl());
        
        holder.titleTextView.setText(painting.getTitle());
        holder.artistTextView.setText(painting.getArtist());
        holder.priceTextView.setText("Rs. " + painting.getPrice());

        // Load image using Glide with error handling
        Glide.with(context)
                .load(painting.getImageUrl())
                .placeholder(R.drawable.placeholder_image) // Add a placeholder image
                .error(R.drawable.placeholder_image) // Add an error image
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e("GLIDE_ERROR", "Error loading image: " + painting.getImageUrl(), e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("GLIDE_SUCCESS", "Image loaded successfully: " + painting.getImageUrl());
                        return false;
                    }
                })
                .centerCrop()
                .into(holder.imageView);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaintingDetailActivity.class);
            intent.putExtra("painting_name", painting.getTitle());
            intent.putExtra("artist_name", painting.getArtist());
            intent.putExtra("painting_price", "Rs. " + painting.getPrice());
            intent.putExtra("painting_image", painting.getImageUrl());
            intent.putExtra("painting_description", painting.getDescription());
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
        // Remove mediumTextView declaration

        PaintingViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.paintingImageView);
            titleTextView = itemView.findViewById(R.id.paintingTitleTextView);
            artistTextView = itemView.findViewById(R.id.artistNameTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            // Remove mediumTextView initialization
        }
    }
} 