package com.example.my_application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CommentsAdapter extends FirestoreRecyclerAdapter<Comment, CommentsAdapter.CommentViewHolder> {

    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Constructor
    public CommentsAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, Context context) {
        super(options);
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment comment) {
        holder.commentText.setText(comment.getText());
        holder.userName.setText(comment.getUserName());
        holder.timestamp.setText(comment.getTimestamp());

        // Show delete button only for the comment owner or admin
        String currentUserId = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (currentUserId != null && (currentUserId.equals(comment.getUserId()) || isAdmin(currentUserId))) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
                db.collection("comments").document(snapshot.getId()).delete();
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    private boolean isAdmin(String userId) {
        // Implement admin check logic here
        // This could be a Firestore query or a local check
        return false; // Default to false, implement your admin check logic
    }

    // ViewHolder class to represent each comment item
    static class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView commentText;
        TextView userName;
        TextView timestamp;
        ImageButton deleteButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize the views
            commentText = itemView.findViewById(R.id.commentText);
            userName = itemView.findViewById(R.id.userName);
            timestamp = itemView.findViewById(R.id.timestamp);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
