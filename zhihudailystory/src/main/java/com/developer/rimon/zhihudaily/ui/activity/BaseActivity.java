package com.developer.rimon.zhihudaily.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.developer.rimon.zhihudaily.MyApplication;
import com.developer.rimon.zhihudaily.R;

import cn.bmob.v3.exception.BmobException;

public class BaseActivity extends AppCompatActivity {

    public static String TAG = "BaseActivity";
    Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkTheme();
    }

    public void checkTheme(){
        boolean isNight = false;
        if (MyApplication.appConfig.isNighTheme()) {
            this.setTheme(R.style.NightTheme);
            isNight = true;
        } else {
            this.setTheme(R.style.DayTheme);
            isNight = false;
        }
    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String text) {
        if (!TextUtils.isEmpty(text)) {
            if (mToast == null) {
                mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
            } else {
                mToast.setText(text);
            }
            mToast.show();
        }
    }

    public void showToast(int resId) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), resId,Toast.LENGTH_SHORT);
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }

    public static void log(String msg) {
        Log.i(TAG, "===============================================================================");
        Log.i(TAG, msg);
    }

    public static void loge(Throwable e) {
        Log.i(TAG, "===============================================================================");
        if (e instanceof BmobException) {
            Log.e(TAG, "错误码：" + ((BmobException) e).getErrorCode() + ",错误描述：" + ((BmobException) e).getMessage());
        } else {
            if (e != null){
                Log.e(TAG, "错误描述：" + e.getMessage());
            }
        }
    }
}
