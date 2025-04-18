package com.example.my_application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ContactMe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_me);

        // Back Button Functionality
        //ImageView backButton = findViewById(R.id.backButton);
        //backButton.setOnClickListener(v -> finish());

        // WhatsApp Clickable - Opens WhatsApp Chat
        TextView whatsappText = findViewById(R.id.contactMeContent);
        whatsappText.setOnClickListener(v -> {
            String phoneNumber = "919667965550"; // Remove the '+' and any spaces
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://wa.me/" + phoneNumber));
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "WhatsApp is not installed", Toast.LENGTH_SHORT).show();
            }
        });

        // Email Clickable - Opens Email App
        TextView emailText = findViewById(R.id.emailText);
        emailText.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:samagyasharma05@gmail.com"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Customer Concern");
            startActivity(emailIntent);
        });

        // Website Clickable - Opens Browser
        TextView websiteLink = findViewById(R.id.websiteLink);
        websiteLink.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://samagyasharma.github.io/Customer_Concerns/"));
            startActivity(browserIntent);
        });
    }
}
