package com.github.chagall.notificationlistenerexample;

import android.content.Intent;
import android.app.Notification;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

/**
 * MIT License
 *
 *  Copyright (c) 2016 Fábio Alves Martins Pereira (Chagall)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

//이게 돌아가는 원리를 이해해보기
public class NotificationListenerExampleService extends NotificationListenerService {

    /*
        These are the package names of the apps. for which we want to
        listen the notifications
     */
    private static final class ApplicationPackageNames {
        public static final String SAMSUNGPAY_PACK_NAME = "com.samsung.android.spay";
        public static final String KAKAOBANK_PACK_NAME = "com.kakaobank.channel";
    }

    /*
        These are the return codes we use in the method which intercepts
        the notifications, to decide whether we should do something or not
     */
    public static final class InterceptedNotificationCode {
        public static final int SAMSUNGPAY_PACK_CODE = 4;
        public static final int KAKAOBANK_PACK_CODE = 5;
        public static final int OTHER_NOTIFICATIONS_CODE = 6; // We ignore all notification with code == 4
    }

    private int matchNotificationCode(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();

        if (packageName.equals(ApplicationPackageNames.SAMSUNGPAY_PACK_NAME)) {
            return (InterceptedNotificationCode.SAMSUNGPAY_PACK_CODE);
        } else if (packageName.equals(ApplicationPackageNames.KAKAOBANK_PACK_NAME)) {
            return (InterceptedNotificationCode.KAKAOBANK_PACK_CODE);
        } else {
            return (InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE);
        }
    }

    // send notification
    private void sendNotificationToSheets(StatusBarNotification sbn){
        String packageName = sbn.getPackageName();
        long postTime = sbn.getPostTime();
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        String title = extras.getString(Notification.EXTRA_TITLE);
        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
    }

    // 이게 굳이 왜 필요한지 아직 모르겠음
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        int notificationCode = matchNotificationCode(sbn);

        if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {


            Log.i("NotificationListener", "[snowdeer] onNotificationPosted() - " + sbn.toString());
            Log.i("NotificationListener", "[snowdeer] PackageName:" + sbn.getPackageName());
            Log.i("NotificationListener", "[snowdeer] PostTime:" + sbn.getPostTime());

            Notification notification = sbn.getNotification();
            Bundle extras = notification.extras;
            String title = extras.getString(Notification.EXTRA_TITLE);
            CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
            CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);

            Log.i("NotificationListener", "[snowdeer] Title:" + title);
            Log.i("NotificationListener", "[snowdeer] Text:" + text);
            Log.i("NotificationListener", "[snowdeer] Sub Text:" + subText);
        }
    }

    //
//
//        int notificationCode = matchNotificationCode(sbn);
//
//        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE){
//            Intent intent = new  Intent("com.github.chagall.notificationlistenerexample");
//            intent.putExtra("Notification Code", notificationCode);
//            sendBroadcast(intent);
//        }
//
//    }
//
//    @Override
//    public void onNotificationRemoved(StatusBarNotification sbn){
//        int notificationCode = matchNotificationCode(sbn);
//
//        if(notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {
//
//            StatusBarNotification[] activeNotifications = this.getActiveNotifications();
//
//            if(activeNotifications != null && activeNotifications.length > 0) {
//                for (int i = 0; i < activeNotifications.length; i++) {
//                    if (notificationCode == matchNotificationCode(activeNotifications[i])) {
//                        Intent intent = new  Intent("com.github.chagall.notificationlistenerexample");
//                        intent.putExtra("Notification Code", notificationCode);
//                        sendBroadcast(intent);
//                        break;
//                    }
//                }
//            }
//        }
//    }


    //모든 notification을 다 받으면 혼란스러움... 일단 필터링을 하기


}

