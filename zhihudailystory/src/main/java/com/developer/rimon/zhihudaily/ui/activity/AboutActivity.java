package com.developer.rimon.zhihudaily.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.head)
    ImageView head;
    @BindView(R.id.blog_address)
    TextView blogAddress;
    @BindView(R.id.github_address)
    TextView githubAddress;
    @BindView(R.id.project_address)
    TextView projectAddress;
    @BindView(R.id.activity_about)
    LinearLayout activityAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("关于");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        blogAddress.setOnClickListener(this);
        githubAddress.setOnClickListener(this);
        projectAddress.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.blog_address:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ABOUT_BLOG_ADDRESS)));
                break;
            case R.id.github_address:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ABOUT_GITHUB_ADDRESS)));
                break;
            case R.id.project_address:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.ABOUT_PROJECT_ADDRESS)));
                break;
        }
    }
}
