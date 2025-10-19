package com.example.ecomove;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecomove.adapter.VoucherAdapter;
import com.example.ecomove.api.ApiService;
import com.example.ecomove.api.RetrofitClient;
import com.example.ecomove.models.Voucher;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointManager extends AppCompatActivity {

    private int currentPoints = 0;
    private TextView pointsText;
    private RecyclerView voucherRecyclerView;
    private ApiService apiService;
    private SharedPreferences prefs;
    private VoucherAdapter voucherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_point_manager);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        currentPoints = prefs.getInt("current_points", 0);

        pointsText = findViewById(R.id.points_text);
        Button vouchersButton = findViewById(R.id.redeem_button);
        voucherRecyclerView = findViewById(R.id.voucherRecyclerView);
        voucherRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiService = RetrofitClient.getApiService();

        updatePointsDisplay();

        vouchersButton.setOnClickListener(v -> {
            String userId = prefs.getString("userId", null);
            Log.d("VoucherDebug", "User ID from prefs: " + userId);
            if (userId != null) {
                fetchVouchers(userId);
            } else {
                Toast.makeText(this, "User ID not found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentPoints = prefs.getInt("current_points", 0);
        updatePointsDisplay();
    }

    public void updatePointsDisplay() {
        String label = getString(R.string.points_label, currentPoints);
        pointsText.setText(label);
    }

    public void updatePointsAfterRedemption(int cost) {
        currentPoints -= cost;
        prefs.edit().putInt("current_points", currentPoints).apply();
        updatePointsDisplay();
        if (voucherAdapter != null) {
            voucherAdapter.updateCredits(currentPoints);
        }
    }

    private void fetchVouchers(String userId) {
        Call<List<Voucher>> call = apiService.getVouchersByUser(userId);
        call.enqueue(new Callback<List<Voucher>>() {
            @Override
            public void onResponse(Call<List<Voucher>> call, Response<List<Voucher>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Voucher> vouchers = response.body();
                    Log.d("VoucherDebug", "Fetched " + vouchers.size() + " vouchers");
                    if (vouchers.isEmpty()) {
                        Toast.makeText(PointManager.this, "No vouchers available", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    voucherAdapter = new VoucherAdapter(vouchers, PointManager.this, currentPoints);
                    voucherRecyclerView.setAdapter(voucherAdapter);
                } else {
                    Log.e("VoucherDebug", "Response failed or empty");
                    Toast.makeText(PointManager.this, "Failed to load vouchers", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Voucher>> call, Throwable t) {
                Log.e("VoucherDebug", "Network error: " + t.getMessage());
                Toast.makeText(PointManager.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}