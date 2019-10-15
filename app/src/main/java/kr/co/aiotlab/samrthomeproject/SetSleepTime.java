package kr.co.aiotlab.samrthomeproject;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SetSleepTime extends AppCompatActivity implements View.OnClickListener {

    AlarmManager alarmManager;
    TimePicker timePicker;
    TextView updateText;
    Context context;
    Button btn_set_alarm, btn_cancel_alarm;
    String hourS, minuteS;
    TextView txt_updateTime;
    int hour = 0;
    int minute = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setsleep);

        updateText = findViewById(R.id.updateTime);
        this.context = this;

        txt_updateTime = findViewById(R.id.updateTime);

        // 알람 매니저 초기화
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // 시간 초기화
        timePicker = findViewById(R.id.timepicker);

        // update Text 초기화
        updateText = findViewById(R.id.updateTime);

        // 버튼 초기화
        btn_cancel_alarm = findViewById(R.id.btn_alarm_on);
        btn_set_alarm = findViewById(R.id.btn_alarm_off);

        btn_set_alarm.setOnClickListener(this);
        btn_cancel_alarm.setOnClickListener(this);

        //저장된 sleep 시간 불러오기
        SharedPreferences sharedPreferences = getSharedPreferences("SLEEP", MODE_PRIVATE);
        hourS = sharedPreferences.getString("HOUR", " - ");
        minuteS = sharedPreferences.getString("MINUTE", " - ");

        txt_updateTime.setText(hourS + " 시 " + minuteS + " 분 ");

        if (!(hourS.equals(" - ")) && !(minuteS.equals(" - ")) && !(hourS.equals("-")) && !(minuteS.equals("-"))){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timePicker.setHour(Integer.parseInt(hourS));
                timePicker.setMinute(Integer.parseInt(minuteS));
            }
        }


    }

    @Override
    public void onClick(View v) {
        Intent serviceIntent = new Intent(this, Service_Sleep.class);

        SharedPreferences sharedPreferences = getSharedPreferences("SLEEP", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (v.getId()) {
            // 저장버튼
            case R.id.btn_alarm_on:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    if (hour < 10) {
                        hourS = "0" + String.valueOf(hour);
                    }else {
                        hourS = String.valueOf(hour);
                    }
                }


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    minute = timePicker.getMinute();
                    if (minute < 10) {
                        minuteS = "0" + String.valueOf(minute);
                    }else{
                        minuteS = String.valueOf(minute);
                    }
                }

                Toast.makeText(context, "매일 " + hour + " 시 " + minute + " 분에 조명이 꺼집니다.", Toast.LENGTH_LONG).show();

                editor.putString("HOUR", hourS);
                editor.putString("MINUTE", minuteS);
                editor.commit();

                // 서비스 시작
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                }else {
                    startService(serviceIntent);
                }

                finish();
                break;

                // 알람취소 버튼
            case R.id.btn_alarm_off:

                stopService(serviceIntent);

                editor.putString("HOUR", "-");
                editor.putString("MINUTE", "-");
                editor.commit();

                Toast.makeText(context, "자동 불끄기 기능이 꺼졌습니다.", Toast.LENGTH_SHORT).show();

                finish();

                break;

        }

    }
}
