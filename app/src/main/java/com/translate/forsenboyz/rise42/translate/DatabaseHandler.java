package com.translate.forsenboyz.rise42.translate;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.translate.forsenboyz.rise42.translate.ListUtils.translationsToStringWithComma;
import static com.translate.forsenboyz.rise42.translate.MainActivity.TAG;

/**
 * Created by rise42 on 15/03/17.
 */

public class DatabaseHandler {

    private static final String DATABASE_NAME = "dict";
    private static final String DATABASE_TABLE = "words";
    private static final int DATABASE_VERSION = 3;

    static final String KEY_COLUMN = "tired";
    static final String VALUE_COLUMN = "of";

    private SQLiteDatabase database;

    private static DatabaseHandler instance;

    static DatabaseHandler getInstance(Context context){
        if(instance == null){
            instance = new DatabaseHandler(context);
        }
        return instance;
    }

    private DatabaseHandler(Context context){
        database = new DatabaseCreator(context).getWritableDatabase();
    }

    List<String> get(String key){
        Cursor cursor = database.rawQuery("select * from "+DATABASE_TABLE+" where "+KEY_COLUMN+"='"+key+"'", null);
        cursor.moveToFirst();
        List<String> result = Arrays.asList(cursor.getString(cursor.getColumnIndex(VALUE_COLUMN)).split(","));
        cursor.close();
        return result;
    }

    List<Map<String,String>> getAll(){
        Cursor cursor = database.rawQuery("select * from "+DATABASE_TABLE, null);

        if(cursor.getCount() == 0){
            return null;
        }

        List<Map<String,String>> list = new ArrayList<>(cursor.getCount());

        Map<String,String> map;
        cursor.moveToFirst();
        while(cursor.moveToNext()){
            map = new HashMap<>(2);
            map.put(KEY_COLUMN, cursor.getString(cursor.getColumnIndex(KEY_COLUMN)));

            String values = cursor.getString(cursor.getColumnIndex(VALUE_COLUMN)).replaceAll(",",", ");
            map.put(VALUE_COLUMN, values.substring(0,values.length()-2));
            list.add(map);
        }

        cursor.close();
        return list;
    }

    void insert(String word, String[] translations){
        String value = translationsToStringWithComma(translations);
        Log.d(TAG, "insert: before actual insert, whole table: ");
        Log.d(TAG, getAll().toString());
        Log.d(TAG, "insert: "+"insert into "+DATABASE_TABLE+" values('"+word+"','"+value+"')");
        database.execSQL("insert into "+DATABASE_TABLE+" values('"+word+"','"+value+"')");
    }

    void update(String word, String[] translations){
        String sql = "update "+DATABASE_TABLE+" set "+VALUE_COLUMN+"='"
                + translationsToStringWithComma(translations)+ "' where "+KEY_COLUMN+"='"+word+"'";
        Log.d(TAG, "update: "+sql);
        database.execSQL(sql);
    }

    void delete(String word){
        String sql = "delete from "+DATABASE_TABLE+" where "+KEY_COLUMN+"='"+word+"'";
        Log.d(TAG, "delete: "+sql);
        database.execSQL(sql);
    }

    private class DatabaseCreator extends SQLiteOpenHelper {

        DatabaseCreator(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            String createTable = "create table " + DATABASE_TABLE + " ("
                    + KEY_COLUMN + " varchar(40), "
                    + VALUE_COLUMN + " text);";
            db.execSQL(createTable);
            Log.d("MY_TAG", createTable);
            Log.d("MY_TAG", "DataBase created");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            Log.d("MY_TAG", "DataBase wasted");
            onCreate(db);
        }
    }

}
