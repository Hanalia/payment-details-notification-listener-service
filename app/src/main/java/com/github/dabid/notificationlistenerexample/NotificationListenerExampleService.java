package com.github.dabid.notificationlistenerexample;

import android.content.Intent;
import android.app.Notification;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


//이게 돌아가는 원리를 이해해보기
public class NotificationListenerExampleService extends NotificationListenerService {


    static RequestQueue requestQueue;

    public void makeRequest() {
        // create requestQueue instance in case there is no instance
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        String url = "https://jsonplaceholder.typicode.com/todos/1";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("response",response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "Error occurred ", error);
                    }
                }) {
        };

        jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);
    }

    public void makePostRequest(StatusBarNotification sbn) throws JSONException {
        // get the details from sbn object
        String packageName = sbn.getPackageName();
        long postTime = sbn.getPostTime();
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        String title = Objects.toString(extras.getString(Notification.EXTRA_TITLE),"");
        CharSequence text = Objects.toString(extras.getCharSequence(Notification.EXTRA_TEXT),"");
        CharSequence subText = Objects.toString(extras.getCharSequence(Notification.EXTRA_SUB_TEXT),"");

        // the target sheetId in which the data is placed
        String sheetId = BuildConfig.sheetId;

        //convert the details to jsonBody
        JSONObject jsonBody = new JSONObject()
                .put("packageName",packageName)
                .put("postTime",postTime)
                .put("title",title)
                .put("text",text)
                .put("subText",subText)
                .put("sheetId",sheetId);


        String myjson = jsonBody.toString();
        Log.i("info",myjson);
        // create requestQueue instance in case there is no instance
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(this);
        }
        String scriptId = BuildConfig.scriptId;

        String url = String.format( "https://script.google.com/macros/s/%s/exec" , scriptId);
        Log.i("info",url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url,jsonBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("response",response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERROR", "Error occurred ", error);
                    }
                }) {
        };

        jsonObjectRequest.setShouldCache(false);
        requestQueue.add(jsonObjectRequest);
    }

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
        public static final int OTHER_NOTIFICATIONS_CODE = 0; // We ignore all notification with code == 4
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


    // 이게 굳이 왜 필요한지 아직 모르겠음
    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        int notificationCode = matchNotificationCode(sbn);

        if (notificationCode != InterceptedNotificationCode.OTHER_NOTIFICATIONS_CODE) {

            try {
                makePostRequest(sbn);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
    }


}

