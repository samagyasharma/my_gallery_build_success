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
        
        holder.titleTextView.setText(painting.getTitle());
        holder.artistTextView.setText(painting.getArtist());
        holder.priceTextView.setText("Rs " + painting.getPrice());

        Glide.with(context)
                .load(painting.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .centerCrop()
                .into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PaintingDetailActivity.class);
            // Use painting title as the ID
            intent.putExtra("paintingId", painting.getTitle());
            intent.putExtra("painting_name", painting.getTitle());
            intent.putExtra("artist_name", painting.getArtist());
            intent.putExtra("painting_price", painting.getPrice());
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