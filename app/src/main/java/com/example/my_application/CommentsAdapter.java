package com.example.my_application;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class CommentsAdapter extends FirestoreRecyclerAdapter<Comment, CommentsAdapter.CommentViewHolder> {

    private Context context;
    private FirebaseFirestore db;
    private String currentUserName;

    public CommentsAdapter(@NonNull FirestoreRecyclerOptions<Comment> options, Context context, String currentUserName) {
        super(options);
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.currentUserName = currentUserName;
    }

    public void setCurrentUserName(String userName) {
        this.currentUserName = userName;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment comment) {
        Log.d("CommentsAdapter", "Binding comment at position " + position + 
                ": text=" + comment.getText() + 
                ", user=" + comment.getUserName() + 
                ", timestamp=" + comment.getTimestamp() +
                ", currentUser=" + currentUserName);

        holder.commentText.setText(comment.getText());
        holder.userName.setText(comment.getUserName());
        holder.timestamp.setText(comment.getTimestamp());

        // Show delete button only if the current user is the comment owner
        if (currentUserName != null && currentUserName.equals(comment.getUserName())) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
                db.collection("paintings")
                        .document(comment.getPaintingId())
                        .collection("comments")
                        .document(snapshot.getId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(context, "Comment deleted", Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(context, "Failed to delete comment", Toast.LENGTH_SHORT).show();
                        });
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentText;
        TextView userName;
        TextView timestamp;
        ImageButton deleteButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.commentText);
            userName = itemView.findViewById(R.id.userName);
            timestamp = itemView.findViewById(R.id.timestamp);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
