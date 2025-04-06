package com.example.my_application;

import static com.google.android.material.internal.ViewUtils.hideKeyboard;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PaintingDetailActivity extends AppCompatActivity {

    ImageView paintingImageView;
    ImageButton heartButton;
    Button addToToteBagButton, commentSubmitButton;
    EditText commentEditText;
    RecyclerView commentsRecyclerView;

    // Comment list and adapter
    private final ArrayList<Comment> commentsList = new ArrayList<>();
    private CommentsAdapter commentsAdapter;

    // Singleton for Tote Bag
    public static class ToteBag {
        private static ToteBag instance;
        private final ArrayList<Painting> toteBagItems = new ArrayList<>();

        private ToteBag() {}

        public static ToteBag getInstance() {
            if (instance == null) {
                instance = new ToteBag();
            }
            return instance;
        }

        public void addPainting(Painting painting) {
            toteBagItems.add(painting);
        }

        public void removePainting(Painting painting) {
            toteBagItems.remove(painting);
        }

        public boolean isPaintingInBag(Painting painting) {
            return toteBagItems.contains(painting);
        }

        public ArrayList<Painting> getToteBagItems() {
            return toteBagItems;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting_detail);



        // Initialize views
        paintingImageView = findViewById(R.id.paintingImageView);
        heartButton = findViewById(R.id.heartButton);
        addToToteBagButton = findViewById(R.id.addToToteBagButton);
        commentEditText = findViewById(R.id.commentEditText);
        commentSubmitButton = findViewById(R.id.commentSubmitButton);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);


        // Get painting details from intent
        int paintingResId = getIntent().getIntExtra("paintingResId", 0);
        String paintingName = getIntent().getStringExtra("paintingName");
        Painting currentPainting = new Painting(paintingName, paintingResId);

        paintingImageView.setOnClickListener(v -> {
            Intent intent = new Intent(PaintingDetailActivity.this, ZoomedPaintingActivity.class);
            intent.putExtra("paintingResId", paintingResId);
            startActivity(intent);
        });

        // Display painting
        paintingImageView.setImageResource(paintingResId);

        // Update heart button state based on tote bag contents
        updateHeartButtonState(currentPainting);

        // Handle heart button toggle
        heartButton.setOnClickListener(v -> {
            if (ToteBag.getInstance().isPaintingInBag(currentPainting)) {
                // Remove from tote bag and reset heart icon
                ToteBag.getInstance().removePainting(currentPainting);
                Toast.makeText(this, "Removed from Tote Bag", Toast.LENGTH_SHORT).show();
            } else {
                // Add to tote bag and update heart icon
                ToteBag.getInstance().addPainting(currentPainting);
                Toast.makeText(this, "Added to Tote Bag", Toast.LENGTH_SHORT).show();
            }
            // Update heart button state
            updateHeartButtonState(currentPainting);
        });

        // Handle Add to Tote Bag button
        addToToteBagButton.setOnClickListener(v -> {
            ToteBag.getInstance().addPainting(currentPainting);
            Toast.makeText(this, "Thank you! Added to Tote Bag!", Toast.LENGTH_SHORT).show();
        });

        // Initialize comments RecyclerView
        commentsAdapter = new CommentsAdapter(commentsList);
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentsRecyclerView.setAdapter(commentsAdapter);

        // Handle comment submission
        commentSubmitButton.setOnClickListener(v -> submitComment());
    }

    /**
     * Updates the appearance of the heart button based on whether the painting
     * is in the tote bag.
     */
    private void updateHeartButtonState(Painting painting) {
        if (ToteBag.getInstance().isPaintingInBag(painting)) {
            heartButton.setImageResource(R.drawable.ic_heart_red); // Red fill with orange outline
            Toast.makeText(this, "Thank you for liking the painting! Added to Tote Bag!", Toast.LENGTH_SHORT).show();

        } else {
            heartButton.setImageResource(R.drawable.ic_heart_white); // White fill with orange outline
        }
    }

    private void submitComment() {
        String commentText = commentEditText.getText().toString().trim();

        if (!commentText.isEmpty()) {
            // Create custom dialog
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_name_input);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            // Set dialog width to 80% of the screen width
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.8);
            dialog.getWindow().setAttributes(layoutParams);

            dialog.setCancelable(false);

            EditText nameInput = dialog.findViewById(R.id.nameEditText);
            Button submitButton = dialog.findViewById(R.id.submitButton);
            Button cancelButton = dialog.findViewById(R.id.cancelButton);

            submitButton.setOnClickListener(v -> {
                String userName = nameInput.getText().toString().trim();
                if (userName.isEmpty()) {
                    userName = "Anonymous";
                }

                // Add the comment
                Comment newComment = new Comment(userName, commentText);
                commentsList.add(newComment);
                commentsAdapter.notifyItemInserted(commentsList.size() - 1);
                commentsRecyclerView.scrollToPosition(commentsList.size() - 1);

                // Clear input & dismiss dialog
                commentEditText.setText("");
                dialog.dismiss();
            });

            cancelButton.setOnClickListener(v -> dialog.dismiss());

            // Show dialog
            dialog.show();
        } else {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
        }
    }




    // Painting class for holding painting details
    public static class Painting {
        private final String name;
        private final int imageResId;

        public Painting(String name, int imageResId) {
            this.name = name;
            this.imageResId = imageResId;
        }

        public String getName() {
            return name;
        }

        public int getImageResId() {
            return imageResId;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            Painting painting = (Painting) obj;
            return imageResId == painting.imageResId && name.equals(painting.name);
        }

        @Override
        public int hashCode() {
            return name.hashCode() * 31 + imageResId;
        }
    }

    // Comment class
    public static class Comment {
        private final String userName;
        private final String commentText;

        public Comment(String userName, String commentText) {
            this.userName = userName;
            this.commentText = commentText;
        }

        public String getUserName() {
            return userName;
        }

        public String getCommentText() {
            return commentText;
        }
    }
}
