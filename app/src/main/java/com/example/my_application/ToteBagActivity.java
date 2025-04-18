package com.example.my_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.my_application.Painting;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ToteBagActivity extends AppCompatActivity implements ToteBagAdapter.ToteBagListener {

    private RecyclerView toteBagRecyclerView;
    private ToteBagAdapter toteBagAdapter;
    private TextView totalText;
    private Button buyNowButton;
    private List<Painting> paintings;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ToteBagPrefs";
    private static final String KEY_TOTE_BAG = "ToteBagItems";
    private TextView emptyStateText;
    private static final String TAG = "ToteBagActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tote_bag);

        // Initialize views
        toteBagRecyclerView = findViewById(R.id.toteBagRecyclerView);
        totalText = findViewById(R.id.totalText);
        buyNowButton = findViewById(R.id.buyNowButton);
        emptyStateText = findViewById(R.id.emptyStateText);

        // Set up RecyclerView with LayoutManager
        toteBagRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        toteBagRecyclerView.setHasFixedSize(true);

        // Get paintings from ToteBag
        paintings = ToteBag.getInstance(this).getSelectedPaintings();
        Log.d(TAG, "Number of paintings in tote bag: " + paintings.size());
        for (Painting p : paintings) {
            Log.d(TAG, "Painting in tote bag: " + p.getTitle() + ", Price: " + p.getPrice());
        }

        // Initialize total as 0
        totalText.setText("Your total: Rs 0");

        // Set up adapter
        toteBagAdapter = new ToteBagAdapter(paintings, this);
        toteBagRecyclerView.setAdapter(toteBagAdapter);

        // Show/hide empty state
        updateEmptyState();

        // Set up Buy Now button
        buyNowButton.setOnClickListener(v -> {
            ArrayList<Painting> selectedPaintings = new ArrayList<>();
            List<Painting> toteBagPaintings = ToteBag.getInstance(this).getSelectedPaintings();
            
            for (Painting painting : toteBagPaintings) {
                if (painting.isSelected()) {
                    selectedPaintings.add(painting);
                }
            }
            
            if (!selectedPaintings.isEmpty()) {
                Intent intent = new Intent(ToteBagActivity.this, OrderConfirmationActivity.class);
                intent.putParcelableArrayListExtra("selectedPaintings", selectedPaintings);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Please select items to purchase", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (paintings == null || paintings.isEmpty()) {
            Log.d(TAG, "Your bag is empty");
            emptyStateText.setVisibility(View.VISIBLE);
            toteBagRecyclerView.setVisibility(View.GONE);
            buyNowButton.setEnabled(false);
        } else {
            Log.d(TAG, "Tote bag has " + paintings.size() + " items");
            emptyStateText.setVisibility(View.GONE);
            toteBagRecyclerView.setVisibility(View.VISIBLE);
            buyNowButton.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list when activity resumes
        paintings = ToteBag.getInstance(this).getSelectedPaintings();
        if (toteBagAdapter != null) {
            toteBagAdapter.updatePaintings(paintings);
            toteBagAdapter.notifyDataSetChanged();
        }
        updateEmptyState();
        // Reset total to 0 and recalculate based on selected items
        totalText.setText("Your total: Rs 0");
        onCheckboxChanged();
    }

    @Override
    public void onPaintingRemoved(int position) {
        if (paintings != null && position >= 0 && position < paintings.size()) {
            Painting painting = paintings.get(position);
            if (painting != null) {
                ToteBag.getInstance(this).removePainting(painting);
                paintings = ToteBag.getInstance(this).getSelectedPaintings();
                toteBagAdapter.updatePaintings(paintings);
                toteBagAdapter.notifyDataSetChanged();
                updateEmptyState();
                onCheckboxChanged();  // Replace updateTotalPrice() with onCheckboxChanged()
            }
        }
    }

    @Override
    public void onCheckboxChanged() {
        if (totalText == null || paintings == null) {
            return;
        }
        
        int total = 0;
        for (int i = 0; i < paintings.size(); i++) {
            RecyclerView.ViewHolder viewHolder = 
                toteBagRecyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                CheckBox checkbox = viewHolder.itemView.findViewById(R.id.paintingCheckbox);
                if (checkbox != null && checkbox.isChecked()) {
                    Painting painting = paintings.get(i);
                    String price = painting.getPrice();
                    if (price != null) {
                        try {
                            total += Integer.parseInt(price);
                        } catch (NumberFormatException e) {
                            Log.e(TAG, "Error parsing price for painting: " + painting.getTitle(), e);
                        }
                    }
                }
            }
        }
        totalText.setText("Your total: Rs " + total);
    }

    // Remove or modify updateTotalPrice() as it's no longer needed
    // since we're using onCheckboxChanged() for all total updates

    private int calculateSelectedTotal() {
        int total = 0;
        if (paintings == null) {
            return total;
        }
        
        for (Painting painting : paintings) {
            String price = painting.getPrice();
            if (price != null) {
                try {
                    // Price is already numeric, no need to remove "Rs"
                    total += Integer.parseInt(price);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error parsing price for painting: " + painting.getTitle(), e);
                }
            } else {
                Log.w(TAG, "Null price found for painting: " + painting.getTitle());
            }
        }
        return total;
    }

    private void saveToteBagItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(paintings);
        editor.putString(KEY_TOTE_BAG, json);
        editor.apply();
    }

    private ArrayList<Painting> loadToteBagItems() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_TOTE_BAG, "");
        Type type = new TypeToken<ArrayList<Painting>>() {}.getType();
        ArrayList<Painting> items = gson.fromJson(json, type);
        return items != null ? items : new ArrayList<>();
    }
}
