package com.developer.rimon.zhihudaily.entity;

import java.util.ArrayList;

/**
 * Created by Rimon on 2016/8/26.
 */
public class NewsDetail {


    public String body;
    public String image_source;
    public String title;
    public String image;
    public String share_url;
    public String[] js;
    public ArrayList<Recommender> recommenders = new ArrayList<>();
    public String ga_prefix;
    //    public String section;
    public String type;
    public String id;
    public String[] css;
    public ArrayList<String> images;
}
