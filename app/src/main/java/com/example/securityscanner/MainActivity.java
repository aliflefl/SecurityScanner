package com.example.securityscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText proxyInput, targetInput;
    private Button startButton, stopButton;
    private TextView outputText;
    private ProgressBar progressBar;
    private ExecutorService executorService;
    private boolean isScanning = false;
    private Handler handler;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // تهيئة العناصر
        proxyInput = findViewById(R.id.proxy_input);
        targetInput = findViewById(R.id.target_input);
        startButton = findViewById(R.id.start_button);
        stopButton = findViewById(R.id.stop_button);
        outputText = findViewById(R.id.output_text);
        progressBar = findViewById(R.id.progress_bar);
        
        handler = new Handler(Looper.getMainLooper());
        executorService = Executors.newSingleThreadExecutor();

        // طلب الأذونات
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE
            }, 0);
        }

        // القيم الافتراضية
        proxyInput.setText("172.22.25.135");
        targetInput.setText("my.rcell.me");

        // المستمعون للأزرار
        startButton.setOnClickListener(v -> startScan());
        stopButton.setOnClickListener(v -> stopScan());
    }

    private void startScan() {
        if (isScanning) {
            showToast("المسح جارٍ بالفعل!");
            return;
        }

        String proxy = proxyInput.getText().toString().trim();
        String target = targetInput.getText().toString().trim();

        if (proxy.isEmpty() || target.isEmpty()) {
            showToast("يرجى إدخال جميع البيانات المطلوبة");
            return;
        }

        isScanning = true;
        progressBar.setVisibility(View.VISIBLE);
        outputText.setText("> بدء عملية المسح...");
        startButton.setEnabled(false);
        stopButton.setEnabled(true);

        executorService.execute(() -> {
            try {
                runScan(proxy, target);
            } catch (Exception e) {
                appendOutput("حدث خطأ: " + e.getMessage());
            } finally {
                handler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
                    isScanning = false;
                });
            }
        });
    }

    private void runScan(String proxy, String target) {
        appendOutput("البروكسي: " + proxy);
        appendOutput("الهدف: " + target);
        
        simulatePhase("فحص الشبكة", "nmap -Pn -sV " + proxy, 3);
        simulatePhase("فحص ثغرات الويب", "nikto -h " + target, 2);
        checkWAF(target);
        simulatePhase("استخراج DNS", "dnsenum " + target, 2);
        
        appendOutput("\nتم الاختراق بنجاح! ✅");
        appendOutput("المنافذ المفتوحة: 80, 443, 8080");
    }

    private void simulatePhase(String phaseName, String command, int steps) {
        appendOutput("\n[ " + phaseName + " ]");
        appendOutput("$ " + command);
        
        for (int i = 0; i < steps; i++) {
            try {
                Thread.sleep(1500);
                appendOutput("[" + (i+1) + "/" + steps + "] جارِ التنفيذ...");
            } catch (InterruptedException e) {
                appendOutput("تم إيقاف العملية");
                return;
            }
        }
        
        // نتائج محاكاة
        if (phaseName.contains("شبكة")) {
            appendOutput("تم اكتشاف 3 منافذ مفتوحة");
        } else if (phaseName.contains("ويب")) {
            appendOutput("تم اكتشاف ثغرة XSS و SQLi");
        } else {
            appendOutput("العملية مكتملة بنجاح");
        }
    }

    private void checkWAF(String target) {
        appendOutput("\n[ فحص جدران الحماية ]");
        appendOutput("$ wafw00f -a " + target);
        appendOutput("$ nmap --script http-waf-detect " + target);
        
        try {
            Thread.sleep(2500);
        } catch (InterruptedException e) {}
        
        appendOutput("تم اختراق جدار الحماية! 🔓");
    }

    public void quickScan(View view) {
        if (!isScanning) {
            isScanning = true;
            progressBar.setVisibility(View.VISIBLE);
            outputText.setText("> بدء المسح السريع...");
            startButton.setEnabled(false);
            
            executorService.execute(() -> {
                try {
                    appendOutput("\n[ المسح السريع - وضع TURBO ]");
                    appendOutput("$ nmap -T5 -F " + targetInput.getText());
                    
                    for (int i = 1; i <= 3; i++) {
                        Thread.sleep(800);
                        appendOutput("جارِ المسح [" + i + "/3]...");
                    }
                    
                    appendOutput("تم اختراق 5 أجهزة في 10 ثواني! ⚡");
                } catch (InterruptedException e) {
                    appendOutput("تم إيقاف العملية");
                } finally {
                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        startButton.setEnabled(true);
                        isScanning = false;
                    });
                }
            });
        } else {
            showToast("يوجد عملية مسح جارية!");
        }
    }

    public void wifiHack(View view) {
        if (!isScanning) {
            isScanning = true;
            progressBar.setVisibility(View.VISIBLE);
            outputText.setText("> بدء اختراق الواي فاي...");
            startButton.setEnabled(false);
            
            executorService.execute(() -> {
                try {
                    appendOutput("\n[ اختراق الواي فاي ]");
                    appendOutput("تفعيل وضع المراقبة...");
                    appendOutput("$ airodump-ng wlan0");
                    
                    for (int i = 1; i <= 3; i++) {
                        Thread.sleep(1200);
                        appendOutput("جارِ كسر التشفير [" + i + "/3]...");
                    }
                    
                    appendOutput("تم الاختراق! كلمة السر: " + generatePassword());
                } catch (InterruptedException e) {
                    appendOutput("تم إيقاف العملية");
                } finally {
                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        startButton.setEnabled(true);
                        isScanning = false;
                    });
                }
            });
        } else {
            showToast("يوجد عملية مسح جارية!");
        }
    }

    private String generatePassword() {
        String[] words = {"Admin", "Root", "Secure", "Pass", "Access"};
        String[] numbers = {"123", "2023", "2024", "1001", "777"};
        return words[random.nextInt(words.length)] + numbers[random.nextInt(numbers.length)];
    }

    private void appendOutput(String text) {
        handler.post(() -> outputText.append("\n" + text));
    }

    private void showToast(String message) {
        handler.post(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdownNow();
        }
    }
}