package com.android.instaprofilegrabber;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ramin on 8/3/2017.
 */

public class RateMe {
    private final static int DAYS_UNTIL_PROMPT = 3;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 3;//Min number of launches

    public static void app_launched(final Context context) {
        SharedPreferences prefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        if (prefs.getBoolean(context.getString(R.string.preference_rateme_dontshowagain), false))
            return;

        final SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong(context.getString(R.string.preference_rateme_launchcount), 0) + 1;
        editor.putLong(context.getString(R.string.preference_rateme_launchcount), launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong(context.getString(R.string.preference_rateme_datefirstlaunch), 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(context.getString(R.string.preference_rateme_datefirstlaunch), date_firstLaunch);
        }

        long dayMiliSeconds = 24 * 60 * 60 * 1000;

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch + (DAYS_UNTIL_PROMPT * dayMiliSeconds)) {
                try {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showRateDialog(context, editor);
                        }
                    }, 10000);
                } catch (Exception ex) {
                    //Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }

        editor.apply();
    }

    private static void showRateDialog(final Context context, final SharedPreferences.Editor editor) {
        LayoutInflater inflater = LayoutInflater.from(context);

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.rate, null);

        final RDialog dialog = new RDialog(MainActivity.dialogsManager, true);

        if (context.getString(R.string.market_name).isEmpty())
            ((TextView) view.findViewById(R.id.rate_message)).setText("ما را با ۵ ستاره همراهی کنید!");
        else
            ((TextView) view.findViewById(R.id.rate_message)).setText("ما را در " + context.getString(R.string.market_name) + " با ۵ ستاره همراهی کنید!");

        view.findViewById(R.id.rate_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RUtil.open_rate_url(context, true);
                dialog.hide();
            }
        });

        view.findViewById(R.id.rate_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        view.findViewById(R.id.rate_already_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editor != null)
                    editor.putBoolean(context.getString(R.string.preference_rateme_dontshowagain), true).apply();
                dialog.hide();
            }
        });

        dialog.show(view);
    }
}
