package com.example.my_application;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    ImageButton shareButton;
    Button addToToteBagButton, commentSubmitButton;
    EditText commentEditText;
    ImageView zoomIcon;

    private FirebaseAuth mAuth;
    private String currentPaintingId;
    private String currentUserName;
    private boolean isFavorite = false;
    private String currentPaintingName;
    private int currentPaintingResourceId;
    private String currentPaintingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_painting_detail);

        // Initialize Firebase only if needed
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        initializeViews();
        
        // Load the painting details
        loadPaintingDetails();

        // Only setup comments if we have a paintingId
        String paintingId = getIntent().getStringExtra("paintingId");
        if (paintingId != null && commentsRecyclerView != null) {
            setupCommentsRecyclerView();
            setupCommentInput();
        }

        setupShareButton();
    }

    private void initializeViews() {
        paintingImageView = findViewById(R.id.paintingImageView);
        paintingTitle = findViewById(R.id.paintingTitle);
        paintingDescription = findViewById(R.id.paintingDescription);  // Make sure this ID matches your layout
        artistNameText = findViewById(R.id.artistName);
        paintingPriceText = findViewById(R.id.paintingPrice);
        heartButton = findViewById(R.id.heartButton);
        shareButton = findViewById(R.id.shareButton);
        addToToteBagButton = findViewById(R.id.addToToteBagButton);
        zoomIcon = findViewById(R.id.zoomIcon);
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
        if (commentsRecyclerView == null || currentPaintingId == null) {
            return;  // Exit if RecyclerView or paintingId is not available
        }

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

        // Get the last used name from SharedPreferences
        String lastUsedName = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                .getString("lastUsedName", null);
        
        // Set up adapter and attach to RecyclerView
        commentsAdapter = new CommentsAdapter(options, this, lastUsedName);
        commentsRecyclerView.setAdapter(commentsAdapter);
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
        if (heartButton != null) {
            boolean isInToteBag = ToteBag.getInstance(this).isPaintingInBag(painting);
            heartButton.setImageResource(isInToteBag ? R.drawable.ic_heart_red : R.drawable.ic_heart_white);
        }
    }

    private void setupHeartButton() {
        heartButton.setOnClickListener(v -> {
            ToteBag toteBag = ToteBag.getInstance(this);
            if (toteBag.isPaintingInBag(currentPainting)) {
                toteBag.removePainting(currentPainting);
                Toast.makeText(this, "Removed from Tote Bag", Toast.LENGTH_SHORT).show();
                heartButton.setImageResource(R.drawable.ic_heart_white);
            } else {
                toteBag.addPainting(currentPainting);
                Toast.makeText(this, "Added to Tote Bag", Toast.LENGTH_SHORT).show();
                heartButton.setImageResource(R.drawable.ic_heart_red);
            }
        });
    }

    private void setupAddToToteBagButton() {
        addToToteBagButton.setOnClickListener(v -> {
            ToteBag toteBag = ToteBag.getInstance(this);
            
            // Create a painting object with all necessary information
            Painting paintingToAdd;
            if (paintingResId != 0) {
                // For resource-based paintings
                paintingToAdd = new Painting(
                    paintingName,
                    artistName,
                    paintingPrice,
                    paintingResId
                );
            } else {
                // For URL-based paintings
                paintingToAdd = new Painting(
                    paintingName,
                    artistName,
                    paintingPrice,
                    paintingImage  // URL for featured paintings
                );
            }

            if (!toteBag.isPaintingInBag(paintingToAdd)) {
                toteBag.addPainting(paintingToAdd);
                Toast.makeText(this, "Added to Tote Bag", Toast.LENGTH_SHORT).show();
                updateHeartButtonState(paintingToAdd);
            } else {
                Toast.makeText(this, "Already in Tote Bag", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupImageClickListeners() {
        View.OnClickListener zoomClickListener = v -> {
            Intent intent = new Intent(PaintingDetailActivity.this, ZoomedPaintingActivity.class);
            intent.putExtra("paintingName", paintingName);
            
            // Pass both the resource ID and image URL
            intent.putExtra("paintingResId", paintingResId);
            intent.putExtra("paintingImage", paintingImage);
            
            startActivity(intent);
        };

        // Set up click listener for both the painting image and zoom icon
        paintingImageView.setOnClickListener(zoomClickListener);
        zoomIcon.setOnClickListener(zoomClickListener);
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

    private void setupShareButton() {
        shareButton.setOnClickListener(v -> {
            try {
                // Create sharing intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                
                // Add painting details to the share text
                String shareText = "Check out this beautiful painting!\n\n" +
                        "Title: " + paintingName + "\n" +
                        "Artist: " + artistNameText.getText().toString() + "\n" +
                        "Price: " + paintingPriceText.getText().toString() + "\n\n" +
                        "Shared from Art Gallery App";

                // For both URL and resource-based images, we'll create a bitmap to share
                paintingImageView.setDrawingCacheEnabled(true);
                Bitmap bitmap = paintingImageView.getDrawingCache();
                
                if (bitmap == null) {
                    bitmap = ((android.graphics.drawable.BitmapDrawable) paintingImageView.getDrawable()).getBitmap();
                }
                
                if (bitmap != null) {
                    // Create a unique filename
                    String filename = "shared_painting_" + System.currentTimeMillis() + ".jpg";
                    File file = new File(getExternalCacheDir(), filename);
                    
                    // Save the bitmap to a temporary file
                    FileOutputStream fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                    fOut.flush();
                    fOut.close();
                    
                    // Get the URI using FileProvider
                    Uri contentUri = FileProvider.getUriForFile(
                            this,
                            getPackageName() + ".provider",
                            file
                    );
                    
                    // Grant temporary read permission to the content URI
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    
                    // Add the file to the share intent
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    
                    // Start the share activity
                    startActivity(Intent.createChooser(shareIntent, "Share painting via"));
                } else {
                    Toast.makeText(this, "Could not get image to share", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error sharing image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                paintingImageView.setDrawingCacheEnabled(false);
            }
        });
    }

    private void loadPaintingDetails() {
        Intent intent = getIntent();
        if (intent != null) {
            // Get all painting details
            paintingName = intent.getStringExtra("painting_name");
            paintingResId = intent.getIntExtra("paintingResId", 0);
            paintingImage = intent.getStringExtra("painting_image");
            String artistName = intent.getStringExtra("artist_name");
            String paintingPrice = intent.getStringExtra("painting_price");
            String description = intent.getStringExtra("painting_description");

            // Set the views with null checks
            if (paintingTitle != null && paintingName != null) {
                paintingTitle.setText(paintingName);
                paintingTitle.setVisibility(View.VISIBLE);
            }
            
            if (artistNameText != null && artistName != null) {
                artistNameText.setText("Artist: " + artistName);
                artistNameText.setVisibility(View.VISIBLE);
            }
            
            if (paintingPriceText != null && paintingPrice != null) {
                paintingPriceText.setText("Price: " + paintingPrice);
                paintingPriceText.setVisibility(View.VISIBLE);
            }
            
            if (description != null && !description.isEmpty() && paintingDescription != null) {
                paintingDescription.setText(description);
                paintingDescription.setVisibility(View.VISIBLE);
            } else {
                paintingDescription.setVisibility(View.GONE);
            }

            // Load the image
            if (paintingImageView != null) {
                if (paintingImage != null && !paintingImage.isEmpty()) {
                    Glide.with(this)
                            .load(paintingImage)
                            .into(paintingImageView);
                } else if (paintingResId != 0) {
                    paintingImageView.setImageResource(paintingResId);
                }
            }

            // Create current painting object
            if (paintingResId != 0) {
                currentPainting = new Painting(
                    paintingName != null ? paintingName : "",
                    artistName != null ? artistName : "",
                    paintingPrice != null ? paintingPrice : "",
                    paintingResId
                );
            } else {
                currentPainting = new Painting(
                    paintingName != null ? paintingName : "",
                    artistName != null ? artistName : "",
                    paintingPrice != null ? paintingPrice : "",
                    paintingImage != null ? paintingImage : ""
                );
            }
            
            // Set the description
            if (description != null) {
                currentPainting.setDescription(description);
            }

            // Update heart button state before setting up click listeners
            updateHeartButtonState(currentPainting);
            
            setupHeartButton();
            setupAddToToteBagButton();
            setupImageClickListeners();
        }
    }
}
