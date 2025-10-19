package com.example.ecomove;

import android.os.Bundle;
import android.widget.TextView;
import android.content.SharedPreferences;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.ecomove.models.User;

public class PersonalInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_personal_info);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        com.example.ecomove.api.UserResponse user = (com.example.ecomove.api.UserResponse) getIntent().getSerializableExtra("user");

        TextView nameText = findViewById(R.id.name_text);
        TextView emailText = findViewById(R.id.email_text);
        TextView phoneText = findViewById(R.id.phone_text);
        TextView verifiedText = findViewById(R.id.verified_text);
        TextView creditText = findViewById(R.id.credit_text);

        if (user != null) {
            nameText.setText(getString(R.string.name_format, user.getName()));
            emailText.setText(getString(R.string.email_format, user.getEmail()));
            phoneText.setText(getString(R.string.phone_format, user.getPhoneNumber()));
            creditText.setText(getString(R.string.credit_format, user.getTotalCreditPoints()));
            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            prefs.edit().putInt("current_points", user.getTotalCreditPoints()).apply();
            verifiedText.setText(user.isVerified()
                    ? getString(R.string.verified_status_true)
                    : getString(R.string.verified_status_false));
        }
    }
}