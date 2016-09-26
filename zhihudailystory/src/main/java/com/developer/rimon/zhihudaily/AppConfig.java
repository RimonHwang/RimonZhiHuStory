package com.developer.rimon.zhihudaily;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AlertDialog;

import com.xiaomi.market.sdk.UpdateResponse;
import com.xiaomi.market.sdk.UpdateStatus;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;
import com.xiaomi.market.sdk.XiaomiUpdateListener;

public class AppConfig {

    private SharedPreferences sharedPreferences;
    private static final String KEY_NIGHT_MODE_SWITCH = "night_theme";
    private static final String KEY_IGNORE_APP_VERSION_CODE = "version_code";

    AppConfig(final Context context) {
        sharedPreferences = context.getSharedPreferences("app_config", Application.MODE_PRIVATE);
    }

    //夜间模式
    public boolean isNighTheme() {
        return sharedPreferences.getBoolean(KEY_NIGHT_MODE_SWITCH, false);
    }

    public void setNightTheme(boolean isNightTheme) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_NIGHT_MODE_SWITCH, isNightTheme);
        editor.apply();
    }

    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void setIgnoreAppVersionCode(int versionCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_IGNORE_APP_VERSION_CODE, versionCode);
        editor.apply();
    }

    private int getIgnoreAppVersionCode() {
        return sharedPreferences.getInt(KEY_IGNORE_APP_VERSION_CODE, 0);
    }

    public void checkUpdate(final Context context, final boolean isManualCheckUpdate) {
        XiaomiUpdateAgent.setUpdateAutoPopup(false);
        XiaomiUpdateAgent.setUpdateListener(new XiaomiUpdateListener() {

            @Override
            public void onUpdateReturned(int updateStatus, final UpdateResponse updateInfo) {
                boolean ignored = getIgnoreAppVersionCode() == updateInfo.versionCode;
                switch (updateStatus) {
                    case UpdateStatus.STATUS_UPDATE:
                        PackageInfo packageInfo = null;
                        try {
                            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (packageInfo != null && packageInfo.versionCode < updateInfo.versionCode && !ignored || isManualCheckUpdate) {
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context, R.style.AlertDialog);
                            alertDialogBuilder.setTitle(R.string.alert_dialog_title);
                            alertDialogBuilder.setIcon(R.drawable.ic_launcher);
                            alertDialogBuilder.setMessage(R.string.alert_dialog_message + updateInfo.versionName);
                            alertDialogBuilder.setCancelable(true);

                            alertDialogBuilder.setPositiveButton(R.string.alert_dialog_positive_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    XiaomiUpdateAgent.arrange();
                                    dialog.dismiss();
                                }
                            });
                            alertDialogBuilder.setNegativeButton(R.string.alert_dialog_negative_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            alertDialogBuilder.setNeutralButton(R.string.alert_dialog_neutral_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setIgnoreAppVersionCode(updateInfo.versionCode);
                                    dialog.dismiss();
                                }
                            });
                            alertDialogBuilder.create();
                            alertDialogBuilder.show();
                        }
                        break;
                    case UpdateStatus.STATUS_NO_UPDATE:
                        // 无更新， UpdateResponse为null
                        break;
                    case UpdateStatus.STATUS_NO_WIFI:
                        // 设置了只在WiFi下更新，且WiFi不可用时， UpdateResponse为null
                        break;
                    case UpdateStatus.STATUS_NO_NET:
                        // 没有网络， UpdateResponse为null
                        break;
                    case UpdateStatus.STATUS_FAILED:
                        // 检查更新与服务器通讯失败，可稍后再试， UpdateResponse为null
                        break;
                    case UpdateStatus.STATUS_LOCAL_APP_FAILED:
                        // 检查更新获取本地安装应用信息失败， UpdateResponse为null
                        break;
                    default:
                        break;
                }
            }
        });
        XiaomiUpdateAgent.update(context);  //这种情况下, 若本地版本是debug版本则使用沙盒环境，否则使用线上环境或：
//        XiaomiUpdateAgent.update(context, true);//第二个参数为true时使用沙盒环境，否则使用线上环境
    }

}
