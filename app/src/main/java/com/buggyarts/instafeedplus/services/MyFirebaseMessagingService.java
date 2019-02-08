package com.buggyarts.instafeedplus.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.widget.RemoteViews;

import com.buggyarts.instafeedplus.BrowserActivity;
import com.buggyarts.instafeedplus.HomeTabActivity;
import com.buggyarts.instafeedplus.Models.IFNotificationObject;
import com.buggyarts.instafeedplus.R;
import com.buggyarts.instafeedplus.customClasses.ForegroundCheckTask;
import com.buggyarts.instafeedplus.customClasses.GlideApp;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);

        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//        sendRegistrationToServer(token);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

//        Log.d(TAG, "From: " + remoteMessage.toString());
        if (remoteMessage.getData() != null) {
//            Log.d(TAG, "From: " + remoteMessage.getData().toString());
            if (remoteMessage.getData().get("body") != null) {
                if (remoteMessage.getData().get("body").length() > 0) {
                    IFNotificationObject notifObject = new IFNotificationObject(remoteMessage.getData(), this);
                    sendNotification(notifObject);
                    Log.d(TAG, "onMessageReceived: " + notifObject.getTitle());
                }
            }
        }
    }


    private void sendNotification(final IFNotificationObject notificationObject) {

        final PendingIntent pendingIntent = getResultIntent(notificationObject);

        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Long tsLong = System.currentTimeMillis();
        final Integer notificationID = tsLong.intValue();

        if (notificationObject.getLargeIcon() != null) {
            if (notificationObject.getLargeIcon().length() > 0) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        GlideApp.with(getApplicationContext())
                                .asBitmap()
                                .load(notificationObject.getLargeIcon())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                                        Notification.BigPictureStyle style = new Notification.BigPictureStyle()
                                                .bigPicture(resource);

                                        Notification.Builder nBuilder = new Notification.Builder(MyFirebaseMessagingService.this);

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                            final String time = new SimpleDateFormat("h:mm a").format(new Date()).toString();

                                            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.if_notification_collapsed);
                                            contentView.setTextViewText(R.id.title,notificationObject.getTitle());
                                            contentView.setTextViewText(R.id.text,notificationObject.getBody());
                                            contentView.setImageViewBitmap(R.id.notif_image,resource);
                                            contentView.setTextViewText(R.id.header_text,time);
                                            nBuilder.setCustomContentView(contentView)
                                                    .setSmallIcon(R.drawable.notification_icon)
                                                    .setShowWhen(true)
                                                    .setAutoCancel(true)
                                                    .setSound(defaultSoundUri)
                                                    .setContentIntent(pendingIntent);

                                            RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.if_notification_expanded);
                                            expandedView.setTextViewText(R.id.title,notificationObject.getTitle());
                                            expandedView.setTextViewText(R.id.text,notificationObject.getBody());
                                            expandedView.setImageViewBitmap(R.id.notif_image,resource);
                                            expandedView.setTextViewText(R.id.header_text,time);
                                            nBuilder.setCustomBigContentView(expandedView);

                                        }else {

                                                nBuilder.setContentTitle(notificationObject.getTitle())
                                                    .setContentText(Html.fromHtml(notificationObject.getBody()))
                                                    .setSmallIcon(R.drawable.notification_icon)
                                                    .setStyle(style)
                                                    .setShowWhen(true)
                                                    .setAutoCancel(true)
                                                    .setSound(defaultSoundUri)
                                                    .setContentIntent(pendingIntent);

                                        }

                                        goToNotificationBuilder(nBuilder, notificationID, notificationObject);
                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        launchDefaultNotification(pendingIntent, notificationObject, notificationID);
                                    }
                                });
                    }
                });

            } else {
                launchWithIcon(pendingIntent, notificationObject, notificationID);
            }
        } else {
            launchWithIcon(pendingIntent, notificationObject, notificationID);
        }
    }

    private void launchWithIcon(final PendingIntent pendingIntent,
                                          final IFNotificationObject notificationObject,
                                          final Integer notificationID) {
        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        if (notificationObject.getIcon() != null) {
            if (notificationObject.getIcon().length() > 0) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        GlideApp.with(getApplicationContext())
                                .asBitmap()
                                .load(notificationObject.getIcon())
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                                        Notification.Builder nBuilder = new Notification.Builder(MyFirebaseMessagingService.this);

                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                                            final String time = new SimpleDateFormat("h:mm a").format(new Date()).toString();

                                            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.if_notification_collapsed);
                                            contentView.setTextViewText(R.id.title,notificationObject.getTitle());
                                            contentView.setTextViewText(R.id.text,notificationObject.getBody());
                                            contentView.setImageViewBitmap(R.id.notif_image,resource);
                                            contentView.setTextViewText(R.id.header_text,time);
                                            nBuilder.setCustomContentView(contentView)
                                                    .setSmallIcon(R.drawable.notification_icon)
                                                    .setAutoCancel(true)
                                                    .setShowWhen(true)
                                                    .setSound(defaultSoundUri)
                                                    .setContentIntent(pendingIntent);

                                        }else {

                                            nBuilder.setContentTitle(notificationObject.getTitle())
                                                    .setContentText(Html.fromHtml(notificationObject.getBody()))
                                                    .setSmallIcon(R.drawable.notification_icon)
                                                    .setLargeIcon(resource)
                                                    .setStyle(new Notification.BigTextStyle()
                                                            .setBigContentTitle(notificationObject.getTitle())
                                                            .bigText(Html.fromHtml(notificationObject.getBody())))
                                                    .setAutoCancel(true)
                                                    .setSound(defaultSoundUri)
                                                    .setContentIntent(pendingIntent);
                                        }
                                        goToNotificationBuilder(nBuilder, notificationID, notificationObject);

                                    }

                                    @Override
                                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                        launchDefaultNotification(pendingIntent, notificationObject, notificationID);
                                    }
                                });
                    }
                });
            } else {
                launchDefaultNotifWithIcon(pendingIntent, notificationObject, notificationID);
            }
        } else {
            launchDefaultNotifWithIcon(pendingIntent, notificationObject, notificationID);
        }
    }

    private void launchDefaultNotifWithIcon(final PendingIntent pendingIntent,
                                            final IFNotificationObject notificationObject,
                                            final Integer notificationID) {
        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                GlideApp.with(getApplicationContext())
                        .asBitmap()
                        .load("https://drive.google.com/uc?export=view&id=1y8k4nWIiqLZVSKwC0mIIYzK-lQ4_TLKv")
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {

                                Notification.Builder nBuilder = new Notification.Builder(MyFirebaseMessagingService.this);

                                nBuilder.setContentTitle(notificationObject.getTitle())
                                        .setContentText(Html.fromHtml(notificationObject.getBody()))
                                        .setSmallIcon(R.drawable.notification_icon)
                                        .setLargeIcon(resource)
                                        .setStyle(new Notification.BigTextStyle()
                                                .setBigContentTitle(notificationObject.getTitle())
                                                .bigText(Html.fromHtml(notificationObject.getBody())))
                                        .setAutoCancel(true)
                                        .setShowWhen(true)
                                        .setSound(defaultSoundUri)
                                        .setContentIntent(pendingIntent);

                                goToNotificationBuilder(nBuilder, notificationID, notificationObject);

                            }

                            @Override
                            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                launchDefaultNotification(pendingIntent, notificationObject, notificationID);
                            }
                        });
            }
        });
    }

    private void launchDefaultNotification(PendingIntent pendingIntent,
                                           IFNotificationObject notificationObject,
                                           final Integer notificationID) {

        final Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder nBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(notificationObject.getTitle())
                .setContentText(Html.fromHtml(notificationObject.getBody()))
                .setStyle(new Notification.BigTextStyle()
                        .setBigContentTitle(notificationObject.getTitle())
                        .bigText(Html.fromHtml(notificationObject.getBody())))
                .setAutoCancel(true)
                .setShowWhen(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        goToNotificationBuilder(nBuilder, notificationID, notificationObject);

    }

    public void goToNotificationBuilder(Notification.Builder notification,
                                              Integer notificationID, IFNotificationObject notificationObject) {

        Log.v(TAG, "notificationID" + notificationID.toString());

//        Intent intentAction2 = new Intent(MyFirebaseMessagingService.this, ActionReceiver.class);
//        intentAction2.putExtra(getResources().getString(R.string.notification_action), getResources().getString(R.string.notification_action_2));
//        intentAction2.putExtra("feedstring", feedString);
//        intentAction2.putExtra(getResources().getString(R.string.notification_feed), feedString);
//        intentAction2.putExtra(getResources().getString(R.string.notification_id), notificationID);
//        PendingIntent pIntentIgnore = PendingIntent.getBroadcast(MyFirebaseMessagingService.this, new Random().nextInt(150) * 37, intentAction2, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        notification.addAction(R.drawable.if_clear, "Ignore", pIntentIgnore);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification.setChannelId(notificationObject.getChannelID());

            CharSequence name = getString(R.string.notifications_primary_channel_name);
            String description = getString(R.string.notifications_primary_channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(notificationObject.getChannelID(), name, importance);
            channel.setDescription(description);
            notificationManager.createNotificationChannel(channel);

        }else{
            if (notificationObject.getChannelID().equals(getResources().getString(R.string.notification_channel_urgent))) {
                notification.setPriority(Notification.PRIORITY_HIGH);
            }else if (notificationObject.getChannelID().equals(getResources().getString(R.string.notification_channel_medium))) {
                notification.setPriority(Notification.PRIORITY_DEFAULT);
            }else if (notificationObject.getChannelID().equals(getResources().getString(R.string.notification_channel_low))) {
                notification.setPriority(Notification.PRIORITY_LOW);
            }else{
                notification.setPriority(Notification.PRIORITY_DEFAULT);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setColor(getResources().getColor(R.color.themeColorSecondaryDark));
        }


        try {
            notificationManager.notify(notificationID, notification.build());

        }catch (Exception e){
            Log.v(TAG, e.toString());
        }
    }

    private PendingIntent getResultIntent(IFNotificationObject notificationObject) {

        Intent intent;
        try {
            boolean foreGround = new ForegroundCheckTask().execute(this).get();

            if (foreGround) {
                intent = getResolvedIntent(notificationObject);
            } else {
                intent = new Intent(this, HomeTabActivity.class);
            }
        } catch (Exception e) {
            intent = new Intent(this, HomeTabActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        Gson gson = new Gson();
        intent.putExtra(getResources().getString(R.string.is_notification), true);
        intent.putExtra(getResources().getString(R.string.notification_object), gson.toJson(notificationObject));

        int iUniqueId = (int) (System.currentTimeMillis() & 0xfffffff);

        return PendingIntent.getActivity(this, iUniqueId /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Intent getResolvedIntent(IFNotificationObject notificationObject){

        Intent intent = new Intent(this, HomeTabActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        if(notificationObject.getType().equals("0")){
            if(notificationObject.getDataUrl() != null) {
                if (notificationObject.getDataUrl().length() > 0) {
                    intent = new Intent(this, BrowserActivity.class);
                    intent.putExtra("visit", notificationObject.getDataUrl());
                }
            }
        }else if(notificationObject.getType().equals("1")){
            intent.putExtra(getResources().getString(R.string.notification_type_cards), notificationObject.getTypeIndex());
        }else if(notificationObject.getType().equals("2")){
            intent.putExtra(getResources().getString(R.string.notification_type_trending), notificationObject.getTypeIndex());
        }else if(notificationObject.getType().equals("3")){
            intent.putExtra(getResources().getString(R.string.notification_type_magazine), notificationObject.getTypeIndex());
        }else if(notificationObject.getType().equals("4")){
            intent.putExtra(getResources().getString(R.string.notification_type_category), notificationObject.getTypeIndex());
        }

        return intent;
    }

}
