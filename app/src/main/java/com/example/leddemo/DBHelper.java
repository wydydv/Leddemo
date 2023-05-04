package com.example.leddemo;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;

public class DBHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "user";

    public static final String STUDENT_ID = "id";
    public static final String STUDENT_NAME = "typeA";
    public static final String STUDENT_GENDER = "typeB";
    public static final String STUDENT_AGE = "unit";
    public static final String STUDENT_EPC = "epc";


    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    private static boolean mainTmpDirSet = false;

    @Override
    public SQLiteDatabase getReadableDatabase() {
        if (!mainTmpDirSet) {
            boolean rs = new File("/data/data/com.cmp.pkg/databases/main").mkdir();
            Log.d("ahang", rs + "");
            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory = '/data/data/com.cmp.pkg/databases/main'");
            mainTmpDirSet = true;
            return super.getReadableDatabase();
        }
        return super.getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + " (" + STUDENT_ID + " integer," + STUDENT_NAME
                + " varchar(60) ," + STUDENT_EPC + " varchar(40) primary key," + STUDENT_GENDER + " varchar(1)," + STUDENT_AGE + " int)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void initDataInfo(String id, String epc, String typeA, String typeB, String unit) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(STUDENT_ID, id);
        cv.put(STUDENT_NAME, typeA);
        cv.put(STUDENT_GENDER, typeB);
        cv.put(STUDENT_AGE, unit);
        cv.put(STUDENT_EPC, epc);
        db.insert("user", null, cv);
    }

    public void inserts(String id, String epc, String typeA, String typeB, String unit) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(STUDENT_ID, id);
        cv.put(STUDENT_NAME, typeA);
        cv.put(STUDENT_GENDER, typeB);
        cv.put(STUDENT_AGE, unit);
        cv.put(STUDENT_EPC, epc);
        db.insert("user", null, cv);
    }



}

