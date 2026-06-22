package com.example.itp4501ptms;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private TextView tvWelcomeQuiz, tvQuestionCount, tvTimer, tvQuizQuestion, tvQuestionTypeHint;
    private EditText etQuizAnswer;
    private Button btnNextQuestion, btnPauseResume;
    private LinearLayout layoutQuestionCard;

    private int secondsElapsed = 0;
    private boolean isPaused = false;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    private static final String JSON_URL = "https://keen-lodge-3m6b.here.now/question300v3.json";

    private List<JSONObject> allMultiplication = new ArrayList<>();
    private List<JSONObject> allDivision = new ArrayList<>();
    private List<JSONObject> allRemainder = new ArrayList<>();

    private List<JSONObject> currentQuizList = new ArrayList<>();
    private int currentQuestionIndex = 0;

    private int wrongCount = 0;
    private ArrayList<String> savedQuestions = new ArrayList<>();
    private ArrayList<String> savedUserAnswers = new ArrayList<>();
    private ArrayList<String> savedCorrectAnswers = new ArrayList<>();
    private ArrayList<String> savedSolutions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvWelcomeQuiz = findViewById(R.id.tvWelcomeQuiz);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuizQuestion = findViewById(R.id.tvQuizQuestion);
        tvQuestionTypeHint = findViewById(R.id.tvQuestionTypeHint); // 新增這一行
        etQuizAnswer = findViewById(R.id.etQuizAnswer);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);
        btnPauseResume = findViewById(R.id.btnPauseResume);
        layoutQuestionCard = findViewById(R.id.layoutQuestionCard);

        SharedPreferences prefs = getSharedPreferences("MathAppPrefs", Context.MODE_PRIVATE);
        tvWelcomeQuiz.setText("Hello! " + prefs.getString("username", "學生"));

        setLoadingState(true);
        fetchQuestionsFromServer();

        btnPauseResume.setOnClickListener(v -> {
            if (isPaused) resumeQuiz(); else pauseQuiz();
        });

        btnNextQuestion.setOnClickListener(v -> handleNextQuestion());
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            tvQuizQuestion.setText("正在從雲端下載 300 題庫，請稍候... ⏳");
            etQuizAnswer.setEnabled(false);
            btnNextQuestion.setEnabled(false);
            btnPauseResume.setEnabled(false);
        } else {
            etQuizAnswer.setEnabled(true);
            btnNextQuestion.setEnabled(true);
            btnPauseResume.setEnabled(true);
        }
    }

    private void fetchQuestionsFromServer() {
        new Thread(() -> {
            try {
                URL url = new URL(JSON_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(4000);
                conn.setReadTimeout(4000);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) response.append(line);
                    in.close();
                    parseJsonAndGenerateQuiz(response.toString());
                } else {
                    loadFallbackLocalQuestions();
                }
            } catch (Exception e) {
                e.printStackTrace();
                loadFallbackLocalQuestions();
            }
        }).start();
    }

    private void loadFallbackLocalQuestions() {
        showToastOnMainThread("已切換至離線模擬測試模式 🚀");
        try {
            currentQuizList.clear();
            currentQuizList.add(new JSONObject("{\"q\":\"小明買了 3 包糖果，每包有 5 個，一共有幾個？\",\"a\":\"15\",\"s\":\"3 * 5 = 15\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"一隻青蛙 4 條腿，6 隻青蛙共有幾條腿？\",\"a\":\"24\",\"s\":\"4 * 6 = 24\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"每套文具 8 元，買 3 套需要多少元？\",\"a\":\"24\",\"s\":\"8 * 3 = 24\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"把 20 個蘋果平均分給 4 個小朋友，每人分得幾個？\",\"a\":\"5\",\"s\":\"20 / 4 = 5\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"媽媽有 12 塊蛋糕，每 3 塊裝一盒，可以裝幾盒？\",\"a\":\"4\",\"s\":\"12 / 3 = 4\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"有 18 支鉛筆，平均分給 6 人，每人有幾支？\",\"a\":\"3\",\"s\":\"18 / 6 = 3\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"把 35 本書平分給 5 個班級，每個班級分到幾本？\",\"a\":\"7\",\"s\":\"35 / 5 = 7\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"有 11 個草莓，每 3 個分一盤，剩下幾個？\",\"a\":\"2\",\"s\":\"11 / 3 = 3 ... 2\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"老師有 22 張貼紙，平分給 5 人，還剩幾張？\",\"a\":\"2\",\"s\":\"22 / 5 = 4 ... 2\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"把 14 個皮球裝進箱子，每個箱子裝 4 個，最後剩下幾個？\",\"a\":\"2\",\"s\":\"14 / 4 = 3 ... 2\"}"));
            Collections.shuffle(currentQuizList);
            runOnUiThread(() -> {
                setLoadingState(false);
                startTimer();
                displayQuestion();
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void parseJsonAndGenerateQuiz(String jsonStr) {
        try {
            if (jsonStr == null || jsonStr.trim().isEmpty()) {
                loadFallbackLocalQuestions();
                return;
            }

            JSONObject root = new JSONObject(jsonStr.trim());

            allMultiplication.clear();
            allDivision.clear();
            allRemainder.clear();

            if (root.has("multiplication")) {
                JSONArray multiArray = root.getJSONArray("multiplication");
                for (int i = 0; i < multiArray.length(); i++) {
                    allMultiplication.add(multiArray.getJSONObject(i));
                }
            }

            if (root.has("division")) {
                JSONArray divArray = root.getJSONArray("division");
                for (int i = 0; i < divArray.length(); i++) {
                    allDivision.add(divArray.getJSONObject(i));
                }
            }

            if (root.has("remainder")) {
                JSONArray remArray = root.getJSONArray("remainder");
                for (int i = 0; i < remArray.length(); i++) {
                    allRemainder.add(remArray.getJSONObject(i));
                }
            }

            Collections.shuffle(allMultiplication);
            Collections.shuffle(allDivision);
            Collections.shuffle(allRemainder);

            currentQuizList.clear();
            for (int i = 0; i < 3 && i < allMultiplication.size(); i++) currentQuizList.add(allMultiplication.get(i));
            for (int i = 0; i < 4 && i < allDivision.size(); i++) currentQuizList.add(allDivision.get(i));
            for (int i = 0; i < 3 && i < allRemainder.size(); i++) currentQuizList.add(allRemainder.get(i));

            if (currentQuizList.size() < 10) {
                loadFallbackLocalQuestions();
                return;
            }

            Collections.shuffle(currentQuizList);

            runOnUiThread(() -> {
                setLoadingState(false);
                startTimer();
                displayQuestion();
            });

        } catch (Exception e) {
            e.printStackTrace();
            loadFallbackLocalQuestions();
        }
    }

    private void displayQuestion() {
        if (currentQuizList.isEmpty() || currentQuestionIndex >= currentQuizList.size()) return;

        try {
            tvQuestionCount.setText("問題：" + (currentQuestionIndex + 1) + " / 10");

            JSONObject currentQuestion = currentQuizList.get(currentQuestionIndex);
            String questionText = currentQuestion.getString("q");
            String solutionText = currentQuestion.optString("s", "").toLowerCase(); // 讀取解題步驟來精準判斷

            if (solutionText.contains("…") || solutionText.contains("...") || solutionText.contains("餘") || questionText.contains("剩")) {
                tvQuestionTypeHint.setText("【 當前題型：餘數除法 🔢 】");
                tvQuestionTypeHint.setTextColor(0xFF3498DB); // 紫色
            } else if (solutionText.contains("/") || solutionText.contains("÷") || solutionText.contains("分")) {
                tvQuestionTypeHint.setText("【 當前題型：整除除法 ➗ 】");
                tvQuestionTypeHint.setTextColor(0xFF3498DB); // 藍色
            } else {
                tvQuestionTypeHint.setText("【 當前題型：基礎乘法 ✖️ 】");
                tvQuestionTypeHint.setTextColor(0xFF2ECC71); // 綠色
            }

            tvQuizQuestion.setText(questionText);
            etQuizAnswer.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleNextQuestion() {
        String answerInput = etQuizAnswer.getText().toString().trim();
        if (answerInput.isEmpty()) {
            Toast.makeText(this, "請先輸入答案再進行下一題喔！", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject currentQuestion = currentQuizList.get(currentQuestionIndex);
            String correctAnswer = currentQuestion.getString("a").trim();
            if (!answerInput.equals(correctAnswer)) {
                wrongCount++;
            }
        } catch (Exception e) { e.printStackTrace(); }

        if (currentQuestionIndex < 9) {
            try {
                JSONObject currentQuestion = currentQuizList.get(currentQuestionIndex);
                savedQuestions.add(currentQuestion.getString("q"));
                savedUserAnswers.add(answerInput);
                savedCorrectAnswers.add(currentQuestion.getString("a"));
                savedSolutions.add(currentQuestion.has("s") ? currentQuestion.getString("s") : "");
            } catch (Exception e) { e.printStackTrace(); }

            currentQuestionIndex++;
            displayQuestion();
        } else {
            // 這是最後一題（第10題）做完的時候
            try {
                JSONObject currentQuestion = currentQuizList.get(currentQuestionIndex);
                savedQuestions.add(currentQuestion.getString("q"));
                savedUserAnswers.add(answerInput);
                savedCorrectAnswers.add(currentQuestion.getString("a"));
                savedSolutions.add(currentQuestion.has("s") ? currentQuestion.getString("s") : "");
            } catch (Exception e) { e.printStackTrace(); }

            if (timerHandler != null && timerRunnable != null) {
                timerHandler.removeCallbacks(timerRunnable);
            }

            Toast.makeText(this, "太棒了！完成測試！🎉", Toast.LENGTH_SHORT).show();

            try {
                DatabaseHelper dbHelper = new DatabaseHelper(QuizActivity.this);
                android.content.ContentValues pValues = new android.content.ContentValues();
                String dateTimeStr = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(new java.util.Date());
                pValues.put("date_time", dateTimeStr);
                pValues.put("duration", secondsElapsed);
                pValues.put("wrong_count", wrongCount);
                dbHelper.getWritableDatabase().insert("PracticeLog", null, pValues);

                for (int i = 0; i < 10; i++) {
                    if (!savedUserAnswers.get(i).equals(savedCorrectAnswers.get(i))) {
                        android.content.ContentValues wValues = new android.content.ContentValues();
                        wValues.put("question", savedQuestions.get(i));
                        wValues.put("wrong_answer", savedUserAnswers.get(i));
                        wValues.put("correct_answer", savedCorrectAnswers.get(i));
                        wValues.put("solution", savedSolutions.get(i));
                        dbHelper.getWritableDatabase().insert("WrongAnswerLog", null, wValues);
                    }
                }
            } catch (Exception e) { e.printStackTrace(); }

            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("DURATION", secondsElapsed);
            intent.putExtra("WRONG_COUNT", wrongCount);
            intent.putStringArrayListExtra("QUESTIONS", savedQuestions);
            intent.putStringArrayListExtra("USER_ANSWERS", savedUserAnswers);
            intent.putStringArrayListExtra("CORRECT_ANSWERS", savedCorrectAnswers);
            intent.putStringArrayListExtra("SOLUTIONS", savedSolutions);
            startActivity(intent);
            finish();
        }
    }

    private void startTimer() {
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isPaused) {
                    secondsElapsed++;
                    tvTimer.setText("⏱️ 時間：" + secondsElapsed + " 秒");
                }
                timerHandler.postDelayed(this, 1000);
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void pauseQuiz() {
        isPaused = true;
        btnPauseResume.setText("▶️ 恢復控制，繼續挑戰");
        btnPauseResume.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF2ECC71));
        tvQuizQuestion.setText("⏸️ 休息中...\n題目已隱藏，點擊下方按鈕繼續！");
        etQuizAnswer.setEnabled(false);
        btnNextQuestion.setEnabled(false);
    }

    private void resumeQuiz() {
        isPaused = false;
        btnPauseResume.setText("⏸️ 暫停休息一下");
        btnPauseResume.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFE67E22));
        displayQuestion();
        etQuizAnswer.setEnabled(true);
        btnNextQuestion.setEnabled(true);
    }

    private void showToastOnMainThread(String msg) {
        runOnUiThread(() -> Toast.makeText(QuizActivity.this, msg, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timerHandler != null && timerRunnable != null) {
            timerHandler.removeCallbacks(timerRunnable);
        }
    }
}
