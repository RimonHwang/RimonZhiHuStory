package com.developer.rimon.zhihudaily.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.entity.Collect;
import com.developer.rimon.zhihudaily.entity.NewsDetail;
import com.developer.rimon.zhihudaily.entity.StoryExtra;
import com.developer.rimon.zhihudaily.entity.User;
import com.developer.rimon.zhihudaily.listener.OnGetListener;
import com.developer.rimon.zhihudaily.utils.HttpUtil;
import com.developer.rimon.zhihudaily.utils.weibo.AccessTokenKeeper;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import static com.developer.rimon.zhihudaily.R.id.webView;

public class StoryDetailActivity extends BaseActivity implements IWeiboHandler.Response {

    @BindView(webView)
    WebView webview;
    @BindView(R.id.comment)
    TextView comment;
    @BindView(R.id.like)
    TextView like;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.story_detail_image)
    ImageView storyDetailImage;
    @BindView(R.id.story_detail_description)
    TextView storyDetailDescription;
    @BindView(R.id.image_source)
    TextView imageSource;
    @BindView(R.id.collect)
    ImageView collectImgaeView;
    @BindView(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.heaser_image_layout)
    FrameLayout heaserImageLayout;
    @BindView(R.id.share)
    ImageView share;

    private BmobUser bmobUser;
    private NewsDetail newsDetail;
    private IWeiboShareAPI mWeiboShareAPI;
    private GestureDetector gestureDetector;
    private SharedPreferences preferences;

    private String id;
    private String title;
    private ArrayList<String> imageUrl = new ArrayList<>();
    private int longCommentsCount;
    private int shortCommentsCount;
    private int likeCount;
    private int commentsCount;
    private boolean collected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_detail);
        ButterKnife.bind(this);

        bmobUser = BmobUser.getCurrentUser();
        preferences = getSharedPreferences(Constants.PREFERENCES_NAME_COLLECT, MODE_PRIVATE);
        id = getIntent().getStringExtra(Constants.MAIN_TO_STORY_DETAIL_INTENT_KEY_ID);
        title = getIntent().getStringExtra("title");
        imageUrl.add(getIntent().getStringExtra("image"));

        /*微博分享注册*/
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.WEIBO_APP_KEY);
        mWeiboShareAPI.registerApp();
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }

        /*接收小米推送的日报id*/
        if (TextUtils.isEmpty(id)) {
            MiPushMessage message = (MiPushMessage) getIntent().getSerializableExtra(PushMessageHelper.KEY_MESSAGE);
            id = message.getContent();
        }

        initToolbar();
        initWebView();
        getData();
        queryIsCollected();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webview.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initToolbar() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(StoryDetailActivity.this);
                if (accessToken != null && accessToken.isSessionValid()){
                    sendMultiMessage(true, false, true, false, false, false);
                    //                Intent intent = new Intent(StoryDetailActivity.this, WBShareActivity.class);
//                intent.putExtra("share_text", newsDetail.title + "(分享自 @简报 App)");
//                intent.putExtra("share_url", newsDetail.share_url);
//                startActivity(intent);
                }else {
                    Intent intent = new Intent(StoryDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        collectImgaeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bmobUser != null) {
                    if (collected) {
                        unCollectStory();
                    } else {
                        collectStory();
                    }
                } else {
                    Intent intent = new Intent(StoryDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryDetailActivity.this, CommentDetailActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("long_comments", longCommentsCount);
                intent.putExtra("short_comments", shortCommentsCount);
                intent.putExtra("comments", commentsCount);
                startActivity(intent);
            }
        });
    }

    private void queryIsCollected() {
        BmobQuery<Collect> query = new BmobQuery<>();
        query.addWhereRelatedTo("collect", new BmobPointer(bmobUser));
        query.findObjects(new FindListener<Collect>() {
            @Override
            public void done(List<Collect> list, BmobException e) {
                if (e == null && list != null) {
                    for (Collect collect : list) {
                        if (collect.getId().equals(id)) {
                            preferences.edit().putString(id, collect.getObjectId()).apply();
                            collected = true;
                            collectImgaeView.setImageResource(R.drawable.ic_star_yellow_a700_24dp);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void getData() {
        HttpUtil.getStoryExtra(id, new OnGetListener() {
            @Override
            public void onNext(Object object) {
                StoryExtra storyExtra = (StoryExtra) object;
                longCommentsCount = storyExtra.long_comments;
                shortCommentsCount = storyExtra.short_comments;
                commentsCount = storyExtra.comments;
                likeCount = storyExtra.popularity;
                like.setText(String.valueOf(likeCount));
                comment.setText(String.valueOf(commentsCount));
            }
        });

        HttpUtil.getNewsDetail(id, new OnGetListener() {
            @Override
            public void onNext(Object object) {
                newsDetail = (NewsDetail) object;
                if (newsDetail.images != null) {
                    SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            storyDetailImage.setImageBitmap(resource);
                            storyDetailDescription.setText(newsDetail.title);
                            imageSource.setText(newsDetail.image_source);
                        }
                    };
                    Glide.with(StoryDetailActivity.this)
                            .load(newsDetail.image == null ? newsDetail.images.get(0) : newsDetail.image)
                            .asBitmap()
                            .into(target);
                } else {
                    storyDetailDescription.setTextColor(Color.BLACK);
                    storyDetailDescription.setText(newsDetail.title);
                }

                String body = newsDetail.body;
                body = body.substring(body.indexOf("<div class=\"question\">"));
                final String webContent = "<!DOCTYPE html>" +
                        "<html>" +
                        "<head><meta charset=\"UTF-8\"><link rel=\"stylesheet\" href=\"style.css\"></head>" +
                        "<body>" + body + "</body>" +
                        "</html>";
                webview.loadDataWithBaseURL("file:///android_asset/", webContent, "authorText/html", "UTF-8", null);
            }
        });
    }

    private void unCollectStory() {
        collected = false;
        collectImgaeView.setImageResource(R.drawable.ic_star_white_24dp);

        Collect collect = new Collect();
        collect.setObjectId(preferences.getString(id, ""));
        BmobRelation relation = new BmobRelation();
        relation.remove(bmobUser);
        collect.setLikes(relation);
        collect.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                Collect collect = new Collect();
                collect.setObjectId(preferences.getString(id, ""));
                BmobRelation relation = new BmobRelation();
                relation.remove(collect);
                User user = new User();
                user.setCollect(relation);
                user.update(bmobUser.getObjectId(), new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            preferences.edit().putString(id, "").apply();
                        } else {
                            loge(e);
                        }
                    }
                });
            }
        });
    }

    private void collectStory() {
        collected = true;
        collectImgaeView.setImageResource(R.drawable.ic_star_yellow_a700_24dp);

        BmobQuery<Collect> query = new BmobQuery<>();
        query.addWhereEqualTo("id", id);
        query.findObjects(new FindListener<Collect>() {
            @Override
            public void done(List<Collect> list, BmobException e) {
                log(String.valueOf(e == null) + String.valueOf(list == null));
                if (e == null && list != null) {
                    log(list.get(0).getTitle());
                    Collect collect = new Collect();
                    final String objcetId = list.get(0).getObjectId();
                    collect.setObjectId(objcetId);
                    BmobRelation relation = new BmobRelation();
                    BmobUser bmobUser = BmobUser.getCurrentUser();
                    relation.add(bmobUser);
                    collect.setLikes(relation);
                    collect.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            updateUser(objcetId);
                            loge(e);
                        }
                    });
                } else {
                    log("新建收藏执行");
                    Collect collect = new Collect();
                    BmobRelation relation = new BmobRelation();
                    relation.add(bmobUser);
                    collect.setId(id);
                    collect.setTitle(title);
                    collect.setImages(imageUrl);
                    collect.setLikes(relation);
                    collect.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                updateUser(s);
                                log("创建数据成功：" + s);
                            } else {
                                log("创建数据失败：" + e.getMessage() + "," + e.getErrorCode());
                            }
                        }
                    });
                }
            }
        });

    }

    private void updateUser(final String objectId) {
        Collect newCollect = new Collect();
        newCollect.setObjectId(objectId);
        BmobRelation collectRelation = new BmobRelation();
        collectRelation.add(newCollect);
        User user = new User();
        user.setCollect(collectRelation);
        log(bmobUser.getObjectId());
        user.update(bmobUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    preferences.edit().putString(id, objectId).apply();
                } else {
                    loge(e);
                }
            }
        });
    }

    private void initWebView() {
        //webview的双击事件
        gestureDetector = new GestureDetector(this, new MyGestureListener());
        webview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                Toast.makeText(this, R.string.weibosdk_demo_toast_share_success, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                Toast.makeText(this, R.string.weibosdk_demo_toast_share_canceled, Toast.LENGTH_LONG).show();
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                Toast.makeText(this, getString(R.string.weibosdk_demo_toast_share_failed) + "Error Message: " + baseResponse.errMsg, Toast
                        .LENGTH_LONG).show();
                break;
        }
    }

    private void sendMultiMessage(boolean hasText, boolean hasImage, boolean hasWebpage, boolean hasMusic, boolean hasVideo, boolean hasVoice) {

        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        if (hasText) {
            weiboMessage.textObject = getTextObj();
        }
        if (hasWebpage) {
            weiboMessage.mediaObject = getWebpageObj();
        }
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = weiboMessage;
        AuthInfo authInfo = new AuthInfo(this, Constants.WEIBO_APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException arg0) {
            }

            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
            }

            @Override
            public void onCancel() {
            }
        });
    }

    private TextObject getTextObj() {
        TextObject textObject = new TextObject();
        textObject.text = newsDetail.title + "(分享自 @简报 App)";
        return textObject;
    }

    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject = new WebpageObject();
        mediaObject.identify = Utility.generateGUID();
        mediaObject.title = "(分享自 @简报 App)";
        mediaObject.description = "来自简报 ";

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        // 设置 Bitmap 类型的图片到视频对象里         设置缩略图。 注意：最终压缩过的缩略图大小不得超过 32kb。
        mediaObject.setThumbImage(bitmap);
        mediaObject.actionUrl = newsDetail.share_url;
        mediaObject.defaultText = "Webpage 默认文案";
        return mediaObject;
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            finish();
            return super.onDoubleTap(e);
        }
    }
}
