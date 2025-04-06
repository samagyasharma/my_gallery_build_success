package com.example.my_application;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class AboutMe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_me);

        // Handle back button click
        //ImageView backButton = findViewById(R.id.backButton);
        //backButton.setOnClickListener(view -> finish()); // Closes the activity
    }
}
