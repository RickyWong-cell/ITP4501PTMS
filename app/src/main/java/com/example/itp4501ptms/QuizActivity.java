package com.example.itp4501ptms; // ⚠️ 已自動與你的專案 Package 名稱一致

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

import com.example.itp4501ptms.R;

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

    // --- UI 元件 ---
    private TextView tvWelcomeQuiz, tvQuestionCount, tvTimer, tvQuizQuestion;
    private EditText etQuizAnswer;
    private Button btnNextQuestion, btnPauseResume;
    private LinearLayout layoutQuestionCard;

    // --- 計時器變數 ---
    private int secondsElapsed = 0;
    private boolean isPaused = false;
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    // --- 題目邏輯變數 (規格書要求) ---
    private static final String JSON_URL = "https://keen-lodge-3m6b.here.now/question300v2.json";

    // 用來存放從網路下載下來，分門別類的題庫
    private List<JSONObject> allMultiplication = new ArrayList<>();
    private List<JSONObject> allDivision = new ArrayList<>();
    private List<JSONObject> allRemainder = new ArrayList<>();

    // 最終抽出來要給學生做的 10 題
    private List<JSONObject> currentQuizList = new ArrayList<>();
    private int currentQuestionIndex = 0; // 目前在做第幾題 (0 到 9)

    // 規格書要求統計的數據：答錯題數
    private int wrongCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // 1. 初始化 UI
        tvWelcomeQuiz = findViewById(R.id.tvWelcomeQuiz);
        tvQuestionCount = findViewById(R.id.tvQuestionCount);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuizQuestion = findViewById(R.id.tvQuizQuestion);
        etQuizAnswer = findViewById(R.id.etQuizAnswer);
        btnNextQuestion = findViewById(R.id.btnNextQuestion);
        btnPauseResume = findViewById(R.id.btnPauseResume);
        layoutQuestionCard = findViewById(R.id.layoutQuestionCard);

        // 2. 歡迎詞
        SharedPreferences prefs = getSharedPreferences("MathAppPrefs", Context.MODE_PRIVATE);
        tvWelcomeQuiz.setText("Hello! " + prefs.getString("username", "學生"));

        // 3. 一進入頁面，先禁用作答元件，等待題目下載完成
        setLoadingState(true);

        // 4. 開啟背景執行緒，去網路下載 JSON 題庫
        fetchQuestionsFromServer();

        // 5. 設定暫停按鈕監聽
        btnPauseResume.setOnClickListener(v -> {
            if (isPaused) resumeQuiz(); else pauseQuiz();
        });

        // 6. 下一題按鈕監聽
        btnNextQuestion.setOnClickListener(v -> handleNextQuestion());
    }

    /**
     * 設定載入狀態（下載中不能讓學生亂按）
     */
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

    /**
     * 連網下載 JSON 題庫核心方法 (純原生 Thread + Handler)
     */
    private void fetchQuestionsFromServer() {
        new Thread(() -> {
            try {
                URL url = new URL(JSON_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(4000); // 縮短超時等待，避免卡死
                conn.setReadTimeout(4000);

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // 讀取網路串流
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    // 解析 JSON 資料
                    parseJsonAndGenerateQuiz(response.toString());

                } else {
                    // 核心防禦：聯網失敗時，自動啟用本機離線模擬題庫測試
                    loadFallbackLocalQuestions();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 核心防禦：發生異常時，同樣自動切換到本機離線模式，保證 App 絕不卡死
                loadFallbackLocalQuestions();
            }
        }).start();
    }

    /**
     * 核心安全防禦機制：當學校內網 API 連不上時，自動加載規格書要求的 3乘/4除/3餘 本地題目，確保 100% 能跳轉測試
     */
    private void loadFallbackLocalQuestions() {
        showToastOnMainThread("已切換至離線模擬測試模式 🚀");
        try {
            currentQuizList.clear();
            // 模擬 3 題乘法 (Multiplication)
            currentQuizList.add(new JSONObject("{\"q\":\"小明買了 3 包糖果，每包有 5 個，一共有幾個？\",\"a\":\"15\",\"s\":\"3 * 5 = 15\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"一隻青蛙 4 條腿，6 隻青蛙共有幾條腿？\",\"a\":\"24\",\"s\":\"4 * 6 = 24\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"每套文具 8 元，買 3 套需要多少元？\",\"a\":\"24\",\"s\":\"8 * 3 = 24\"}"));

            // 模擬 4 題除法 (Division)
            currentQuizList.add(new JSONObject("{\"q\":\"把 20 個蘋果平均分給 4 個小朋友，每人分得幾個？\",\"a\":\"5\",\"s\":\"20 / 4 = 5\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"媽媽有 12 塊蛋糕，每 3 塊裝一盒，可以裝幾盒？\",\"a\":\"4\",\"s\":\"12 / 3 = 4\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"有 18 支鉛筆，平均分給 6 人，每人有幾支？\",\"a\":\"3\",\"s\":\"18 / 6 = 3\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"把 35 本書平分給 5 個班級，每個班級分到幾本？\",\"a\":\"7\",\"s\":\"35 / 5 = 7\"}"));

            // 模擬 3 題餘數題 (Remainder)
            currentQuizList.add(new JSONObject("{\"q\":\"有 11 個草莓，每 3 個分一盤，剩下幾個？\",\"a\":\"2\",\"s\":\"11 / 3 = 3 ... 2\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"老師有 22 張貼紙，平分給 5 人，還剩幾張？\",\"a\":\"2\",\"s\":\"22 / 5 = 4 ... 2\"}"));
            currentQuizList.add(new JSONObject("{\"q\":\"把 14 個皮球裝進箱子，每個箱子裝 4 個，最後剩下幾個？\",\"a\":\"2\",\"s\":\"14 / 4 = 3 ... 2\"}"));

            // 混合打亂題目
            Collections.shuffle(currentQuizList);

            runOnUiThread(() -> {
                setLoadingState(false);
                startTimer();
                displayQuestion();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 終極智能相容版：自動適應 {} 或 [] 結構，並精準分類乘除餘
     */
    private void parseJsonAndGenerateQuiz(String jsonStr) {
        try {
            if (jsonStr == null || jsonStr.trim().isEmpty()) {
                loadFallbackLocalQuestions();
                return;
            }

            jsonStr = jsonStr.trim();
            JSONArray questionsArray = null;

            // 1. 智能判斷最外層是 物件{} 還是 陣列[]
            if (jsonStr.startsWith("{")) {
                JSONObject root = new JSONObject(jsonStr);
                if (root.has("questions")) {
                    questionsArray = root.getJSONArray("questions");
                } else if (root.has("Questions")) {
                    questionsArray = root.getJSONArray("Questions");
                } else {
                    java.util.Iterator<String> keys = root.keys();
                    if (keys.hasNext()) {
                        String firstKey = keys.next();
                        if (root.get(firstKey) instanceof JSONArray) {
                            questionsArray = root.getJSONArray(firstKey);
                        }
                    }
                }
            } else if (jsonStr.startsWith("[")) {
                questionsArray = new JSONArray(jsonStr);
            }

            if (questionsArray == null) {
                loadFallbackLocalQuestions();
                return;
            }

            // 清空舊題庫
            allMultiplication.clear();
            allDivision.clear();
            allRemainder.clear();

            // 2. 精準分類
            for (int i = 0; i < questionsArray.length(); i++) {
                JSONObject item = questionsArray.getJSONObject(i);
                if (!item.has("q")) continue;

                String questionText = item.getString("q").toLowerCase();

                if (questionText.contains("%") || questionText.contains("餘") || questionText.contains("remain") || questionText.contains("left")) {
                    allRemainder.add(item);
                } else if (questionText.contains("÷") || questionText.contains("/") || questionText.contains("除") || questionText.contains("divide") || questionText.contains("share")) {
                    allDivision.add(item);
                } else {
                    allMultiplication.add(item);
                }
            }

            // 3. 洗牌打亂
            Collections.shuffle(allMultiplication);
            Collections.shuffle(allDivision);
            Collections.shuffle(allRemainder);

            // 4. 嚴格遵照規格書配比抽題：3乘、4除、3餘
            currentQuizList.clear();
            for (int i = 0; i < 3 && i < allMultiplication.size(); i++) currentQuizList.add(allMultiplication.get(i));
            for (int i = 0; i < 4 && i < allDivision.size(); i++) currentQuizList.add(allDivision.get(i));
            for (int i = 0; i < 3 && i < allRemainder.size(); i++) currentQuizList.add(allRemainder.get(i));

            // 防禦性補齊機制
            if (currentQuizList.size() < 10) {
                List<JSONObject> allCombined = new ArrayList<>();
                allCombined.addAll(allMultiplication);
                allCombined.addAll(allDivision);
                allCombined.addAll(allRemainder);
                Collections.shuffle(allCombined);
                for (JSONObject item : allCombined) {
                    if (currentQuizList.size() >= 10) break;
                    if (!currentQuizList.contains(item)) {
                        currentQuizList.add(item);
                    }
                }
            }

            // 最後把這 10 題再度混編打亂
            Collections.shuffle(currentQuizList);

            // 5. 回到主執行緒啟動測驗
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

    /**
     * 在畫面上呈現目前的題目
     */
    private void displayQuestion() {
        if (currentQuizList.isEmpty() || currentQuestionIndex >= currentQuizList.size()) return;

        try {
            // 更新題號
            tvQuestionCount.setText("問題：" + (currentQuestionIndex + 1) + " / 10");

            // 抓取目前的題目物件
            JSONObject currentQuestion = currentQuizList.get(currentQuizList.size() > currentQuestionIndex ? currentQuestionIndex : 0);
            String questionText = currentQuestion.getString("q");

            // 顯示題目
            tvQuizQuestion.setText(questionText);
            etQuizAnswer.setText(""); // 清空上一題輸入框

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 處理點擊「下一題」的邏輯 (已整合正確率統計與 ResultActivity 跳轉功能)
     */
    private void handleNextQuestion() {
        String answerInput = etQuizAnswer.getText().toString().trim();
        if (answerInput.isEmpty()) {
            Toast.makeText(this, "請先輸入答案再進行下一題喔！", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. 即時檢查答案是否正確，用來統計錯題數
        try {
            JSONObject currentQuestion = currentQuizList.get(currentQuestionIndex);
            String correctAnswer = currentQuestion.getString("a").trim();
            if (!answerInput.equals(correctAnswer)) {
                wrongCount++; // 如果輸入的答案不等於正確答案，錯題數 +1
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. 推進到下一題，或者結算跳轉
        if (currentQuestionIndex < 9) {
            currentQuestionIndex++;
            displayQuestion();
        } else {
            // 🎉 做滿 10 題了！關閉後台計時器
            if (timerHandler != null && timerRunnable != null) {
                timerHandler.removeCallbacks(timerRunnable);
            }

            Toast.makeText(this, "太棒了！完成測試！🎉", Toast.LENGTH_SHORT).show();

            // 3. 核心跳轉邏輯：綁定數據並安全前往 ResultActivity
            Intent intent = new Intent(QuizActivity.this, ResultActivity.class);
            intent.putExtra("DURATION", secondsElapsed); // 傳送總共花費的秒數
            intent.putExtra("WRONG_COUNT", wrongCount);  // 傳送總共答錯的題數
            startActivity(intent);

            // 4. 關閉測驗頁面，防止使用者點擊真機的「返回鍵」重新回來偷改答案
            finish();
        }
    }

    // --- 計時器與暫停控制 ---
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