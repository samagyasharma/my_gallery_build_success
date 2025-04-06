package com.example.my_application;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class FeaturedPaintingsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FeaturedPaintingsAdapter adapter;
    private List<Painting> paintings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_featured_paintings);

        recyclerView = findViewById(R.id.featuredPaintingsRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columns
        
        paintings = new ArrayList<>();
        adapter = new FeaturedPaintingsAdapter(this, paintings);
        recyclerView.setAdapter(adapter);

        loadPaintingsFromCSV();
    }

    private void loadPaintingsFromCSV() {
        // Update the URL to your actual CSV file URL
        String csvUrl = "https://raw.githubusercontent.com/samagyasharma/art_gallery_application/refs/heads/main/csv_file2.csv";
        
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, csvUrl,
                response -> {
                    try {
                        BufferedReader reader = new BufferedReader(new StringReader(response));
                        String line;
                        boolean isFirstLine = true;
                        
                        while ((line = reader.readLine()) != null) {
                            if (isFirstLine) {
                                isFirstLine = false;
                                continue; // Skip header row
                            }
                            
                            String[] data = line.split(",");
                            // Add logging to debug CSV parsing
                            Log.d("CSV_PARSING", "Line: " + line);
                            Log.d("CSV_PARSING", "Data length: " + data.length);
                            
                            if (data.length >= 5) {  // Minimum required columns
                                String title = data[0].trim();
                                String artist = data[1].trim();
                                String price = data[2].trim();
                                String imageUrl = data[4].trim();
                                String description = data.length >= 6 ? data[5].trim() : "";
                                
                                // Add logging for parsed data
                                Log.d("CSV_PARSING", "Title: " + title);
                                Log.d("CSV_PARSING", "ImageUrl: " + imageUrl);
                                
                                Painting painting = new Painting(title, artist, price, imageUrl);
                                painting.setDescription(description);
                                paintings.add(painting);
                            }
                        }
                        
                        Log.d("CSV_PARSING", "Total paintings loaded: " + paintings.size());
                        adapter.notifyDataSetChanged();
                        
                    } catch (Exception e) {
                        Log.e("CSV_PARSING", "Error parsing CSV: " + e.getMessage());
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading paintings: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("CSV_PARSING", "Network Error: " + error.getMessage());
                    Toast.makeText(this, "Error: " + error.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                });

        queue.add(stringRequest);
    }
} 
