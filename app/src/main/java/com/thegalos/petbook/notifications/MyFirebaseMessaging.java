package com.thegalos.petbook.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.thegalos.petbook.MainActivity;
import com.thegalos.petbook.R;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    boolean passToFragment = true;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String sent = remoteMessage.getData().get("sent");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && sent.equals(user.getUid())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                sendOreoNotification(remoteMessage);
            else
                sendNotification(remoteMessage);
        }
    }

    private void sendOreoNotification(RemoteMessage remoteMessage) {
        SharedPreferences sp;
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("boolNotification",passToFragment);

        sp.edit().putString("ownerId", user).apply();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        OreoNotification oreoNotification = new OreoNotification(this);
        Notification.Builder builder = oreoNotification.getOreoNotification(title , body ,pendingIntent , defaultSound , icon) ;

        int i = 0;
        if (j>0)
            i=j;
        oreoNotification.getManger().notify(i,builder.build());
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        SharedPreferences sp;
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        String channelId = null;
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");
//        if (Build.VERSION.SDK_INT>=26) {
//            channelId="news_channel";
//            CharSequence channelName = "News channel";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel notificationChannel = new NotificationChannel(channelId,channelName,importance);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }


        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]",""));
        ////////////////////////////////////////////////////////////////////////////////////////////////////////
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("boolNotification",passToFragment);
        sp.edit().putString("ownerId", user).apply();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,j,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
//                .setSmallIcon(Integer.parseInt(icon))
                .setSmallIcon(R.drawable.pet_logo_new)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int i = 0;
        if (j>0)
            i=j;
        notificationManager.notify(i,builder.build());
    }
}
