package com.android.instaprofilegrabber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Ramin on 8/3/2017.
 */

public class LoginAlert {
    private final static int DAYS_UNTIL_PROMPT = 4;//Min number of days

    public static void app_launched(final Context context) {
        if (RUtil.is_logged_in(context)) return;

        final SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        long dayMiliSeconds = 24 * 60 * 60 * 1000;
        long now = System.currentTimeMillis();

        Long date_firstLaunch = prefs.getLong(context.getString(R.string.preference_login_datefirstlaunch), 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = now;
            prefs.edit().putLong(context.getString(R.string.preference_login_datefirstlaunch), date_firstLaunch).apply();
        }

        long date_lastLaunch = prefs.getLong(context.getString(R.string.preference_login_datelastlaunch), 0);

        if(System.currentTimeMillis() < Math.max(date_firstLaunch, date_lastLaunch) + (DAYS_UNTIL_PROMPT * dayMiliSeconds)) return;

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    show_dialog(context, prefs.edit());
                }
            }, 30000);
        } catch (Exception ex) {
            //Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private static void show_dialog(final Context context, final SharedPreferences.Editor editor) {
        View view = LayoutInflater.from(context).inflate(R.layout.login_alert, null);

        final RDialog dialog = new RDialog(MainActivity.dialogsManager, true);

        view.findViewById(R.id.lbp_right_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(MainActivity.Activity, InstagramLogin.class));
                dialog.hide();
            }
        });

        view.findViewById(R.id.lbp_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        editor.putLong(context.getString(R.string.preference_login_datelastlaunch), System.currentTimeMillis()).apply();

        dialog.show(view);
    }
}
