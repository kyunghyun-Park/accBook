package com.example.accountbook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

public class DetailActivity extends AppCompatActivity {
    TextView txt1, txtTotal;
    private RecyclerAdapter2 adapter2;

    String myData;

    // SQLite
    SQLiteHelper mSQLiteHelper;
    SQLiteDatabase mDb;
    Cursor mCursor;

    // 포맷
    private DecimalFormat myFormatter = new DecimalFormat("###,###원");
    private int showMoney;
    private String money;

    private String deUsage;
    private String dePriceString;
    private int dePrice = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        txt1 = (TextView) findViewById(R.id.txt1);
        txtTotal = (TextView) findViewById(R.id.txtTotal);

        //날짜받아오기
        Intent intent = getIntent();
        myData = intent.getStringExtra("date");

        //날짜받아왔는지 실험
        //txtTotal.setText(myData);

        init();

        getData();
    }

    private void init() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView2);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter2 = new RecyclerAdapter2();
        recyclerView.setAdapter(adapter2);
    }

    private void getData() {
        // 해당 일 총 금액
        mSQLiteHelper = new SQLiteHelper(this);
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getReadableDatabase();
        mCursor = mDb.rawQuery("SELECT SUM(price) detailprice FROM " + SQLiteHelper.TABLE_NAME + " WHERE time LIKE '" + myData + "%';", null);
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            String dprice = mCursor.getString(mCursor.getColumnIndex("detailprice"));

            dprice = dprice + "";
            if (TextUtils.equals(dprice, null + "")) {
                txtTotal.setText("데이터가 없습니다.");
            } else {
                dePrice = Integer.parseInt(dprice);
                if (dePrice > 0) {
                    dePriceString = "+" + myFormatter.format(dePrice);
                } else {
                    dePriceString = myFormatter.format(dePrice);
                }
                txtTotal.setText(dePriceString);
            }
        }
        mDb.close();

        // 리사이클러뷰
        mDb = openOrCreateDatabase("today.db", MODE_PRIVATE, null);
        mDb = mSQLiteHelper.getReadableDatabase();
        mCursor = mDb.rawQuery("SELECT usage, SUM(price) detailprice FROM " + SQLiteHelper.TABLE_NAME
                + " WHERE time LIKE '" + myData + "%' GROUP BY usage ORDER BY SUM(price) ASC;", null);
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

            adapter2.addItem(data2);
        }
        mDb.close();

    }
}
