package com.example.my_application;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
        
        // Check if we're being restored from recent apps
        if (savedInstanceState != null) {
            // Redirect to MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                          Intent.FLAG_ACTIVITY_NEW_TASK | 
                          Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

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

    private void displayOrderDetails() {
        double total = 0.0;
        StringBuilder details = new StringBuilder();
        details.append("Order Details:\n\n");

        for (Painting painting : selectedPaintings) {
            String artistName = painting.getArtist() != null && !painting.getArtist().isEmpty() 
                              ? painting.getArtist() 
                              : "Unknown Artist";
                              
            details.append("- ").append(painting.getTitle())
                   .append(" by ").append(artistName)
                   .append(": Rs ").append(painting.getPrice()).append("\n");

            // Parse price string to get numeric value
            String priceStr = painting.getPrice().replaceAll("[^0-9.]", "");
            try {
                total += Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        details.append("\nTotal: Rs ").append(String.format("%.2f", total));
        orderDetailsTextView.setText(details.toString());
        totalPriceText.setText("Total: Rs " + String.format("%.2f", total));
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
                    Toast.makeText(this, "Order submitted successfully!", Toast.LENGTH_LONG).show();
                    
                    // Remove selected paintings from ToteBag
                    ToteBag toteBag = ToteBag.getInstance(this);
                    for (Painting painting : selectedPaintings) {
                        Log.d("OrderConfirmation", "Attempting to remove painting: " + painting.getTitle());
                        boolean removed = toteBag.removePainting(painting);
                        Log.d("OrderConfirmation", "Removal success: " + removed);
                    }

                    List<Painting> remainingPaintings = toteBag.getSelectedPaintings();
                    Log.d("OrderConfirmation", "Remaining paintings in tote bag: " + remainingPaintings.size());

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Failed to submit order. Please try again.", Toast.LENGTH_LONG).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                StringBuilder orderDetails = new StringBuilder();
                orderDetails.append("Order Details:\n\n");
                double total = 0.0;

                for (Painting painting : selectedPaintings) {
                    String price = painting.getPrice();
                    orderDetails.append("- ").append(painting.getTitle())
                               .append(" by ").append(painting.getArtist())
                               .append(": Rs ").append(price).append("\n");

                    try {
                        String priceStr = price.replaceAll("[^0-9.]", "");
                        total += Double.parseDouble(priceStr);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }

                orderDetails.append("\nTotal Amount: Rs ").append(String.format("%.2f", total));

                String finalMessage = String.format(
                    "Order Confirmation\n\n" +
                    "Customer: %s\n" +
                    "Email: %s\n" +
                    "Delivery Address: %s\n\n" +
                    "%s\n\n" +
                    "Thank you for your order!",
                    nameInput.getText().toString().trim(),
                    emailInput.getText().toString().trim(),
                    addressInput.getText().toString().trim(),
                    orderDetails.toString()
                );

                params.put(ENTRY_ORDER_DETAILS, finalMessage);
                return params;
            }
        };

        queue.add(stringRequest);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Handle new intents by redirecting to MainActivity
        Intent mainIntent = new Intent(this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | 
                          Intent.FLAG_ACTIVITY_NEW_TASK | 
                          Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Clear any stored state
        if (isFinishing()) {
            clearReferences();
        }
    }

    private void clearReferences() {
        if (selectedPaintings != null) {
            selectedPaintings.clear();
        }
        if (toteBagItems != null) {
            toteBagItems.clear();
        }
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }
}
