package com.example.my_application;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ToteBagAdapter extends RecyclerView.Adapter<ToteBagAdapter.ViewHolder> {
    private List<Painting> paintings;
    private ToteBagListener listener;

    public interface ToteBagListener {
        void onPaintingRemoved(int position);
        void onCheckboxChanged();
    }

    public ToteBagAdapter(List<Painting> paintings, ToteBagListener listener) {
        this.paintings = paintings;
        this.listener = listener;
    }

    public void updatePaintings(List<Painting> newPaintings) {
        this.paintings = new ArrayList<>(newPaintings);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tote_bag, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Painting painting = paintings.get(position);
        holder.paintingNameText.setText(painting.getTitle());
        holder.paintingPriceText.setText("Rs " + painting.getPrice());
        holder.paintingCheckbox.setChecked(false);  // Default state

        // Load painting image based on type
        if (painting.getImageUrl() != null && !painting.getImageUrl().isEmpty()) {
            // Load URL-based image
            Glide.with(holder.itemView.getContext())
                    .load(painting.getImageUrl())
                    .placeholder(R.drawable.placeholder_image)
                    .error(R.drawable.placeholder_image)
                    .into(holder.paintingPreview);
        } else if (painting.getImageResId() != 0) {
            // Load resource-based image
            holder.paintingPreview.setImageResource(painting.getImageResId());
        } else {
            // Set a placeholder if no image is available
            holder.paintingPreview.setImageResource(R.drawable.placeholder_image);
        }

        // Set click listener for the entire item
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), PaintingDetailActivity.class);
            intent.putExtra("paintingResId", painting.getImageResId());
            intent.putExtra("painting_image", painting.getImageUrl());
            intent.putExtra("painting_name", painting.getTitle());
            intent.putExtra("painting_medium", painting.getDescription() != null ? 
                                             painting.getDescription() : "");
            intent.putExtra("painting_price", "Rs " + painting.getPrice());
            intent.putExtra("artist_name", painting.getArtist());
            holder.itemView.getContext().startActivity(intent);
        });

        // Set up checkbox listener
        holder.paintingCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            painting.setSelected(isChecked);
            listener.onCheckboxChanged();
        });

        // Set up remove button
        if (holder.removeButton != null) {
            holder.removeButton.setOnClickListener(v -> {
                listener.onPaintingRemoved(position);
            });
        }
    }

    @Override
    public int getItemCount() {
        return paintings.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemDetailsContainer;
        CheckBox paintingCheckbox;
        TextView paintingNameText;
        TextView paintingPriceText;
        Button removeButton;
        ImageView paintingPreview;

        ViewHolder(View itemView) {
            super(itemView);
            itemDetailsContainer = itemView.findViewById(R.id.itemDetailsContainer);
            paintingCheckbox = itemView.findViewById(R.id.paintingCheckbox);
            paintingNameText = itemView.findViewById(R.id.paintingNameText);
            paintingPriceText = itemView.findViewById(R.id.paintingPriceText);
            removeButton = itemView.findViewById(R.id.removeButton);
            paintingPreview = itemView.findViewById(R.id.paintingPreview);
        }
    }
} 