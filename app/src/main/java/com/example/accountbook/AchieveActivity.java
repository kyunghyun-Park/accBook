package com.example.accountbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AchieveActivity extends AppCompatActivity {

    // SQLite
    SQLiteHelper mSQLiteHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;

    String todayYMD;
    String currentMonth;

    //ImageView imageview = null;

    private int imp, spd;
    private String money;

    TextView lastMonthTextView;
    TextView lastMonthPriceTextView;

    private DecimalFormat myFormatter = new DecimalFormat("###,###원");

    private LinearLayout test3;

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achieve);

        lastMonthTextView = findViewById(R.id.lastMonth);
        lastMonthPriceTextView = findViewById(R.id.lastMonthPrice);

        test3 = findViewById(R.id.AchieveActivityLayout);

        test3.setOnTouchListener(new OnSwipeTouchListener(AchieveActivity.this){
            public void onSwipeRight(){
                startActivity(new Intent(AchieveActivity.this, CalendarActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                //finish();

                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_LONG).show();
            }
            public void onSwipeLeft(){
                startActivity(new Intent(AchieveActivity.this, TodayActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                finish();
            }
        });

        //업적 인텐트
        /*Button button = (Button) findViewById(R.id.btnAchieve);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,AchieveActivity.class);
                startActivity(intent);
            }
        });*/

        // 시간
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Date currentTime = new Date();
        todayYMD = formatter.format(currentTime) + " ";
        currentMonth = todayYMD.substring(0, 7);

        // 저번 달
        String year;
        String lastMonth;
        int toLastMonth;

        year = currentMonth.substring(0, 4);
        lastMonth = currentMonth.substring(5, 7);
        toLastMonth = Integer.parseInt(lastMonth) - 1;


        if (toLastMonth < 10) {
            lastMonthTextView.setText(year + "년 0" + Integer.toString(toLastMonth) + "월:");
            lastMonth = year + "-0" + Integer.toString(toLastMonth);
        } else {
            lastMonthTextView.setText(year + "년 " + Integer.toString(toLastMonth) + "월:");
            lastMonth = year + "-" + Integer.toString(toLastMonth);
        }

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

        // 테스트용 지난 달 데이터 추가
        mDb = mSQLiteHelper.getWritableDatabase();
        mDb.execSQL("INSERT OR IGNORE INTO todayTb (price, time)" +
                " Values('12345','2019-09-21 AM 11:11');");

        /*mDb.execSQL("INSERT OR IGNORE INTO todayTb (price, time)" +
                " Values('-5','2019-09-21 AM 11:15');");*/
        mDb.close();

        // 해당 달 지출
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getReadableDatabase();
        //String[] columns = {mSQLiteHelper.COLUMN_PRICE, mSQLiteHelper.COLUMN_USAGE, mSQLiteHelper.COLUMN_PLACE, mSQLiteHelper.COLUMN_DATE};
        //Cursor cursor = mDb.query(mSQLiteHelper.TABLE_NAME, columns, null, null, null, null, null);
        mCursor = mDb.rawQuery("SELECT SUM(price) totalprice FROM " + SQLiteHelper.TABLE_NAME + " WHERE time LIKE '" + lastMonth + "%' AND price LIKE '-%'", null);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            String rprice = mCursor.getString(mCursor.getColumnIndex("totalprice"));

            if (TextUtils.equals(rprice, null)){
                spd = 0;
            } else {

                Log.d("테스트", rprice);
                spd = Integer.parseInt(rprice);
            }
        }
        mDb.close();

        // 해당 달 수입
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getReadableDatabase();
        //String[] columns = {mSQLiteHelper.COLUMN_PRICE, mSQLiteHelper.COLUMN_USAGE, mSQLiteHelper.COLUMN_PLACE, mSQLiteHelper.COLUMN_DATE};
        //Cursor cursor = mDb.query(mSQLiteHelper.TABLE_NAME, columns, null, null, null, null, null);
        mCursor = mDb.rawQuery("SELECT SUM(price) totalprice FROM " + SQLiteHelper.TABLE_NAME + " WHERE time LIKE '" + lastMonth + "%' AND price not in (select price from " + SQLiteHelper.TABLE_NAME + " where time like '" + lastMonth + "%' and price LIKE '-%')", null);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            String rprice = mCursor.getString(mCursor.getColumnIndex("totalprice"));

            if (TextUtils.equals(rprice, null)){
                imp = 0;
            } else {

                Log.d("테스트", rprice);
                imp = Integer.parseInt(rprice);
            }/*

            imp = Integer.parseInt(rprice);*/
        }
        mDb.close();

        // 지난 달 총 수입 지출
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getReadableDatabase();
        //String[] columns = {mSQLiteHelper.COLUMN_PRICE, mSQLiteHelper.COLUMN_USAGE, mSQLiteHelper.COLUMN_PLACE, mSQLiteHelper.COLUMN_DATE};
        //Cursor cursor = mDb.query(mSQLiteHelper.TABLE_NAME, columns, null, null, null, null, null);
        mCursor = mDb.rawQuery("SELECT SUM(price) totalprice FROM " + SQLiteHelper.TABLE_NAME + " WHERE time LIKE '" + lastMonth + "%'", null);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            String rprice = mCursor.getString(mCursor.getColumnIndex("totalprice"));

            int formatmoney = Integer.parseInt(rprice);

            if (formatmoney > 0) {
                rprice = "+" + myFormatter.format(formatmoney);
                lastMonthPriceTextView.setText(rprice);
            } else {
                rprice = "-" + myFormatter.format(formatmoney);
                lastMonthPriceTextView.setText(rprice);
            }

        }
        mDb.close();


        ImageView imageview = (ImageView) findViewById(R.id.imageView);


        if (imp > spd) { //수입이 지출보다 크면
            imageview.setImageResource(R.drawable.rich2);
        } else if (imp == spd) {
            imageview.setImageResource(R.drawable.normal);
        } else {
            imageview.setImageResource(R.drawable.poor);
        }

    }
}