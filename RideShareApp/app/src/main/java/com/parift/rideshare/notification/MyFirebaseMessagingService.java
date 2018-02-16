package com.parift.rideshare.notification;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;


import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.parift.rideshare.R;
import com.parift.rideshare.activity.SplashScreenActivity;
import com.parift.rideshare.helper.Logger;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    public static final String TAG = MyFirebaseMessagingService.class.getName();
    private RemoteMessage mRemoteMessage;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        mRemoteMessage = remoteMessage;
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Logger.debug(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Logger.debug(TAG, "Message data payload: " + remoteMessage.getData());
            //handleNow();
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Logger.debug(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        //NOTE - If you want to customize the notification, then write your own custom notification in handleNow
        //else Notification manager default behavior is to show up the notification with Title and Body and onClick
        //it will launch the main activity.
        //Default behavior of notification, is to show up notification when app is in background and
        //no notification when its running in foreground
        //For my requirement, default is fine

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        handleNow();
    }
    // [END receive_message]



    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Logger.debug(TAG, "Short lived task is done.");
        sendNotification();
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     */
    private void sendNotification() {
        Logger.debug(TAG,"Send Notification Method");
        Intent intent = new Intent(this, SplashScreenActivity.class);
        //Disabling this as we will open up standard application
        //Otherwise this intent extra can be used to make decision which fragment to load once home page activity is loaded
        //So by default we need to always launch the Splash activity and once home page is loaded, depending on notification type
        //we can load different fragment
        //intent.putExtra(getPackageName()+ Constant.INTENT_EXTRA_NOTIFICATION_TYPE_KEY, NotificationType.RideInfo);
        //This is causing removal of extras which we are setting here, so commented it
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(mRemoteMessage.getNotification().getTitle())
                        .setContentText(mRemoteMessage.getNotification().getBody())
                        .setAutoCancel(true)
                        .setColor(getResources().getColor(R.color.colorPrimary))
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //Lets show notification for all scenario irrespective of its in foreground or background
        notificationManager.notify(NotificationID.getID() /* ID of notification */, notificationBuilder.build());

        if (isAppIsInBackground(getApplicationContext())){
            Logger.debug(TAG,"App is in Background");
            //We can customize notification depending on the state of application
        } else {
            Logger.debug(TAG,"App is in Foreground");
        }
    }

    private boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}