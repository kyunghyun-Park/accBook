package com.example.accountbook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.accountbook.decorators.OneDayDecorator;
import com.example.accountbook.decorators.SaturdayDecorator;
import com.example.accountbook.decorators.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private LinearLayout test2;

    int syear,smonth,sday;
    String sdate;

    // SQLite
    SQLiteHelper mSQLiteHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;

    // 시간
    String todayYMD;
    String currentMonth;

    private DecimalFormat myFormatter = new DecimalFormat("###,###원");

    TextView Jichul;
    TextView Suip;
    private int showMoney;
    private String money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        test2 = findViewById(R.id.CalendarActivityLayout);

        test2.setOnTouchListener(new OnSwipeTouchListener(CalendarActivity.this){
            public void onSwipeRight(){
                startActivity(new Intent(CalendarActivity.this, TodayActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
                //finish();

                //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                //Toast.makeText(MainActivity.this, "right", Toast.LENGTH_LONG).show();
            }
            public void onSwipeLeft(){
                startActivity(new Intent(CalendarActivity.this, AchieveActivity.class));
                overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
                finish();
            }
        });

        final MaterialCalendarView materialCalendarView = findViewById(R.id.calendarView);
        final TextView txtJichul = findViewById(R.id.txtJichul);
        final TextView txtSuip = findViewById(R.id.txtSuip);

        Jichul = findViewById(R.id.txtJichul);
        Suip = findViewById(R.id.txtSuip);

        // 시간
        SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy-MM-dd", Locale.KOREA );
        Date currentTime = new Date ( );
        todayYMD = formatter.format ( currentTime ) + " ";
        currentMonth = todayYMD.substring(0, 7);

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

        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        materialCalendarView.addDecorators(
                new SundayDecorator(),
                new SaturdayDecorator(),
                new OneDayDecorator());

        // 이번 달 지출 표시
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getReadableDatabase();
        //String[] columns = {mSQLiteHelper.COLUMN_PRICE, mSQLiteHelper.COLUMN_USAGE, mSQLiteHelper.COLUMN_PLACE, mSQLiteHelper.COLUMN_DATE};
        //Cursor cursor = mDb.query(mSQLiteHelper.TABLE_NAME, columns, null, null, null, null, null);
        mCursor = mDb.rawQuery("SELECT SUM(price) totalprice FROM " + SQLiteHelper.TABLE_NAME + " WHERE time LIKE '" + currentMonth + "%' AND price LIKE '-%'", null);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // 각 List의 값들을 data 객체에 set
            TodayData data = new TodayData();

            String rprice = mCursor.getString(mCursor.getColumnIndex("totalprice"));

            // 타임 필요한 표시 변경
            // 타임 형식 yyyy-MM-dd AM 00:00


            if (TextUtils.equals(rprice, null)){
                showMoney = 0;
                money = myFormatter.format(showMoney);
            } else {
                showMoney = Integer.parseInt(rprice);
                money = myFormatter.format(showMoney);
            }
            Jichul.setText(money);
        }
        mDb.close();

        // 이번 달 수입 표시
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getReadableDatabase();
        //String[] columns = {mSQLiteHelper.COLUMN_PRICE, mSQLiteHelper.COLUMN_USAGE, mSQLiteHelper.COLUMN_PLACE, mSQLiteHelper.COLUMN_DATE};
        //Cursor cursor = mDb.query(mSQLiteHelper.TABLE_NAME, columns, null, null, null, null, null);
        mCursor = mDb.rawQuery("SELECT SUM(price) totalprice FROM " + SQLiteHelper.TABLE_NAME + " WHERE time LIKE '" + currentMonth + "%' AND price not in (select price from " + SQLiteHelper.TABLE_NAME + " where time like '" + currentMonth + "%' and price LIKE '-%')", null);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // 각 List의 값들을 data 객체에 set
            TodayData data = new TodayData();

            String rprice = mCursor.getString(mCursor.getColumnIndex("totalprice"));

            // 타임 필요한 표시 변경
            // 타임 형식 yyyy-MM-dd AM 00:00

            if (TextUtils.equals(rprice, null)){
                showMoney = 0;
                money = myFormatter.format(showMoney);
            } else {
                showMoney = Integer.parseInt(rprice);
                money = myFormatter.format(showMoney);
            }
            Suip.setText(money);
        }
        mDb.close();

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                syear=date.getYear();
                smonth=date.getMonth()+1;
                sday=date.getDay();

                //txtJichul.setText(Integer.toString(syear)+"년"+Integer.toString(smonth)+"월"+Integer.toString(sday)+"일");

                //날짜형식 만들기 YYYY-MM-DD
                if (smonth<10){
                    sdate = Integer.toString(syear) + "-0" + Integer.toString(smonth) + "-" + Integer.toString(sday);
                }else if(sday<10){
                    sdate = Integer.toString(syear) + "-" + Integer.toString(smonth) + "-0" + Integer.toString(sday);
                }else {
                    sdate = Integer.toString(syear) + "-" + Integer.toString(smonth) + "-" + Integer.toString(sday);
                }

                Intent intent=new Intent(CalendarActivity.this,DetailActivity.class);
                intent.putExtra("date",sdate);
                startActivity(intent);
            }
        });
    }
}
