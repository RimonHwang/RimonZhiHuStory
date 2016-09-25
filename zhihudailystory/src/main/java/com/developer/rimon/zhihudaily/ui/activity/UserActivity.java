package com.developer.rimon.zhihudaily.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.utils.weibo.AccessTokenKeeper;
import com.developer.rimon.zhihudaily.utils.FileUtil;
import com.developer.rimon.zhihudaily.utils.ImageLoaderUtils;
import com.developer.rimon.zhihudaily.utils.weibo.LogoutAPI;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserActivity extends BaseActivity {

    @BindView(R.id.logout_button)
    Button logoutButton;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_avatar)
    ImageView userAvatar;
    @BindView(R.id.user_name)
    TextView userName;

    private Oauth2AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        ButterKnife.bind(this);

        toolbar.setTitle("个人主页");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCES_NAME_USERINFO,MODE_PRIVATE);
        String name = preferences.getString(Constants.KEY_USERINFO_NAME,"");
        String url = preferences.getString(Constants.KEY_USERINFO_PROFILE_IMAGE_URL,"");
        userName.setText(name);

        if (!FileUtil.loadLocalImage(this,Environment.DIRECTORY_PICTURES,Constants.AVATAR_FILE_NAME,userAvatar)){
            ImageLoaderUtils.load(this,url,null,null,userAvatar);
        }

        mAccessToken = AccessTokenKeeper.readAccessToken(this);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LogoutAPI(UserActivity.this, Constants.WEIBO_APP_KEY, mAccessToken).logout(new LogOutRequestListener());
            }
        });
    }

    private class LogOutRequestListener implements RequestListener {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject obj = new JSONObject(response);
                    String value = obj.getString("result");
                    if ("true".equalsIgnoreCase(value)) {
                        AccessTokenKeeper.clear(UserActivity.this);
                        getSharedPreferences(Constants.PREFERENCES_NAME_USERINFO, MODE_PRIVATE).edit().clear().apply();
                        getSharedPreferences(Constants.PREFERENCES_NAME_COLLECT, MODE_PRIVATE).edit().clear().apply();
                        Toast.makeText(UserActivity.this, "注销成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(UserActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            Toast.makeText(UserActivity.this, "注销失败", Toast.LENGTH_SHORT).show();
        }
    }
}
