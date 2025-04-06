package com.example.my_application;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.chrisbanes.photoview.PhotoView;

public class ZoomedPaintingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoomed_painting);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Have a Closer Look!");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Retrieve Painting Image
        PhotoView zoomableImageView = findViewById(R.id.zoomableImageView);
        Intent intent = getIntent();
        int paintingResId = intent.getIntExtra("paintingResId", 0);
        zoomableImageView.setImageResource(paintingResId);

        // Enable zoom with max scale of 3x
        zoomableImageView.setMaximumScale(3.0f);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
