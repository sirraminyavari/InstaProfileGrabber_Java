package com.android.instaprofilegrabber;

import android.content.Context;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramin on 8/18/2017.
 */

public class DialogsManager {
    private Context context;
    private FrameLayout parent;
    private List<RDialog> dialogsList;

    public DialogsManager(Context context, FrameLayout parent) {
        this.context = context;
        this.parent = parent;

        dialogsList = new ArrayList<RDialog>();
    }

    public Context get_context() {
        return context;
    }

    public FrameLayout dialogs_container() {
        return parent;
    }

    public void new_dialog(RDialog dialog){
        dialogsList.add(dialog);
    }

    public boolean pop(){
        for(int i = dialogsList.size() - 1; i >= 0; --i){
            if(!dialogsList.get(i).isHidden()){
                dialogsList.get(i).hide();
                return true;
            }
        }

        return false;
    }
}
