package com.android.instaprofilegrabber;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;

/**
 * Created by Ramin on 8/9/2017.
 */

public class Fragment_Recent extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View RootView = null;

    public Fragment_Recent() {
    }

    public static Fragment_Recent newInstance(int sectionNumber) {
        Fragment_Recent fragment = new Fragment_Recent();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (RootView != null) return RootView;

        RootView = inflater.inflate(R.layout.recent, container, false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initialize((LinearLayout) RootView.findViewById(R.id.usersContainer));
            }
        }, 500);

        return RootView;
    }

    private void initialize(LinearLayout view) {
        try {
            FragmentActivity activity = getActivity();

            JSONArray users = RUtil.get_recent(activity);

            if (users.length() == 0) view.findViewById(R.id.recent_no_users).setVisibility(View.VISIBLE);
            else view.removeView(view.findViewById(R.id.recent_no_users));

            for (int i = 0; i < users.length(); ++i)
                new UserView(getActivity(), view, users.getJSONObject(i), false, true, false);
        } catch (Exception e) {
            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
