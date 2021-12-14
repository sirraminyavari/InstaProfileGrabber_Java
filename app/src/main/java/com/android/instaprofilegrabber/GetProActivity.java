package com.android.instaprofilegrabber;

import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.instaprofilegrabber.purchase.PaymentHandler;

public class GetProActivity extends AppCompatActivity {
    private static GetProActivity Activity;
    public static DialogsManager dialogManager;

    private PaymentHandler paymentHandler;

    private boolean isCalledFromIPGMain = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_pro);

        Activity = this;

        //set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = GetProActivity.this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(GetProActivity.this, R.color.thm_color_verywarm));
        }
        //end of set status bar color

        GetProActivity.dialogManager = new DialogsManager(GetProActivity.this, (FrameLayout) findViewById(R.id.dialog_container));

        paymentHandler = new PaymentHandler(GetProActivity.this){
            @Override
            public void onPurchaseSucceed(String purchaseId, int price){
                RUtil.app_purchase_log(GetProActivity.this, purchaseId, price);
                RUtil.is_pro_version(GetProActivity.this, true);

                ((TextView) findViewById(R.id.payment_message)).setText(
                        ((TextView) findViewById(R.id.payment_message)).getText().toString().replace("[x]", purchaseId)
                );

                findViewById(R.id.payment_options).setVisibility(View.GONE);
                findViewById(R.id.payment_done).setVisibility(View.VISIBLE);

                RUtil.toast(GetProActivity.this, "پرداخت شما به کد رهگیری " + purchaseId + " با موفقیت انجام شد");

                try {
                    MainActivity.Activity.on_purchase();
                } catch (Exception ex) {
                }
            }

            @Override
            public void onPurchaseFailure(){
                RUtil.alert(GetProActivity.dialogManager, "پرداخت انجام نشد!");
            }
        };

        String caller = getIntent().getStringExtra("caller");
        if (caller != null && caller.equals("ipg_main")) isCalledFromIPGMain = true;

        //back button
        ((ImageView) findViewById(R.id.back_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                on_finish();
            }
        });
        //end of back button

        TextView buyButton = (TextView) findViewById(R.id.get_pro_buy);
        TextView buyButtonVip = (TextView) findViewById(R.id.get_pro_buy_vip);

        buyButton.setText("حمایت (" + RUtil.numbers_to_persian(paymentHandler.price_tomans() + "") + " تومان)");
        buyButtonVip.setText("حمایت ویژه (" + RUtil.numbers_to_persian(paymentHandler.price_tomans_special() + "") + " تومان)");

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentHandler.purchase();
            }
        });

        buyButtonVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paymentHandler.purchase_special();
            }
        });
    }

    private void on_finish() {
        finish();
        if (!isCalledFromIPGMain)
            startActivity(new Intent(GetProActivity.this, MainActivity.class));
    }

    @Override
    public void onBackPressed() {
        if (!dialogManager.pop()) on_finish();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(paymentHandler != null) paymentHandler.dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!paymentHandler.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
}