package com.example.itp4501ptms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private TextView tvWelcomeResult, tvResultSummary;
    private ListView lvResultDetails;
    private Button btnResultBackToMain;

    private ArrayList<String> questions;
    private ArrayList<String> userAnswers;
    private ArrayList<String> correctAnswers;
    private ArrayList<String> solutions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvWelcomeResult = findViewById(R.id.tvWelcomeResult);
        tvResultSummary = findViewById(R.id.tvResultSummary);
        lvResultDetails = findViewById(R.id.lvResultDetails);
        btnResultBackToMain = findViewById(R.id.btnResultBackToMain);

        SharedPreferences prefs = getSharedPreferences("MathAppPrefs", Context.MODE_PRIVATE);
        tvWelcomeResult.setText("Hello! " + prefs.getString("username", "學生"));

        int duration = getIntent().getIntExtra("DURATION", 0);
        int wrongCount = getIntent().getIntExtra("WRONG_COUNT", 0);

        String resultText = "總共花費：" + duration + " 秒\n\n答錯題數：" + wrongCount + " 題";
        tvResultSummary.setText(resultText);

        questions = getIntent().getStringArrayListExtra("QUESTIONS");
        userAnswers = getIntent().getStringArrayListExtra("USER_ANSWERS");
        correctAnswers = getIntent().getStringArrayListExtra("CORRECT_ANSWERS");
        solutions = getIntent().getStringArrayListExtra("SOLUTIONS");

        if (questions != null && questions.size() == 10) {
            lvResultDetails.setAdapter(new ResultDetailAdapter());
        }

        btnResultBackToMain.setOnClickListener(v -> finish());
    }

    private class ResultDetailAdapter extends BaseAdapter {
        @Override
        public int getCount() { return 10; }
        @Override
        public Object getItem(int position) { return questions.get(position); }
        @Override
        public long getItemId(int position) { return position; }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            }
            TextView text = convertView.findViewById(android.R.id.text1);
            
            String q = questions.get(position);
            String uAns = userAnswers.get(position);
            String cAns = correctAnswers.get(position);
            String sol = (solutions != null && position < solutions.size()) ? solutions.get(position) : "";

            if (uAns.equals(cAns)) {
                text.setText("題 " + (position + 1) + " (⭕ 正確)\n" + q + "\n你的答案: " + uAns);
                text.setTextColor(0xFF2ECC71);
            } else {
                text.setText("題 " + (position + 1) + " (❌ 答錯)\n" + q + "\n你的答案: " + uAns + "\n正確答案: " + cAns + "\n詳解: " + sol);
                text.setTextColor(0xFFE74C3C);
            }
            return convertView;
        }
    }
}
