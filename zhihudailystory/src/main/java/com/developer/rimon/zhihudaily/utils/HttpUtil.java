package com.developer.rimon.zhihudaily.utils;

import android.util.Log;

import com.developer.rimon.zhihudaily.APIService;
import com.developer.rimon.zhihudaily.entity.CommentList;
import com.developer.rimon.zhihudaily.entity.News;
import com.developer.rimon.zhihudaily.entity.NewsDetail;
import com.developer.rimon.zhihudaily.entity.StartImage;
import com.developer.rimon.zhihudaily.entity.StoryExtra;
import com.developer.rimon.zhihudaily.entity.ThemeList;
import com.developer.rimon.zhihudaily.entity.ThemeStory;
import com.developer.rimon.zhihudaily.entity.User;
import com.developer.rimon.zhihudaily.listener.OnGetListener;

import java.util.HashMap;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Rimon on 2016/8/27.
 */
public class HttpUtil {
    private static final String TAG = "HttpUtil";
    private static APIService apiService;
    private static APIService weiboService;


    public static void getWelcomeImage(final OnGetListener listener) {
        getAPIService().getWelcomeImage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StartImage>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "请求欢迎界面图片ur完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "请求欢迎界面图片url错误"+e.toString());
                    }

                    @Override
                    public void onNext(StartImage startImage) {
                        listener.onNext(startImage);
                    }
                });
    }

    public static Observable<News> getNews() {
        return getAPIService().getNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void getNews(final OnGetListener listener) {
        getAPIService().getNews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<News>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "请求日报完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "请求日报错误"+e.toString());
                    }

                    @Override
                    public void onNext(News news) {
                        listener.onNext(news);
                    }
                });
    }

    public static Observable<NewsDetail> getNewsDetail(String id) {
        return getAPIService().getNewsDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void getNewsDetail(String id, final OnGetListener listener) {
        getAPIService().getNewsDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NewsDetail>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG,"请求日报内容完成" );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG,"请求日报内容错误" +e.toString());
                    }

                    @Override
                    public void onNext(final NewsDetail newsDetail) {
                        listener.onNext(newsDetail);
                    }
                });
    }

    public static Observable<News> getBeforeNews(String date) {

        return getAPIService().getBeforeNews(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void getBeforeNews(String date, final OnGetListener listener) {
        getAPIService().getBeforeNews(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<News>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "请求历史文章完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "请求历史文章错误"+e.toString());
                    }

                    @Override
                    public void onNext(News news) {
                        listener.onNext(news);
                    }
                });
    }

    public static void getStoryExtra(String id,final OnGetListener listener) {
        getAPIService().getStoryExtra(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<StoryExtra>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "请求日报额外数据完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "请求日报额外数据错误"+e.toString());
                    }

                    @Override
                    public void onNext(StoryExtra storyExtra) {
                        listener.onNext(storyExtra);
                    }
                });
    }

    public static void getLongComment(String id,final OnGetListener listener) {
        getAPIService().getLongComment(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentList>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "请求长评论完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(TAG, "请求长评论错误"+e.toString());
                    }

                    @Override
                    public void onNext(CommentList commentList) {
                        listener.onNext(commentList);
                    }
                });
    }

    public static void getShortComment(String id, final OnGetListener listener) {
        getAPIService().getShortComment(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CommentList>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "请求短评论完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "请求短评论错误"+e.toString());
                    }

                    @Override
                    public void onNext(CommentList commentList) {
                        listener.onNext(commentList);
                    }
                });
    }

    public static void getThemesList(final OnGetListener listener) {
        getAPIService().getThemesList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ThemeList>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "主题日报列表请求完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "主题日报列表请求错误"+e.toString());
                    }

                    @Override
                    public void onNext(ThemeList themeList) {
                        listener.onNext(themeList);
                    }
                });
    }

    public static Observable<ThemeStory> getThemeStory(String id) {
        return getAPIService().getThemesStory(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void getThemeStory(String id, final OnGetListener listener) {
        getAPIService().getThemesStory(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ThemeStory>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "主题日报请求完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "主题日报请求错误"+e.toString());
                    }

                    @Override
                    public void onNext(ThemeStory themeStory) {
                        listener.onNext(themeStory);
                    }
                });
    }

    public static void getUserInfo(HashMap<String, String> params, final OnGetListener listener) {
        getWeiboService().getUserInfo(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<User>() {
                    @Override
                    public void onCompleted() {
                        Log.i("请求用户信息", "完成");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("请求用户信息错误", e.toString());
                    }

                    @Override
                    public void onNext(User user) {
                        listener.onNext(user);
                    }
                });
    }

    private static APIService getAPIService() {
        if (apiService == null){
            String storyBaseUrl = "http://news-at.zhihu.com/api/4/";
            apiService = getService(storyBaseUrl).create(APIService.class);
        }
        return apiService;
    }

    private static APIService getWeiboService() {
        if (weiboService == null){
            String weiboBaseUrl = "https://api.weibo.com/2/";
            weiboService = getService(weiboBaseUrl).create(APIService.class);
        }
        return weiboService;
    }

    private static Retrofit getService(String baseUrl) {

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

}
