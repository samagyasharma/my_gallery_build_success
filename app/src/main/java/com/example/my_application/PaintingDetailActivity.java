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
    private int paintingResId;
    private String paintingImage;
    private String paintingDesc;
    private String artistName;
    private String paintingPrice;

    ImageView paintingImageView;
    TextView paintingTitle, paintingDescription, artistNameText, paintingPriceText;
    ImageButton heartButton;
    Button addToToteBagButton, commentSubmitButton;
    EditText commentEditText;
    ImageView zoomIcon;

    private FirebaseAuth mAuth;
    private String currentPaintingId;
    private String currentUserName;

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
        paintingName = getIntent().getStringExtra("paintingName");
        paintingResId = getIntent().getIntExtra("paintingResId", 0);
        paintingImage = getIntent().getStringExtra("paintingImage");
        paintingDesc = getIntent().getStringExtra("paintingDescription");
        artistName = getIntent().getStringExtra("paintingArtist");
        paintingPrice = getIntent().getStringExtra("paintingPrice");
        currentPaintingId = getIntent().getStringExtra("paintingId");
        
        // Get the current user's name from the comment dialog
        currentUserName = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getString("lastUsedName", null);

        // Initialize views and setup
        initializeViews();
        setupPaintingDetails(paintingName, paintingDesc, artistName, paintingPrice);
        setupCommentsRecyclerView();
        setupCommentInput();
    }

    private void initializeViews() {
        // Initialize Firestore references
        commentsRef = db.collection("paintings")
                .document(currentPaintingId)
                .collection("comments");

        // Display painting details
        if (paintingImage != null && !paintingImage.isEmpty()) {
            Glide.with(this).load(paintingImage).into(paintingImageView);
            paintingImageView.post(() -> adjustImageViewSize(paintingImageView));
        } else if (paintingResId != 0) {
            paintingImageView.setImageResource(paintingResId);
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
            parsePrice(paintingPrice)
        );
        
        // Update heart button initial state
        updateHeartButtonState(currentPainting);

        // Set up click listeners and other functionality
        setupHeartButton();
        setupAddToToteBagButton();
        setupImageClickListeners();
    }

    private int parsePrice(String paintingPrice) {
        if (paintingPrice != null) {
            try {
                String numericPrice = paintingPrice.replaceAll("[^0-9]", "");
                if (!numericPrice.isEmpty()) {
                    return Integer.parseInt(numericPrice);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing price: " + paintingPrice, e);
            }
        }
        return 0;
    }

    private void setupCommentsRecyclerView() {
        Log.d(TAG, "Setting up comments recycler view for painting: " + currentPaintingId);
        
        // Set up the layout manager
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        
        // Query comments for this painting
        Query query = db.collection("paintings")
                .document(currentPaintingId)
                .collection("comments")
                .orderBy("timestamp", Query.Direction.DESCENDING);

        Log.d(TAG, "Firestore query path: paintings/" + currentPaintingId + "/comments");

        FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>()
                .setQuery(query, Comment.class)
                .build();

        commentsAdapter = new CommentsAdapter(options, this, currentUserName);
        commentsRecyclerView.setAdapter(commentsAdapter);
        commentsAdapter.startListening();
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

    private void setupCommentInput() {
        commentSubmitButton.setOnClickListener(v -> {
            String commentText = commentEditText.getText().toString().trim();
            if (!commentText.isEmpty()) {
                // Show dialog to get user's name
                showNameInputDialog(commentText);
            } else {
                Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNameInputDialog(String commentText) {
        // Create dialog
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_name_input);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        // Initialize views
        EditText nameInput = dialog.findViewById(R.id.nameInput);
        Button submitButton = dialog.findViewById(R.id.submitButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);

        // Handle submit button click
        submitButton.setOnClickListener(v -> {
            String userName = nameInput.getText().toString().trim();
            if (!userName.isEmpty()) {
                submitComment(commentText, userName);
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle cancel button click
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        // Show dialog
        dialog.show();
    }

    private void submitComment(String commentText, String userName) {
        // Save the user's name for future use
        getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .edit()
                .putString("lastUsedName", userName)
                .apply();
        
        // Update current user name
        currentUserName = userName;
        
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());

        Comment comment = new Comment(
                commentText,
                "anonymous", // Since we don't have user authentication, using "anonymous" as userId
                userName,
                timestamp,
                currentPaintingId
        );

        commentsRef.add(comment)
                .addOnSuccessListener(documentReference -> {
                    commentEditText.setText("");
                    Toast.makeText(this, "Comment posted successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to post comment", Toast.LENGTH_SHORT).show();
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

    private void setupHeartButton() {
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
    }

    private void setupAddToToteBagButton() {
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
    }

    private void setupImageClickListeners() {
        paintingImageView.setOnClickListener(v -> {
            Intent intent = new Intent(this, ZoomedPaintingActivity.class);
            intent.putExtra("paintingResId", paintingResId);
            intent.putExtra("paintingImage", paintingImage);
            startActivity(intent);
        });

        zoomIcon.setOnClickListener(v -> {
            Intent intent = new Intent(this, ZoomedPaintingActivity.class);
            intent.putExtra("paintingResId", paintingResId);
            intent.putExtra("paintingImage", paintingImage);
            startActivity(intent);
        });
    }

    private void setupPaintingDetails(String name, String description, String artist, String price) {
        // Set painting title
        paintingTitle.setText(name);
        
        // Set painting description
        paintingDescription.setText(description);
        
        // Set artist name
        artistNameText.setText("Artist: " + artist);
        
        // Set painting price
        paintingPriceText.setText("Price: " + price);
        
        // Set painting image from resource ID
        if (paintingResId != 0) {
            paintingImageView.setImageResource(paintingResId);
        }
    }
}
