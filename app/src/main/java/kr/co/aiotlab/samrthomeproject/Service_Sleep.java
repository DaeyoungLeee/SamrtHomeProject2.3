package kr.co.aiotlab.samrthomeproject;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static kr.co.aiotlab.samrthomeproject.App.CHANNEL_ID_1;
import static kr.co.aiotlab.samrthomeproject.MainActivity.weBView;

public class Service_Sleep extends Service {
    private static final String TAG = "Service";
    private Thread thread;
    private Date date;
    private String nowtime;
    private String setHour;
    private String setMinute;
    private String ip_address, text1, nowSwitchState;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // Service 실행중이면 상태표시바에 어떻게 보일지 표시해주는 세팅
        Intent notificationIntent =  new Intent(this, MainActivity.class);
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
        }
        else {
            builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent);
        }


        startForeground(1, builder.build());

        NowTimeBackThread thread_time = new NowTimeBackThread();
        thread_time.setDaemon(true);
        thread_time.start();

        if (thread == null){
            thread = new Thread(new BackThread());
            Log.d(TAG, "BackThread Start");
            thread.start();
        }
        return START_STICKY;
    }

    class BackThread implements Runnable {
        @Override
        public void run() {
            while (true) {

                SharedPreferences sharedPreferences = getSharedPreferences("SLEEP", MODE_PRIVATE);
                setHour = sharedPreferences.getString("HOUR", "default");
                setMinute = sharedPreferences.getString("MINUTE", "default");

                long now = System.currentTimeMillis();
                date = new Date(now);
                SimpleDateFormat simpleDateFormat_hour_minute = new SimpleDateFormat("HH:mm");
                nowtime = simpleDateFormat_hour_minute.format(date);
                //Code를 받아 실행해야하기 때문에 Handler 사용해준다. 여기서 Looper.getMainLooper를 사용해주어야 한다. 쓰레드 간 메시지 전달
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, nowtime + "   "  + setHour + ":" + setMinute);

                        if (nowtime.equals(setHour + ":" + setMinute)){
                            Log.d(TAG, "same");

                            SharedPreferences ip = getSharedPreferences("IP_ADDRESS", MODE_PRIVATE);
                            ip_address = ip.getString("IP", "0");

                            new Thread() {
                                public void run() {
                                    nowSwitchState = getNowState();
                                    //web검색 이후 1초 기다리기
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    Message msg = handler2.obtainMessage();
                                    handler2.sendMessage(msg);

                                }
                            }.start();
                        }
                    }
                });
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return; // 인터럽트 받을 경우 return됨
                }
            }
        }
    }

    //백엔드 쓰레드로 현재시간 표시
    class NowTimeBackThread extends Thread {
        @Override
        public void run() {
            while (true) {
                handler.sendEmptyMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                long now = System.currentTimeMillis();
                date = new Date(now);
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                nowtime = dateFormat.format(date);
            }
        }
    };


    // 현재 상태 읽어오는 함수
    private String getNowState() {
        try {
            Connection.Response response1 = Jsoup
                    .connect("http://" + ip_address + "/status")
                    .method(Connection.Method.GET)
                    .execute();

            //web검색 이후 1초 기다리기
            Thread.sleep(1000);

            Document document = response1.parse();
            text1 = document.text();

            Log.d("MainActivity", "getNowState1 : " + text1);


        } catch (IOException ignored) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return text1;
    }


    //핸들러

    @SuppressLint("HandlerLeak")
    final Handler handler2 = new Handler(){
        public void handleMessage(Message msg){
            if (nowSwitchState.equals("{'rl':'0','ml':'0','bl':'0','fan':'0'}")){
                Log.d(TAG, "turn off!!");

                weBView.loadUrl("http:/" + ip_address + "/room_light");

            }
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        // serviceStop이 호출될 시

        if (thread != null){
            thread.interrupt();
            thread = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
