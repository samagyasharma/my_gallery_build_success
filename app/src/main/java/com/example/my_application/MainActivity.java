package com.example.my_application;

import android.content.Intent;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.Toast;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    GridView paintingsGridView;
    LinearLayout bottomNavigation;
    Button toteBagButton, aboutMeButton, contactButton;

    // Variables to track scrolling behavior
    private int lastScrollY = 0; // Tracks the last Y scroll position
    private boolean isBottomNavigationVisible = true; // Tracks if the bottom navigation is visible

    // Example paintings for the GridView
    int[] paintingImages = {
            R.drawable.my_paint,
            R.drawable.my_paint2,
            R.drawable.my_paint3,
            R.drawable.my_paint4,
            R.drawable.my_paint5,
            R.drawable.my_paint6,
            R.drawable.my_paint7,
            R.drawable.my_paint8,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        paintingsGridView = findViewById(R.id.paintingsGridView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        toteBagButton = findViewById(R.id.toteBagButton);
        aboutMeButton = findViewById(R.id.aboutMeButton);
        contactButton = findViewById(R.id.contactButton);

        // Set Adapter for GridView
        PaintingsAdapter adapter = new PaintingsAdapter(this, paintingImages);
        paintingsGridView.setAdapter(adapter);

        // Handle Grid Item Clicks
        paintingsGridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent detailIntent = new Intent(MainActivity.this, PaintingDetailActivity.class);
            detailIntent.putExtra("paintingResId", paintingImages[position]);
            detailIntent.putExtra("paintingName", "Painting " + (position + 1));
            startActivity(detailIntent);
        });

        // Handle Bottom Navigation Button Clicks
        toteBagButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Tote Bag Button Clicked!", Toast.LENGTH_SHORT).show();
            Intent toteBagIntent = new Intent(MainActivity.this, ToteBagActivity.class);
            startActivity(toteBagIntent);
        });
        aboutMeButton.setOnClickListener(v -> {
            // Show Toast message
            Toast.makeText(MainActivity.this, "About Me", Toast.LENGTH_SHORT).show();

            // Navigate to NewActivity
            Intent intent = new Intent(MainActivity.this, AboutMe.class);
            startActivity(intent);
        });

        contactButton.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "Contact Me", Toast.LENGTH_SHORT).show();
            Intent contactIntent = new Intent(MainActivity.this, ContactMe.class);
            startActivity(contactIntent);
        });

        // Add Scroll Listener to GridView
        paintingsGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    showBottomNavigation();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int currentScrollY = absListView.getChildAt(0) == null
                        ? 0
                        : absListView.getChildAt(0).getTop();

                if (currentScrollY > lastScrollY) {
                    showBottomNavigation();
                } else if (currentScrollY < lastScrollY) {
                    hideBottomNavigation();
                }

                lastScrollY = currentScrollY;
            }
        });

        // Handle back press using OnBackPressedDispatcher
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Move the task to the background instead of exiting the app
                moveTaskToBack(true);
            }
        });
    }

    private void hideBottomNavigation() {
        if (isBottomNavigationVisible) {
            bottomNavigation.animate().translationY(bottomNavigation.getHeight()).setDuration(300).start();
            isBottomNavigationVisible = false;
        }
    }

    private void showBottomNavigation() {
        if (!isBottomNavigationVisible) {
            bottomNavigation.animate().translationY(0).setDuration(300).start();
            isBottomNavigationVisible = true;
        }
    }
}