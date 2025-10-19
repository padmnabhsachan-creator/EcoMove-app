package com.example.ecomove.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecomove.R;
import com.example.ecomove.api.ApiService;
import com.example.ecomove.api.RetrofitClient;
import com.example.ecomove.api.VoucherRedemption;
import com.example.ecomove.models.Voucher;
import com.example.ecomove.PointManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {

    private final List<Voucher> voucherList;
    private final Context context;
    private final ApiService apiService;
    private int userCredits;

    public VoucherAdapter(List<Voucher> voucherList, Context context, int userCredits) {
        this.voucherList = voucherList;
        this.context = context;
        this.userCredits = userCredits;
        this.apiService = RetrofitClient.getApiService();
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);

        // Display voucher name
        holder.voucherName.setText(context.getString(R.string.voucher_name) + ": " + voucher.getVoucherName());

        // Build voucher details
        StringBuilder details = new StringBuilder();
        details.append(context.getString(R.string.voucher_details)).append(": ").append(voucher.getDescription());
        details.append("\nAmount: ₹").append(voucher.getAmount());
        details.append("\nCredits Required: ").append(voucher.getCreditCost());
        details.append("\nIssued: ").append(voucher.getIssuedAt());
        details.append("\nExpires: ").append(voucher.getExpiryDate());

        holder.voucherDetails.setText(details.toString());

        // Handle redemption state
        if (voucher.isRedeemed()) {
            holder.redeemButton.setEnabled(false);
            holder.redeemButton.setText("Redeemed");
            holder.redeemButton.setAlpha(0.5f);
        } else {
            holder.redeemButton.setEnabled(true);
            holder.redeemButton.setText(context.getString(R.string.redeem));
            holder.redeemButton.setAlpha(1f);

            holder.redeemButton.setOnClickListener(v -> {
                if (voucher.getCreditCost() != null && voucher.getCreditCost() > userCredits) {
                    Toast.makeText(context, "Not enough credits to redeem this voucher", Toast.LENGTH_SHORT).show();
                    return;
                }

                SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
                String userId = prefs.getString("userId", null);

                if (userId != null) {
                    VoucherRedemption redemption = new VoucherRedemption(userId, String.valueOf(voucher.getId()));
                    Call<VoucherRedemption> call = apiService.redeemVoucher(redemption);

                    call.enqueue(new Callback<VoucherRedemption>() {
                        @Override
                        public void onResponse(Call<VoucherRedemption> call, Response<VoucherRedemption> response) {
                            if (response.isSuccessful()) {
                                int currentPosition = holder.getAdapterPosition();
                                if (currentPosition != RecyclerView.NO_POSITION) {
                                    voucherList.get(currentPosition).setRedeemed(true);
                                    notifyItemChanged(currentPosition);
                                    Toast.makeText(context, "Voucher redeemed!", Toast.LENGTH_SHORT).show();

                                    if (context instanceof PointManager) {
                                        ((PointManager) context).updatePointsAfterRedemption(voucher.getCreditCost());
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Redemption failed. Please try again.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<VoucherRedemption> call, Throwable t) {
                            Toast.makeText(context, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    public void updateCredits(int newCredits) {
        this.userCredits = newCredits;
        notifyDataSetChanged();
    }

    public static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView voucherName, voucherDetails;
        Button redeemButton;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            voucherName = itemView.findViewById(R.id.voucherName);
            voucherDetails = itemView.findViewById(R.id.voucherDetails);
            redeemButton = itemView.findViewById(R.id.redeemButton);
        }
    }
}