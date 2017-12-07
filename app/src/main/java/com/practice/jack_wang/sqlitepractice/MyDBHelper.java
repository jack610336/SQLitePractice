package com.practice.jack_wang.sqlitepractice;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jack_Wang on 2017/12/7.
 */

public class MyDBHelper extends SQLiteOpenHelper {

    private static MyDBHelper instance = null;
    public static MyDBHelper getInstance(Context ctx){
        if (instance==null){
            instance = new MyDBHelper(ctx, "taipeiParkInfo.db", null, 1);
        }
        return instance;
    }


    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE  TABLE parkInfo" +
                "(_id INTEGER PRIMARY KEY  NOT NULL , " +
                "ParkName VARCHAR NOT NULL , " +
                "Name VARCHAR, " +
                "OpenTime VARCHAR," +
                "Introduction VARCHAR)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}