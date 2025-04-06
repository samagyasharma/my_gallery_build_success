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
    private static ToteBag instance;
    private List<Painting> paintings;
    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "ToteBagPrefs";
    private static final String KEY_TOTE_BAG = "ToteBagItems";

    private ToteBag(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        paintings = loadPaintings();
    }

    public static synchronized ToteBag getInstance(Context context) {
        if (instance == null) {
            instance = new ToteBag(context.getApplicationContext());
        }
        return instance;
    }

    public List<Painting> getPaintings() {
        return paintings;
    }

    public void addPainting(Painting painting) {
        if (!paintings.contains(painting)) {
            paintings.add(painting);
            Log.d(TAG, "Adding painting: " + painting.getName() + ", Artist: " + painting.getArtist());
            savePaintings();
        }
    }

    public void removePainting(Painting painting) {
        paintings.remove(painting);
        savePaintings();
    }

    public boolean isPaintingInBag(Painting painting) {
        return paintings.contains(painting);
    }

    private void savePaintings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(paintings);
        Log.d(TAG, "Saving paintings to SharedPreferences: " + json);
        editor.putString(KEY_TOTE_BAG, json);
        editor.apply();
    }

    private List<Painting> loadPaintings() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_TOTE_BAG, "");
        Type type = new TypeToken<ArrayList<Painting>>() {}.getType();
        List<Painting> loadedPaintings = gson.fromJson(json, type);
        if (loadedPaintings != null) {
            Log.d(TAG, "Loaded paintings from SharedPreferences: " + loadedPaintings.size() + " items");
            for (Painting p : loadedPaintings) {
                Log.d(TAG, "Loaded painting: " + p.getName() + ", Artist: " + p.getArtist());
            }
        }
        return loadedPaintings != null ? loadedPaintings : new ArrayList<>();
    }
}
