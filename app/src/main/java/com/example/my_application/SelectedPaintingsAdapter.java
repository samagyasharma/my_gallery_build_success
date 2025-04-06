package com.example.my_application;

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
    private List<Painting> selectedPaintings;

    public SelectedPaintingsAdapter(List<Painting> selectedPaintings) {
        this.selectedPaintings = selectedPaintings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_selected_painting, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Painting painting = selectedPaintings.get(position);
        holder.paintingName.setText(painting.getName());
        holder.paintingPrice.setText("Rs " + painting.getPrice());

        if (painting.getImageUrl() != null && !painting.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(painting.getImageUrl())
                    .into(holder.paintingPreview);
        } else if (painting.getImageResId() != 0) {
            holder.paintingPreview.setImageResource(painting.getImageResId());
        }
    }

    @Override
    public int getItemCount() {
        return selectedPaintings.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView paintingPreview;
        TextView paintingName;
        TextView paintingPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            paintingPreview = itemView.findViewById(R.id.paintingPreview);
            paintingName = itemView.findViewById(R.id.paintingName);
            paintingPrice = itemView.findViewById(R.id.paintingPrice);
        }
    }
} 