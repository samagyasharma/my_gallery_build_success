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
                            if (data.length >= 5) {
                                String title = data[0].trim();
                                String artist = data[1].trim();
                                String price = data[2].trim();
                                String medium = data[3].trim();
                                String imageUrl = data[4].trim();
                                
                                paintings.add(new Painting(title, artist, price, medium, imageUrl));
                            }
                        }
                        
                        adapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading paintings: " + e.getMessage(), 
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error: " + error.getMessage(), 
                        Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }
} 
