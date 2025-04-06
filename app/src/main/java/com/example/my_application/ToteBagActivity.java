package com.example.my_application;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ToteBagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tote_bag);

        // Get the Tote Bag items
        ArrayList<PaintingDetailActivity.Painting> toteBagItems = PaintingDetailActivity.ToteBag.getInstance().getToteBagItems();

        // Get the layout to display paintings
        LinearLayout toteBagContainer = findViewById(R.id.toteBagContainer);

        // Handle case when tote bag is empty
        if (toteBagItems.isEmpty()) {
            TextView noItemsMessage = findViewById(R.id.noItemsMessage);
            noItemsMessage.setVisibility(TextView.VISIBLE); // Show "No items" message
            return; // Exit if no items to display
        }

        // Hide the "No items" message
        findViewById(R.id.noItemsMessage).setVisibility(TextView.GONE);

        // Dynamically add views for each painting in the tote bag
        for (PaintingDetailActivity.Painting painting : toteBagItems) {
            // Create a container for each painting
            LinearLayout paintingContainer = new LinearLayout(this);
            paintingContainer.setOrientation(LinearLayout.VERTICAL);
            paintingContainer.setPadding(16, 16, 16, 16);

            // Create a TextView for the painting name
            TextView paintingName = new TextView(this);
            paintingName.setText(painting.getName());
            paintingName.setTextSize(18);
            paintingName.setPadding(8, 8, 8, 8);

            // Create an ImageView for the painting preview
            ImageView paintingPreview = new ImageView(this);
            paintingPreview.setImageResource(painting.getImageResId());
            paintingPreview.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    500 // Fixed height for preview images
            ));
            paintingPreview.setPadding(8, 8, 8, 8);
            paintingPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);

            // Add the TextView and ImageView to the container
            paintingContainer.addView(paintingName);
            paintingContainer.addView(paintingPreview);

            // Add the container to the main layout
            toteBagContainer.addView(paintingContainer);
        }
    }
}
