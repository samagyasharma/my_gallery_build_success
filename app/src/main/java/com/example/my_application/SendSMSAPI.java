package com.example.my_application;

import android.content.Context;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class SendSMSAPI {
    private static final String API_URL = "https://api.twilio.com/2010-04-01/Accounts/ACf1c0c4c4c4c4c4c4c4c4c4c4c4c4c4c4/Messages.json";
    private static final String AUTH_TOKEN = "your_auth_token_here"; // Replace with your actual Twilio auth token

    public static void sendSMS(String toPhoneNumber, String userName, String userPhone, 
                             String deliveryAddress, String paintingsList) {
        Context context = MyApplication.getAppContext();
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, API_URL,
                response -> {
                    // Handle success
                    Toast.makeText(context, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    // Handle error
                    Toast.makeText(context, "Failed to send SMS: " + error.getMessage(), 
                            Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("To", toPhoneNumber);
                params.put("From", "+1234567890"); // Replace with your Twilio phone number
                params.put("Body", createMessageBody(userName, userPhone, deliveryAddress, paintingsList));
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                String credentials = "ACf1c0c4c4c4c4c4c4c4c4c4c4c4c4c4c4:" + AUTH_TOKEN;
                String base64EncodedCredentials = android.util.Base64.encodeToString(
                        credentials.getBytes(), android.util.Base64.NO_WRAP);
                headers.put("Authorization", "Basic " + base64EncodedCredentials);
                return headers;
            }
        };

        queue.add(stringRequest);
    }

    private static String createMessageBody(String userName, String userPhone, 
                                         String deliveryAddress, String paintingsList) {
        return String.format(
            "Order Confirmation\n\n" +
            "Customer: %s\n" +
            "Phone: %s\n" +
            "Delivery Address: %s\n\n" +
            "Order Details:\n%s\n\n" +
            "Thank you for your order!",
            userName, userPhone, deliveryAddress, paintingsList
        );
    }
} 