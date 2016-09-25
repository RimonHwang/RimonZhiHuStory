package com.developer.rimon.zhihudaily.entity;

import java.util.ArrayList;

/**
 * Created by Rimon on 2016/9/13.
 */
public class ThemeStory {

    public String description;
    public String background;
    public int color;
    public String name;
    public String image;
    public String image_source;
    public ArrayList<Story> stories;
    public ArrayList<Editors> editors;

    public static class Editors {
        public String url;
        public String bio;
        public int id;
        public String avatar;
        public String name;
    }
}
