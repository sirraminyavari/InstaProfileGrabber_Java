package com.android.instaprofilegrabber;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ramin on 8/11/2017.
 */

public class UserGet {
    private Context context;

    public UserGet(Context context, String username, String userId){
        this.context = context;

        if(userId != null && !userId.isEmpty()) {
            new RetrieveProfile().execute(userId);
        }
        else {
            (new GetUserID(){
                @Override
                public void callback(String uId){
                    new RetrieveProfile().execute(uId);
                }
            }).execute(username);
        }
    }

    public void onUserGet(JSONObject user){

    }

    private JSONObject update_db_user(String userId, String username, String fullname, String biography,
                                      String picUrl, String picUrlHD, String fullSizePicUrl, int followedBy){
        try {
            JSONObject searchParams = new JSONObject();
            searchParams.put(UsersContract.UsersEntry.COLUMN_NAME_UserID, userId);
            List<JSONObject> users = DBUtil.getUsers(context, searchParams);

            JSONObject dbUser = null;
            String following = "false";

            if (users.size() == 0) dbUser = new JSONObject();
            else {
                dbUser = users.get(0);

                following = dbUser.has(UsersContract.UsersEntry.COLUMN_NAME_Following) ?
                        (dbUser.getString(UsersContract.UsersEntry.COLUMN_NAME_Following).equals("true") ? "true" : "false") : "false";
            }

            dbUser.put(UsersContract.UsersEntry.COLUMN_NAME_UserID, userId);
            dbUser.put(UsersContract.UsersEntry.COLUMN_NAME_Username, username);
            dbUser.put(UsersContract.UsersEntry.COLUMN_NAME_FullName, fullname);
            dbUser.put(UsersContract.UsersEntry.COLUMN_NAME_Biography, biography);
            dbUser.put(UsersContract.UsersEntry.COLUMN_NAME_PicURL, picUrl);
            dbUser.put(UsersContract.UsersEntry.COLUMN_NAME_Private, "false");
            dbUser.put(UsersContract.UsersEntry.COLUMN_NAME_Following, following);
            dbUser.put(UsersContract.UsersEntry.COLUMN_NAME_FollowedBy, followedBy);
            dbUser.put(UserImagesContract.UserImagesEntry.COLUMN_NAME_FullSizePicURL, fullSizePicUrl);
            dbUser.put(UserImagesContract.UserImagesEntry.COLUMN_NAME_PicURLHD, picUrlHD);

            DBUtil.new_user(context, dbUser);
            DBUtil.new_user_image(context, dbUser);

            return dbUser;
        }
        catch (Exception ex){
            return null;
        }
    }

    class GetUserID extends AsyncTask<Object, Void, String> {
        private String Username;
        private Exception exception;

        public void callback(String userId){

        }

        protected String doInBackground(Object... params) {
            try {
                if (params.length > 0) Username = (String) params[0];

                //return new HttpRequest().send("https://www.instagram.com/" + Username + "/?__a=1");
                return new HttpRequest().send("https://www.instagram.com/" + Username);
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        protected void onPostExecute(String result) {
            try {
                if (result == null || exception != null) {
                    callback(null);
                    return;
                }

                /* __a=1 version
                JSONObject data = new JSONObject(result);
                if (data != null) data = data.getJSONObject("graphql");
                JSONObject user = data == null ? null : data.getJSONObject("user");
                if (user == null) user = data; //maybe the data is equivalent to the user
                if (user != null && user.has("id")) callback(user.getString("id"));
                else callback(null);
                */

                String str = "\"id\":\"";
                int ind = result.indexOf(str);
                if(ind > 0) result = result.substring(ind + str.length());
                if(result.indexOf("\"") > 0) result = result.substring(0, result.indexOf("\""));

                if(Pattern.compile("[A-Za-z0-9._\\-]+").matcher(result).matches()) callback(result);
                else callback(null);
            } catch (Exception e) {
                callback(null);
            }
        }
    }

    class RetrieveProfile extends AsyncTask<Object, Void, String> {
        private String UserID;
        private Exception exception;

        protected String doInBackground(Object... params) {
            try {
                if (params.length > 0) UserID = (String) params[0];

                if(UserID == null || UserID.isEmpty()) return null;
                else return new HttpRequest().send("https://i.instagram.com/api/v1/users/" + UserID + "/info/");
            } catch (Exception e) {
                exception = e;
                return null;
            }
        }

        protected void onPostExecute(String result) {
            try {
                if (result == null || exception != null) {
                    onUserGet(null);
                    return;
                }

                JSONObject data = new JSONObject(result);

                JSONObject user = data == null ? null : data.getJSONObject("user");

                if (user == null) user = data; //maybe the data is equivalent to the user

                if (user == null || !user.has("username")) {
                    onUserGet(null);
                    return;
                }

                String username = !user.has("username") ? "" : user.getString("username");
                String fullname = !user.has("full_name") ? "" : user.getString("full_name");
                String biography = !user.has("biography") ? "" : user.getString("biography");
                String picUrl = !user.has("profile_pic_url") ? "" : user.getString("profile_pic_url");
                int followedBy = !user.has("follower_count") ? 0 : user.getInt("follower_count");

                JSONArray hdVersions = user.has("hd_profile_pic_versions") ? user.getJSONArray("hd_profile_pic_versions") : null;
                JSONObject hdInfo = user.has("hd_profile_pic_url_info") ? user.getJSONObject("hd_profile_pic_url_info") : null;

                if(hdInfo == null && hdVersions != null && hdVersions.length() > 0)
                    hdInfo = hdVersions.getJSONObject(hdVersions.length() - 1);

                String picUrlHD = hdInfo != null && hdInfo.has("url") ? hdInfo.getString("url") : null;

                String fullSizePicUrl = picUrlHD == null || picUrlHD.isEmpty() ? picUrl : picUrlHD;

                if (fullSizePicUrl == null || fullSizePicUrl.isEmpty()) {
                    onUserGet(null);
                    return;
                } else fullSizePicUrl = RUtil.get_full_size_url(fullSizePicUrl);

                JSONObject dbUser =
                        update_db_user(UserID, username, fullname, biography, picUrl, picUrlHD, fullSizePicUrl, followedBy);

                onUserGet(dbUser);
            } catch (Exception e) {
            }
        }
    }
}
