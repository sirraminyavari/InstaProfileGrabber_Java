package com.android.instaprofilegrabber;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Ramin on 8/17/2017.
 */

public class RTextView extends TextView {
    public RTextView(Context context){
        super(context);
        this.setTypeface(RUtil.iran_sans(context));
    }

    public RTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(RUtil.iran_sans(context));
    }

    public RTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setTypeface(RUtil.iran_sans(context));
    }

    protected void onDraw (Canvas canvas) {
        super.onDraw(canvas);
    }
}
