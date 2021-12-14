package com.android.instaprofilegrabber;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Ramin on 8/6/2017.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class Fragment_HomePage extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private View RootView = null;

    public Fragment_HomePage() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Fragment_HomePage newInstance(int sectionNumber) {
        Fragment_HomePage fragment = new Fragment_HomePage();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(RootView != null) return RootView;

        RootView = inflater.inflate(R.layout.home_page, container, false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initialize();
            }
        }, 500);

        return RootView;
    }

    private void initialize(){
        try {
            FragmentActivity activity = getActivity();

            LinearLayout usersContainer = (LinearLayout) RootView.findViewById(R.id.usersContainer);

            JSONObject searchParams = new JSONObject();
            searchParams.put(UsersContract.UsersEntry.COLUMN_NAME_Following, "true");
            List<JSONObject> users = DBUtil.getUsers(activity, searchParams);

            View noUsers = usersContainer.findViewById(R.id.home_page_no_users);
            if(noUsers != null) {
                if (users.size() == 0)
                    usersContainer.findViewById(R.id.home_page_no_users).setVisibility(View.VISIBLE);
                else
                    usersContainer.removeView(usersContainer.findViewById(R.id.home_page_no_users));
            }

            for (int i = 0, lnt = users.size(); i < lnt; ++i)
                new UserView(getActivity(), usersContainer, users.get(i), false, false, true);
        } catch (Exception e) {
            //Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }
}
