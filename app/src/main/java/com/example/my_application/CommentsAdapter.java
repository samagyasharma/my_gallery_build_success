package com.example.my_application;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {

    private final ArrayList<PaintingDetailActivity.Comment> commentsList;

    // Constructor
    public CommentsAdapter(ArrayList<PaintingDetailActivity.Comment> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(itemView);
    }

    /*@Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        // Bind data to the view holder
        PaintingDetailActivity.Comment comment = commentsList.get(position);
        holder.userNameTextView.setText(comment.getUserName());
        holder.commentTextView.setText(comment.getCommentText());
    }*/

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        PaintingDetailActivity.Comment comment = commentsList.get(position);
        holder.userNameTextView.setText(comment.getUserName());
        holder.commentTextView.setText(comment.getCommentText());

        // Ensure at least 2 comments are shown before scrolling is required
        if (position == commentsList.size() - 1) {
            holder.itemView.post(() -> holder.itemView.requestLayout());
        }
    }



    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    // ViewHolder class to represent each comment item
    static class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView userNameTextView;
        TextView commentTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize the views
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
        }
    }
}
