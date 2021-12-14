package com.android.instaprofilegrabber.purchase;

import android.app.Activity;
import android.content.Intent;

import com.android.instaprofilegrabber.util.IabHelper;
import com.android.instaprofilegrabber.util.IabResult;
import com.android.instaprofilegrabber.util.Purchase;

/**
 * Created by Ramin on 8/19/2017.
 */

public class IranApps extends IPaymentProvider {
    private IabHelper mHelper;
    private IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener;
    private static String SKU_PROVERSION = "1";
    private static String SKU_PROVERSION_SPECIAL = "2";
    private String randomString;

    private int price;

    public IranApps(Activity context) {
        super(context);

         this.randomString = "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ";

        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC+n0C/EV7HQxaDEw9qXJ9Wpn5PUX9O/NNTszexlyveLyizBqhyfeTzOpN+MF3zw9uy0IFkfliiPFTSWy9S3u+xZ+J0uKz4YjXtmg+rTIjyk2u9Fi6657dqZXq6n6l/tto9GikG4TjTRxo6oBnpDp9wsIv62wfqEqsLZSuri2dtvQIDAQAB";

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
