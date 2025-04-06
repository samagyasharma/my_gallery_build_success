package com.example.my_application;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AboutMe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_me);

        // Set up email click listener
        TextView contactInfo = findViewById(R.id.contactInfo);
        contactInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:samagya.sharma@samsung.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry from Art App");
                try {
                    startActivity(emailIntent);
                } catch (Exception e) {
                    Toast.makeText(AboutMe.this, "No email app found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
