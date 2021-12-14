package com.android.instaprofilegrabber;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by Ramin on 8/16/2017.
 */

public class RDialog {
    private Context context;
    private FrameLayout parent;
    private FrameLayout container;
    private boolean stick;
    private boolean goingHidden;
    private boolean goingShown;
    private boolean isHidden = true;

    public RDialog(DialogsManager dialogsManager, boolean stick){
        dialogsManager.new_dialog(this);

        this.context = dialogsManager.get_context();
        this.parent = dialogsManager.dialogs_container();
        this.stick = stick;
    }

    public boolean isHidden(){
        return isHidden;
    }

    public void onHide(){
    }

    public void onShow(){
    }

    public void hide(){
        if(goingHidden || container == null || container.getParent() == null) return;

        goingHidden = true;

        container.animate()
                .alpha(0.0f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        parent.removeView(container);
                        if(parent.getChildCount() == 0) parent.setVisibility(View.GONE);
                        goingHidden = false;
                        isHidden = true;
                        onHide();
                    }
                });
    }

    public void show(int resId){
        show(LayoutInflater.from(context).inflate(resId, null));
    }

    public void show(View view){
        if(goingShown || container != null && container.getParent() != null) return;

        goingShown = true;

        parent.setVisibility(View.VISIBLE);

        if(container == null) {
            container = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.dialog, null);
            container.setLayoutParams(new ViewGroup.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            if(!stick){
                container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hide();
                    }
                });
            }
        }

        //prepare view
        view.setLayoutParams(new ViewGroup.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        //end of prepare view

        parent.addView(container);

        LinearLayout inner = (LinearLayout) container.findViewById(R.id.dialog_inner);
        inner.removeAllViews();
        inner.addView(view);

        container.setAlpha(0.0f);

        container.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        goingShown = false;
                        isHidden = false;
                        onShow();
                    }
                });
    }
}
