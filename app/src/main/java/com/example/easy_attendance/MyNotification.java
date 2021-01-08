package com.example.easy_attendance;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class MyNotification extends Application {
    public static final String CHANNEL_Reset_Password = "channelResetPassword";
    public static final String CHANNEL_Update_HourlyPay = "channelUpdateHourlyPay";



    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel resetPassword = new NotificationChannel(CHANNEL_Reset_Password, "Reset Password", NotificationManager.IMPORTANCE_DEFAULT);
            resetPassword.setDescription("Reset Password Notification");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(resetPassword);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel updatePayment = new NotificationChannel(CHANNEL_Update_HourlyPay, "Update Hourly Pay", NotificationManager.IMPORTANCE_DEFAULT);
            updatePayment.setDescription("Update Hourly Payment Notification");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(updatePayment);
        }
    }
}