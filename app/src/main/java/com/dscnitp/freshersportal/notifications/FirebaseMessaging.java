package com.dscnitp.freshersportal.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.dscnitp.freshersportal.ChatActivity;
import com.dscnitp.freshersportal.MainActivity;
import com.dscnitp.freshersportal.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Random;

public class FirebaseMessaging extends FirebaseMessagingService {

    private static final String ADMIN_CHANNEL_ID ="admin_channel" ;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SharedPreferences sp=getSharedPreferences("SP_USER",MODE_PRIVATE);
        String savecurrentuser=sp.getString("CURRENT_USERID", "None");

        String notificationType=remoteMessage.getData().get("notificationType");
        if(notificationType.equals("ChatNotification")){
            String sent=remoteMessage.getData().get("sent");
            String user=remoteMessage.getData().get("user");
            FirebaseUser user1= FirebaseAuth.getInstance().getCurrentUser();
            if(user1!=null&&sent.equals(user1.getUid())){
                if(!savecurrentuser.equals(user)){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        sendAboveNotificationd(remoteMessage);
                    }
                    else {
                        sendNormalNotification(remoteMessage);
                    }
                }
            }
        }
        if(notificationType.equals("ChaNotification")){
            String sent=remoteMessage.getData().get("sent");
            String user=remoteMessage.getData().get("user");
            FirebaseUser user1= FirebaseAuth.getInstance().getCurrentUser();
            if(user1!=null&&sent.equals(user1.getUid())){
                if(!savecurrentuser.equals(user)){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        sendboveNotificationd(remoteMessage);
                    }
                    else {
                        sendormalNotification(remoteMessage);
                    }
                }
            }
        }

        if(notificationType.equals("Chaotification")){
            String sent=remoteMessage.getData().get("sent");
            String user=remoteMessage.getData().get("user");
            FirebaseUser user1= FirebaseAuth.getInstance().getCurrentUser();
            if(user1!=null&&sent.equals(user1.getUid())){
                if(!savecurrentuser.equals(user)){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        sendoveNotificationd(remoteMessage);
                    }
                    else {
                        sendrmalNotification(remoteMessage);
                    }
                }
            }
        }

        else if(notificationType.equals("GroupChatNotification")){
            String sent=remoteMessage.getData().get("sent");
            String user=remoteMessage.getData().get("user");
            FirebaseUser user1 = FirebaseAuth.getInstance().getCurrentUser();
            if(user1!=null&&sent.equals(user1.getUid())){
                if(!savecurrentuser.equals(user)){
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        sendAboveNotificationd(remoteMessage);
                    }
                    else {
                        sendNormalNotification(remoteMessage);
                    }
                }
            }
        }
        else if(notificationType.equals("POST_NOTIFICATION")){
            String sender=remoteMessage.getData().get("sender");
            String pId=remoteMessage.getData().get("pId");
            String pTitle=remoteMessage.getData().get("pTitle");
            String pDescription=remoteMessage.getData().get("pDescription");
            if(!sender.equals(savecurrentuser)){
                showPostNotification(""+pId,""+pTitle,""+pDescription);
            }
        }
    }


    private void showPostNotification(String pid, String title, String description) {

        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        int notificationId=new Random().nextInt(3000);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            setUpPostNotification(notificationManager);
        }
        Intent intent=new Intent(this, MainActivity.class);
        intent.putExtra("pid",pid);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent intent1=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Bitmap largeIcon= BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        Uri notificationUri=RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,""+ADMIN_CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setLargeIcon(largeIcon)
                .setContentTitle(title)
                .setContentText(description)
                .setSound(notificationUri)
                .setContentIntent(intent1);

        notificationManager.notify(notificationId,builder.build());
    }

    private void setUpPostNotification(NotificationManager notificationManager) {
        CharSequence sequence="New Notification";
        String channelDescription="Device To Device Post Notification";
        NotificationChannel channel=new NotificationChannel(ADMIN_CHANNEL_ID,sequence,NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(channelDescription);
        channel.enableLights(true);
        channel.setLightColor(Color.RED);
        channel.enableVibration(true);
        if(notificationManager!=null){
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void sendboveNotificationd(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, MainActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sounduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoAndAboveNotification oreoAndAboveNotification=new OreoAndAboveNotification(this);
        Notification.Builder builder=oreoAndAboveNotification.getOnNotification(title,body,pendingIntent,sounduri,icon);

        int j=0;
        if(i>0){
            j=1;
        }
        oreoAndAboveNotification.getNotificationManager().notify(j,builder.build());

    }
    private void sendormalNotification(RemoteMessage remoteMessage) {

        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, MainActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sounduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sounduri)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){
            j=1;
        }

        notificationManager.notify(j,builder.build());


    }


    private void sendoveNotificationd(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, MainActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sounduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoAndAboveNotification oreoAndAboveNotification=new OreoAndAboveNotification(this);
        Notification.Builder builder=oreoAndAboveNotification.getOnNotification(title,body,pendingIntent,sounduri,icon);

        int j=0;
        if(i>0){
            j=1;
        }
        oreoAndAboveNotification.getNotificationManager().notify(j,builder.build());

    }
    private void sendrmalNotification(RemoteMessage remoteMessage) {

        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, MainActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sounduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sounduri)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){
            j=1;
        }

        notificationManager.notify(j,builder.build());


    }



    private void sendAboveNotificationd(RemoteMessage remoteMessage) {
        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sounduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        OreoAndAboveNotification oreoAndAboveNotification=new OreoAndAboveNotification(this);
        Notification.Builder builder=oreoAndAboveNotification.getOnNotification(title,body,pendingIntent,sounduri,icon);

        int j=0;
        if(i>0){
            j=1;
        }
        oreoAndAboveNotification.getNotificationManager().notify(j,builder.build());

    }
    private void sendNormalNotification(RemoteMessage remoteMessage) {

        String user=remoteMessage.getData().get("user");
        String icon=remoteMessage.getData().get("icon");
        String title=remoteMessage.getData().get("title");
        String body=remoteMessage.getData().get("body");
        RemoteMessage.Notification notification=remoteMessage.getNotification();
        int i=Integer.parseInt(user.replaceAll("[\\D]",""));
        Intent intent=new Intent(this, ChatActivity.class);
        Bundle bundle=new Bundle();
        bundle.putString("uid",user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,i,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri sounduri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSound(sounduri)
                .setAutoCancel(true)
                .setSmallIcon(Integer.parseInt(icon));
        NotificationManager notificationManager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        int j=0;
        if(i>0){
            j=1;
        }

        notificationManager.notify(j,builder.build());


    }

    String uid;
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            uid=firebaseUser.getUid();
            updateToken(s);
        }
    }

    private void updateToken(String token) {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Tokens");
        Token tokena=new Token(token);
        reference.child(user.getUid()).setValue(tokena);
        DatabaseReference references= FirebaseDatabase.getInstance().getReference("Users").child(uid);
        references.child("device_token").setValue(tokena);

    }
}
