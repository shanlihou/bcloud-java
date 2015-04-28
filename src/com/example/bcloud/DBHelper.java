package com.example.bcloud;

/**
 * Created by shanlihou on 15-4-28.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2015/1/17 0017.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "CookieDB.db";

    private final static int DATABASE_VERSION = 1;

    private final static String TABLE_NAME_COOKIE = "cookie";
    private final static String TABLE_NAME_TOKENS = "tokens";

    public final static String FIELD_ID = "_id";

    public final static String FIELD_KEY = "key";

    public final static String FIELD_VALUE = "value";
    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table " + TABLE_NAME_COOKIE + "(" + FIELD_ID + " integer primary key autoincrement,"
                + FIELD_KEY + " text, " + FIELD_VALUE + " text );";
        db.execSQL(sql);
        sql = "Create table " + TABLE_NAME_TOKENS + "(" + FIELD_ID + " integer primary key autoincrement,"
                + FIELD_KEY + " text, " + FIELD_VALUE + " text );";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = " DROP TABLE IF EXISTS " + TABLE_NAME_COOKIE;
        db.execSQL(sql);
        sql = " DROP TABLE IF EXISTS " + TABLE_NAME_TOKENS;
        db.execSQL(sql);
        onCreate(db);
    }

    public Cursor query(int id) {
        SQLiteDatabase db;
        Cursor cursor;
        if (id == 0) {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME_COOKIE, null, null, null, null, null, null);
        }else {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_NAME_TOKENS, null, null, null, null, null, null);

        }
        return cursor;
    }
    public long insert(int id, String title, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        long ret;
        if (id == 0){
            ret = db.insert(TABLE_NAME_COOKIE, null, createValues(title, location));
        }else {
            ret = db.insert(TABLE_NAME_TOKENS, null, createValues(title, location));
        }
        return ret;
    }

    public void delete(int id, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = FIELD_VALUE + "=?";
        String[] whereValue = {
                location
        };
        if (id == 0){
            db.delete(TABLE_NAME_COOKIE, where, whereValue);
        }else{
            db.delete(TABLE_NAME_TOKENS, where, whereValue);
        }
    }
    private ContentValues createValues(String title, String location) {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_KEY, title);
        cv.put(FIELD_VALUE, location);
        return cv;
    }
}

