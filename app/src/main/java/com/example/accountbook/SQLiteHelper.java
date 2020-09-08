package com.example.accountbook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {

    // 데이터베이스
    private static final String DATABASE_NAME      = "today.db";
    private static final int DATABASE_VERSION      = 1;

    // 테이블
    public static final String TABLE_NAME       = "todayTb";
    public static final String COLUMN_ID        = "id";
    public static final String COLUMN_PRICE      = "price";
    public static final String COLUMN_USAGE      = "usage";
    public static final String COLUMN_PLACE     = "place";
    public static final String COLUMN_TIME      = "time";

    private static final String DATABASE_CREATE_TEAM = "create table if not exists "
            + TABLE_NAME + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_PRICE + " text not null, "
            + COLUMN_USAGE + " text, "
            + COLUMN_PLACE + " text, "
            + COLUMN_TIME + " text not null," +
            " UNIQUE(" + COLUMN_PRICE + ", " + COLUMN_TIME + "));";


// 기존 테이블에 레코드 추가시 사용
//    private static final String DATABASE_ALTER_TEAM_1 = "ALTER TABLE "
//            + TABLE_TEAM + " ADD COLUMN " + COLUMN_COACH + " string;";
//
//    private static final String DATABASE_ALTER_TEAM_2 = "ALTER TABLE "
//            + TABLE_TEAM + " ADD COLUMN " + COLUMN_STADIUM + " string;";


    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 앱을 삭제후 앱을 재설치하면 기존 DB파일은 앱 삭제시 지워지지 않기 때문에
        // 테이블이 이미 있다고 생성 에러남
        // 앱을 재설치시 데이터베이스를 삭제해줘야함.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(DATABASE_CREATE_TEAM);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

        //기존 테이블에 레코드 추가시 사용
//        if (oldVersion < 2) {
//            db.execSQL(DATABASE_ALTER_TEAM_1);
//        }
//        if (oldVersion < 3) {
//            db.execSQL(DATABASE_ALTER_TEAM_2);
//        }
    }
}
