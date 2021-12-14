package com.android.instaprofilegrabber;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.android.instaprofilegrabber.UsersContract.UsersEntry;
import com.android.instaprofilegrabber.UserImagesContract.UserImagesEntry;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramin on 1/27/2017.
 */

public final class DBUtil {
    public static List<JSONObject> getUsers(Context context, JSONObject params){
        List<JSONObject> retList = new ArrayList<JSONObject>();

        try {
            if (params == null) params = new JSONObject();

            String userId = params.has(UsersEntry.COLUMN_NAME_UserID) ? params.getString(UsersEntry.COLUMN_NAME_UserID) : "";
            boolean following = params.has(UsersEntry.COLUMN_NAME_Following) &&
                    params.getBoolean(UsersEntry.COLUMN_NAME_Following);

            String query = "";
            query += userId != null && !userId.isEmpty() ? UsersEntry.COLUMN_NAME_UserID + " = ?" : "";
            query += following ? (!query.isEmpty() ? " AND " : "") + UsersEntry.COLUMN_NAME_Following + " = ?" : "";

            List<String> selectionArgs = new ArrayList<String>();

            if(userId != null && !userId.isEmpty()) selectionArgs.add(userId);
            if(following) selectionArgs.add("true");

            String[] arrArgs = new String[selectionArgs.size()];
            selectionArgs.toArray(arrArgs);

            String sortOrder = UsersEntry._ID + " ASC";

            String[] projection = {
                    UsersEntry._ID,
                    UsersEntry.COLUMN_NAME_UserID,
                    UsersEntry.COLUMN_NAME_Username,
                    UsersEntry.COLUMN_NAME_FullName,
                    UsersEntry.COLUMN_NAME_Biography,
                    UsersEntry.COLUMN_NAME_PicURL,
                    UsersEntry.COLUMN_NAME_Private,
                    UsersEntry.COLUMN_NAME_Following,
                    UsersEntry.COLUMN_NAME_FollowedBy
            };

            UsersDBHelper dbHelper = new UsersDBHelper(context);

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(
                    UsersEntry.TABLE_NAME,
                    projection,
                    query,
                    arrArgs,
                    null,
                    null,
                    sortOrder
            );

            while (cursor.moveToNext()){
                try {
                    JSONObject obj = new JSONObject();

                    obj.put(UsersEntry._ID, cursor.getString(cursor.getColumnIndex(UsersEntry._ID)));
                    obj.put(UsersEntry.COLUMN_NAME_UserID, cursor.getString(cursor.getColumnIndex(UsersEntry.COLUMN_NAME_UserID)));
                    obj.put(UsersEntry.COLUMN_NAME_Username, cursor.getString(cursor.getColumnIndex(UsersEntry.COLUMN_NAME_Username)));
                    obj.put(UsersEntry.COLUMN_NAME_FullName, cursor.getString(cursor.getColumnIndex(UsersEntry.COLUMN_NAME_FullName)));
                    obj.put(UsersEntry.COLUMN_NAME_Biography, cursor.getString(cursor.getColumnIndex(UsersEntry.COLUMN_NAME_Biography)));
                    obj.put(UsersEntry.COLUMN_NAME_PicURL, cursor.getString(cursor.getColumnIndex(UsersEntry.COLUMN_NAME_PicURL)));
                    obj.put(UsersEntry.COLUMN_NAME_Private, cursor.getString(cursor.getColumnIndex(UsersEntry.COLUMN_NAME_Private)));
                    obj.put(UsersEntry.COLUMN_NAME_Following, cursor.getString(cursor.getColumnIndex(UsersEntry.COLUMN_NAME_Following)));
                    obj.put(UsersEntry.COLUMN_NAME_FollowedBy, cursor.getString(cursor.getColumnIndex(UsersEntry.COLUMN_NAME_FollowedBy)));

                    retList.add(obj);
                }
                catch (Exception e){}
            }

            if(!cursor.isClosed()) cursor.close();

            dbHelper.close();
        }
        catch (Exception e){}

        return retList;
    }

    private static long _new_user(Context context, JSONObject params){
        try {
            if (params == null) return 0;

            ContentValues values = new ContentValues();
            values.put(UsersEntry.COLUMN_NAME_UserID, params.getString(UsersEntry.COLUMN_NAME_UserID));
            values.put(UsersEntry.COLUMN_NAME_Username, params.getString(UsersEntry.COLUMN_NAME_Username));
            values.put(UsersEntry.COLUMN_NAME_FullName, params.getString(UsersEntry.COLUMN_NAME_FullName));
            values.put(UsersEntry.COLUMN_NAME_Biography, params.getString(UsersEntry.COLUMN_NAME_Biography));
            values.put(UsersEntry.COLUMN_NAME_PicURL, params.getString(UsersEntry.COLUMN_NAME_PicURL));
            values.put(UsersEntry.COLUMN_NAME_Private, "false");
            values.put(UsersEntry.COLUMN_NAME_Following, "false");
            values.put(UsersEntry.COLUMN_NAME_FollowedBy, params.getString(UsersEntry.COLUMN_NAME_FollowedBy));

            UsersDBHelper dbHelper = new UsersDBHelper(context);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            long id = db.insert(UsersEntry.TABLE_NAME, null, values);

            dbHelper.close();

            return id;
        }
        catch (Exception e) {
            return 0;
        }
    }

    private static boolean _update_user(Context context, JSONObject params){
        try {
            if (params == null) params = new JSONObject();

            String userId = params.has(UsersEntry.COLUMN_NAME_UserID) ? params.getString(UsersEntry.COLUMN_NAME_UserID) : "";

            if(userId == null || userId.isEmpty()) return false;

            String query = UsersEntry.COLUMN_NAME_UserID + " = ?";
            String[] queryArgs = { userId };

            ContentValues values = new ContentValues();
            if(params.has(UsersEntry.COLUMN_NAME_Username))
                values.put(UsersEntry.COLUMN_NAME_Username, params.getString(UsersEntry.COLUMN_NAME_Username));
            if(params.has(UsersEntry.COLUMN_NAME_FullName))
                values.put(UsersEntry.COLUMN_NAME_FullName, params.getString(UsersEntry.COLUMN_NAME_FullName));
            if(params.has(UsersEntry.COLUMN_NAME_Biography))
                values.put(UsersEntry.COLUMN_NAME_Biography, params.getString(UsersEntry.COLUMN_NAME_Biography));
            if(params.has(UsersEntry.COLUMN_NAME_PicURL))
                values.put(UsersEntry.COLUMN_NAME_PicURL, params.getString(UsersEntry.COLUMN_NAME_PicURL));
            if(params.has(UsersEntry.COLUMN_NAME_FollowedBy))
                values.put(UsersEntry.COLUMN_NAME_FollowedBy, params.getString(UsersEntry.COLUMN_NAME_FollowedBy));

            UsersDBHelper dbHelper = new UsersDBHelper(context);

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            int count = db.update(
                    UsersEntry.TABLE_NAME,
                    values,
                    query,
                    queryArgs
            );

            dbHelper.close();

            return count > 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean new_user(Context context, JSONObject params) {
        try {
            if (params == null) params = new JSONObject();

            JSONObject searchParams = new JSONObject();
            if (params.has(UsersEntry.COLUMN_NAME_UserID))
                searchParams.put(UsersEntry.COLUMN_NAME_UserID, params.getString(UsersEntry.COLUMN_NAME_UserID));

            List<JSONObject> users = getUsers(context, searchParams);

            return users.size() > 0 ? _update_user(context, params) : _new_user(context, params) > 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static List<JSONObject> getUserImages(Context context, JSONObject params){
        List<JSONObject> retList = new ArrayList<JSONObject>();

        try {
            if (params == null) params = new JSONObject();

            String userId = params.has(UserImagesEntry.COLUMN_NAME_UserID) ?
                    params.getString(UserImagesEntry.COLUMN_NAME_UserID) : "";
            String picUrl = params.has(UserImagesEntry.COLUMN_NAME_FullSizePicURL) ?
                    params.getString(UserImagesEntry.COLUMN_NAME_FullSizePicURL) : "";

            int count = params.has("count") ? params.getInt("count") : 10;

            String query = UsersEntry.COLUMN_NAME_UserID + " = ?";
            query += picUrl != null && !picUrl.isEmpty() ?
                    (!query.isEmpty() ? " AND " : "") + UserImagesEntry.COLUMN_NAME_FullSizePicURL + " = ?" : "";

            List<String> selectionArgs = new ArrayList<String>();

            selectionArgs.add(userId);
            if(picUrl != null && !picUrl.isEmpty()) selectionArgs.add(picUrl);

            String[] arrArgs = new String[selectionArgs.size()];
            selectionArgs.toArray(arrArgs);

            String sortOrder = UsersEntry._ID + " DESC";

            String[] projection = {
                    UserImagesEntry._ID,
                    UserImagesEntry.COLUMN_NAME_UserID,
                    UserImagesEntry.COLUMN_NAME_Username,
                    UserImagesEntry.COLUMN_NAME_FullName,
                    UserImagesEntry.COLUMN_NAME_FullSizePicURL,
                    UserImagesEntry.COLUMN_NAME_PicURL,
                    UserImagesEntry.COLUMN_NAME_PicURLHD
            };

            UserImagesDBHelper dbHelper = new UserImagesDBHelper(context);

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.query(
                    UserImagesEntry.TABLE_NAME,
                    projection,
                    query,
                    arrArgs,
                    null,
                    null,
                    sortOrder
            );

            while (cursor.moveToNext() && count > 0){
                --count;

                try {
                    JSONObject obj = new JSONObject();

                    obj.put(UserImagesEntry._ID, cursor.getString(cursor.getColumnIndex(UsersEntry._ID)));
                    obj.put(UserImagesEntry.COLUMN_NAME_UserID, cursor.getString(cursor.getColumnIndex(UserImagesEntry.COLUMN_NAME_UserID)));
                    obj.put(UserImagesEntry.COLUMN_NAME_Username, cursor.getString(cursor.getColumnIndex(UserImagesEntry.COLUMN_NAME_Username)));
                    obj.put(UserImagesEntry.COLUMN_NAME_FullName, cursor.getString(cursor.getColumnIndex(UserImagesEntry.COLUMN_NAME_FullName)));
                    obj.put(UserImagesEntry.COLUMN_NAME_FullSizePicURL, cursor.getString(cursor.getColumnIndex(UserImagesEntry.COLUMN_NAME_FullSizePicURL)));
                    obj.put(UserImagesEntry.COLUMN_NAME_PicURL, cursor.getString(cursor.getColumnIndex(UserImagesEntry.COLUMN_NAME_PicURL)));
                    obj.put(UserImagesEntry.COLUMN_NAME_PicURLHD, cursor.getString(cursor.getColumnIndex(UserImagesEntry.COLUMN_NAME_PicURLHD)));

                    retList.add(obj);
                }
                catch (Exception e){}
            }

            if(!cursor.isClosed()) cursor.close();

            dbHelper.close();
        }
        catch (Exception e){}

        return retList;
    }

    private static long _new_user_image(Context context, JSONObject params){
        try {
            if (params == null) return 0;

            ContentValues values = new ContentValues();
            if(params.has(UserImagesEntry.COLUMN_NAME_UserID))
                values.put(UserImagesEntry.COLUMN_NAME_UserID, params.getString(UserImagesEntry.COLUMN_NAME_UserID));
            if(params.has(UserImagesEntry.COLUMN_NAME_Username))
                values.put(UserImagesEntry.COLUMN_NAME_Username, params.getString(UserImagesEntry.COLUMN_NAME_Username));
            if(params.has(UserImagesEntry.COLUMN_NAME_FullName))
                values.put(UserImagesEntry.COLUMN_NAME_FullName, params.getString(UserImagesEntry.COLUMN_NAME_FullName));
            if(params.has(UserImagesEntry.COLUMN_NAME_FullSizePicURL))
                values.put(UserImagesEntry.COLUMN_NAME_FullSizePicURL, params.getString(UserImagesEntry.COLUMN_NAME_FullSizePicURL));
            if(params.has(UserImagesEntry.COLUMN_NAME_PicURL))
                values.put(UserImagesEntry.COLUMN_NAME_PicURL, params.getString(UserImagesEntry.COLUMN_NAME_PicURL));
            if(params.has(UserImagesEntry.COLUMN_NAME_PicURLHD))
                values.put(UserImagesEntry.COLUMN_NAME_PicURLHD, params.getString(UserImagesEntry.COLUMN_NAME_PicURLHD));

            UserImagesDBHelper dbHelper = new UserImagesDBHelper(context);

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            long id = db.insert(UserImagesEntry.TABLE_NAME, null, values);

            dbHelper.close();

            return id;
        }
        catch (Exception e) {
            return 0;
        }
    }

    private static boolean _update_user_image(Context context, JSONObject params){
        try {
            if (params == null) params = new JSONObject();

            String userId = params.has(UserImagesEntry.COLUMN_NAME_UserID) ?
                    params.getString(UserImagesEntry.COLUMN_NAME_UserID) : "";
            String picUrl = params.has(UserImagesEntry.COLUMN_NAME_FullSizePicURL) ?
                    params.getString(UserImagesEntry.COLUMN_NAME_FullSizePicURL) : "";

            if(userId == null || userId.isEmpty() || picUrl == null || picUrl.isEmpty()) return false;

            String query = UserImagesEntry.COLUMN_NAME_UserID + " = ?, " +
                    UserImagesEntry.COLUMN_NAME_FullSizePicURL + " = ?";
            String[] queryArgs = { userId, picUrl };

            ContentValues values = new ContentValues();
            if(params.has(UserImagesEntry.COLUMN_NAME_Username))
                values.put(UserImagesEntry.COLUMN_NAME_Username, params.getString(UserImagesEntry.COLUMN_NAME_Username));
            if(params.has(UserImagesEntry.COLUMN_NAME_FullName))
                values.put(UserImagesEntry.COLUMN_NAME_FullName, params.getString(UserImagesEntry.COLUMN_NAME_FullName));
            if(params.has(UserImagesEntry.COLUMN_NAME_PicURL))
                values.put(UserImagesEntry.COLUMN_NAME_PicURL, params.getString(UserImagesEntry.COLUMN_NAME_PicURL));
            if(params.has(UserImagesEntry.COLUMN_NAME_PicURLHD))
                values.put(UserImagesEntry.COLUMN_NAME_PicURLHD, params.getString(UserImagesEntry.COLUMN_NAME_PicURLHD));

            UserImagesDBHelper dbHelper = new UserImagesDBHelper(context);

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            int count = db.update(
                    UserImagesEntry.TABLE_NAME,
                    values,
                    query,
                    queryArgs
            );

            dbHelper.close();

            return count > 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean new_user_image(Context context, JSONObject params) {
        try {
            if (params == null) params = new JSONObject();

            JSONObject searchParams = new JSONObject();
            if(params.has(UserImagesEntry.COLUMN_NAME_UserID))
                searchParams.put(UserImagesEntry.COLUMN_NAME_UserID, params.getString(UserImagesEntry.COLUMN_NAME_UserID));
            if(params.has(UserImagesEntry.COLUMN_NAME_FullSizePicURL))
                searchParams.put(UserImagesEntry.COLUMN_NAME_FullSizePicURL, params.getString(UserImagesEntry.COLUMN_NAME_FullSizePicURL));
            searchParams.put("count", 1);

            List<JSONObject> userImages = getUserImages(context, searchParams);

            return userImages.size() > 0 ? _update_user_image(context, params) : _new_user_image(context, params) > 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    private static boolean _follow_unfollow(Context context, JSONObject params, boolean follow){
        try {
            if (params == null) params = new JSONObject();

            String userId = params.has(UsersEntry.COLUMN_NAME_UserID) ? params.getString(UsersEntry.COLUMN_NAME_UserID) : "";
            if(userId == null || userId.isEmpty()) return false;

            String query = UsersEntry.COLUMN_NAME_UserID + " = ?";
            String[] queryArgs = { userId };

            ContentValues values = new ContentValues();
            values.put(UsersEntry.COLUMN_NAME_Following, follow ? "true" : "false");

            UsersDBHelper dbHelper = new UsersDBHelper(context);

            SQLiteDatabase db = dbHelper.getReadableDatabase();

            int count = db.update(
                    UsersEntry.TABLE_NAME,
                    values,
                    query,
                    queryArgs
            );

            dbHelper.close();

            return count > 0;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static boolean follow(Context context, JSONObject params) {
        return _follow_unfollow(context, params, true);
    }

    public static boolean unfollow(Context context, JSONObject params) {
        return _follow_unfollow(context, params, false);
    }
}
