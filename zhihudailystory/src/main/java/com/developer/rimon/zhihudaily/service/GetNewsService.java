package com.developer.rimon.zhihudaily.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.entity.News;
import com.developer.rimon.zhihudaily.utils.HttpUtil;
import com.developer.rimon.zhihudaily.listener.OnGetListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GetNewsService extends Service {
    private ScheduledExecutorService scheduledExecutorService;

    public GetNewsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            final String firstId = intent.getStringExtra(Constants.FIRST_STORY_ID);
            //定时检查新日报
            scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    HttpUtil.getNews(new OnGetListener() {
                        @Override
                        public void onNext(Object object) {
                            News news = (News) object;
                            int num = news.stories.size();
                            for (int i = 0; i < num; i++) {
                                if (news.stories.get(i).id.equals(firstId)) {
                                    Intent intent = new Intent(Constants.ACTION_SEND);
                                    intent.putExtra("NewsNum", i);
                                    sendBroadcast(intent);
                                    break;
                                }
                            }
                        }
                    });
                }
            }, 0, 5, TimeUnit.MINUTES);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public static void startActionGetNewStory(Context context, String param1) {
        Log.e("开启服务","start");
        Intent intent = new Intent(context, GetNewsService.class);
        intent.putExtra(Constants.FIRST_STORY_ID, param1);
        context.startService(intent);
    }

    public static void stopActionGetNewStory(Context context) {
        Log.e("停止服务","start");
        Intent intent = new Intent(context, GetNewsService.class);
        context.stopService(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduledExecutorService != null){
            scheduledExecutorService.shutdown();
        }
    }
}
