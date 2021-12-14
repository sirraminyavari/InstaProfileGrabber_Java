package com.android.instaprofilegrabber;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.instaprofilegrabber.UsersContract.UsersEntry;

/**
 * Created by Ramin on 1/27/2017.
 */

public class UsersDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "instaprofilegrabber.db";

    private static final String SQL_CREATE_USERS = "CREATE TABLE IF NOT EXISTS " +
            UsersEntry.TABLE_NAME + " (" +
            UsersEntry._ID + " INTEGER PRIMARY KEY" +
            "," + UsersEntry.COLUMN_NAME_UserID + " TEXT" +
            "," + UsersEntry.COLUMN_NAME_Username + " TEXT" +
            "," + UsersEntry.COLUMN_NAME_FullName + " TEXT" +
            "," + UsersEntry.COLUMN_NAME_Biography + " TEXT" +
            "," + UsersEntry.COLUMN_NAME_PicURL + " TEXT" +
            "," + UsersEntry.COLUMN_NAME_Private + " TEXT" +
            "," + UsersEntry.COLUMN_NAME_Following + " TEXT" +
            "," + UsersEntry.COLUMN_NAME_FollowedBy + " TEXT" +
            ")";

    private static final String SQL_DETELE_USERS = "DROP TABLE IF EXISTS " + UsersEntry.TABLE_NAME;

    public UsersDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_USERS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(SQL_DETELE_USERS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion){
        onUpgrade(db, oldVersion, newVersion);
    }
}
