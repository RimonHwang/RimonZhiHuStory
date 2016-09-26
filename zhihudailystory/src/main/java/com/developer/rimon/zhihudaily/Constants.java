package com.developer.rimon.zhihudaily;

/**
 * Created by Rimon on 2016/9/2.
 */
public class Constants {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_HEADER_SECOND = 2;
    public static final int TYPE_DATE = 3;
    public static final int TYPE_FOOTER = 4;

    public static final String FIRST_STORY_ID = "firstStoryID";
    public final static String ACTION_SEND = "com.developer.rimon.zhihudaily.sendBroadcast";
    public final static String MAIN_TO_STORY_DETAIL_INTENT_KEY_ID = "id";
    public static final String SAVE_FILE_NAME = "data";

    public static final String PREFERENCES_NAME_WELCOME = "welcome";
    public static final String PREFERENCES_NAME_USERINFO = "userInfo";
    public static final String PREFERENCES_NAME_COLLECT = "collect";

    public static final String KEY_WELCOME_IMAGE_URL = "welcomeImageUrl";
    public static final String KEY_WELCOME_AUTHOR_INFO = "authorInfo";
    public static final String KEY_USERINFO_NAME = "name";
    public static final String KEY_USERINFO_PROFILE_IMAGE_URL = "profileImageUrl";

    public static final String FROM_LOGIN_ACTIVITY = "from_login_activity";
    public static final String AVATAR_FILE_NAME = "Avatar.jpg";
    public static final String WELCOME_FILE_NAME = "welcome.png";

    public static final String ABOUT_BLOG_ADDRESS = "http://rimonhwang.com/";
    public static final String ABOUT_GITHUB_ADDRESS = "https://github.com/RimonHwang";
    public static final String ABOUT_PROJECT_ADDRESS = "https://github.com/RimonHwang/RimonZhiHuStory";

    //TODO:填写相关账户信息
    static String Bmob_APP_ID = "填写Bmob后端云APP_ID";

    public static final String WEIBO_APP_KEY = "填写新浪微博APP_KEY";
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";
    public static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";


    static final String MI_PUSH_APP_ID = "填写小米推送APP_ID";
    static final String MI_PUSH_APP_KEY = "填写小米推送APP_KEY";
}
