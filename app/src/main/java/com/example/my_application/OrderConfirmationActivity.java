package com.example.my_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Add these Volley imports
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OrderConfirmationActivity extends AppCompatActivity {
    private ArrayList<Painting> selectedPaintings;
    private ArrayList<Painting> toteBagItems;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ToteBagPrefs";
    private static final String KEY_TOTE_BAG = "ToteBagItems";
    private EditText nameInput, emailInput, addressInput;
    private RecyclerView selectedPaintingsGrid;
    private SelectedPaintingsAdapter selectedPaintingsAdapter;
    private TextView totalPriceText;
    private TextView orderDetailsTextView;
    private Button confirmOrderButton;
    private Button cancelOrderButton;

    // Google Form submission URL
    private static final String GOOGLE_FORM_URL = "https://docs.google.com/forms/d/1NYlbDFmKLheLXltXnD7J5J3WIXBY7de7UD_yyrwPnfk/formResponse";
    
    // Form field entry ID
    private static final String ENTRY_ORDER_DETAILS = "entry.564278724";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        selectedPaintings = getIntent().getParcelableArrayListExtra("selectedPaintings", Painting.class);

        // Initialize views
        selectedPaintingsGrid = findViewById(R.id.selectedPaintingsGrid);
        totalPriceText = findViewById(R.id.totalPriceText);
        confirmOrderButton = findViewById(R.id.confirmOrderButton);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);
        nameInput = findViewById(R.id.fullName);
        emailInput = findViewById(R.id.email);
        addressInput = findViewById(R.id.address);
        orderDetailsTextView = findViewById(R.id.orderDetailsTextView);

        if (selectedPaintings == null || selectedPaintings.isEmpty()) {
            Toast.makeText(this, "No paintings selected!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up the grid layout
        int spanCount = 2; // 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        selectedPaintingsGrid.setLayoutManager(layoutManager);

        // Set up the adapter
        selectedPaintingsAdapter = new SelectedPaintingsAdapter(this, selectedPaintings);
        selectedPaintingsGrid.setAdapter(selectedPaintingsAdapter);

        // Calculate and display total price
        displayOrderDetails();

        // Set up button listeners
        setupButtons();
    }

    private void removeSelectedPaintingsFromToteBag() {
        toteBagItems = loadToteBagItems();

        // Use an iterator to safely remove items
        Iterator<Painting> iterator = toteBagItems.iterator();
        while (iterator.hasNext()) {
            Painting painting = iterator.next();
            if (selectedPaintings.contains(painting)) {
                iterator.remove();
            }
        }

        // Save the updated tote bag list back to SharedPreferences
        saveToteBagItems();
    }

    private void saveToteBagItems() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(toteBagItems);
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

    private void displayOrderDetails() {
        double total = 0.0;
        StringBuilder details = new StringBuilder();
        details.append("Order Details:\n\n");

        for (Painting painting : selectedPaintings) {
            details.append("- ").append(painting.getTitle())
                   .append(" by ").append(painting.getArtist())
                   .append(": ").append(painting.getPrice()).append("\n");

            // Parse price string to get numeric value
            String priceStr = painting.getPrice().replaceAll("[^0-9.]", "");
            try {
                total += Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        details.append("\nTotal: $").append(String.format("%.2f", total));
        orderDetailsTextView.setText(details.toString());
    }

    private void setupButtons() {
        confirmOrderButton.setOnClickListener(v -> {
            String userName = nameInput.getText().toString().trim();
            String userPhone = emailInput.getText().toString().trim();
            String deliveryAddress = addressInput.getText().toString().trim();

            if (userName.isEmpty() || userPhone.isEmpty() || deliveryAddress.isEmpty()) {
                Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
                return;
            }

            // Collect selected paintings from the list
            StringBuilder paintingsList = new StringBuilder();
            for (Painting painting : selectedPaintings) {
                paintingsList.append(painting.getTitle()).append(", ");
            }

            // Remove last comma if list is not empty
            if (paintingsList.length() > 0) {
                paintingsList.setLength(paintingsList.length() - 2);
            }

            // Create message body in the same format as SMS
            String messageBody = String.format(
                "Order Confirmation\n\n" +
                "Customer: %s\n" +
                "Phone: %s\n" +
                "Delivery Address: %s\n\n" +
                "Order Details:\n%s\n\n" +
                "Thank you for your order!",
                userName, userPhone, deliveryAddress, paintingsList.toString()
            );

            // Submit to Google Form
            submitToGoogleForm(messageBody);
        });

        cancelOrderButton.setOnClickListener(v -> finish());
    }

    private void submitToGoogleForm(String messageBody) {
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GOOGLE_FORM_URL,
                response -> {
                    // Success handling
                    Toast.makeText(this, "Order submitted successfully!", Toast.LENGTH_LONG).show();
                    
                    // Remove selected paintings from ToteBag
                    removeSelectedPaintingsFromToteBag();

                    // Clear selected paintings after confirmation
                    selectedPaintings.clear();

                    // Navigate back to the main screen
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    // Error handling
                    Toast.makeText(this, "Failed to submit order. Please try again.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(ENTRY_ORDER_DETAILS, messageBody);
                return params;
            }
        };

        queue.add(stringRequest);
    }
}
