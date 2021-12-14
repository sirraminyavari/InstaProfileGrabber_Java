package com.android.instaprofilegrabber;

import java.util.List;

/**
 * Created by Ramin on 8/10/2017.
 */

public class SuggestedUser {
    public String Username;
    public String FullName;
    public String ProfilePicURL;
    public String ProfilePicURLHD;
    public boolean Searched;

    public SuggestedUser(){
        Username = "";
        FullName = "";
        ProfilePicURL = "";
        ProfilePicURLHD = "";
        Searched = false;
    }

    public SuggestedUser(String username, String fullName, String profilePicURL, String profilePicURLHD, boolean searched){
        this.Username = username == null ? "" : username;
        this.FullName = fullName == null ? "" : fullName;
        this.ProfilePicURL = profilePicURL == null ? "" : profilePicURL;
        this.ProfilePicURLHD = profilePicURLHD == null ? "" : profilePicURLHD;
        this.Searched = searched;
    }

    public static SuggestedUser[] toArray(List<SuggestedUser> lst){
        if(lst == null) return new SuggestedUser[]{};

        SuggestedUser[] arr = new SuggestedUser[lst.size()];

        for(int i = 0; i < lst.size(); ++i)
            arr[i] = lst.get(i);

        return arr;
    }
}
