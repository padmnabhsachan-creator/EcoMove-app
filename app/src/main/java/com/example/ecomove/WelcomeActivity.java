package com.example.ecomove;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already signed up
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String userId = prefs.getString("userId", null);

        if (userId != null) {
            // Auto-login
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userId", userId);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        Button loginButton = findViewById(R.id.login_button);
        Button signupButton = findViewById(R.id.signup_button);

        loginButton.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        signupButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SignupActivity.class));
        });
    }
}