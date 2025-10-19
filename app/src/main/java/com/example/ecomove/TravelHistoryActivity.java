package com.example.ecomove;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.example.ecomove.api.ApiService;
import com.example.ecomove.api.RetrofitClient;
import com.example.ecomove.adapter.TravelHistoryAdapter;
import com.example.ecomove.travel.model.TravelVerification;
import androidx.recyclerview.widget.DividerItemDecoration;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TravelHistoryActivity extends AppCompatActivity {

    private RecyclerView travelHistoryRecyclerView;
    private TravelHistoryAdapter adapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_history);

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getString("userId", null);

        if (userId == null) {
            Toast.makeText(this, "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            finish(); // or redirect to LoginActivity
            return;
        }

        travelHistoryRecyclerView = findViewById(R.id.travelHistoryRecyclerView);

        // ✅ Ensure vertical scrolling and performance
        travelHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        travelHistoryRecyclerView.setHasFixedSize(true); // improves performance if item size is fixed

        // ✅ Optional: add divider between items
        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        travelHistoryRecyclerView.addItemDecoration(divider);

        fetchTravelHistory();
    }

    private void fetchTravelHistory() {
        Log.d("TravelHistory", "Calling getTravelHistory for userId: " + userId);
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<TravelVerification>> call = apiService.getTravelHistory(userId);

        call.enqueue(new Callback<List<TravelVerification>>() {
            @Override
            public void onResponse(Call<List<TravelVerification>> call, Response<List<TravelVerification>> response) {
                Log.d("TravelHistory", "onResponse triggered");
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("TravelHistory", "Fetched entries: " + new Gson().toJson(response.body()));
                    adapter = new TravelHistoryAdapter(TravelHistoryActivity.this, response.body());
                    travelHistoryRecyclerView.setAdapter(adapter);
                } else {
                    Log.d("TravelHistory", "Response failed or empty. Code: " + response.code());
                    Toast.makeText(TravelHistoryActivity.this, "No travel history found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TravelVerification>> call, Throwable t) {
                Log.e("TravelHistory", "API call failed", t);
                Toast.makeText(TravelHistoryActivity.this, "Failed to load travel history.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}