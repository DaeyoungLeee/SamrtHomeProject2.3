package kr.co.aiotlab.samrthomeproject;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SetWakeUp extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private AlarmManager alarmManager;
    private TimePicker timePicker;
    private Button btn_alert_off2, btn_alert_on2;
    private ImageButton btn_set_date;
    private TextView txt_alert_time, txt_alert_timeset;
    private String nowtime_year, nowtime_month, nowtime_day, nowtime_hour, nowtime_minute;
    public static int setting_year, setting_month, setting_day;
    public static Calendar calendar;

    public static int hour, minute;
    public static String setHour, setMinute;
    private String hourS, minuteS;

    private int int_resttime_day;
    private int int_resttime_hour;
    private int int_resttime_minute;
    private Date nowdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setalert);

        timePicker = findViewById(R.id.alert_timepicker);
        btn_set_date = findViewById(R.id.set_alert_date);
        txt_alert_time = findViewById(R.id.txt_alert_time);
        btn_alert_off2 = findViewById(R.id.btn_alarm_off2);
        btn_alert_on2 = findViewById(R.id.btn_alarm_on2);
        txt_alert_timeset = findViewById(R.id.txt_alert_timeset);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        btn_alert_on2.setOnClickListener(this);
        btn_alert_off2.setOnClickListener(this);
        btn_set_date.setOnClickListener(this);

        //현재 시간
        long now = System.currentTimeMillis();
        nowdate = new Date(now);
        SimpleDateFormat dateFormat_year = new SimpleDateFormat("yyyy");
        SimpleDateFormat dateFormat_month = new SimpleDateFormat("MM");
        SimpleDateFormat dateFormat_day = new SimpleDateFormat("dd");
        SimpleDateFormat dateFormat_hour = new SimpleDateFormat("HH");
        SimpleDateFormat dateFormat_minute = new SimpleDateFormat("mm");

        nowtime_year = dateFormat_year.format(nowdate);
        nowtime_month = dateFormat_month.format(nowdate);
        nowtime_day = dateFormat_day.format(nowdate);
        nowtime_hour = dateFormat_hour.format(nowdate);
        nowtime_minute = dateFormat_minute.format(nowdate);


        // 알람시간 보여주기
        SharedPreferences sharedPreferences_alert_time = getSharedPreferences("WAKEUPTIME", MODE_PRIVATE);
        setHour = sharedPreferences_alert_time.getString("HOUR", " - ");
        setMinute = sharedPreferences_alert_time.getString("MINUTE", " - ");
        String setDate = sharedPreferences_alert_time.getString("DATE", nowtime_month + "월 " + nowtime_day + "일 ");

        txt_alert_time.setText(setHour + "시 " + setMinute + "분 ");
        txt_alert_timeset.setText(setDate + " 한 번 울림");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!setHour.equals(" - ") && !setMinute.equals(" - ")) {
                timePicker.setHour(Integer.parseInt(setHour));
                timePicker.setMinute(Integer.parseInt(setMinute));
            }

        }
    }

    @Override
    public void onClick(View v) {
        Intent intent_wakeup_service = new Intent(this, Service_WakeUp.class);
        DialogFragment datePicker = new WakeUpDatePicker();
        switch (v.getId()) {
            // 데이트 피커창
            case R.id.set_alert_date:
                datePicker.show(getSupportFragmentManager(), "date picker");
                break;

            // 알람 저장버튼 클릭
            case R.id.btn_alarm_on2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    hourS = String.valueOf(hour);
                    if (hour < 10) {
                        hourS = "0" + hourS;
                    }
                    minute = timePicker.getMinute();
                    minuteS = String.valueOf(minute);
                    if (minute < 10) {
                        minuteS = "0" + minuteS;
                    } else if (minute == 0) {
                        minuteS = "00";
                    }
                }

                // 선택한 시간, 분 저장
                SharedPreferences sharedPreferences_clock = getSharedPreferences("WAKEUPTIME", MODE_PRIVATE);
                SharedPreferences.Editor editor_clock = sharedPreferences_clock.edit();
                editor_clock.putString("HOUR", hourS);
                editor_clock.putString("MINUTE", minuteS);
                editor_clock.commit();

                setting_year = sharedPreferences_clock.getInt("DATEYEAR", 0000);
                setting_month = sharedPreferences_clock.getInt("DATEMONTH", 00);
                setting_day = sharedPreferences_clock.getInt("DATEDAY", 0);

                //남은 시간 계산
                /*
                if (setting_day != 0){
                    int_resttime_day = ((setting_day * 1440 + Integer.parseInt(hourS) * 60 + Integer.parseInt(minuteS))
                            - (Integer.parseInt(nowtime_day) * 1440 + Integer.parseInt(nowtime_hour) * 60 + Integer.parseInt(nowtime_minute)))/60/24;
                }

                if (Integer.parseInt(hourS) < Integer.parseInt(nowtime_hour) && setting_day > Integer.parseInt(nowtime_day)){
                    int_resttime_hour = 24 - (((Integer.parseInt(nowtime_hour) * 60 + Integer.parseInt(nowtime_minute)) - (Integer.parseInt(hourS) * 60 + Integer.parseInt(minuteS))) / 60);
                }else {
                    int_resttime_hour = 24 - (-((Integer.parseInt(nowtime_hour) * 60 + Integer.parseInt(nowtime_minute)) + (Integer.parseInt(hourS) * 60 + Integer.parseInt(minuteS))) / 60);
                }

                int_resttime_minute = ( - (Integer.parseInt(nowtime_hour) * 60 + Integer.parseInt(nowtime_minute)) + (Integer.parseInt(hourS) * 60 + Integer.parseInt(minuteS))) % 60;

                Toast.makeText(getApplicationContext(), + int_resttime_day + "일 "+  int_resttime_hour + " 시간 " + int_resttime_minute + " 분 후에 알람이 울립니다.", Toast.LENGTH_LONG).show();

                 */


                // 설정한 날짜가 현재 날짜보다 낮으면 스낵바 띄워주기
                if (setting_year < Integer.parseInt(nowtime_year)
                        || setting_month < Integer.parseInt(nowtime_month)
                        || setting_day < Integer.parseInt(nowtime_day)) {
                    Snackbar.make(v, "현재 날짜보다 설정 날짜가 작습니다.", Snackbar.LENGTH_LONG).setAction("닫기", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    }).setActionTextColor(Color.RED).show();
                } else {
                    int now_day = Integer.parseInt(nowtime_day);
                    int now_hour = Integer.parseInt(nowtime_hour);
                    int now_minute = Integer.parseInt(nowtime_minute);
                    int setting_hour = Integer.parseInt(hourS);
                    int setting_minute = Integer.parseInt(minuteS);

                    int remained_hour = 0;
                    int remained_minute = 0;

                    // 남은 시간 알려주기
                    if (setting_day > now_day) {
                        remained_hour = 24 * (setting_day - now_day) + setting_hour - now_hour;

                        if (setting_minute < now_minute) {
                            setting_minute = setting_minute + 60;
                            remained_hour = remained_hour - 1;
                        }
                        remained_minute = setting_minute - now_minute;

                    } else if (setting_day == now_day) {
                        if (setting_hour >= now_hour) {
                            if (setting_hour == now_hour) {
                                if (setting_minute <= now_minute) {
                                    Snackbar.make(v, "현재 날짜보다 설정 날짜가 작습니다.", Snackbar.LENGTH_LONG).setAction("닫기", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    }).setActionTextColor(Color.RED).show();
                                    return;
                                } else {
                                    remained_minute = setting_minute - now_minute;
                                }
                            } else {
                                remained_hour = setting_hour - now_hour;
                                if (setting_minute < now_minute) {
                                    setting_minute = setting_minute + 60;
                                    remained_hour = remained_hour - 1;
                                }
                                remained_minute = setting_minute - now_minute;
                            }
                        } else {
                            Snackbar.make(v, "현재 날짜보다 설정 날짜가 작습니다.", Snackbar.LENGTH_LONG).setAction("닫기", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).setActionTextColor(Color.RED).show();
                            return;
                        }
                    }

                    Toast.makeText(this, remained_hour + "시간 " + remained_minute + "분 후에 알람이 울립니다.", Toast.LENGTH_SHORT).show();

                    // 설정한 날짜가 적절하면 시작
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent_wakeup_service);
                    } else {
                        startService(intent_wakeup_service);
                    }
                    finish();

                }

                break;

            //알람 끄기
            case R.id.btn_alarm_off2:
                // 세팅 시간 없애기
                SharedPreferences sharedPreferences_alert_time = getSharedPreferences("WAKEUPTIME", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences_alert_time.edit();
                editor.putString("MINUTE", " - ");
                editor.putString("HOUR", " - ");
                editor.putString("DATE", nowtime_month + "월 " + nowtime_day + "일 ");
                editor.commit();

                // 현재 날짜로 초기화
                setting_day = Integer.parseInt(nowtime_day);
                setting_month = Integer.parseInt(nowtime_month);
                setting_year = Integer.parseInt(nowtime_year);

                Toast.makeText(getApplicationContext(), "알람기능이 꺼졌습니다.", Toast.LENGTH_SHORT).show();
                stopService(intent_wakeup_service);
                finish();
                break;
        }
    }

    // date picker dialog 값
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());

        // 하루 당겨진 월 처리
        int a = 1;
        month = month + a;
        txt_alert_timeset.setText(year + "년 " + month + "월 " + dayOfMonth + "일 알람이 울립니다.");

        SharedPreferences sharedPreferences_date = getSharedPreferences("WAKEUPTIME", MODE_PRIVATE);
        SharedPreferences.Editor editor_date = sharedPreferences_date.edit();
        editor_date.putString("DATE", currentDateString);
        editor_date.putInt("DATEYEAR", year);
        editor_date.putInt("DATEMONTH", month);
        editor_date.putInt("DATEDAY", dayOfMonth);
        editor_date.commit();

    }
}
