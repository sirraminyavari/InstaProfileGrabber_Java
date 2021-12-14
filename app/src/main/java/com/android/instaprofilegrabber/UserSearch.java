package com.android.instaprofilegrabber;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramin on 8/10/2017.
 */

public class UserSearch {
    public UserSearch(String text){
        if(!text.equals(null) && !text.isEmpty() && text.length() > 1) new DoSearch().execute(text);
    }

    public void onSearchComplete(List<SuggestedUser> arr){
    }

    class DoSearch extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... params) {
            try {
                String text = params[0];

                    return new HttpRequest().send("https://www.instagram.com/web/search/topsearch/?query=" + text);
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(String result) {
            try {
                if(result == null || result.isEmpty()) {
                    onSearchComplete(new ArrayList<SuggestedUser>());
                    return;
                }

                JSONObject data = new JSONObject(result);

                if(!data.has("users")) {
                    onSearchComplete(new ArrayList<SuggestedUser>());
                    return;
                }

                List<SuggestedUser> ret = new ArrayList<SuggestedUser>();

                JSONArray found = data.getJSONArray("users");

                for(int i = 0; i < found.length(); ++i){
                    JSONObject user = found.getJSONObject(i).getJSONObject("user");

                    String username = user.getString("username");
                    String fullname = user.getString("full_name");
                    String picUrl = user.getString("profile_pic_url");

                    ret.add(new SuggestedUser(username, fullname, picUrl, picUrl, true));
                }

                onSearchComplete(ret);

            } catch (Exception e) {
            }
        }
    }
}
