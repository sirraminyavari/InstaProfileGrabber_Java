package com.android.instaprofilegrabber;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by Ramin on 8/8/2017.
 */

public class RUtil {
    public static boolean profile_downloadable(){
        return false;
    }

    public static boolean is_pro_version(Context context) {
        try {
            SharedPreferences mPrefs =
                    context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            return mPrefs.getBoolean(context.getString(R.string.preference_is_pro_version), false);
        } catch (Exception ex) {
            return false;
        }
    }

    public static void is_pro_version(Context context, boolean value) {
        try {
            SharedPreferences mPrefs =
                    context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            if (value)
                mPrefs.edit().putBoolean(context.getString(R.string.preference_is_pro_version), true).apply();
            else
                mPrefs.edit().remove(context.getString(R.string.preference_is_pro_version)).apply();
        } catch (Exception ex) {
        }
    }

    public static boolean is_logged_in(Context context) {
        try {
            SharedPreferences mPrefs =
                    context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String token = mPrefs.getString(context.getString(R.string.preference_token_var_name), "");

            return token != null && !token.isEmpty();
        } catch (Exception ex) {
            return false;
        }
    }

    public static void set_token(Context context, String token) {
        context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .edit().putString(context.getString(R.string.preference_token_var_name), token).apply();
    }

    public static String get_token(Context context) {
        try {
            SharedPreferences mPrefs =
                    context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            return mPrefs.getString(context.getString(R.string.preference_token_var_name), "");
        } catch (Exception ex) {
            return "";
        }
    }

    public static String get_device_model() {
        try {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            return model.startsWith(manufacturer) ? model : manufacturer + " " + model;
        } catch (Exception ex) {
            return "";
        }
    }

    public static String get_device_id(Context context) {
        try {
            int stPermission = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);

            if (stPermission == PackageManager.PERMISSION_GRANTED) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                return telephonyManager.getDeviceId();
            } else return "";
        } catch (Exception ex) {
            return "";
        }
    }

    public static boolean has_memory_read_permission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean has_memory_write_permission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean has_phone_state_permission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    public static Typeface iran_sans(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/IRANSansMobile.ttf");
    }

    public static void toast(Context context, String text) {
        View layout = LayoutInflater.from(context).inflate(R.layout.r_toast, null).findViewById(R.id.toast_container);
        ((TextView) layout.findViewById(R.id.toast_text)).setText(text);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    public static void alert(DialogsManager dialogsManager, String text, boolean stick, String neverShowAgainPreferenceName) {
        new RAlert(dialogsManager, stick, neverShowAgainPreferenceName).show(text);
    }

    public static void alert(DialogsManager dialogsManager, String text, boolean stick) {
        RUtil.alert(dialogsManager, text, stick, null);
    }

    public static void alert(DialogsManager dialogsManager, String text, String neverShowAgainPreferenceName) {
        RUtil.alert(dialogsManager, text, false, neverShowAgainPreferenceName);
    }

    public static void alert(DialogsManager dialogsManager, String text) {
        RUtil.alert(dialogsManager, text, false, null);
    }

    public static int dp2px(float dp, Context context) {
        return (int) (dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static String numbers_to_persian(String input){
        return input.replaceAll("0", "۰").replaceAll("1", "۱").replaceAll("2", "۲").replaceAll("3", "۳").replaceAll("4", "۴")
                .replaceAll("5", "۵").replaceAll("6", "۶").replaceAll("7", "۷").replaceAll("8", "۸").replaceAll("9", "۹");
    }

    public static String unix_timestamp(){
        return ((Long)(System.currentTimeMillis() / 1000)).toString();
    }

    public static void set_agent_id(Context context){
        SharedPreferences mPrefs =
                context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String agentId = mPrefs.getString(context.getString(R.string.preference_agent_id), "");
        String resAgentId = context.getString(R.string.agent_id);

        if(agentId.isEmpty())
            mPrefs.edit().putString(context.getString(R.string.preference_agent_id), resAgentId.isEmpty() ? "no_agent" : resAgentId).apply();
    }

    public static String get_agent_id(Context context){
        String agentId = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .getString(context.getString(R.string.preference_agent_id), "");
        return agentId.isEmpty() ? context.getString(R.string.agent_id) : (agentId.equals("no_agent") ? "" : agentId);
    }

    public static Bitmap add_watermark(Context context, Bitmap bitmap) {
        try {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Bitmap result = Bitmap.createBitmap(w, h, bitmap.getConfig());

            int textSize = w / 15;
            int textSize_half = textSize / 2;
            int radiusSize = textSize / 4;

            Canvas canvas = new Canvas(result);
            canvas.drawBitmap(bitmap, 0, 0, null);

            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            paint.setTextSize(textSize);
            paint.setAntiAlias(true);

            String text = context.getString(R.string.app_name);

            Rect r = new Rect();
            paint.getTextBounds(text, 0, text.length(), r);

            float xPos = canvas.getWidth() / 2;
            float yPos = (canvas.getHeight() - (paint.descent() + paint.ascent()) + r.height()) / 2;

            paint.setTextAlign(Paint.Align.CENTER);

            paint.setColor(Color.BLACK);
            paint.setAlpha(100);
            float bgLeft = ((w - r.width()) / 2) - textSize_half;
            float bgTop = (h - r.height()) / 2;
            RectF bg = new RectF(bgLeft, bgTop, bgLeft + r.width() + textSize, bgTop + r.height() + textSize);
            if (Build.VERSION.SDK_INT >= 23)
                canvas.drawRoundRect(bg, radiusSize, radiusSize, paint);
            else canvas.drawRect(bg, paint);

            paint.setColor(Color.WHITE);
            paint.setAlpha(255);
            canvas.drawText(text, xPos, yPos, paint);

            return result;
        } catch (Exception ex) {
            return bitmap;
        }
    }

    public static boolean add_watermark(Context context, String path) {
        try {
            File file = new File(path);
            Bitmap bitmap = add_watermark(context, BitmapFactory.decodeFile(file.getAbsolutePath()));
            if (file.exists()) file.delete();
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public static Bitmap get_square_bitmap(Bitmap bitmap) {
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int lnt = Math.min(width, height);

            int xStart = width == lnt ? 0 : (int) Math.floor(((float) width - (float) lnt) / 2);
            int yStart = height == lnt ? 0 : (int) Math.floor(((float) height - (float) lnt) / 2);

            return Bitmap.createBitmap(bitmap, xStart, yStart, lnt, lnt);
        } catch (Exception ex) {
            return bitmap;
        }
    }

    public static String get_full_size_url(String url){
        return url;
        //return url.replaceAll("\\/s\\d{3}x\\d{3}", "/s1080x1080").replaceAll("\\/vp\\/", "/");
    }

    public static String username2filename(String username){
        try {
            Pattern pattern = Pattern.compile("[^a-zA-Z0-9_\\-\\.]");
            return pattern.matcher(username.isEmpty() ? "jpg" : username).replaceAll("") + "_" + (new Date().getTime());
        }
        catch (Exception ex) {
            return "jpg" + "_" + (new Date().getTime());
        }
    }

    public static void save(Context context, Bitmap bitmap, String username) {
        /*
        try {
            if (!is_pro_version(context)) bitmap = add_watermark(context, bitmap);
        } catch (Exception ex) {
        }
        */

        String dirName = Environment.getExternalStorageDirectory() + "/" + context.getString(R.string.app_folder);
        File direct = new File(dirName);

        if (!direct.exists()) {
            File wallpaperDirectory = new File(dirName);
            wallpaperDirectory.mkdirs();
        }

        try {
            String fileName = username2filename(username) + ".jpg";

            File file = new File(direct, fileName);
            if (file.exists()) file.delete();

            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            MediaScannerConnection.scanFile(context, new String[]{dirName + "/" + fileName}, null, null);

            RUtil.toast(context, "تصویر ذخیره شد");
        } catch (Exception e) {
            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            //e.printStackTrace();
        }
    }

    public static void add_to_recent(Context context, JSONObject user) {
        try {
            if (user == null || !user.has(UsersContract.UsersEntry.COLUMN_NAME_UserID)) return;

            SharedPreferences mPrefs =
                    context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String recent = mPrefs.getString(context.getString(R.string.preference_recent_users), "{}");

            JSONObject recentUsersDic = new JSONObject(recent);

            JSONArray recentUsers = recentUsersDic.has("users") ?
                    recentUsersDic.getJSONArray("users") : new JSONArray();

            JSONObject inserted = new JSONObject();

            JSONArray newUsers = new JSONArray();

            newUsers.put(user);
            inserted.put(user.getString(UsersContract.UsersEntry.COLUMN_NAME_UserID), true);

            for (int i = 0; newUsers.length() <= 10 && i < recentUsers.length(); ++i) {
                String id = recentUsers.getJSONObject(i).getString(UsersContract.UsersEntry.COLUMN_NAME_UserID);
                if (inserted.has(id)) continue;
                newUsers.put(recentUsers.getJSONObject(i));
                inserted.put(id, true);
            }

            JSONObject toBeSaved = new JSONObject();
            toBeSaved.put("users", newUsers);

            mPrefs.edit().putString(context.getString(R.string.preference_recent_users), toBeSaved.toString()).apply();
        } catch (Exception ex) {
        }
    }

    public static JSONArray get_recent(Context context) {
        try {
            SharedPreferences mPrefs =
                    context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String recent = mPrefs.getString(context.getString(R.string.preference_recent_users), "{}");

            JSONObject recentUsersDic = new JSONObject(recent);

            return recentUsersDic.has("users") ? recentUsersDic.getJSONArray("users") : new JSONArray();
        } catch (Exception ex) {
            return new JSONArray();
        }
    }

    public static void clear_recent(Context context) {
        try {
            context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                            .edit().remove(context.getString(R.string.preference_recent_users)).apply();
        } catch (Exception ex) {
        }
    }

    public static JSONObject dbUser2instaUser(JSONObject dbUser) {
        try {
            JSONObject usr = new JSONObject();

            if (dbUser.has(UsersContract.UsersEntry.COLUMN_NAME_UserID))
                usr.put("id", dbUser.getString(UsersContract.UsersEntry.COLUMN_NAME_UserID));
            else return null;

            if (dbUser.has(UsersContract.UsersEntry.COLUMN_NAME_Username))
                usr.put("username", dbUser.getString(UsersContract.UsersEntry.COLUMN_NAME_Username));
            else return null;

            if (dbUser.has(UsersContract.UsersEntry.COLUMN_NAME_FullName))
                usr.put("full_name", dbUser.getString(UsersContract.UsersEntry.COLUMN_NAME_FullName));

            if (dbUser.has(UsersContract.UsersEntry.COLUMN_NAME_PicURL))
                usr.put("profile_picture", dbUser.getString(UsersContract.UsersEntry.COLUMN_NAME_PicURL));
            else return null;

            return usr;
        } catch (Exception ex) {
            return null;
        }
    }

    public static void log(final Context context, String action, JSONArray users) {
        try {
            String usersStr = "";

            if (users != null && users.length() > 0) {
                JSONObject jsonObject = new JSONObject();
                JSONArray usersArr = new JSONArray();

                for (int i = 0; i < users.length(); ++i) {
                    JSONObject usr = RUtil.dbUser2instaUser(users.getJSONObject(i));
                    if (usr != null) usersArr.put(usr);
                }

                jsonObject.put("data", usersArr);

                usersStr = Base64.encodeToString(jsonObject.toString().getBytes(), Base64.NO_WRAP);
            }

            JSONObject obj = new JSONObject();
            obj.put("action", action);
            obj.put("device_id", RUtil.get_device_id(context));
            obj.put("device_model", Base64.encodeToString(RUtil.get_device_model().getBytes(), Base64.NO_WRAP));
            obj.put("users", usersStr);
            obj.put("time", RUtil.unix_timestamp());

            final SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            JSONObject toBeLogged = null;
            try {
                toBeLogged = new JSONObject(prefs.getString(context.getString(R.string.preference_to_be_logged), ""));
            }catch (Exception e){
                toBeLogged = new JSONObject();
            }

            if(!toBeLogged.has("items")) toBeLogged.put("items", new JSONArray());

            JSONArray items = toBeLogged.getJSONArray("items");

            items.put(obj);

            final long now = System.currentTimeMillis();

            for(int i = 0, lnt = items.length(); i < lnt; ++i)
                items.getJSONObject(i).put("timestamp", now);

            prefs.edit().putString(context.getString(R.string.preference_to_be_logged), toBeLogged.toString()).apply();

            long lastLogTime = prefs.getLong(context.getString(R.string.preference_last_log_time), 0);
            long intervalTime = 30 * 60 * 1000; //half and hour

            if(now > (lastLogTime + intervalTime)) {
                Map<String, String> data = new HashMap<String, String>();

                data.put("command", "ipglog");
                data.put("token", RUtil.get_token(context));
                data.put("data", Base64.encodeToString(toBeLogged.toString().getBytes(), Base64.NO_WRAP));

                new HttpRequest(){
                    @Override
                    public void onResponse(String response) {
                        try{
                            JSONObject res = new JSONObject(response);

                            if (res.has("result") && res.getString("result").equals("ok")) {
                                SharedPreferences.Editor editor = prefs.edit();

                                JSONObject curObj = new JSONObject(prefs.getString(context.getString(R.string.preference_to_be_logged), ""));
                                JSONArray curArr = curObj.has("items") ? curObj.getJSONArray("items") : new JSONArray();
                                JSONArray newArr = new JSONArray();

                                for(int i = 0, lnt = curArr.length(); i < lnt; ++i)
                                    if(curArr.getJSONObject(i).getLong("timestamp") != now) newArr.put(curArr.getJSONObject(i));

                                JSONObject newObj = new JSONObject();
                                newObj.put("items", newArr);

                                editor.putLong(context.getString(R.string.preference_last_log_time), now);
                                editor.putString(context.getString(R.string.preference_to_be_logged), newObj.toString());
                                editor.apply();
                            }
                        }
                        catch (Exception ex){
                        }
                    }
                }.send_async_post(context.getString(R.string.mysociome_api_url), data);
            }
        } catch (Exception ex) {
            //RUtil.toast(context, ex.toString());
        }
    }

    public static void log(Context context, String action, JSONObject user) {
        JSONArray arr = new JSONArray();
        arr.put(user);
        RUtil.log(context, action, arr);
    }

    public static void log(Context context, String action) {
        JSONArray arr = new JSONArray();
        RUtil.log(context, action, arr);
    }

    public static void app_purchase_log(final Context context, final String purchaseId, final int amount) {
        try {
            final String token = RUtil.get_token(context);

            if (purchaseId == null || purchaseId.isEmpty() || amount <= 0) return;

            String url = context.getString(R.string.mysociome_api_url);

            Map<String, String> data = new HashMap<String, String>();

            data.put("command", "apppurchaselog");
            if(token != null && !token.isEmpty()) data.put("token", token);
            data.put("app_name", "InstaProfileGrabber");
            data.put("purchase_id", purchaseId);
            data.put("amount", amount + "");
            data.put("payment_provider", context.getString(R.string.payment_provider));
            data.put("agent_id", RUtil.get_agent_id(context));

            new HttpRequest() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject res = new JSONObject(response);

                        JSONObject purchaseData = new JSONObject();
                        purchaseData.put("parchase_id", purchaseId);
                        purchaseData.put("amount", amount);

                        if (!res.has("result") || !res.getString("result").equals("ok")) {
                            context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                                    .edit().putString(context.getString(R.string.preference_purchase_data), purchaseData.toString()).apply();
                        }
                        else if(token == null || token.isEmpty()){
                            purchaseData.put("require_token", "true");

                            context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                                    .edit().putString(context.getString(R.string.preference_purchase_data), purchaseData.toString()).apply();
                        }
                        else {
                            context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                                    .edit().remove(context.getString(R.string.preference_purchase_data)).apply();
                        }
                    } catch (Exception ex) {
                    }
                }
            }.send_async_post(url, data);
        } catch (Exception ex) {
            //Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public static void send_purchase_data(Context context) {
        try {
            String strPurchaseData = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                    .getString(context.getString(R.string.preference_purchase_data), "");

            if(strPurchaseData == null || strPurchaseData.isEmpty()) return;

            JSONObject purchaseData = new JSONObject(strPurchaseData);

            String requireToken = purchaseData.getString("require_token");

            if(requireToken != null && requireToken.equals("true")) {
                String token = RUtil.get_token(context);
                if(token == null || token.isEmpty()) return;
            }

            String purchaseId = purchaseData.getString("parchase_id");
            int amount = purchaseData.getInt("amount");

            app_purchase_log(context, purchaseId, amount);
        } catch (Exception ex) {
        }
    }

    public static void open_rate_url(Context context, boolean showDialog){
        try {
            boolean isBazaar = showDialog && context.getString(R.string.payment_provider).toLowerCase().equals("bazaar");

            Intent intent = new Intent(isBazaar ? Intent.ACTION_EDIT : Intent.ACTION_VIEW);
            intent.setData(Uri.parse(context.getString(R.string.rate_url).replace("[PackageName]", context.getString(R.string.app_name_apk))));
            if(!context.getString(R.string.payment_package_name).isEmpty())
                intent.setPackage(context.getString(R.string.payment_package_name));
            context.startActivity(intent);
        }catch (Exception ex){
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse(context.getString(R.string.rate_url_public).replace("[PackageName]", context.getString(R.string.app_name_apk)))));
        }
    }

    public static void open_instagram(Context context, String username){
        try {
            Uri uri = Uri.parse("https://instagram.com/_u/" + username);
            Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);

            likeIng.setPackage("com.instagram.android");
            context.startActivity(likeIng);
        } catch (Exception e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://instagram.com/" + username)));
        }
    }
}
