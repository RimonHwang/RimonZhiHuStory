package com.developer.rimon.zhihudaily;

import com.developer.rimon.zhihudaily.entity.CommentList;
import com.developer.rimon.zhihudaily.entity.News;
import com.developer.rimon.zhihudaily.entity.NewsDetail;
import com.developer.rimon.zhihudaily.entity.StartImage;
import com.developer.rimon.zhihudaily.entity.StoryExtra;
import com.developer.rimon.zhihudaily.entity.ThemeList;
import com.developer.rimon.zhihudaily.entity.ThemeStory;
import com.developer.rimon.zhihudaily.entity.User;

import java.util.HashMap;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Rimon on 2016/8/26.
 */
public interface APIService {
    @GET("start-image/480*800")
    Observable<StartImage> getWelcomeImage();

    @GET("news/latest")
    Observable<News> getNews();

    @GET("news/{id}")
    Observable<NewsDetail> getNewsDetail(@Path("id") String id);

    @GET("news/before/{date}")
    Observable<News> getBeforeNews(@Path("date") String date);

    @GET("story-extra/{id}")
    Observable<StoryExtra> getStoryExtra(@Path("id") String id);

    @GET("story/{id}/long-comments")
    Observable<CommentList> getLongComment(@Path("id") String id);

    @GET("story/{id}/short-comments")
    Observable<CommentList> getShortComment(@Path("id") String id);

    @GET("themes")
    Observable<ThemeList> getThemesList();

    @GET("theme/{id}")
    Observable<ThemeStory> getThemesStory(@Path("id") String id);

    @GET("users/show.json")
    Observable<User> getUserInfo(@QueryMap HashMap<String, String> params);

}
