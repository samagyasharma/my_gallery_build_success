package com.example.my_application;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ToteBag {
    private static final String TAG = "ToteBag";
    private static final String PREF_NAME = "ToteBagPrefs";
    private static final String KEY_SELECTED_PAINTINGS = "selectedPaintings";
    private static ToteBag instance;
    private final SharedPreferences preferences;
    private final List<Painting> selectedPaintings;

    private ToteBag(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        selectedPaintings = loadSelectedPaintings();
    }

    public static synchronized ToteBag getInstance(Context context) {
        if (instance == null) {
            instance = new ToteBag(context.getApplicationContext());
        }
        return instance;
    }

    public void addPainting(Painting painting) {
        if (!selectedPaintings.contains(painting)) {
            selectedPaintings.add(painting);
            Log.d(TAG, "Adding painting: " + painting.getTitle() + ", Artist: " + painting.getArtist() + 
                      (painting.isUrlBased() ? ", URL: " + painting.getImageUrl() : 
                      ", ResId: " + painting.getImageResId()));
            saveSelectedPaintings();
        }
    }

    public boolean removePainting(Painting painting) {
        if (painting == null) return false;
        
        // Find the painting to remove
        Painting paintingToRemove = null;
        for (Painting p : selectedPaintings) {
            if (p == null) continue;
            
            // Check if it's a URL-based painting
            if (p.isUrlBased() && painting.isUrlBased()) {
                if (safeStringEquals(p.getTitle(), painting.getTitle()) && 
                    safeStringEquals(p.getImageUrl(), painting.getImageUrl())) {
                    paintingToRemove = p;
                    break;
                }
            } 
            // Check if it's a resource-based painting
            else if (!p.isUrlBased() && !painting.isUrlBased()) {
                if (safeStringEquals(p.getTitle(), painting.getTitle()) && 
                    p.getImageResId() == painting.getImageResId()) {
                    paintingToRemove = p;
                    break;
                }
            }
        }
        
        // Remove the painting if found
        if (paintingToRemove != null) {
            boolean removed = selectedPaintings.remove(paintingToRemove);
            Log.d(TAG, "Removing painting: " + painting.getTitle() + " - Success: " + removed);
            saveSelectedPaintings();
            return true;
        }
        
        Log.d(TAG, "Failed to remove painting: " + painting.getTitle() + " (not found in tote bag)");
        return false;
    }

    public boolean isPaintingInBag(Painting painting) {
        if (painting == null) return false;
        
        for (Painting p : selectedPaintings) {
            if (p.isUrlBased() && painting.isUrlBased()) {
                // Compare URL-based paintings
                if (p.getTitle().equals(painting.getTitle()) && 
                    p.getImageUrl().equals(painting.getImageUrl())) {
                    return true;
                }
            } else if (!p.isUrlBased() && !painting.isUrlBased()) {
                // Compare resource-based paintings
                if (p.getTitle().equals(painting.getTitle()) && 
                    p.getImageResId() == painting.getImageResId()) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Painting> getSelectedPaintings() {
        return new ArrayList<>(selectedPaintings);
    }

    public void clearSelectedPaintings() {
        selectedPaintings.clear();
        saveSelectedPaintings();
        Log.d(TAG, "Cleared all paintings from tote bag");
    }

    private void saveSelectedPaintings() {
        String json = new Gson().toJson(selectedPaintings);
        preferences.edit().putString(KEY_SELECTED_PAINTINGS, json).apply();
        Log.d(TAG, "Saved " + selectedPaintings.size() + " paintings to SharedPreferences");
    }

    private List<Painting> loadSelectedPaintings() {
        String json = preferences.getString(KEY_SELECTED_PAINTINGS, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }
        Type type = new TypeToken<ArrayList<Painting>>(){}.getType();
        List<Painting> paintings = new Gson().fromJson(json, type);
        if (paintings != null) {
            Log.d(TAG, "Loaded paintings from SharedPreferences: " + paintings.size() + " items");
            for (Painting p : paintings) {
                Log.d(TAG, "Loaded painting: " + p.getTitle() + ", Artist: " + p.getArtist() + 
                          (p.isUrlBased() ? ", URL: " + p.getImageUrl() : 
                          ", ResId: " + p.getImageResId()));
            }
        }
        return paintings != null ? paintings : new ArrayList<>();
    }

    public int getTotalPrice() {
        int total = 0;
        for (Painting painting : selectedPaintings) {
            String price = painting.getPrice();
            if (price != null) {
                try {
                    // Price is already numeric, no need to remove "Rs"
                    total += Integer.parseInt(price);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error parsing price for painting: " + painting.getTitle(), e);
                }
            }
        }
        return total;
    }

    // Helper method for safe String comparison
    private boolean safeStringEquals(String str1, String str2) {
        if (str1 == str2) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
}
