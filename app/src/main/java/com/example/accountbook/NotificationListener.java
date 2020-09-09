package com.example.accountbook;

import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.StringTokenizer;

/**
 * Created by Kim So Hyeon on 2019-10-21.
 */

public class NotificationListener extends NotificationListenerService {

    static TextView tv, tv2, tv3;
//    String stralarm;
//    Notification ntf;
//    int i = 0;
    final String nametitle = "카카오페이";

    private static final String TAG = NotificationListener.class.getSimpleName();

    public NotificationListener() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.w("Created! : ", "Notification Listener created!");
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void addNotification(StatusBarNotification sbn, boolean updateDash) {
        if (sbn == null) return;

        Notification mNotification = sbn.getNotification();
        Bundle extras = mNotification.extras;
        String from = null;
        String text = null;

        //from = extras.getString(Notification.EXTRA_TITLE);
        //text = extras.getString(Notification.EXTRA_TEXT);

        //tv.setText("Title: " + from + ", Text: " + text);           // Debug Log 로그만 안뜨는 거였더,,

        if (sbn != null && sbn.getPackageName().equalsIgnoreCase("com.whatsapp")) {
            from = sbn.getNotification().tickerText.toString();

            //tv.setText("Kim So Hyeon's Debugged : " + from + " 원이라능");
        } else if (sbn != null && sbn.getPackageName().equalsIgnoreCase("com.kakao.talk")) {
            // 김지은 17 or 정지희 or 박경현 언니   에게서 카카오톡이 오면 내용 받기
            from = extras.getString(Notification.EXTRA_TITLE);
            text = extras.getString(Notification.EXTRA_TEXT);

            try {                       // 카카오 페이로 결제 시
                if(from.equals(nametitle)) {
                    String[] change_target = text.split("\\n");

                    String txtchanged = change_target[0];
                    String txtchanged2 = null;

                    txtchanged2 = txtchanged.substring(txtchanged.length() - 5, txtchanged.length());
                    tv.setText(txtchanged2);

                    if(txtchanged2.equals("받으세요.")) {
                        int getprice = Integer.parseInt(txtchanged.replaceAll("[^0-9]",""));
                        //Date today = new Date ();

                        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd a hh:mm", Locale.KOREA );
                        Date currentTime = new Date ( );
                        String todayYMD = formatter.format ( currentTime );
                        Log.d("년, 월, 일 : ", todayYMD);

                        if(getprice == 0) {

                        } else {
                            ((TodayActivity) TodayActivity.mainContext).addData(getprice, todayYMD);
                            TodayActivity.price.add(getprice);
                            //TodayActivity.time.add(시간지정,toString());

                            //tv.setText(getprice + " 원");
                        }
                    } else {
                        txtchanged = change_target[4];

                        int paidprice = Integer.parseInt(txtchanged.replaceAll("[^0-9]","")) * (-1);
                        //Date today = new Date ();

                        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd a hh:mm", Locale.KOREA );
                        Date currentTime = new Date ( );
                        String todayYMD = formatter.format ( currentTime );
                        Log.d("년, 월, 일 : ", todayYMD);

                        if(paidprice == 0) {

                        } else {
                            ((TodayActivity)TodayActivity.mainContext).addData(paidprice, todayYMD);
                            TodayActivity.price.add(paidprice);
                            //TodayActivity.time.add(시간지정,toString());

                            //tv.setText(paidprice + " 원");
                        }
                    }
                } else {
                    //tv.setText("어이 정신차리라고");
                }
            } catch(Exception e) {
                //tv.setText(e.getMessage());
            }


            /*

            try {                       // 카카오 페이에 입금 시
                if(from.equals(nametitle)) {
                    String[] change_target = text.split("\\n");
                    String txtchanged = change_target[0];
                    String txtchanged2 = null;

                    txtchanged2 = txtchanged.substring(txtchanged.length() - 8, txtchanged.length());

                    int getprice = Integer.parseInt(txtchanged2.replaceAll("[^0-9]",""));
                    //Date today = new Date ();

                    SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd a hh:mm", Locale.KOREA );
                    Date currentTime = new Date ( );
                    String todayYMD = formatter.format ( currentTime );
                    Log.d("년, 월, 일 : ", todayYMD);

                    if(getprice == 0) {

                    } else {
                        ((TodayActivity)TodayActivity.mainContext).addData(getprice, todayYMD);
                        TodayActivity.price.add(getprice);
                        //TodayActivity.time.add(시간지정,toString());

                        tv.setText(getprice + " 원");
                    }
                } else {
                    tv.setText("어이 정신차리라고");
                }
            } catch(Exception e) {
                tv.setText(e.getMessage());
            }*/
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.w(TAG, "Notification Posted:\n");
        Log.w(TAG, "Notification=");
        addNotification(sbn, true);
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.d(TAG, "Notification Removed:\n");
        if (sbn != null && sbn.getPackageName().equalsIgnoreCase("com.whatsapp")) {
//            MessageManager mManager=MessageManager.getInstance(this);
//            if (mManager!=null){
//                mManager.clearAll();
//            }
        } else if (sbn != null && sbn.getPackageName().equalsIgnoreCase("com.facebook.orca")) {

        }
    }
}