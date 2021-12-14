package com.android.instaprofilegrabber.purchase;

import android.app.Activity;
import android.content.Intent;

import com.android.instaprofilegrabber.R;

/**
 * Created by Ramin on 8/20/2017.
 */

public abstract class IPaymentProvider {
    private Activity context;

    public IPaymentProvider(Activity context){
        this.context = context;

    }

    public Activity getContext(){
        return context;
    }

    public abstract int price_tomans();

    public abstract int price_tomans_special();

    public void dispose(){}

    public void purchase(){}

    public void purchase_special(){}

    public void onPurchaseSucceed(String purchaseId, int price){}

    public void onPurchaseFailure(){}

    public boolean handleActivityResult(int requestCode, int resultCode, Intent data){ return false; }
}
