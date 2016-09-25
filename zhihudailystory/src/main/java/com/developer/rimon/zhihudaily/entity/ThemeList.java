package com.developer.rimon.zhihudaily.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Transient;

import java.util.ArrayList;

/**
 * Created by Rimon on 2016/9/13.
 */
@Entity
public class ThemeList {

    @Property
    private String jsonString;
    @Property
    private Long createdTime;

    @Transient
    public int limit;
    @Transient
    public ArrayList<String> subscribed;
    @Transient
    public ArrayList<Other> others;

    @Generated(hash = 1409248129)
    public ThemeList(String jsonString, Long createdTime) {
        this.jsonString = jsonString;
        this.createdTime = createdTime;
    }

    @Generated(hash = 561750565)
    public ThemeList() {
    }

    public String getJsonString() {
        return this.jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public Long getCreatedTime() {
        return this.createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public static class Other {
        public int color;
        public String thumbnail;
        public String description;
        public String id;
        public String name;
    }
}
