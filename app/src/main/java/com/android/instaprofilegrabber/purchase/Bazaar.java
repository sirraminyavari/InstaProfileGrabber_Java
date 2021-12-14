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

public class Bazaar extends IPaymentProvider {
    private IabHelper mHelper;
    private IabHelper.OnIabPurchaseFinishedListener purchaseFinishedListener;
    private static String SKU_PROVERSION = "1";
    private static String SKU_PROVERSION_SPECIAL = "2";
    private String randomString;

    private int price;

    public Bazaar(Activity context) {
        super(context);

         this.randomString = "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ";

        String publicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwCaGqE6dm6PdbWeicZRfmRRgyWUC525yOSIds/krspvX9eQJvyCcfoonv1viNk8YABrGOSEGlH/qRsNrHfw7cpp7O8OWxP7FKUsz7JiVrD0BcpKmPv4KOKASA0gLSkbiZgkHpAa3C+bqJSaHfZMjPAEsA9PGIMySlIJWjsbm+vDt2oZKKGHquzBuAMpB6BgcULY1xIgs4ZAa7z5Vv3fGJSz0nAkU08KDFVGwuyThd0CAwEAAQ==";

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
        return 2990;
    }

    public int price_tomans_special(){
        return 4990;
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data){
        return mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    private void check_purchased_bazaar() {
        if(mHelper == null) return;

        IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) return;

                String proPrice = inventory.getSkuDetails(SKU_PROVERSION).getPrice();
                String proSpecialPrice = inventory.getSkuDetails(SKU_PROVERSION_SPECIAL).getPrice();

                boolean hasPro = inventory.hasPurchase(SKU_PROVERSION);
                boolean hasProS = inventory.hasPurchase(SKU_PROVERSION_SPECIAL);

                if(!hasPro && !hasProS){
                    //purchase_request_bazaar();
                }
            }
        };

        List<String> additionalSkuList = new ArrayList<String>();
        additionalSkuList.add(SKU_PROVERSION);
        additionalSkuList.add(SKU_PROVERSION_SPECIAL);
        mHelper.queryInventoryAsync(true, additionalSkuList, mGotInventoryListener);
    }
}
