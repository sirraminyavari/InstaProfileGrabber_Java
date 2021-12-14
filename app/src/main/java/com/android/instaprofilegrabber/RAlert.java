package com.android.instaprofilegrabber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Ramin on 8/18/2017.
 */

public class RAlert {
    private RAlert rAlert;

    private Context context;
    private View container;
    private RDialog dialog;
    private String neverShowAgainPreferenceName;

    public RAlert(DialogsManager dialogsManager, boolean stick, String neverShowAgainPreferenceName) {
        this.rAlert = this;

        this.context = dialogsManager.get_context();
        this.neverShowAgainPreferenceName = neverShowAgainPreferenceName;

        dialog = new RDialog(dialogsManager, stick) {
            @Override
            public void onHide() {
                rAlert.never_show_again();
                rAlert.onHide();
            }

            @Override
            public void onShow() {
                rAlert.onShow();
            }
        };
    }

    private void never_show_again() {
        if (container == null || neverShowAgainPreferenceName == null || neverShowAgainPreferenceName.equals("") ||
                !((CheckBox) container.findViewById(R.id.alert_never_show_again_checkbox)).isChecked())
            return;

        context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .edit().putBoolean(neverShowAgainPreferenceName, true).apply();
    }

    public boolean isHidden() {
        return dialog == null || dialog.isHidden();
    }

    public void onShow() {
    }

    public void onHide() {
    }

    public void hide() {
        if (dialog == null) return;
        dialog.hide();
    }

    public void show(String text) {
        show(text, null);
    }

    public void show(String text, String okText) {
        if (neverShowAgainPreferenceName != null && !neverShowAgainPreferenceName.isEmpty() &&
                context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                        .getBoolean(neverShowAgainPreferenceName, false)) {
            onHide();
            return;
        }

        View view = container = LayoutInflater.from(context).inflate(R.layout.alert, null);

        if (neverShowAgainPreferenceName != null && !neverShowAgainPreferenceName.isEmpty())
            view.findViewById(R.id.alert_never_show_again_container).setVisibility(View.VISIBLE);

        ((TextView) view.findViewById(R.id.alert_message)).setText(text);

        //initialize ok button
        TextView okButton = (TextView) view.findViewById(R.id.alert_ok);

        if (okText != null && !okText.isEmpty()) okButton.setText(okText);

        view.findViewById(R.id.alert_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide();
            }
        });
        //end of initialize ok button

        dialog.show(view);
    }
}
