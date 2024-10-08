package com.unty.chat.firebase;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.unty.chat.R;
import com.unty.chat.activities.ChatActivity;
import com.unty.chat.models.User;
import com.unty.chat.utilities.Constants;

import java.util.Random;

public class MessagingService extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Token: " + token);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Log.d("FCM","Message: " + remoteMessage.getNotification().getBody());
        User user=new User();
        user.id=remoteMessage.getData().get(Constants.KEY_USER_ID);
        user.name=remoteMessage.getData().get(Constants.KEY_NAME);
        user.token=remoteMessage.getData().get(Constants.KEY_FCM_TOKEN);

        int notificationId=new Random().nextInt();
        String channelId="chat_message";

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(Constants.KEY_USER,user);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,0);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,channelId);
        builder.setSmallIcon(R.drawable.ic_notification);
        builder.setContentTitle(user.name);
        builder.setContentText(remoteMessage.getData().get(Constants.KEY_MESSAGE));
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(
                remoteMessage.getData().get(Constants.KEY_MESSAGE)
        ));
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence channelName = "Chat Message";
            String channelDescription="This notification channel is used for chat message notifications";
            int importance= NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel= new NotificationChannel(channelId,channelName,importance);
            channel.setDescription(channelDescription);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCompat= NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId, builder.build());
    }
}
