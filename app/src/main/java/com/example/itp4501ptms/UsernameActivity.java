package com.example.itp4501ptms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class UsernameActivity extends AppCompatActivity {

    private TextView tvWelcomeUsername;
    private EditText etNewUsername;
    private Button btnSaveUsername, btnCancelUsername;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        tvWelcomeUsername = findViewById(R.id.tvWelcomeUsername);
        etNewUsername = findViewById(R.id.etNewUsername);
        btnSaveUsername = findViewById(R.id.btnSaveUsername);
        btnCancelUsername = findViewById(R.id.btnCancelUsername);

        sharedPreferences = getSharedPreferences("MathAppPrefs", Context.MODE_PRIVATE);
        String currentName = sharedPreferences.getString("username", "");

        tvWelcomeUsername.setText("Hello! " + currentName);
        etNewUsername.setText(currentName);

        btnSaveUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etNewUsername.getText().toString().trim();
                if (!newName.isEmpty()) {
                    sharedPreferences.edit().putString("username", newName).apply();
                    Toast.makeText(UsernameActivity.this, "名字修改成功！✨", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(UsernameActivity.this, "名字不能是空的喔！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 取消按鈕邏輯
        btnCancelUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 直接不儲存返回
            }
        });
    }
}