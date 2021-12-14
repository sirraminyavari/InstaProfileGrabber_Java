package com.android.instaprofilegrabber;

import android.os.AsyncTask;
import android.os.Looper;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ramin on 1/13/2017.
 */

public class HttpRequest {
    public void onResponse(String res) {
    }

    public void send_async(String url) {
        new AsyncGetRequest().execute(url);
    }

    public void send_async_post(String url, Map<String, String> data) {
        new AsyncPostRequest().execute(url, data);
    }

    public String send(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            conn.connect();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return convertInputStreamToString(is);
        } finally {
            if (is != null) is.close();
        }
    }

    public String send_post(String myurl, Map<String, String> data) throws IOException {
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(myurl);

            //add data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(data.size());

            Looper.prepare();

            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String val = entry.getValue();

                nameValuePairs.add(new BasicNameValuePair(key, val));
            }

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //execute http post
            HttpResponse response = httpclient.execute(httppost);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception ex) {
            return "";
        }
    }

    private String convertInputStreamToString(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder result = new StringBuilder();
        String line = "";
        while ((line = reader.readLine()) != null) result.append(line);
        return result.toString();
    }

    private class AsyncGetRequest extends AsyncTask<String, Void, String> {
        private Exception exception;

        protected String doInBackground(String... params) {
            try {
                return send(params[0]);
            } catch (Exception ex) {
                exception = ex;
                return "";
            }
        }

        protected void onPostExecute(String res) {
            try {
                //if (exception != null) Toast.makeText(MainActivity.Activity, exception.toString(), Toast.LENGTH_LONG).show();
                if(exception == null) onResponse(res);
            } catch (Exception e) {
                //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class AsyncPostRequest extends AsyncTask<Object, Void, String> {
        private Exception exception;

        protected String doInBackground(Object... params) {
            try {
                return send_post((String) params[0], (Map<String, String>) params[1]);
            } catch (Exception ex) {
                exception = ex;
                return "";
            }
        }

        protected void onPostExecute(String res) {
            try {
                //if (exception != null) Toast.makeText(MainActivity.Activity, exception.toString(), Toast.LENGTH_LONG).show();
                if(exception == null) onResponse(res);
            } catch (Exception e) {
                //Toast.makeText(MainActivity.Activity, e.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
