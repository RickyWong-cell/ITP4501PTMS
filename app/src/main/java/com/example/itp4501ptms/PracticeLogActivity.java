package com.example.itp4501ptms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PracticeLogActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_log);

        TextView tvWelcome = findViewById(R.id.tvWelcomePracticeLog);
        Button btnBack = findViewById(R.id.btnLogBack);

        SharedPreferences prefs = getSharedPreferences("MathAppPrefs", Context.MODE_PRIVATE);
        tvWelcome.setText("Hello! " + prefs.getString("username", "學生"));

        btnBack.setOnClickListener(v -> finish());
    }
}
