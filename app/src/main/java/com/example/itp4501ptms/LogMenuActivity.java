package com.example.itp4501ptms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LogMenuActivity extends AppCompatActivity {

    private TextView tvWelcomeLogMenu;
    private Button btnGoPracticeLog, btnGoWrongLog, btnLogMenuBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_menu);

        tvWelcomeLogMenu = findViewById(R.id.tvWelcomeLogMenu);
        btnGoPracticeLog = findViewById(R.id.btnGoPracticeLog);
        btnGoWrongLog = findViewById(R.id.btnGoWrongLog);
        btnLogMenuBack = findViewById(R.id.btnLogMenuBack);

        SharedPreferences prefs = getSharedPreferences("MathAppPrefs", Context.MODE_PRIVATE);
        tvWelcomeLogMenu.setText("Hello! " + prefs.getString("username", "學生"));

        btnGoPracticeLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogMenuActivity.this, PracticeLogActivity.class);
                startActivity(intent);
            }
        });

        btnGoWrongLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogMenuActivity.this, WrongLogActivity.class);
                startActivity(intent);
            }
        });

        btnLogMenuBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}