package com.developer.rimon.zhihudaily;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class AppConfig {

    private SharedPreferences sharedPreferences;
    private static final String KEY_NIGHT_MODE_SWITCH = "night_theme";

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

}
