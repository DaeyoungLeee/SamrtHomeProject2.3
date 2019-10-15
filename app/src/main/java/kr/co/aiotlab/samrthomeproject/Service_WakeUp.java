package kr.co.aiotlab.samrthomeproject;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import static kr.co.aiotlab.samrthomeproject.MainActivity.ip_address;
import static kr.co.aiotlab.samrthomeproject.MainActivity.weBView;
import static kr.co.aiotlab.samrthomeproject.SetWakeUp.hour;
import static kr.co.aiotlab.samrthomeproject.SetWakeUp.minute;
import static kr.co.aiotlab.samrthomeproject.SetWakeUp.setting_day;
import static kr.co.aiotlab.samrthomeproject.SetWakeUp.setting_month;
import static kr.co.aiotlab.samrthomeproject.SetWakeUp.setting_year;

public class Service_WakeUp extends Service {

    private Date nowdate;
    private String nowtime_year, nowtime_month, nowtime_day, nowtime_hour, nowtime_minute;
    public static final String TAG = "Service_WakeUp";
    private Thread thread;
    public static int flag2 = 1;

    @SuppressLint("InvalidWakeLockTag")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PowerManager.WakeLock wakeLock = null;

        if (wakeLock == null) {
            PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakelock");
            wakeLock.acquire();
            Log.d(TAG, "onStartCommand: powermanager on");
        }
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }

        // Service 실행중이면 상태표시바에 어떻게 보일지 표시해주는 세팅
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, notificationIntent, 0);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.activity_setalert);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26 && Build.VERSION.SDK_INT < 29) {
            String CHANNEL_ID = "smarthome_service_channel_Id";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "SnowDeer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.button_on2)
                    .setContentIntent(pendingIntent);

        }else if (Build.VERSION.SDK_INT >= 29) {
            String CHANNEL_ID = "smarthome_service_channel_Id";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "SnowDeer Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.button_on2)
                    .setContent(remoteViews)
                    .setContentIntent(pendingIntent);
        } else {
            builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent);
        }


        startForeground(1, builder.build());

        flag2 = 1;

        if (thread == null) {

            thread = new Thread(new AlertAutoThread());
            Log.d(TAG, "BackThread Start");
            thread.start();
        }

        return START_STICKY;
    }

    class AlertAutoThread implements Runnable {
        @Override
        public void run() {
            int i = 0;
            while (true) {
                // 쓰레드의 동작 현황을 확인하기 위한 test code
                i++;
                Log.d(TAG, "" + i);

                //Code를 받아 실행해야하기 때문에 Handler 사용해준다. 여기서 Looper.getMainLooper를 사용해주어야 한다. 쓰레드 간 메시지 전달
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {

                        // 현재 시간 실시간 측정
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

                        Log.d(TAG, "nowTime: "
                                + nowtime_year
                                + nowtime_month
                                + nowtime_day
                                + nowtime_hour
                                + nowtime_minute);
                        Log.d(TAG, "SetTime: "
                                + setting_year
                                + setting_month
                                + setting_day
                                + hour
                                + minute);
                        new Thread() {
                            public void run() {
                                Message msg = handler2.obtainMessage();
                                handler2.sendMessage(msg);
                            }
                        }.start();
                    }
                });
                try {
                    Thread.sleep(700);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return; // 인터럽트 받을 경우 return됨
                }

            }
        }
    }

    @SuppressLint("HandlerLeak")
    final Handler handler2 = new Handler() {
        @SuppressLint("ResourceType")
        public void handleMessage(Message msg) {
            Log.d(TAG, "현재시간:"
                    + Integer.parseInt(nowtime_year )
                    + "/" + Integer.parseInt(nowtime_month )
                    + "/" + Integer.parseInt(nowtime_day )
                    + " " + Integer.parseInt(nowtime_hour )
                    + ":" + Integer.parseInt(nowtime_minute )
                    + "설정 시간:"
                    + setting_year
                    + "/" + setting_month
                    + "/" + setting_day
                    + " " + hour
                    + ":" + minute);

            //  설정한 날짜와 현재 날짜가 같으면 알람
            if (setting_year == Integer.parseInt(nowtime_year)
                    && setting_month == Integer.parseInt(nowtime_month)
                    && setting_day == Integer.parseInt(nowtime_day)
                    && hour == Integer.parseInt(nowtime_hour)
                    && minute == Integer.parseInt(nowtime_minute)) {

                weBView.loadUrl("http://" + ip_address + "/room_light");

                if (flag2 == 1) {
                    Intent intent = new Intent(getApplicationContext(), AlertStopActivity.class);
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    flag2 = 0;
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // serviceStop이 호출될 시

        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
