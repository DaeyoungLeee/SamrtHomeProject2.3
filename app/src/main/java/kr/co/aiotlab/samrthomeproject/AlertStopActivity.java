package kr.co.aiotlab.samrthomeproject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static kr.co.aiotlab.samrthomeproject.MainActivity.ip_address;
import static kr.co.aiotlab.samrthomeproject.MainActivity.weBView;
import static kr.co.aiotlab.samrthomeproject.SetWakeUp.hour;
import static kr.co.aiotlab.samrthomeproject.SetWakeUp.minute;

public class AlertStopActivity extends AppCompatActivity {

    Button btn_alert_stop, btn_alert_later;
    private String text1, nowSwitchState;
    private String state;
    private Boolean a = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_alert_stop);

        btn_alert_later = findViewById(R.id.btn_alert_later);
        btn_alert_stop = findViewById(R.id.btn_alert_stop);

        a = true;

        new Thread(){
            public void run(){
                while (a){

                    // 현재 시간과 설정 값이 다르면 액티비티 종료
                    long now = System.currentTimeMillis();
                    Date nowdate = new Date(now);
                    SimpleDateFormat dateFormat_minute = new SimpleDateFormat("mm");
                    String nowtime_minute = dateFormat_minute.format(nowdate);

                    if ((Integer.parseInt(nowtime_minute) == minute + 1) || (Integer.parseInt(nowtime_minute) + 1 == 60)){

                        nowSwitchState = getNowState();

                        Message msg = handler3.obtainMessage();
                        handler3.sendMessage(msg);

                        a = false;
                        Log.d("AlertStopActivity", "time: while문");
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();

        // 버튼 동작

        //종료버튼
        final Intent serviceIntent = new Intent(AlertStopActivity.this, Service_WakeUp.class);

        btn_alert_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        a = false;

                        nowSwitchState = getNowState();

                        Message msg = handler2.obtainMessage();
                        handler2.sendMessage(msg);

                        stopService(serviceIntent);

                    }
                }.start();

            }
        });

        // 5분뒤 다시 알람 버튼
        btn_alert_later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(){
                    public void run(){
                        nowSwitchState = getNowState();

                        Message msg = handler3.obtainMessage();
                        handler3.sendMessage(msg);

                        stopService(serviceIntent);
                        startService(serviceIntent);
                    }
                }.start();
            }
        });
    }
    // 현재 상태 읽어오는 함수
    private String getNowState() {
        try {
            Document response = Jsoup.connect("http://" + ip_address + "/status").get();
            Connection.Response response1 = Jsoup.connect("http://" + ip_address + "/status").method(Connection.Method.GET).execute();
            Thread.sleep(200);
            Document document = response1.parse();
            text1 = document.text();
            String text = response.text();


        } catch (IOException ignored) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return text1;
    }

    @SuppressLint("HandlerLeak")
    final Handler handler2 = new Handler() {
        public void handleMessage(Message msg) {
            Log.d("AlertStopActivity", "nowSwitchState: " + nowSwitchState);

            if (nowSwitchState.equals("{'rl':'1','ml':'0','bl':'0','fan':'0'}")) {

                weBView.loadUrl("http://" + ip_address + "/room_light");

            }
            SharedPreferences sharedPreferences_alert_time = getSharedPreferences("WAKEUPTIME", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences_alert_time.edit();
            editor.putString("HOUR", " - ");
            editor.putString("MINUTE", " - ");
            editor.commit();

            a = false;
            finish();

            Intent open_button_intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(open_button_intent);


        }
    };

    // 불을 끄고 5분 뒤 다시 알람 울리는 동작
    @SuppressLint("HandlerLeak")
    final Handler handler3 = new Handler() {
        public void handleMessage(Message msg) {

            Log.d("AlertStopActivity", "nowSwitchState: " + nowSwitchState);

            //불이 켜져있으면 일단 불을 꺼라
            if (nowSwitchState.equals("{'rl':'0','ml':'0','bl':'0','fan':'0'}")) {
                weBView.loadUrl("http://" + ip_address + "/room_light");
            }

            //불이 꺼져있으면 그냥 진행

            if (minute < 55){
                minute = minute + 5;
            }else {
                if (hour == 24){
                    hour = 0;
                    minute = minute + 5 - 60;
                }else {
                    hour = hour + 1;
                    minute = minute + 5 - 60;
                }
            }

            Toast.makeText(getApplicationContext(), "5분 후 알람이 다시 울립니다.", Toast.LENGTH_LONG).show();

            finish();


        }
    };
}
