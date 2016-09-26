package com.developer.rimon.zhihudaily.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.FrameLayout;

import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.MyApplication;
import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.adapter.DrawerRecyclerAdapter;
import com.developer.rimon.zhihudaily.entity.Collect;
import com.developer.rimon.zhihudaily.entity.DaoMaster;
import com.developer.rimon.zhihudaily.entity.DaoSession;
import com.developer.rimon.zhihudaily.entity.ThemeList;
import com.developer.rimon.zhihudaily.entity.ThemeListDao;
import com.developer.rimon.zhihudaily.entity.User;
import com.developer.rimon.zhihudaily.listener.OnGetListener;
import com.developer.rimon.zhihudaily.ui.fragment.StoryFragment;
import com.developer.rimon.zhihudaily.ui.fragment.ThemeFragment;
import com.developer.rimon.zhihudaily.utils.DateUtil;
import com.developer.rimon.zhihudaily.utils.HttpUtil;
import com.developer.rimon.zhihudaily.utils.weibo.AccessTokenKeeper;
import com.google.gson.Gson;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends BaseActivity {

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.drawer_recycler)
    RecyclerView drawerRecycler;
    @BindView(R.id.content)
    FrameLayout content;

    private boolean hasThemeFragment = false;

    private SharedPreferences sharedPreferences;
    private ArrayList<ThemeList.Other> otherArrayList = new ArrayList<>();
    private DrawerRecyclerAdapter drawerRecyclerAdapter;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private ThemeList themeList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);




        initDrawer();

        if (getIntent().getBooleanExtra(Constants.FROM_LOGIN_ACTIVITY, false)) {
            loadCollects();
            saveUserInfo();
        }

        showStoryFragment();

        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "theme.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        final ThemeListDao themeListDao = daoSession.getThemeListDao();
        final Gson gson = new Gson();
        List<ThemeList> arrayList = themeListDao.loadAll();

        if (arrayList == null || arrayList.size() == 0 || DateUtil.isExpired(arrayList.get(0).getCreatedTime())) {
            HttpUtil.getThemesList(new OnGetListener() {
                @Override
                public void onNext(Object object) {
                    themeList = (ThemeList) object;
                    themeList.setJsonString(gson.toJson(themeList));
                    Date date = new Date();
                    themeList.setCreatedTime(date.getTime());
                    themeListDao.insert(themeList);
                    otherArrayList.addAll(themeList.others);
                    drawerRecyclerAdapter.notifyDataSetChanged();
                }
            });
        } else {
            themeList = gson.fromJson(arrayList.get(0).getJsonString(), ThemeList.class);
            otherArrayList.addAll(themeList.others);
            drawerRecyclerAdapter.notifyDataSetChanged();
        }

        MyApplication.appConfig.checkUpdate(this,false);
        XiaomiUpdateAgent.update(this,true); //第二个参数为true时使用沙盒环境，否则使用线上环境
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(drawerRecycler)) {
            drawerLayout.closeDrawers();
        } else if (hasThemeFragment){
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(fragmentManager.findFragmentByTag("themeFragment"));
            fragmentTransaction.show(fragmentManager.findFragmentByTag("storyFragment")).commit();
            hasThemeFragment = false;
        }else {
            finish();
        }
    }

    private void initDrawer() {
        LinearLayoutManager drawerLayoutManager = new LinearLayoutManager(this);
        drawerRecyclerAdapter = new DrawerRecyclerAdapter(this, otherArrayList);
        drawerRecyclerAdapter.setOnThemeItemClickListener(new DrawerRecyclerAdapter.OnThemeItemClickListener() {
            @Override
            public void onClick(Bundle bundle) {
                if (bundle != null) {
                    showThemeFragment(bundle);
                } else {
                    showStoryFragment();
                }
                drawerLayout.closeDrawers();
            }
        });
        drawerRecycler.setLayoutManager(drawerLayoutManager);
        drawerRecycler.setAdapter(drawerRecyclerAdapter);
    }

    private void loadCollects() {
        BmobQuery<Collect> query = new BmobQuery<>();
        query.addWhereRelatedTo("collect", new BmobPointer(BmobUser.getCurrentUser()));
        query.findObjects(new FindListener<Collect>() {
            @Override
            public void done(List<Collect> list, BmobException e) {
                if (e == null && list.size() > 0) {
                    SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES_NAME_COLLECT, MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    for (Collect collect : list) {
                        editor.putString(collect.getId(), collect.getObjectId());
                    }
                    editor.apply();

                }
            }
        });
    }

    private void saveUserInfo() {
        sharedPreferences = getSharedPreferences(Constants.PREFERENCES_NAME_USERINFO, MODE_PRIVATE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(this);
        if (accessToken != null && accessToken.isSessionValid()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("uid", accessToken.getUid());
            params.put("access_token", accessToken.getToken());
            HttpUtil.getUserInfo(params, new OnGetListener() {
                @Override
                public void onNext(Object object) {
                    User weiboUser = (User) object;
                    weiboUser.setUsername(weiboUser.getScreen_name());
                    weiboUser.update(BmobUser.getCurrentUser().getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            loge(e);
                        }
                    });
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.KEY_USERINFO_NAME, weiboUser.getName());
                    editor.putString(Constants.KEY_USERINFO_PROFILE_IMAGE_URL, weiboUser.getProfile_image_url());
                    editor.apply();
                    drawerRecyclerAdapter.notifyItemChanged(0);
                }
            });
        }
    }

    private void showStoryFragment() {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        StoryFragment storyFragment = new StoryFragment();
        fragmentTransaction.replace(R.id.content, storyFragment,"storyFragment");
        fragmentTransaction.commit();
        hasThemeFragment = false;
    }

    private void showThemeFragment(Bundle bundle) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager.findFragmentByTag("themeFragment") != null){
            fragmentTransaction.remove(fragmentManager.findFragmentByTag("themeFragment"));
        }
        fragmentTransaction.hide(fragmentManager.findFragmentByTag("storyFragment"));
        ThemeFragment themeFragment = new ThemeFragment();
        themeFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.content, themeFragment,"themeFragment");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        hasThemeFragment = true;
    }


}
