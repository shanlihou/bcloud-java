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

import java.util.Map;

/**
 * Created by Administrator on 2015/1/17 0017.
 */
public class DBHelper extends SQLiteOpenHelper {
    private final static String DATABASE_NAME = "CookieDB.db";

    private final static int DATABASE_VERSION = 1;

    private final static String TABLE_NAME_COOKIE = "cookie";
    private final static String TABLE_NAME_TOKENS = "tokens";
    private final static String TABLE_NAME_STATUS = "status";

    public final static String FIELD_ID = "_id";

    public final static String FIELD_KEY = "key";

    public final static String FIELD_VALUE = "value";
    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d("shanlihou", "init db");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table " + TABLE_NAME_COOKIE + "(" + FIELD_ID + " integer primary key autoincrement,"
                + FIELD_KEY + " text, " + FIELD_VALUE + " text );";
        db.execSQL(sql);
        sql = "Create table " + TABLE_NAME_TOKENS + "(" + FIELD_ID + " integer primary key autoincrement,"
                + FIELD_KEY + " text, " + FIELD_VALUE + " text );";
        db.execSQL(sql);
        Log.d("shanlihou", "add status");
        sql = "Create table " + TABLE_NAME_STATUS + "(" + FIELD_ID + " integer primary key autoincrement,"
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
    public void deleteAll(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        if (id == 0){
            db.delete(TABLE_NAME_COOKIE, null, null);
        }else{
            db.delete(TABLE_NAME_TOKENS, null, null);
        }
    }

    public void saveMap(int id, Map<String, String> map){
        deleteAll(id);
        for(Map.Entry<String, String> entry: map.entrySet()){
            insert(id, entry.getKey(), entry.getValue());
        }
    }

    public void showDB(int id){
        Cursor cursor = query(id);
        if (cursor.moveToFirst()){
            do{
                String key = cursor.getString(1);
                String value = cursor.getString(2);
                Log.d("shanlihou", key + ":" + value);
            }while(cursor.moveToNext());
        }
    }

    public void loadMap(int id, Map<String, String> map){
        Cursor cursor = query(id);
        if (cursor.moveToFirst()){
            do{
                map.put(cursor.getString(1), cursor.getString(2));
            }while(cursor.moveToNext());
        }
    }
    private ContentValues createValues(String title, String location) {
        ContentValues cv = new ContentValues();
        cv.put(FIELD_KEY, title);
        cv.put(FIELD_VALUE, location);
        return cv;
    }

    public void modifyPage(int page){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery("select * from status where key=?", new String[]{"page"});
        if(c.moveToFirst()) {
            String sql = "update [status] set value = '" + page + "' where key='page'";//修改的SQL语句
            db.execSQL(sql);//执行修改`
        }
        else{
            db.insert(TABLE_NAME_STATUS, null, createValues("page", "" + page));
        }
    }
    public int getPage(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("select * from status where key=?", new String[]{"page"});
        Log.d("shanlihou", "get page");
        if (c.moveToFirst()){
            Log.d("shanlihou", c.getInt(2) + "");
            return c.getInt(2);
        }
        return 1;
    }
}

