package com.example.ecomove;
import android.content.SharedPreferences;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.example.ecomove.api.ApiService;
import com.example.ecomove.api.RetrofitClient;
import com.example.ecomove.api.CreditRequest;
import com.example.ecomove.travel.model.TravelVerificationRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;
import com.google.gson.Gson;


import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private EditText modeInput, distanceInput;
    private TextView resultText;
    private Button calculateButton,scanQrButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Drawer and Toolbar setup
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_personal_info) {
                SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                String userId = prefs.getString("userId", null);

                if (userId != null) {
                    com.example.ecomove.api.ApiService apiService = com.example.ecomove.api.RetrofitClient.getApiService();
                    retrofit2.Call<com.example.ecomove.api.UserResponse> call = apiService.getUserById(userId);

                    call.enqueue(new retrofit2.Callback<com.example.ecomove.api.UserResponse>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.example.ecomove.api.UserResponse> call, retrofit2.Response<com.example.ecomove.api.UserResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                com.example.ecomove.api.UserResponse user = response.body();
                                Intent intent = new Intent(MainActivity.this, PersonalInfoActivity.class);
                                intent.putExtra("user", user);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Failed to load user info", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<com.example.ecomove.api.UserResponse> call, Throwable t) {
                            Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.nav_credit_points) {
                startActivity(new Intent(this, PointManager.class));
            } else if (id == R.id.nav_voucher_history) {
                startActivity(new Intent(this, TravelHistoryActivity.class));
            }
            else if (id == R.id.nav_logout) {
                showLogoutConfirmation();
            }
            drawerLayout.closeDrawers();
            return true;
        });

        // UI Elements
        modeInput = findViewById(R.id.modeInput);
        distanceInput = findViewById(R.id.distanceInput);
        resultText = findViewById(R.id.resultText);
        calculateButton = findViewById(R.id.calculateButton);
        scanQrButton = findViewById(R.id.scanQrButton);

        calculateButton.setOnClickListener(v -> {
            String mode = modeInput.getText().toString().trim();
            String distanceStr = distanceInput.getText().toString().trim();

            if (!mode.isEmpty() && !distanceStr.isEmpty()) {
                try {
                    double distance = Double.parseDouble(distanceStr);
                    double savedCO2 = calculateSavedCO2(mode, distance);
                    int earnedCredits = (int) Math.round(savedCO2);

                    String result = String.format(Locale.getDefault(),
                            getString(R.string.result_text), savedCO2);
                    resultText.setText(result);

                    SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    String userId = prefs.getString("userId", null);

                    if (userId != null) {
                        ApiService apiService = RetrofitClient.getApiService();

                        // 1️⃣ Deposit credits
                        CreditRequest creditRequest = new CreditRequest(earnedCredits);
                        Call<Void> creditCall = apiService.addCredits(userId, creditRequest);
                        creditCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Deposited " + earnedCredits + " credits!", Toast.LENGTH_SHORT).show();
                                    int currentPoints = prefs.getInt("current_points", 0);
                                    prefs.edit().putInt("current_points", currentPoints + earnedCredits).apply();
                                } else {
                                    Toast.makeText(MainActivity.this, "Credit deposit failed", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Network error during credit deposit", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // 2️⃣ Submit travel log
                        TravelVerificationRequest travelRequest = new TravelVerificationRequest();
                        travelRequest.setUserId(userId);
                        travelRequest.setMode(mode);
                        travelRequest.setSource("Manual Entry");
                        travelRequest.setDestination("Manual Entry");
                        travelRequest.setDistanceKm(distance);
                        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        travelRequest.setTravelDate(formattedDate);
                        travelRequest.setVerificationMethod("Manual");
                        Log.d("TravelRequest", new Gson().toJson(travelRequest));


                        Call<Void> travelCall = apiService.verifyTravel(travelRequest);
                        travelCall.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> response) {
                                if (response.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Travel log saved!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Failed to save travel log", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                Toast.makeText(MainActivity.this, "Network error saving travel log", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    Toast.makeText(this, getString(R.string.error_invalid_distance), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.error_empty_fields), Toast.LENGTH_SHORT).show();
            }
        });

        scanQrButton.setOnClickListener(v -> {
            Toast.makeText(this, "Launching QR scanner...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, QrScanActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void performLogout() {
        android.content.SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String token = prefs.getString("auth_token", null);

        if (token == null) {
            Toast.makeText(this, "No token found", Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.ecomove.api.ApiService apiService = com.example.ecomove.api.RetrofitClient.getApiService();
        retrofit2.Call<okhttp3.ResponseBody> call = apiService.logout("Bearer " + token);

        call.enqueue(new retrofit2.Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<okhttp3.ResponseBody> call, retrofit2.Response<okhttp3.ResponseBody> response) {
                if (response.isSuccessful()) {
                    prefs.edit().clear().apply();
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                } else {
                    Toast.makeText(MainActivity.this, "Logout failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<okhttp3.ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Network error during logout", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private double calculateSavedCO2(String mode, double distanceKm) {
        double baseline = 0.2; // solo car emissions per km
        double factor;

        switch (mode.toLowerCase()) {
            case "bus":
                factor = 0.05;
                break;
            case "train":
                factor = 0.04;
                break;
            case "carpool":
                factor = 0.07;
                break;
            default:
                factor = baseline;
        }

        return Math.max(0, (baseline - factor) * distanceKm);
    }



    private void showLogoutConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", (dialog, which) -> performLogout())
                .setNegativeButton("Cancel", null)
                .show();
    }
}