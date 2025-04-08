package com.example.my_application;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    GridView paintingsGridView;
    LinearLayout bottomNavigation;
    LinearLayout toteBagLayout, aboutMeLayout, contactLayout;
    TextView toteBagButton, aboutMeButton, contactButton;
    HorizontalScrollView featuredCarousel;
    LinearLayout carouselContainer;

    // Variables to track scrolling behavior
    private int lastScrollY = 0;
    private boolean isBottomNavigationVisible = true;

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

    String[] paintingNames = {
            "Sunset Bliss",
            "Ocean Waves",
            "Mystic Forest",
            "Golden Horizon",
            "Abstract Dreams",
            "Silent Night",
            "Serene Beauty",
            "Colorful Chaos"
    };

    String[] paintingDescriptions = {
            "A beautiful sunset over the ocean",
            "Waves crashing on the shore",
            "A mystical forest at dawn",
            "Golden light on the horizon",
            "Abstract patterns and colors",
            "A peaceful night scene",
            "Serene landscape with mountains",
            "Colorful abstract composition"
    };

    String[] paintingPrices = {
            "1000",
            "1000",
            "1000",
            "1000",
            "1000",
            "1000",
            "1000",
            "1000"
    };

    String[] paintingArtists = {
            "Samagya Sharma",
            "Samagya Sharma",
            "Samagya Sharma",
            "Samagya Sharma",
            "Samagya Sharma",
            "Samagya Sharma",
            "Samagya Sharma",
            "Samagya Sharma"
    };

    private static final String CSV_URL_1 = "https://raw.githubusercontent.com/samagyasharma/art_gallery_application/refs/heads/main/paintings.csv";
    private static final String CSV_URL_2 = "https://raw.githubusercontent.com/samagyasharma/art_gallery_application/refs/heads/main/featured_artist.csv";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        paintingsGridView = findViewById(R.id.paintingsGridView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        toteBagLayout = findViewById(R.id.toteBagLayout);
        aboutMeLayout = findViewById(R.id.aboutMeLayout);
        contactLayout = findViewById(R.id.contactLayout);
        toteBagButton = findViewById(R.id.toteBagButton);
        aboutMeButton = findViewById(R.id.aboutMeButton);
        contactButton = findViewById(R.id.contactButton);
        featuredCarousel = findViewById(R.id.featuredCarousel);
        carouselContainer = findViewById(R.id.carouselContainer);

        // Set click listeners for carousel items
        setupCarousel();

        // Get screen width and calculate item size based on orientation
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int itemWidth;
        
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // In portrait mode, use 90% of screen width
            itemWidth = (int) (screenWidth * 0.9);
        } else {
            // In landscape mode, use 45% of screen width
            itemWidth = (int) (screenWidth * 0.45);
        }

        // Set Adapter for GridView with custom item size
        PaintingsAdapter adapter = new PaintingsAdapter(this, paintingImages, paintingNames, itemWidth);
        paintingsGridView.setAdapter(adapter);

        // Handle Grid Item Clicks
        paintingsGridView.setOnItemClickListener((parent, view, position, id) -> {
            Intent detailIntent = new Intent(MainActivity.this, PaintingDetailActivity.class);
            detailIntent.putExtra("paintingResId", paintingImages[position]);
            detailIntent.putExtra("painting_name", paintingNames[position]);
            detailIntent.putExtra("painting_description", paintingDescriptions[position]);  // Changed key
            detailIntent.putExtra("painting_price", paintingPrices[position]);
            detailIntent.putExtra("artist_name", paintingArtists[position]);
            detailIntent.putExtra("paintingId", "painting_" + position);
            startActivity(detailIntent);
        });

        // Handle Bottom Navigation Button Clicks
        toteBagLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ToteBagActivity.class));
        });

        aboutMeLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AboutMe.class));
        });

        contactLayout.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ContactMe.class));
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
                if (absListView.getChildAt(0) == null) return;

                int currentScrollY = absListView.getChildAt(0).getTop();
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
                moveTaskToBack(true);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        
        // Recalculate item width based on new orientation
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int itemWidth;
        
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            itemWidth = (int) (screenWidth * 0.9);
        } else {
            itemWidth = (int) (screenWidth * 0.45);
        }
        
        // Update adapter with new item width
        PaintingsAdapter adapter = new PaintingsAdapter(this, paintingImages, paintingNames, itemWidth);
        paintingsGridView.setAdapter(adapter);
    }

    private void setupCarousel() {
        ImageView image1 = findViewById(R.id.image1);
        ImageView image2 = findViewById(R.id.image2);
        ImageView image3 = findViewById(R.id.image3);

        // Set click listener for Featured Paintings (first carousel icon)
        image1.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FeaturedPaintingsActivity.class);
            startActivity(intent);
        });

        // Updated click listener for Featured Artist (second carousel icon)
        image2.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FeaturedPaintingsActivity.class);
            intent.putExtra("isArtistFeature", true);
            intent.putExtra("headingText", "Featured Artist");
            intent.putExtra("csvUrl", "https://raw.githubusercontent.com/samagyasharma/art_gallery_application/refs/heads/main/csv_file_artist.csv");
            startActivity(intent);
        });

        image3.setOnClickListener(v -> openGoogleForm());
    }

    private static final String GOOGLE_FORM_URL = "https://docs.google.com/forms/d/e/1FAIpQLSchaG57nroCPadIP4ej9d2XnOx40C3-yTJPZ6t1stHKNX2xug/viewform?usp=dialog";

    private void openGoogleForm() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_FORM_URL));
        startActivity(intent);
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

    // Custom Adapter for GridView
    private class PaintingsAdapter extends BaseAdapter {
        private MainActivity context;
        private int[] images;
        private String[] names;
        private int itemWidth;

        public PaintingsAdapter(MainActivity context, int[] images, String[] names, int itemWidth) {
            this.context = context;
            this.images = images;
            this.names = names;
            this.itemWidth = itemWidth;
        }

        @Override
        public int getCount() {
            return images.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.item_painting, parent, false);
            }

            ImageView paintingImage = view.findViewById(R.id.paintingImage);
            TextView paintingName = view.findViewById(R.id.paintingName);

            // Get the original image dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), images[position], options);
            
            // Calculate aspect ratio
            float aspectRatio = (float) options.outWidth / options.outHeight;
            
            // Determine if image is landscape or portrait
            boolean isLandscape = aspectRatio > 1.0f;
            
            // Calculate width based on orientation
            int targetWidth = isLandscape ? 
                (int) (getResources().getDisplayMetrics().widthPixels * 0.7f) : 
                (int) (getResources().getDisplayMetrics().widthPixels * 0.6f);
            
            // Calculate height to maintain aspect ratio
            int targetHeight = (int) (targetWidth / aspectRatio);
            
            // Set the image view dimensions
            ViewGroup.LayoutParams params = paintingImage.getLayoutParams();
            params.width = targetWidth;
            params.height = targetHeight;
            paintingImage.setLayoutParams(params);

            // Load and set the image
            paintingImage.setImageResource(images[position]);
            paintingName.setText(names[position]);

            return view;
        }
    }
}
