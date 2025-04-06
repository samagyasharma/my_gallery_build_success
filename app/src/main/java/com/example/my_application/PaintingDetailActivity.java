package com.example.my_application;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.my_application.Comment;

public class PaintingDetailActivity extends AppCompatActivity {

    private static final String TAG = "PaintingDetailActivity";
    private FirebaseFirestore db;
    private CollectionReference commentsRef;
    private CommentsAdapter commentsAdapter;
    private RecyclerView commentsRecyclerView;
    private Painting currentPainting;

    private String paintingName;

    ImageView paintingImageView;
    TextView paintingTitle, paintingDescription, artistNameText, paintingPriceText;
    ImageButton heartButton;
    Button addToToteBagButton, commentSubmitButton;
    EditText commentEditText;
    ImageView zoomIcon;

    private FirebaseAuth mAuth;
    private String currentPaintingId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting_detail);

        // Initialize views
        paintingImageView = findViewById(R.id.paintingImageView);
        paintingTitle = findViewById(R.id.paintingTitle);
        paintingDescription = findViewById(R.id.paintingDescription);
        artistNameText = findViewById(R.id.artistName);
        paintingPriceText = findViewById(R.id.paintingPrice);
        heartButton = findViewById(R.id.heartButton);
        addToToteBagButton = findViewById(R.id.addToToteBagButton);
        commentEditText = findViewById(R.id.commentEditText);
        commentSubmitButton = findViewById(R.id.commentSubmitButton);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        zoomIcon = findViewById(R.id.zoomIcon);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Get painting details from intent
        int paintingResId = getIntent().getIntExtra("paintingResId", 0);
        String paintingImage = getIntent().getStringExtra("paintingImage");
        paintingName = getIntent().getStringExtra("paintingName");
        String paintingDesc = getIntent().getStringExtra("paintingDescription");
        String artistName = getIntent().getStringExtra("paintingArtist");
        String paintingPrice = getIntent().getStringExtra("paintingPrice");
        
        // Parse price
        int price = 0;
        if (paintingPrice != null) {
            try {
                // Remove any non-numeric characters and parse
                String numericPrice = paintingPrice.replaceAll("[^0-9]", "");
                if (!numericPrice.isEmpty()) {
                    price = Integer.parseInt(numericPrice);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing price: " + paintingPrice, e);
            }
        }

        // Display painting details
        if (paintingImage != null && !paintingImage.isEmpty()) {
            Glide.with(this).load(paintingImage).into(paintingImageView);
            // Set up aspect ratio after image is loaded
            paintingImageView.post(() -> adjustImageViewSize(paintingImageView));
        } else if (paintingResId != 0) {
            paintingImageView.setImageResource(paintingResId);
            // Set up aspect ratio after image is loaded
            paintingImageView.post(() -> adjustImageViewSize(paintingImageView));
        }
        
        paintingTitle.setText(paintingName);
        paintingDescription.setText(paintingDesc);
        artistNameText.setText("Artist: " + (artistName != null ? artistName : "Unknown"));
        paintingPriceText.setText("Price: " + paintingPrice);

        // Create the Painting object
        currentPainting = new Painting(
            paintingName,
            paintingResId,
            paintingImage != null ? paintingImage : "",
            paintingDesc,
            artistName,
            price
        );
        
        // Update heart button initial state
        updateHeartButtonState(currentPainting);

        // Handle heart button toggle
        heartButton.setOnClickListener(v -> {
            if (ToteBag.getInstance(this).isPaintingInBag(currentPainting)) {
                ToteBag.getInstance(this).removePainting(currentPainting);
                Toast.makeText(this, "Removed from Tote Bag", Toast.LENGTH_SHORT).show();
            } else {
                ToteBag.getInstance(this).addPainting(currentPainting);
                Toast.makeText(this, "Added to Tote Bag", Toast.LENGTH_SHORT).show();
            }
            updateHeartButtonState(currentPainting);
        });

        // Handle Add to Tote Bag button
        addToToteBagButton.setOnClickListener(v -> {
            ToteBag toteBag = ToteBag.getInstance(this);
            if (!toteBag.isPaintingInBag(currentPainting)) {
                toteBag.addPainting(currentPainting);
                Toast.makeText(this, "Added to Tote Bag", Toast.LENGTH_SHORT).show();
                updateHeartButtonState(currentPainting);
            } else {
                Toast.makeText(this, "Already in Tote Bag", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle painting image click to open zoomed view
        paintingImageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ZoomedPaintingActivity.class);
            intent.putExtra("paintingResId", paintingResId);
            intent.putExtra("paintingImage", paintingImage);
            startActivity(intent);
        });

        // Handle zoom icon click
        zoomIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, ZoomedPaintingActivity.class);
            intent.putExtra("paintingResId", paintingResId);
            intent.putExtra("paintingImage", paintingImage);
            startActivity(intent);
        });

        // Get painting ID from intent
        currentPaintingId = getIntent().getStringExtra("paintingId");
        if (currentPaintingId == null) {
            finish();
            return;
        }

        // Initialize Firestore
        commentsRef = db.collection("paintings").document(paintingName).collection("comments");

        // Set up RecyclerView
        setupCommentsRecyclerView();

        // Handle comment submission
        setupCommentInput();
    }

    /**
     * Adjusts the ImageView size to maintain the aspect ratio of the image
     * while setting the height to 40% of the screen height
     */
    private void adjustImageViewSize(ImageView imageView) {
        // Get the screen height
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        
        // Calculate 40% of screen height
        int targetHeight = (int) (screenHeight * 0.4);
        
        // Get the current drawable
        android.graphics.drawable.Drawable drawable = imageView.getDrawable();
        if (drawable == null) return;
        
        // Calculate aspect ratio
        float aspectRatio = (float) drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
        
        // Calculate width based on aspect ratio and target height
        int targetWidth = (int) (targetHeight * aspectRatio);
        
        // Get the screen width
        int screenWidth = displayMetrics.widthPixels;
        
        // If the calculated width is larger than the screen width, adjust
        if (targetWidth > screenWidth) {
            targetWidth = screenWidth;
            targetHeight = (int) (targetWidth / aspectRatio);
        }
        
        // Calculate horizontal margins to center the image
        int horizontalMargin = (screenWidth - targetWidth) / 2;
        
        // Set the layout parameters
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.height = targetHeight;
        params.width = targetWidth;
        
        // Apply the margins with reduced top margin
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
        marginParams.setMargins(horizontalMargin, 8, horizontalMargin, 0); // Reduced top margin to 8dp
        
        // Apply the parameters
        imageView.setLayoutParams(params);
    }

    private void setupCommentsRecyclerView() {
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Query comments for this painting
        Query query = db.collection("comments")
                .whereEqualTo("paintingId", currentPaintingId)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        commentsAdapter = new CommentsAdapter(options, this);
        commentsRecyclerView.setAdapter(commentsAdapter);
    }

    private void setupCommentInput() {
        commentSubmitButton.setOnClickListener(v -> {
            String commentText = commentEditText.getText().toString().trim();
            if (!commentText.isEmpty() && mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();
                String userName = mAuth.getCurrentUser().getDisplayName();
                if (userName == null || userName.isEmpty()) {
                    userName = "Anonymous";
                }

                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(new Date());

                Comment comment = new Comment(
                        commentText,
                        userId,
                        userName,
                        timestamp,
                        currentPaintingId
                );

                db.collection("comments")
                        .add(comment)
                        .addOnSuccessListener(documentReference -> {
                            commentEditText.setText("");
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Failed to post comment", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (commentsAdapter != null) {
            commentsAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (commentsAdapter != null) {
            commentsAdapter.stopListening();
        }
    }

    private void updateHeartButtonState(Painting painting) {
        if (ToteBag.getInstance(this).isPaintingInBag(painting)) {
            heartButton.setImageResource(R.drawable.ic_heart_red);
        } else {
            heartButton.setImageResource(R.drawable.ic_heart_white);
        }
    }
}
