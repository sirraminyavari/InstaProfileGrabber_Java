package com.android.instaprofilegrabber;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.farsitel.bazaar.IUpdateCheckService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.ronash.pushe.Pushe;

public class MainActivity extends AppCompatActivity {
    public static MainActivity Activity;
    private ViewPager viewPager;
    private static PopupMenu popupMenu = null;
    public static DialogsManager dialogsManager;

    private static boolean REFRESHED_AFTER_PERMISSION_REQUEST = false;
    final public int PERMISSION_REQUEST_AND_REFRESH = 1;
    final public int PERMISSION_REQUEST = 2;
    final public int PERMISSION_REQUEST_AND_AUTO_DL = 3;
    final public int PERMISSION_REQUEST_SAVE_TO_GALLERY = 4;

    IUpdateCheckService service;
    UpdateServiceConnection connection;

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(connection != null) unbindService(connection);
        connection = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!RUtil.is_pro_version(MainActivity.this)) ir.adad.client.Adad.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        Activity = this;

        //set status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = MainActivity.this.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.thm_color_verywarm));
        }
        //end of set status bar color

        Pushe.initialize(this, true);

        MainActivity.dialogsManager = new DialogsManager(MainActivity.this, (FrameLayout) findViewById(R.id.dialog_container));

        //splash
        final RDialog splashDialog = new RDialog(MainActivity.dialogsManager, true){
            @Override
            public void onHide(){
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        check_permissions();
                        RateMe.app_launched(MainActivity.this);
                        GetProAlert.app_launched(MainActivity.this);
                        LoginAlert.app_launched(MainActivity.this);
                    }
                }, 4000);
            }
        };

        splashDialog.show(R.layout.splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashDialog.hide();
            }
        }, 3000);
        //end of splash

        hide_keyboard();
        initialize_suggestion();
        initialize_pager();
        initialize_auto_download_switch();
        set_buttons();
        initMenu();
        ping();
        initialize_payment();
        version_check_service();

        RUtil.log(MainActivity.this, getString(R.string.mysociome_log_app_launched));

        RUtil.set_agent_id(MainActivity.this);
        RUtil.send_purchase_data(MainActivity.this);
    }

    private void hide_keyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } else getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void reset_activity(){
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults == null || grantResults.length == 0) return;

        boolean permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

        switch (requestCode) {
            case PERMISSION_REQUEST_AND_REFRESH:
                if (permissionGranted) {
                    MainActivity.REFRESHED_AFTER_PERMISSION_REQUEST = true;
                    reset_activity();
                } else
                    RUtil.alert(MainActivity.dialogsManager,
                            "برای مشاهده تصاویری که از اینستاگرام دانلود شده اند، لازم است تا به برنامه اجازه خواندن از کارت حافظه را بدهید");
                return;
            case PERMISSION_REQUEST_AND_AUTO_DL:
                if (permissionGranted) ((Switch) findViewById(R.id.autodownload)).setChecked(true);
                else
                    RUtil.alert(MainActivity.dialogsManager,
                            "برای فعال شدن سرویس دانلود خودکار از اینستاگرام، لازم است تا به برنامه اجازه نوشتن در کارت حافظه را بدهید");
                return;
            case PERMISSION_REQUEST_SAVE_TO_GALLERY:
                if (!permissionGranted)
                    RUtil.alert(MainActivity.dialogsManager,
                            "برای ذخیره تصاویر در گالری، لازم است تا به برنامه اجازه نوشتن در کارت حافظه را بدهید");
                return;
        }
    }

    public static boolean CANLEAVE = false;

    @Override
    public void onBackPressed() {
        if(dialogsManager.pop()) return;

        if (CANLEAVE) {
            finish();
            System.exit(0);
        } else {
            RUtil.toast(MainActivity.this, "برای خروج دوباره کلید خروج را فشار دهید");

            CANLEAVE = true;

            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            CANLEAVE = false;
                        }
                    },
                    5000
            );
        }
    }

    private void check_permissions(){
        boolean hasReadPermission = RUtil.has_memory_read_permission(MainActivity.this);
        boolean hasWritePermission = RUtil.has_memory_write_permission(MainActivity.this);
        boolean hasPhoneStatePermission = RUtil.has_phone_state_permission(MainActivity.this);

        if (!hasReadPermission) {
            LinearLayout downloadsButton = (LinearLayout) findViewById(R.id.downloads_container);
            downloadsButton.setVisibility(View.GONE);
        }

        if (!hasReadPermission || !hasWritePermission || !hasPhoneStatePermission) {
            int count = 0, curItem = 0;
            boolean refreshNeeded = !hasReadPermission;

            if (!hasReadPermission) ++count;
            if (!hasWritePermission) ++count;
            if (!hasPhoneStatePermission && !REFRESHED_AFTER_PERMISSION_REQUEST) ++count;

            if (count > 0) {
                String perItems[] = new String[count];

                if (!hasReadPermission)
                    perItems[curItem++] = Manifest.permission.READ_EXTERNAL_STORAGE;
                if (!hasWritePermission)
                    perItems[curItem++] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
                if (!hasPhoneStatePermission && !REFRESHED_AFTER_PERMISSION_REQUEST)
                    perItems[curItem++] = Manifest.permission.READ_PHONE_STATE;

                ActivityCompat.requestPermissions(MainActivity.this, perItems,
                        refreshNeeded ? PERMISSION_REQUEST_AND_REFRESH : PERMISSION_REQUEST);
            }
        }
    }

    private void initialize_pager(){
        ViewPager mViewPager = MainActivity.this.viewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(new SectionsPagerAdapter(getSupportFragmentManager(),
                RUtil.has_memory_read_permission(MainActivity.this) ? 3 : 2));
    }

    private void initialize_auto_download_switch(){
        final Intent autoDownloadService = new Intent(MainActivity.this, AutoDownloadService.class);

        final Switch autoDLSwitch = (Switch) findViewById(R.id.autodownload);
        autoDLSwitch.setChecked(AutoDownloadService.Running);

        autoDLSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    if (RUtil.has_memory_write_permission(MainActivity.this)) {
                        startService(autoDownloadService);

                        String text = "برای دانلود پست ها در اینستاگرام، بر روی 'Copy Link' و برای دانلود تصویر پروفایل بر روی 'Copy Profile URL' کلیک کنید";
                        if(!RUtil.profile_downloadable())
                            text =  "برای دانلود پست ها در اینستاگرام، بر روی 'Copy Link' کلیک کنید";

                        RUtil.alert(MainActivity.dialogsManager, text, false,
                                MainActivity.this.getString(R.string.auto_dl_alert_never_show_again));
                    }
                    else {
                        autoDLSwitch.setChecked(false);
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                        }, PERMISSION_REQUEST_AND_AUTO_DL);
                    }
                } else stopService(autoDownloadService);
            }
        });
    }

    private void set_button_images(int page) {
        ImageView bookmarksButton = (ImageView) findViewById(R.id.bookmarks_button);
        ImageView recentButton = (ImageView) findViewById(R.id.recent_button);
        ImageView downloadsButton = (ImageView) findViewById(R.id.downloads_button);

        if (page == 0) bookmarksButton.setImageResource(R.mipmap.bookmark);
        else bookmarksButton.setImageResource(R.mipmap.bookmark_bright);

        if (page == 1) recentButton.setImageResource(R.mipmap.recent);
        else recentButton.setImageResource(R.mipmap.recent_bright);

        if (page == 2) downloadsButton.setImageResource(R.mipmap.download);
        else downloadsButton.setImageResource(R.mipmap.download_bright);
    }

    private void set_buttons() {
        final ViewPager pager = (ViewPager) findViewById(R.id.container);

        final LinearLayout bookmarksContainer = (LinearLayout) findViewById(R.id.bookmarks_container);
        final LinearLayout recentContainer = (LinearLayout) findViewById(R.id.recent_container);
        final LinearLayout downloadsContainer = (LinearLayout) findViewById(R.id.downloads_container);

        set_button_images(pager.getCurrentItem());

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                set_button_images(position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        bookmarksContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(0, true);
            }
        });

        recentContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(1, true);
            }
        });

        downloadsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pager.setCurrentItem(2, true);
            }
        });
    }

    final int GETPRO = 1;
    final int RATE = 2;
    final int SHARE = 3;
    final int FOLLOWME = 4;
    final int CLEARHISTORY = 5;
    final int LOGIN = 6;
    final int LOGOUT = 7;

    private void initMenu(){
        ImageView menuButton = (ImageView) findViewById(R.id.menuButton);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(popupMenu != null){
                    popupMenu.show();
                    return;
                }

                popupMenu = new PopupMenu(MainActivity.this, view);

                if(!RUtil.is_pro_version(MainActivity.this)) popupMenu.getMenu().add(GETPRO, GETPRO, GETPRO, "حمایت");
                popupMenu.getMenu().add(RATE, RATE, RATE, "امتیاز دادن به برنامه");
                popupMenu.getMenu().add(SHARE, SHARE, SHARE, "به اشتراک گذاری با دوستان");
                popupMenu.getMenu().add(FOLLOWME, FOLLOWME, FOLLOWME, "صفحه سازنده در اینستاگرام");
                popupMenu.getMenu().add(CLEARHISTORY, CLEARHISTORY, CLEARHISTORY, "پاک کردن تاریخچه");
                if(RUtil.is_logged_in(MainActivity.this)) popupMenu.getMenu().add(LOGOUT, LOGOUT, LOGOUT, "خروج از اینستاگرام");
                else popupMenu.getMenu().add(LOGIN, LOGIN, LOGIN, "ورود به اینستاگرام");

                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case GETPRO:
                                goto_getpro();
                                return true;
                            case RATE:
                                RUtil.open_rate_url(MainActivity.this, true);
                                return true;
                            case SHARE:
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_url).replace("[PackageName]", getString(R.string.app_name_apk)));
                                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Insta Profile Grabber");

                                startActivity(Intent.createChooser(intent, "Share"));
                                return true;
                            case FOLLOWME:
                                RUtil.open_instagram(MainActivity.this, "raminyavari");
                                return true;
                            case CLEARHISTORY:
                                RUtil.clear_recent(MainActivity.this);
                                reset_activity();
                                return true;
                            case LOGIN:
                                startActivity(new Intent(MainActivity.this, InstagramLogin.class));
                                return true;
                            case LOGOUT:
                                logout();
                                return true;
                        }

                        return false;
                    }
                });
            }
        });
    }

    public void logout(){
        MainActivity.this.getSharedPreferences(MainActivity.this
                .getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .edit().remove(MainActivity.this.getString(R.string.preference_token_var_name)).apply();
        popupMenu.getMenu().removeItem(LOGOUT);
        popupMenu.getMenu().add(LOGIN, LOGIN, LOGIN, "ورود به اینستاگرام");
    }

    public void on_login(){
        if(!RUtil.is_logged_in(MainActivity.this)) return;

        popupMenu.getMenu().removeItem(LOGIN);
        popupMenu.getMenu().add(LOGOUT, LOGOUT, LOGOUT, "خروج از اینستاگرام");

        has_purchased();
    }

    public void on_purchase(){
        if(!RUtil.is_pro_version(MainActivity.this)) return;

        findViewById(R.id.buy_button).setVisibility(View.GONE);
        popupMenu.getMenu().removeItem(GETPRO);
    }

    public void ping(){
        String token = RUtil.get_token(MainActivity.this);

        if(token == null || token.equals("")) return;

        Map<String, String> data = new HashMap<String, String>();
        data.put("command", "ping");
        data.put("token", token);

        new HttpRequest(){
            @Override
            public void onResponse(String response){
                try {
                    JSONObject res = new JSONObject(response);
                    if(res.has("result") && res.get("result").equals("nok")) {
                        RUtil.toast(MainActivity.this, "شما از اینستاگرام خارج شده اید؛ لطفا دوباره لاگین کنید");
                        logout();
                    }
                }
                catch (Exception ex){}
            }
        }.send_async_post(getString(R.string.mysociome_api_url), data);
    }

    public void has_purchased(){
        try {
            String token = RUtil.get_token(MainActivity.this);

            if(RUtil.is_pro_version(MainActivity.this) || token == null || token.isEmpty()) return;

            String url = MainActivity.this.getString(R.string.mysociome_api_url);

            Map<String, String> data = new HashMap<String, String>();

            data.put("command", "haspurchased");
            data.put("token", token);
            data.put("app_name", "InstaProfileGrabber");

            new HttpRequest(){
                @Override
                public void onResponse(String response){
                    try{
                        JSONObject res = new JSONObject(response);
                        if(res.has("result") && res.getString("result").equals("yes")){
                            RUtil.is_pro_version(MainActivity.this, true);
                            reset_activity();
                        }
                    }catch (Exception ex){}
                }
            }.send_async_post(url, data);
        } catch (Exception ex) {
            //Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void goto_getpro() {
        Intent intent = new Intent(MainActivity.this, GetProActivity.class);
        intent.putExtra("caller", "ipg_main");
        startActivity(intent);
    }

    //payment
    private void initialize_payment() {
        FloatingActionButton buyButton = (FloatingActionButton) findViewById(R.id.buy_button);

        if (RUtil.is_pro_version(MainActivity.this))
            ((CoordinatorLayout)buyButton.getParent()).removeView(buyButton);
        else {
            buyButton.setVisibility(View.VISIBLE);

            buyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goto_getpro();
                }
            });
        }
    }
    //end of payment

    //suggestion
    private static CustomAdapter_Recent SuggestionAdapter;
    private static Runnable SuggestionRunnable = null;

    private void initialize_suggestion() {
        try {
            ImageView searchButton = (ImageView) findViewById(R.id.searchButton);
            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show_user();
                }
            });

            final AutoCompleteTextView searchInput = (AutoCompleteTextView) findViewById(R.id.searchInput);

            searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    show_user();
                    return true;
                }
            });

            //if(!RUtil.is_pro_version(MainActivity.this)) return;
            if(!RUtil.is_pro_version(MainActivity.this) && !RUtil.is_logged_in(MainActivity.this)) return;

            final List<SuggestedUser> recentUsers = new ArrayList<SuggestedUser>();

            JSONArray recent = RUtil.get_recent(MainActivity.this);

            for (int i = 0; i < recent.length(); ++i) {
                String username = recent.getJSONObject(i).getString(UsersContract.UsersEntry.COLUMN_NAME_Username);
                String fullname = !recent.getJSONObject(i).has(UsersContract.UsersEntry.COLUMN_NAME_FullName) ? "" :
                        recent.getJSONObject(i).getString(UsersContract.UsersEntry.COLUMN_NAME_FullName);
                String picUrl = !recent.getJSONObject(i).has(UsersContract.UsersEntry.COLUMN_NAME_PicURL) ? "" :
                        recent.getJSONObject(i).getString(UsersContract.UsersEntry.COLUMN_NAME_PicURL);
                String picUrlHd = !recent.getJSONObject(i).has(UserImagesContract.UserImagesEntry.COLUMN_NAME_PicURLHD) ? "" :
                        recent.getJSONObject(i).getString(UserImagesContract.UserImagesEntry.COLUMN_NAME_PicURLHD);

                recentUsers.add(new SuggestedUser(username, fullname, picUrl, picUrlHd, false));
            }

            searchInput.setTypeface(RUtil.iran_sans(MainActivity.this));
            MainActivity.SuggestionAdapter =
                    new CustomAdapter_Recent(MainActivity.this, R.layout.search_item, SuggestedUser.toArray(recentUsers));
            searchInput.setAdapter(MainActivity.SuggestionAdapter);

            searchInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    searchInput.setText(MainActivity.SuggestionAdapter.data[i].Username);
                    searchInput.setSelection(searchInput.getText().toString().length());
                    show_user();
                }
            });

            final Handler handler = new Handler();

            searchInput.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                public void afterTextChanged(Editable s) {
                    final String text = s.toString();

                    if (MainActivity.SuggestionRunnable != null) {
                        handler.removeCallbacks(MainActivity.SuggestionRunnable);
                        MainActivity.SuggestionRunnable = null;
                    }

                    MainActivity.SuggestionRunnable = new Runnable() {
                        @Override
                        public void run() {
                            MainActivity.SuggestionRunnable = null;

                            new UserSearch(text) {
                                @Override
                                public void onSearchComplete(List<SuggestedUser> result) {
                                    try {
                                        JSONObject used = new JSONObject();

                                        List<SuggestedUser> lst = new ArrayList<SuggestedUser>();

                                        for (int i = 0; i < recentUsers.size(); ++i) {
                                            String[] parts = text.toLowerCase().split("\\s");
                                            String refTxt = (recentUsers.get(i).Username + " " + recentUsers.get(i).FullName).toLowerCase();

                                            for (int p = 0; p < parts.length; ++p) {
                                                if (refTxt.indexOf(parts[p]) >= 0) {
                                                    used.put(recentUsers.get(i).Username, true);
                                                    lst.add(recentUsers.get(i));
                                                    break;
                                                }
                                            }
                                        }

                                        for (int i = 0; lst.size() < 20 && i < result.size(); ++i) {
                                            if (used.has(result.get(i).Username)) continue;
                                            used.put(result.get(i).Username, true);
                                            lst.add(result.get(i));
                                        }

                                        MainActivity.SuggestionAdapter.notifyDataSetChanged();

                                        MainActivity.SuggestionAdapter =
                                                new CustomAdapter_Recent(MainActivity.this, R.layout.search_item, SuggestedUser.toArray(lst));

                                        searchInput.setAdapter(MainActivity.SuggestionAdapter);
                                    } catch (Exception ex) {
                                    }
                                }
                            };
                        }
                    };

                    handler.postDelayed(MainActivity.SuggestionRunnable, 1000);
                }
            });
        } catch (Exception ex) {
            //Toast.makeText(MainActivity.this, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void show_user() {
        try {
            String text = ((AutoCompleteTextView) findViewById(R.id.searchInput)).getText().toString().trim();
            if (text.isEmpty()) return;

            MainActivity.this.viewPager.setCurrentItem(0);

            final LinearLayout usersContainer = (LinearLayout) MainActivity.this.viewPager.findViewById(R.id.activity_main)
                    .findViewById(R.id.usersContainer);

            for (int i = 0; i < usersContainer.getChildCount(); ++i) {
                try {
                    View v = usersContainer.getChildAt(i).findViewById(R.id.username);

                    if (v == null) continue;

                    String un = ((TextView) v).getText().toString().toLowerCase();

                    if (text.toLowerCase().equals(un)) {
                        View theView = usersContainer.getChildAt(i);
                        //next view is adad advertisement
                        View nextView = RUtil.is_pro_version(MainActivity.this) ? null : usersContainer.getChildAt(i + 1);

                        usersContainer.removeView(theView);
                        if (nextView != null) usersContainer.removeView(nextView);

                        usersContainer.addView(theView, 0);

                        ((AutoCompleteTextView) findViewById(R.id.searchInput)).setText("");
                        hide_keyboard();

                        try {
                            String userJsonStr = ((TextView) usersContainer.getChildAt(i).findViewById(R.id.user_json)).getText().toString();

                            if (userJsonStr != null && !userJsonStr.isEmpty()) {
                                JSONObject uj = new JSONObject(userJsonStr);
                                RUtil.add_to_recent(MainActivity.this, uj);
                            }
                        } catch (Exception ex) {
                        }

                        return;
                    }
                } catch (Exception ex) {
                }
            }

            //show loading
            LayoutInflater inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View loadingView = inflater.inflate(R.layout.loading, null);
            ((TextView) loadingView.findViewById(R.id.loading_text)).setText(text);

            Glide
                    .with(MainActivity.this)
                    .load(R.mipmap.loading)
                    .into((ImageView) loadingView.findViewById(R.id.loading_gif));

            usersContainer.addView(loadingView, 0);
            //end of show loading

            new UserGet(MainActivity.this, text, null) {
                @Override
                public void onUserGet(JSONObject user) {
                    try {
                        //remove loading view
                        usersContainer.removeView(loadingView);

                        if (user == null) {
                            RUtil.toast(MainActivity.this, "خطا در بازیابی کاربر");
                            return;
                        }

                        View noUsers = usersContainer.findViewById(R.id.home_page_no_users);
                        if (noUsers != null)
                            usersContainer.removeView(usersContainer.findViewById(R.id.home_page_no_users));

                        ((AutoCompleteTextView) findViewById(R.id.searchInput)).setText("");
                        hide_keyboard();

                        RUtil.log(MainActivity.this, getString(R.string.mysociome_log_profile_visit), user);
                        add_to_recent(user);

                        new UserView(MainActivity.this, usersContainer, user, true, false, false);
                    } catch (Exception ex) {
                    }
                }
            };
        } catch (Exception ex) {
        }
    }

    private void add_to_recent(JSONObject user) {
        try {
            if (user == null) return;

            JSONObject toBeSaved = new JSONObject();

            String userId = !user.has(UsersContract.UsersEntry.COLUMN_NAME_UserID) ? "" :
                    user.getString(UsersContract.UsersEntry.COLUMN_NAME_UserID);
            String username = !user.has(UsersContract.UsersEntry.COLUMN_NAME_Username) ? "" :
                    user.getString(UsersContract.UsersEntry.COLUMN_NAME_Username);
            String fullname = !user.has(UsersContract.UsersEntry.COLUMN_NAME_FullName) ? "" :
                    user.getString(UsersContract.UsersEntry.COLUMN_NAME_FullName);
            String picUrl = !user.has(UsersContract.UsersEntry.COLUMN_NAME_PicURL) ? "" :
                    user.getString(UsersContract.UsersEntry.COLUMN_NAME_PicURL);
            String picUrlHd = !user.has(UserImagesContract.UserImagesEntry.COLUMN_NAME_PicURLHD) ? "" :
                    user.getString(UserImagesContract.UserImagesEntry.COLUMN_NAME_PicURLHD);

            if (userId != null && !userId.isEmpty())
                toBeSaved.put(UsersContract.UsersEntry.COLUMN_NAME_UserID, userId);
            if (username != null && !username.isEmpty())
                toBeSaved.put(UsersContract.UsersEntry.COLUMN_NAME_Username, username);
            if (fullname != null && !fullname.isEmpty())
                toBeSaved.put(UsersContract.UsersEntry.COLUMN_NAME_FullName, fullname);
            if (picUrl != null && !picUrl.isEmpty())
                toBeSaved.put(UsersContract.UsersEntry.COLUMN_NAME_PicURL, picUrl);
            if (picUrlHd != null && !picUrlHd.isEmpty())
                toBeSaved.put(UserImagesContract.UserImagesEntry.COLUMN_NAME_PicURLHD, picUrlHd);

            RUtil.add_to_recent(MainActivity.this, toBeSaved);
        } catch (Exception ex) {
        }
    }
    //end of suggestion

    class UpdateServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder boundService) {
            service = IUpdateCheckService.Stub.asInterface((IBinder) boundService);
            try {
                long vCode = service.getVersionCode(getString(R.string.app_name_apk));
                if(vCode > 0) update_available_dialog();
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    }

    protected void version_check_service(){
        connection = new UpdateServiceConnection();
        Intent i = new Intent(getString(R.string.update_check_service));
        i.setPackage(getString(R.string.payment_package_name));
        bindService(i, connection, Context.BIND_AUTO_CREATE);
    }

    protected void update_available_dialog(){
        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.update_available, null);

        final RDialog dialog = new RDialog(MainActivity.dialogsManager, true);

        view.findViewById(R.id.ua_right_now).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RUtil.open_rate_url(MainActivity.this, false);
                dialog.hide();
            }
        });

        view.findViewById(R.id.ua_later).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.hide();
            }
        });

        dialog.show(view);
    }
}