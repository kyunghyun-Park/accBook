package com.example.accountbook;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodayActivity extends AppCompatActivity implements RecyclerAdapter.AdapterCallback {

    private ConstraintLayout test;

    private RecyclerAdapter adapter;
    private DetailAdapter adapter2;

    private DecimalFormat myFormatter = new DecimalFormat("###,###원");
    private int showMoney;
    private String money;
    int totalMoney = 0;
    private TextView totalMoneyText;
    boolean isClose = true;

    // 디테일 전용 변수
    private String deUsage;
    private String dePriceString;
    private int dePrice = 0;

    SQLiteHelper mSQLiteHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;

    // 시간
    String todayYMD;

    public static List<Integer> price = null;
    public static List<String> time = null;

    public static Context mainContext;

    private static final String TAG = NotificationListener.class.getSimpleName();

    // 설정 화면 한번만 보이게 하기
    static boolean onSetting = true;

    @Override
    public void onMethodCallback(String price, String time, String place, String usage) {
        // get your value here
        //Toast.makeText(this, place + usage, Toast.LENGTH_LONG);
        Log.d("메인토스트", "가격" + price + "시간" + time + "장소" + place + "용도" + usage);

        //시간이 AM 00:00, 즉 리사이클러뷰에 표시된 텍스트만 들어오기 때문에 데이터베이스에서 활용하려면 todayYMD를 연결시켜줘야함

        // edittext의 내용을 SQLite에 저장하는거..
        String originprice = price;
        originprice = originprice.substring(0, originprice.length() - 1);
        originprice = originprice.replace(",", "");

        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getWritableDatabase();
        mDb.execSQL("UPDATE todayTb SET place='" + place + "', usage='" + usage + "' WHERE price='" + originprice + "'AND time='" + todayYMD + time + "';");
        mDb.close();
        adapter.notifyDataSetChanged();

        // 디테일 리사이클러뷰 표시
        adapter2.updateData();
        getData2();
        adapter2.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContext = this;

        // -------------------------------------------------------------------------------------------------------------------------
        //Log.d("야아아아아아아", "야야야야");
        if (onSetting == true) {
            NotificationListener.tv = (TextView) findViewById(R.id.textView);
            startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));

            // 푸시 알림을 보내기 위해 시스템에 권한 요청, 생성
            final NotificationManager notificationManager = (NotificationManager) TodayActivity.this.getSystemService(TodayActivity.this.NOTIFICATION_SERVICE);

            // 푸시 알림 터치시 실행할 작업 설정 (여기선 MainActivity로 이동)
            final Intent intent = new Intent(TodayActivity.this.getApplicationContext(), TodayActivity.class);

            final Notification.Builder builder = new Notification.Builder(getApplicationContext());

            // 푸시 알림을 터치해 실행할 작업 Flag 설정
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

            onSetting = false;
        }

        Button btnAlarm = (Button) findViewById(R.id.btn_alarm);

        btnAlarm.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 푸시 알람 생성
                Log.w("스래그륵", "스스슥");

                String channelId = "channel";
                String channelName = "Channel Name";

                NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
                    notifManager.createNotificationChannel(mChannel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
                Intent notificationIntent = new Intent(getApplicationContext(), TodayActivity.class);

                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);

                int requestID = (int) System.currentTimeMillis();

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), requestID
                        , notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                builder.setContentTitle("Title") // required
                        .setContentText("Content")  // required
                        .setDefaults(Notification.DEFAULT_ALL) // 알림, 사운드 진동 설정
                        .setAutoCancel(true) // 알림 터치시 반응 후 삭제
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setSmallIcon(android.R.drawable.btn_star)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground))
                        .setContentIntent(pendingIntent);

                notifManager.notify(0, builder.build());
            }
        });
        // --------------------------------------------------------------------------------------------------------------------------

        test = findViewById(R.id.TodayActivityLayout);

        test.setOnTouchListener(new OnSwipeTouchListener(TodayActivity.this) {
            public void onSwipeRight() {
                startActivity(new Intent(TodayActivity.this, AchieveActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                //finish();

                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_LONG).show();
            }

            public void onSwipeLeft() {
                startActivity(new Intent(TodayActivity.this, CalendarActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                finish();
            }
        });

        // 시간
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Date currentTime = new Date();
        todayYMD = formatter.format(currentTime) + " ";
        //Log.d("년, 월, 일 : ", todayYMD);

        // SQLite
        mSQLiteHelper = new SQLiteHelper(this);
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getWritableDatabase();
        mDb.execSQL("CREATE TABLE IF NOT EXISTS todayTb (" +
                "id integer primary key autoincrement," +
                "price text not null," +
                "usage text," +
                "place text," +
                "time text not null," +
                "unique(price, time));");
        mDb.close();

        // 테스트용 데이터 추가
        /*mDb = mSQLiteHelper.getWritableDatabase();
        mDb.execSQL("INSERT OR IGNORE INTO todayTb (price, time)" +
                " Values('77777','2019-10-21 AM 11:11');");
        mDb.close();*/

        // 테이블 내용 삭제
        /*mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getWritableDatabase();
        mDb.execSQL("delete from todayTb;");
        mDb.close();*/

        // 테이블 삭제
        //mDb = mSQLiteHelper.getReadableDatabase();
        //mDb.delete("todayTb", null, null);

        // 읽을때
        //mDb = mSQLiteHelper.getReadableDatabase();
        // db.execSQL, db.rawQuery

        init();
        getData();
        getData2();
        setTotal();

        //총! 디테일 열고 펼치는 애니메이션
        LinearLayout resize = (LinearLayout) findViewById(R.id.total);
        final ImageView icon = (ImageView) findViewById(R.id.totalCilckImg);
        resize.setOnClickListener(new View.OnClickListener() {

            LinearLayout detail = (LinearLayout) findViewById(R.id.totaldetail);
            RelativeLayout todaylist = (RelativeLayout) findViewById(R.id.todayLayout);

            //LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) detail.getLayoutParams();
            @Override
            public void onClick(View v) {
                ResizeAnimation a = new ResizeAnimation(detail);
                ResizeAnimation b = new ResizeAnimation(todaylist);
                a.setDuration(300);
                b.setDuration(300);

                if (isClose == true) {
                    // set the starting height (the current height) and the new height that the view should have after the animation
                    a.setParams(1, dpToPx(150));
                    b.setParams(dpToPx(423), dpToPx(303));
                    detail.startAnimation(a);
                    todaylist.startAnimation(b);
                    icon.setImageResource(R.drawable.open);
                    isClose = false;
                } else {
                    a.setParams(dpToPx(150), 1);
                    b.setParams(dpToPx(303), dpToPx(423));
                    detail.startAnimation(a);
                    todaylist.startAnimation(b);
                    icon.setImageResource(R.drawable.close);
                    isClose = true;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 시간
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Date currentTime = new Date();
        todayYMD = formatter.format(currentTime) + " ";
        //Log.d("년, 월, 일 : ", todayYMD);

        // SQLite
        mSQLiteHelper = new SQLiteHelper(this);
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getWritableDatabase();
        mDb.execSQL("CREATE TABLE IF NOT EXISTS todayTb (" +
                "id integer primary key autoincrement," +
                "price text not null," +
                "usage text," +
                "place text," +
                "time text not null," +
                "unique(price, time));");
        mDb.close();

        init();
        getData();
        getData2();
        setTotal();

        //총! 디테일 열고 펼치는 애니메이션
        LinearLayout resize = (LinearLayout) findViewById(R.id.total);
        final ImageView icon = (ImageView) findViewById(R.id.totalCilckImg);
        resize.setOnClickListener(new View.OnClickListener() {

            LinearLayout detail = (LinearLayout) findViewById(R.id.totaldetail);
            RelativeLayout todaylist = (RelativeLayout) findViewById(R.id.todayLayout);

            //LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) detail.getLayoutParams();
            @Override
            public void onClick(View v) {
                ResizeAnimation a = new ResizeAnimation(detail);
                ResizeAnimation b = new ResizeAnimation(todaylist);
                a.setDuration(300);
                b.setDuration(300);

                if (isClose == true) {
                    // set the starting height (the current height) and the new height that the view should have after the animation
                    a.setParams(1, dpToPx(150));
                    b.setParams(dpToPx(423), dpToPx(303));
                    detail.startAnimation(a);
                    todaylist.startAnimation(b);
                    icon.setImageResource(R.drawable.open);
                    isClose = false;
                } else {
                    a.setParams(dpToPx(150), 1);
                    b.setParams(dpToPx(303), dpToPx(423));
                    detail.startAnimation(a);
                    todaylist.startAnimation(b);
                    icon.setImageResource(R.drawable.close);
                    isClose = true;
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDb.close();
        mCursor.close();
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.todayList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new RecyclerAdapter(this);
        recyclerView.setAdapter(adapter);

        totalMoneyText = (TextView) findViewById(R.id.totalPrice);
        totalMoneyText.setSelected(true);

        //총 디테일 리사이클러뷰
        RecyclerView detailRecyclerView = findViewById(R.id.detailList);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this);
        detailRecyclerView.setLayoutManager(linearLayoutManager2);

        adapter2 = new DetailAdapter();
        detailRecyclerView.setAdapter(adapter2);

    }

    private void setTotal() {
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getReadableDatabase();
        mCursor = mDb.rawQuery("SELECT SUM(price) detailprice FROM " + SQLiteHelper.TABLE_NAME + " WHERE time LIKE '" + todayYMD + "%';", null);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            String dprice = mCursor.getString(mCursor.getColumnIndex("detailprice"));

            if (TextUtils.equals(dprice, null)) {
                totalMoney = 0;
                totalMoneyText.setText(myFormatter.format(totalMoney));
            } else {
                totalMoney = Integer.parseInt(dprice);
                if (totalMoney > 0) {
                    totalMoneyText.setText("+" + myFormatter.format(totalMoney));
                } else if (totalMoney == 0) {
                    totalMoneyText.setText(myFormatter.format(totalMoney));
                } else {
                    totalMoneyText.setText(myFormatter.format(totalMoney));
                }
            }

            /*totalMoney = Integer.parseInt(dprice);
            if (totalMoney > 0){
                totalMoneyText.setText( "+" + myFormatter.format(totalMoney));
            } else if (totalMoney == 0) {
                totalMoneyText.setText(myFormatter.format(totalMoney));
            } else{
                totalMoneyText.setText(myFormatter.format(totalMoney));
            }*/
        }
        mDb.close();

        /*if (totalMoney > 0){
            totalMoneyText.setText( "+" + myFormatter.format(totalMoney));
        } else if (totalMoney == 0) {
            totalMoneyText.setText(myFormatter.format(totalMoney));
        } else{
            totalMoneyText.setText(myFormatter.format(totalMoney));
        }*/
    }

    public class ResizeAnimation extends Animation {
        private int startHeight;
        private int deltaHeight; // distance between start and end height
        private View view;

        /**
         * constructor, do not forget to use the setParams(int, int) method before
         * starting the animation
         *
         * @param v
         */
        public ResizeAnimation(View v) {
            this.view = v;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            view.getLayoutParams().height = (int) (startHeight + deltaHeight * interpolatedTime);
            view.requestLayout();
        }

        /**
         * set the starting and ending height for the resize animation
         * starting height is usually the views current height, the end height is the height
         * we want to reach after the animation is completed
         *
         * @param start height in pixels
         * @param end   height in pixels
         */
        public void setParams(int start, int end) {

            this.startHeight = start;
            deltaHeight = end - startHeight;
        }

        /**
         * set the duration for the hideshowanimation
         */
        @Override
        public void setDuration(long durationMillis) {
            super.setDuration(durationMillis);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public void addData(int price, String time) {
        // SQLite에 데이터 작성
        mDb = mSQLiteHelper.getWritableDatabase();

        mDb.execSQL("INSERT OR IGNORE INTO todayTb (price, time)" + " Values('" + price + "','" + time + "');");

        /*
        for (int i = 0; i < price.size(); i++){
            mDb.execSQL("INSERT OR IGNORE INTO todayTb (price, time)" +
                    " Values('" + price.get(i) + "','" + todayYMD + time.get(i) + "');");
        }*/
        mDb.close();

        // 디테일 리사이클러뷰 표시
        adapter.updateData();
        getData();
        adapter.notifyDataSetChanged();
        getData2();
        setTotal();
    }

    public void getData() {
        // 임의의 데이터
        //List<String> price = Arrays.asList("1000", "-3200", "2400", "50000", "-3500", "12000", "-23500" ,"150000", "150000");
        //List<String> time = Arrays.asList("AM 02:10", "AM 08:45", "AM 10:10", "AM 11:30", "PM 12:23", "PM 02:15", "PM 04:17", "PM 07:50", "PM 07:51");

        // SQLite에 데이터 작성
        mDb = mSQLiteHelper.getWritableDatabase();
        if (price != null && time != null) {
            for (int i = 0; i < price.size(); i++) {
                mDb.execSQL("INSERT OR IGNORE INTO todayTb (price, time)" +
                        " Values('" + price.get(i) + "','" + todayYMD + time.get(i) + "');");
            }
        }
        mDb.close();

        // 리사이클러뷰에 보이기, SQLite 읽기
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getReadableDatabase();
        //String[] columns = {mSQLiteHelper.COLUMN_PRICE, mSQLiteHelper.COLUMN_USAGE, mSQLiteHelper.COLUMN_PLACE, mSQLiteHelper.COLUMN_DATE};
        //Cursor cursor = mDb.query(mSQLiteHelper.TABLE_NAME, columns, null, null, null, null, null);
        mCursor = mDb.rawQuery("SELECT * FROM " + SQLiteHelper.TABLE_NAME + " WHERE time LIKE '" + todayYMD + "%'", null);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // 각 List의 값들을 data 객체에 set
            TodayData data = new TodayData();

            String rprice = mCursor.getString(mCursor.getColumnIndex("price"));
            String rusage = mCursor.getString(mCursor.getColumnIndex("usage"));
            String rplace = mCursor.getString(mCursor.getColumnIndex("place"));
            String rtime = mCursor.getString(mCursor.getColumnIndex("time"));

            // 타임 필요한 표시 변경
            // 타임 형식 yyyy-MM-dd AM 00:00

            showMoney = Integer.parseInt(rprice);
            //totalMoney += showMoney;
            money = myFormatter.format(showMoney);
            data.setPrice(money);
            data.setUsage(rusage);
            data.setPlace(rplace);
            data.setTime(rtime.substring(11));
            //Log.d("리사이클러뷰 데이터:", "가격" + money + "시간" + rtime + "장소" + rplace + "용도" + rusage);

            adapter.addItem(data);
        }
        mDb.close();
    }

    public void getData2() {
        // 디테일 리사이클러뷰 표시
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getReadableDatabase();
        mCursor = mDb.rawQuery("SELECT usage, SUM(price) detailprice FROM " + SQLiteHelper.TABLE_NAME + " WHERE time LIKE '" + todayYMD + "%' GROUP BY usage ORDER BY SUM(price) ASC;", null);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // 각 List의 값들을 data 객체에 set
            TodayData data2 = new TodayData();

            String dusage = mCursor.getString(mCursor.getColumnIndex("usage"));
            String dprice = mCursor.getString(mCursor.getColumnIndex("detailprice"));


            deUsage = dusage + "";
            if (TextUtils.equals(deUsage, null + "")) {
                deUsage = "기타";
            } else {
                deUsage = dusage;
            }

            dePrice = Integer.parseInt(dprice);
            if (dePrice > 0) {
                dePriceString = "+" + myFormatter.format(dePrice);
            } else {
                dePriceString = myFormatter.format(dePrice);
            }

            data2.setUsage(deUsage + ": ");
            data2.setPrice(dePriceString);

            //Log.d("디테일 리사이클러뷰 데이터:", "용도" + dusage + "용도별 가격 총합" + dprice);

            adapter2.addItem(data2);
        }
        mDb.close();
    }

    /*private void getData(){
        // 임의의 데이터
        List<String> price = Arrays.asList("1000", "-3200", "2400", "50000", "-3500", "12000", "-23500" ,"150000");
        List<String> time = Arrays.asList("AM 02:10", "AM 08:45", "AM 10:10", "AM 11:30", "PM 12:23", "PM 02:15", "PM 04:17", "PM 07:50");

        //mDb.execSQL("INSERT INTO todayTb (price, usage, place, date) VALUES (" + price + ", '" + usage + "', '"+ place + "', '"+ date + "');");
        // db.insert, db.execSQL, db.rawQuery

        if (price.isEmpty()){
            Toast.makeText(this, "입출금 내역이 없습니다.", Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < price.size(); i++){
                // 쓸때
                //mDb = mSQLiteHelper.getWritableDatabase();
                //mDb.execSQL("INSERT INTO todayTb (price, date) VALUES (" + price.get(i) + ", '"+ date + "');");

                // 각 List의 값들을 data 객체에 set
                TodayData data = new TodayData();
                showMoney = Integer.parseInt(price.get(i));
                totalMoney += showMoney;
                money = myFormatter.format(showMoney);
                //data.setPrice(price.get(i));
                data.setPrice(money);
                data.setTime(time.get(i));

                // 각 값이 들어간 data를 adpater에 추가
                adapter.addItem(data);
            }

            // adapter의 값이 변경되었다는 것을 알려줌
            adapter.notifyDataSetChanged();
        }
    }*/
}
