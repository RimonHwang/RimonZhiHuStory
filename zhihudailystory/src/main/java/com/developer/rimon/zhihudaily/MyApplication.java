package com.developer.rimon.zhihudaily;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

import cn.bmob.v3.Bmob;

/**
 * Created by cxm on 2016/8/17.
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    public static AppConfig appConfig;

    @Override
    public void onCreate() {
        super.onCreate();
        appConfig = new AppConfig(getApplicationContext());
        Bmob.initialize(this, Constants.Bmob_APP_ID);

        if (shouldInit()) {
            MiPushClient.registerPush(this, Constants.MI_PUSH_APP_ID, Constants.MI_PUSH_APP_KEY);
        }
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
