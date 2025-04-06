package com.example.my_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.OnScaleChangedListener;

public class ZoomedPaintingActivity extends AppCompatActivity {

    private PhotoView zoomableImageView;
    private TextView zoomHintText;
    private boolean isHintVisible = true;
    private long lastTapTime = 0;
    private static final long DOUBLE_TAP_TIMEOUT = 300; // milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed_painting);

        // Initialize views
        zoomableImageView = findViewById(R.id.zoomableImageView);
        zoomHintText = findViewById(R.id.zoomHintText);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        // Retrieve Painting Image
        Intent intent = getIntent();
        int paintingResId = intent.getIntExtra("paintingResId", 0);
        String paintingImage = intent.getStringExtra("paintingImage");

        // Load the correct image (URL or resource ID)
        if (paintingImage != null && !paintingImage.isEmpty()) {
            Glide.with(this)
                .load(paintingImage)
                .into(zoomableImageView);
        } else if (paintingResId != 0) {
            zoomableImageView.setImageResource(paintingResId);
        }

        // Enable zoom with max scale of 3x
        zoomableImageView.setMaximumScale(3.0f);
        
        // Add scale change listener to hide/show hint
        zoomableImageView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {
            if (scaleFactor > 1.0f && isHintVisible) {
                fadeOutHint();
            } else if (scaleFactor <= 1.0f && !isHintVisible) {
                fadeInHint();
            }
        });

        // Handle single and double taps
        zoomableImageView.setOnViewTapListener((view, x, y) -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTapTime < DOUBLE_TAP_TIMEOUT) {
                // Double tap detected
                float currentScale = zoomableImageView.getScale();
                if (currentScale > 1.0f) {
                    zoomableImageView.setScale(1.0f, true);
                } else {
                    zoomableImageView.setScale(2.0f, true);
                }
            }
            lastTapTime = currentTime;
        });
    }

    private void fadeOutHint() {
        if (isHintVisible) {
            isHintVisible = false;
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(300);
            fadeOut.setFillAfter(true);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    zoomHintText.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            zoomHintText.startAnimation(fadeOut);
        }
    }

    private void fadeInHint() {
        if (!isHintVisible) {
            isHintVisible = true;
            zoomHintText.setVisibility(View.VISIBLE);
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(300);
            fadeIn.setFillAfter(true);
            zoomHintText.startAnimation(fadeIn);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
