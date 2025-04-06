package com.example.my_application;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.github.chrisbanes.photoview.OnScaleChangedListener;

public class ZoomedPaintingActivity extends AppCompatActivity {

    private PhotoView zoomableImageView;
    private TextView zoomHintText;
    private TextView titleText;
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
        titleText = findViewById(R.id.titleText);

        // Get painting details from intent
        String paintingName = getIntent().getStringExtra("paintingName");
        int paintingResId = getIntent().getIntExtra("paintingResId", 0);
        String paintingImage = getIntent().getStringExtra("paintingImage");

        // Set the title
        if (paintingName != null) {
            titleText.setText(paintingName);
        }

        // Load the image - prioritize URL-based image if available
        if (paintingImage != null && !paintingImage.isEmpty()) {
            Glide.with(this)
                .load(paintingImage)
                .error(paintingResId != 0 ? paintingResId : R.drawable.placeholder_image)
                .into(zoomableImageView);
        } else if (paintingResId != 0) {
            zoomableImageView.setImageResource(paintingResId);
        }

        setupZoomListeners();
    }

    private void setupZoomListeners() {
        // Set up double tap to exit
        zoomableImageView.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTapTime < DOUBLE_TAP_TIMEOUT) {
                finish();
            }
            lastTapTime = currentTime;
        });

        // Set up scale change listener to hide/show hint and title
        zoomableImageView.setOnScaleChangeListener((scaleFactor, focusX, focusY) -> {
            if (scaleFactor > 1.0f && isHintVisible) {
                fadeOutHint();
                titleText.setVisibility(View.GONE);
            } else if (scaleFactor <= 1.0f && !isHintVisible) {
                fadeInHint();
                titleText.setVisibility(View.VISIBLE);
            }
        });
    }

    private void fadeOutHint() {
        if (zoomHintText.getVisibility() == View.VISIBLE) {
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(500);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    zoomHintText.setVisibility(View.GONE);
                    isHintVisible = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            zoomHintText.startAnimation(fadeOut);
        }
    }

    private void fadeInHint() {
        if (zoomHintText.getVisibility() == View.GONE) {
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(500);
            fadeIn.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    zoomHintText.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isHintVisible = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            zoomHintText.startAnimation(fadeIn);
        }
    }
}