package com.android.instaprofilegrabber.purchase;

import android.app.Activity;
import android.content.Intent;

import com.android.instaprofilegrabber.RUtil;
import com.android.instaprofilegrabber.util.IabHelper;
import com.android.instaprofilegrabber.util.IabResult;
import com.android.instaprofilegrabber.util.Inventory;
import com.android.instaprofilegrabber.util.Purchase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ramin on 8/19/2017.
 */

public class Myket extends IPaymentProvider {
    private IabHelper mHelper;
    private IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener;
    private static String SKU_PROVERSION = "1";
    private static String SKU_PROVERSION_SPECIAL = "2";
    private String randomString;

    private int price;

    public Myket(Activity context) {
        super(context);

         this.randomString = "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ";

        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCoBn+iVMwjY7z9PeBHWZ4ov1KCYU2n27DUunTkGR0kbtTMXwXDYhuFGsZ2hMD7Non8/KAgCqve6fknND2vQ8i3TAZEOQGaQcCN4ctwimkAbcWDfj/STYnkVrQoIPuY9xXy9jT9JxX4PgIihqk2zub7m5FbyuNxTM1CPUdWIBmGxQIDAQAB";

        mHelper = new IabHelper(context, publicKey);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) return;

                purchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
                    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
                        if (result.isFailure())
                            onPurchaseFailure();
                        else if (purchase.getDeveloperPayload().equals(randomString) &&
                                (purchase.getSku().equals(SKU_PROVERSION) || purchase.getSku().equals(SKU_PROVERSION_SPECIAL))) {

                            onPurchaseSucceed(purchase.getOrderId(), price);
                        }
                    }
                };
            }
        });
    }

    @Override
    public void dispose(){
        super.dispose();

        if(mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    public void purchase(){
        try {
            if(mHelper == null) onPurchaseFailure();
            price = price_tomans();
            mHelper.launchPurchaseFlow(getContext(), SKU_PROVERSION, 10001, purchaseFinishedListener, randomString);
        }catch (Exception ex){
            onPurchaseFailure();
        }
    }

    @Override
    public void purchase_special(){
        try {
            if(mHelper == null) onPurchaseFailure();
            price = price_tomans_special();
            mHelper.launchPurchaseFlow(getContext(), SKU_PROVERSION_SPECIAL, 10002, purchaseFinishedListener, randomString);
        }catch (Exception ex){
            onPurchaseFailure();
        }
    }

    public int price_tomans(){
        return 1900;
    }

    public int price_tomans_special(){
        return 3900;
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data){
        return mHelper.handleActivityResult(requestCode, resultCode, data);
    }
}
