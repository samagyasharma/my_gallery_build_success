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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        selectedPaintings = getIntent().getParcelableArrayListExtra("selectedPaintings");

        // Initialize views
        selectedPaintingsGrid = findViewById(R.id.selectedPaintingsGrid);
        totalPriceText = findViewById(R.id.totalPriceText);
        Button confirmButton = findViewById(R.id.confirmButton);
        nameInput = findViewById(R.id.fullName);
        emailInput = findViewById(R.id.email);
        addressInput = findViewById(R.id.address);

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
        selectedPaintingsAdapter = new SelectedPaintingsAdapter(selectedPaintings);
        selectedPaintingsGrid.setAdapter(selectedPaintingsAdapter);

        // Calculate and display total price
        int total = 0;
        for (Painting painting : selectedPaintings) {
            total += painting.getPrice();
        }
        totalPriceText.setText("Total: Rs " + total);

        confirmButton.setOnClickListener(v -> {
            String userName = nameInput.getText().toString();
            String userPhone = emailInput.getText().toString();
            String deliveryAddress = addressInput.getText().toString();

            if (userName.isEmpty() || userPhone.isEmpty() || deliveryAddress.isEmpty()) {
                Toast.makeText(this, "Please fill in all details", Toast.LENGTH_SHORT).show();
                return;
            }

            // Collect selected paintings from the list
            StringBuilder paintingsList = new StringBuilder();
            for (Painting painting : selectedPaintings) {
                paintingsList.append(painting.getName()).append(", ");
            }

            // Remove last comma if list is not empty
            if (paintingsList.length() > 0) {
                paintingsList.setLength(paintingsList.length() - 2);
            }

            // Send SMS with full details
            SendSMSAPI.sendSMS("+919667965550", userName, userPhone, deliveryAddress, paintingsList.toString());

            // Remove selected paintings from ToteBag
            removeSelectedPaintingsFromToteBag();

            // Clear selected paintings after confirmation
            selectedPaintings.clear();

            // Show confirmation message
            Toast.makeText(this, "Order Confirmed!", Toast.LENGTH_LONG).show();

            // Navigate back to the main screen
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
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
}
