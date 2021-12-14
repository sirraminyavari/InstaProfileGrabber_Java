package com.android.instaprofilegrabber;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by Ramin on 8/2/2017.
 */

public class RClipboard {
    private Context context;
    private boolean Do = false;

    public RClipboard(Context context) {
        this.context = context;
        Do = false;
    }

    public void run() {
        final ClipboardManager mCM = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);

        mCM.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                if (!Do) return;

                int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) return;

                String newClip = mCM.getText().toString();
                boolean isInstaMedia = Pattern.compile("https://(www\\.)?instagram\\.com/p/[A-Za-z0-9_\\-]+/?").matcher(newClip).matches();
                boolean isUserName = !isInstaMedia && Pattern.compile("https://(www\\.)?instagram\\.com/[A-Za-z0-9._\\-]+/?").matcher(newClip).matches();

                if (isInstaMedia) {
                    String shortCode = newClip;
                    if (shortCode.charAt(shortCode.length() - 1) == '/')
                        shortCode = shortCode.substring(0, shortCode.length() - 1);
                    shortCode = shortCode.substring(shortCode.lastIndexOf('/') + 1);

                    new ExtractInstaMediaTask().execute(newClip + "?__a=1", shortCode);
                }
                else if (isUserName && RUtil.profile_downloadable()) {
                    final String username = newClip.substring(newClip.indexOf("instagram.com/") + "instagram.com/".length()).replace("/", "");

                    new UserGet(context, username, null){
                        @Override
                        public void onUserGet(JSONObject user) {
                            try {
                                String imageUrl = user.has(UserImagesContract.UserImagesEntry.COLUMN_NAME_PicURLHD) ?
                                        user.getString(UserImagesContract.UserImagesEntry.COLUMN_NAME_PicURLHD) :
                                        user.getString(UsersContract.UsersEntry.COLUMN_NAME_PicURL);

                                if(imageUrl != null && !imageUrl.isEmpty())
                                    new DownloadTask().execute(RUtil.get_full_size_url(imageUrl), username, "true");
                            } catch (Exception ex) {
                            }
                        }
                    };
                }
            }
        });
    }

    public void start() {
        Do = true;
    }

    public void stop() {
        Do = false;
    }

    private class ExtractInstaMediaTask extends AsyncTask<String, Void, ArrayList<String>> {
        private Exception exception;
        private String shortCode;

        protected ArrayList<String> doInBackground(String... params) {
            try {
                String res = new HttpRequest().send(params[0]);
                shortCode = params[1];

                JSONObject obj = new JSONObject(res);

                ArrayList<String> urls = new ArrayList<String>();

                if (!obj.has("graphql")) return urls;

                obj = obj.getJSONObject("graphql");

                if (!obj.has("shortcode_media")) return urls;

                obj = obj.getJSONObject("shortcode_media");

                urls.add(obj.has("video_url") ? obj.getString("video_url") :
                        (obj.has("display_url") ? obj.getString("display_url") : ""));

                if (obj.has("edge_sidecar_to_children")) {
                    obj = obj.getJSONObject("edge_sidecar_to_children");
                    JSONArray edges = !obj.has("edges") ? new JSONArray() : obj.getJSONArray("edges");

                    if (edges.length() > 0) urls.clear();

                    for (int i = 0, lnt = edges.length(); i < lnt; ++i) {
                        if (!edges.getJSONObject(i).has("node")) continue;

                        JSONObject nd = edges.getJSONObject(i).getJSONObject("node");
                        urls.add(nd.has("video_url") ? nd.getString("video_url") :
                                (nd.has("display_url") ? nd.getString("display_url") : ""));
                    }
                }

                return urls;
            } catch (Exception e) {
                this.exception = e;
                return null;
            }
        }

        protected void onPostExecute(ArrayList<String> urls) {
            do_download(0, urls);
        }

        protected void do_download(final int index, final ArrayList<String> urls){
            if (urls.size() <= index) return;

            try {
                if (urls.get(index) != null && !urls.get(index).isEmpty()){
                    (new DownloadTask(){
                        @Override
                        public void done(){
                            do_download(index + 1, urls);
                        }
                    }).execute(urls.get(index), shortCode, "false");
                }
                else do_download(index + 1, urls);
            } catch (Exception ex) {
                //Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private class DownloadTask extends AsyncTask<Object, Integer, String> {
        private String filePath;
        private boolean isImage;
        private boolean isUser;

        @Override
        protected String doInBackground(Object... params) {
            String fileUrl = (String) params[0];
            String shortCode = (String) params[1];
            isUser = params.length > 2 && ((String) params[2]).toLowerCase() == "true";

            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1).toLowerCase();
            String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
            fileName = fileName.substring(0, fileName.lastIndexOf("."));

            isImage = ext.equals("jpg");

            //prechecks
            String dirName = Environment.getExternalStorageDirectory() +
                    "/" + context.getString(R.string.app_folder) + (isUser ? "" :
                    "/" + context.getString(isImage ? R.string.image_folder : R.string.video_folder));

            File direct = new File(dirName);

            if (!direct.exists()) {
                File wallpaperDirectory = new File(dirName);
                wallpaperDirectory.mkdirs();
            }

            if (shortCode != null && !shortCode.isEmpty()) {
                fileName = !isUser ? shortCode + context.getString(R.string.watermark_postfix) :
                    RUtil.username2filename(shortCode);
            }

            String tempFileName = fileName;

            for(int i = 1; (new File(direct, tempFileName + "." + ext)).exists(); ++i)
                tempFileName = fileName + "_" + i;

            fileName = tempFileName + "." + ext;

            File file = new File(direct, fileName);
            if (file.exists()) return "";
            //end of prechecks

            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(fileUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    return "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage();

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(dirName + "/" + fileName);

                byte data[] = new byte[4096];
                int count;

                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }

                    output.write(data, 0, count);
                }

                filePath = dirName + "/" + fileName;
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null) output.close();
                    if (input != null) input.close();
                } catch (Exception ignored) {
                }

                if (connection != null) connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                done();
                return;
            }

            //if (isImage && !RUtil.is_pro_version(context)) RUtil.add_watermark(context, filePath);
            MediaScannerConnection.scanFile(context, new String[]{filePath}, null, null);
            Toast.makeText(context, "در گالری ذخیره شد", Toast.LENGTH_LONG).show();

            done();
        }

        public void done(){}
    }
}