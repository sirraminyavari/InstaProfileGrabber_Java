package com.android.instaprofilegrabber;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.instaprofilegrabber.UserImagesContract.UserImagesEntry;

/**
 * Created by Ramin on 1/27/2017.
 */

public class UserImagesDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "instaprofilegrabber.db";

    private static final String SQL_CREATE_USERS = "CREATE TABLE IF NOT EXISTS " +
            UserImagesEntry.TABLE_NAME + " (" +
            UserImagesEntry._ID + " INTEGER PRIMARY KEY" +
            "," + UserImagesEntry.COLUMN_NAME_UserID + " TEXT" +
            "," + UserImagesEntry.COLUMN_NAME_Username + " TEXT" +
            "," + UserImagesEntry.COLUMN_NAME_FullName + " TEXT" +
            "," + UserImagesEntry.COLUMN_NAME_FullSizePicURL + " TEXT" +
            "," + UserImagesEntry.COLUMN_NAME_PicURL + " TEXT" +
            "," + UserImagesEntry.COLUMN_NAME_PicURLHD + " TEXT" +
            ")";

    private static final String SQL_DETELE_USERS = "DROP TABLE IF EXISTS " + UserImagesEntry.TABLE_NAME;

    public UserImagesDBHelper(Context context){
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

