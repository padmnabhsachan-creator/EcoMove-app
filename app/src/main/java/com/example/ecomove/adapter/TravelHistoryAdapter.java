package com.example.ecomove.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecomove.R;
import com.example.ecomove.travel.model.TravelVerification;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TravelHistoryAdapter extends RecyclerView.Adapter<TravelHistoryAdapter.ViewHolder> {

    private final List<TravelVerification> travelList;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
    private final Context context;

    public TravelHistoryAdapter(Context context, List<TravelVerification> travelList) {
        this.context = context;
        this.travelList = travelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_travel_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TravelVerification travel = travelList.get(position);

        holder.modeText.setText("Mode: " + travel.getMode());
        holder.routeText.setText(travel.getSource() + " → " + travel.getDestination());
        holder.distanceText.setText("Distance: " + travel.getDistanceKm() + " km");

        try {
            holder.dateText.setText("Date: " + dateFormat.format(travel.getTravelDate()));
            holder.verifiedText.setText("Verified at: " + dateFormat.format(travel.getVerifiedAt()));
        } catch (Exception e) {
            holder.dateText.setText("Date: " + travel.getTravelDate());
            holder.verifiedText.setText("Verified at: " + travel.getVerifiedAt());
        }

        // ✅ Always show credit earned using string resource
        String creditLabel = context.getString(R.string.credit_earned_label, travel.getCreditEarned());
        holder.creditText.setText(creditLabel);
    }

    @Override
    public int getItemCount() {
        return travelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView modeText, routeText, distanceText, dateText, verifiedText, creditText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            modeText = itemView.findViewById(R.id.mode_text);
            routeText = itemView.findViewById(R.id.route_text);
            distanceText = itemView.findViewById(R.id.distance_text);
            dateText = itemView.findViewById(R.id.date_text);
            verifiedText = itemView.findViewById(R.id.verified_text);
            creditText = itemView.findViewById(R.id.credit_text); // ✅ Add this
        }
    }
}