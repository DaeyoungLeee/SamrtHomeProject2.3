package kr.co.aiotlab.samrthomeproject;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

public class CCTV_Activity extends AppCompatActivity {

    WebView webView;
    String cctv_ip;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cctv);

        webView = findViewById(R.id.web_cctv);

        SharedPreferences cctv_shared = getSharedPreferences("IP_ADDRESS", MODE_PRIVATE);
        cctv_ip = cctv_shared.getString("CCTV", "0");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (cctv_ip != "0") {
            webView.loadUrl("http://" + cctv_ip);
        }else {
            Toast.makeText(this, "CCTV 주소를 입력해주세요", Toast.LENGTH_SHORT).show();
        }
    }
}
