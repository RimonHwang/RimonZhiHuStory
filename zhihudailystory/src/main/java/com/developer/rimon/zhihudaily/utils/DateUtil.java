package com.developer.rimon.zhihudaily.utils;

import android.support.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Rimon on 2016/8/28.
 */
public class DateUtil {

    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUTE = ONE_SECOND * 60;
    private static final long ONE_HOUR = ONE_MINUTE * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;

    public static String getOtherDateString(@Nullable Date date, int num, SimpleDateFormat format) {
        Calendar calendar = Calendar.getInstance();
        if (date != null) {
            calendar.setTime(date);
        }
        calendar.roll(Calendar.DAY_OF_YEAR, num);
        return format.format(calendar.getTime());
    }

    public static String getDateWithMillis(long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        Date date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA);

        return format.format(date);
    }

    public static String changeFormat(String yyyyMMdd) throws ParseException {
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd", Locale.US);
        SimpleDateFormat format2 = new SimpleDateFormat("MM月dd日 EEEE", Locale.CHINA);
        return format2.format(format1.parse(yyyyMMdd));
    }

    private static boolean isExpired(Date date) {
        Date curDate = new Date();
        long splitTime = curDate.getTime() - date.getTime();
        return splitTime > 2 * ONE_HOUR;
    }

    public static boolean isExpired(Long time) {
        return isExpired(new Date(time));
    }

}
