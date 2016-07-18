package lh.sichuangsoft.cn.kaoshi;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by 85832_000 on 2016/7/15.
 */
public class MyOpenHelper  extends SQLiteOpenHelper {

    public MyOpenHelper(Context context) {
        super(context, "qadb",null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table panduan(_id integer primary key autoincrement,q char(500),a char(200))");
        db.execSQL("create table danxuan(_id integer primary key autoincrement,q char(500),a char(200))");
        db.execSQL("create table duoxuan(_id integer primary key autoincrement,q char(500),a char(200))");


    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        System.out.print("数据库升级了!!!");

    }



}