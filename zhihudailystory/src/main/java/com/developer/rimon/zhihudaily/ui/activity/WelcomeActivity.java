package com.developer.rimon.zhihudaily.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.entity.StartImage;
import com.developer.rimon.zhihudaily.listener.OnGetListener;
import com.developer.rimon.zhihudaily.utils.DateUtil;
import com.developer.rimon.zhihudaily.utils.FileUtil;
import com.developer.rimon.zhihudaily.utils.HttpUtil;
import com.developer.rimon.zhihudaily.utils.ImageLoaderUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by wenmingvs on 16/5/4.
 */
public class WelcomeActivity extends Activity {

    @BindView(R.id.welcome_image)
    ImageView welcomeImage;
    @BindView(R.id.authorText)
    TextView authorText;
    @BindView(R.id.infoText)
    TextView infoText;

    private MyHandler myHandler ;
    private String welcomeImageUrl;
    private String authorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        ButterKnife.bind(this);

        myHandler = new MyHandler(this);
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.PREFERENCES_NAME_WELCOME, MODE_PRIVATE);
        welcomeImageUrl = sharedPreferences.getString(Constants.KEY_WELCOME_IMAGE_URL, "");
        authorInfo = sharedPreferences.getString(Constants.KEY_WELCOME_AUTHOR_INFO, "");

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), Constants.WELCOME_FILE_NAME);

        if (file.exists() && !DateUtil.isExpired(file.lastModified())) {
            loadLocalImage();
        } else {
            requestWelcomeImage();
        }

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                myHandler.sendMessage(Message.obtain());
            }
        }, 3000);
    }



    @Override
    protected void onDestroy() {
        myHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void requestWelcomeImage() {
        HttpUtil.getWelcomeImage(new OnGetListener() {
            @Override
            public void onNext(Object object) {
                final StartImage startImage = (StartImage) object;
                SharedPreferences.Editor editor = getSharedPreferences(Constants.PREFERENCES_NAME_WELCOME, MODE_PRIVATE).edit();
                editor.putString(Constants.KEY_WELCOME_IMAGE_URL, startImage.img);
                editor.putString(Constants.KEY_WELCOME_AUTHOR_INFO, startImage.text);
                editor.apply();

                if (!startImage.img.equals(welcomeImageUrl)) {
                    SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            welcomeImage.setImageBitmap(resource);
                            infoText.setVisibility(View.VISIBLE);
                            authorText.setText(startImage.text);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    FileUtil.saveFileToSDCardPrivateFilesDir(WelcomeActivity.this, resource, Environment.DIRECTORY_PICTURES,
                                            Constants.WELCOME_FILE_NAME);
                                }
                            }).start();
                        }
                    };
                    ImageLoaderUtils.load(WelcomeActivity.this, startImage.img, null, null, target);
                }else {
                    loadLocalImage();
                }
            }
        });
    }

    private void loadLocalImage() {
        String filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + Constants.WELCOME_FILE_NAME;
        welcomeImage.setImageBitmap(FileUtil.getSmallBitmap(filePath));
        infoText.setVisibility(View.VISIBLE);
        authorText.setText(authorInfo);
    }

    private static class MyHandler extends Handler {
        private final WeakReference<WelcomeActivity> mActivity;

        public MyHandler(WelcomeActivity activity) {
            mActivity = new WeakReference<WelcomeActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            WelcomeActivity activity = mActivity.get();
            if (activity != null) {
                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();
            }
        }
    }

}
