package kr.co.aiotlab.samrthomeproject;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class App extends Application {
    public static final String CHANNEL_ID_1 ="service channel 1";
    public static final String CHANNEL_ID_2 ="service channel 2";
    public static final String CHANNEL_ID_NAME_1 ="service channel 1";
    public static final String CHANNEL_ID_NAME_2 ="service channel 2";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel_1= new NotificationChannel(
                    CHANNEL_ID_1,
                    CHANNEL_ID_NAME_1,
                    NotificationManager.IMPORTANCE_LOW

            );


            NotificationChannel serviceChannel_2= new NotificationChannel(
                    CHANNEL_ID_2,
                    CHANNEL_ID_NAME_2,
                    NotificationManager.IMPORTANCE_LOW
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel_1);
            manager.createNotificationChannel(serviceChannel_2);

        }
    }
}

