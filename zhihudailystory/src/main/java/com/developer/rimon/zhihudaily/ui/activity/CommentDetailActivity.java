package com.developer.rimon.zhihudaily.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.adapter.CommentRecyclerAdapter;
import com.developer.rimon.zhihudaily.entity.Comment;
import com.developer.rimon.zhihudaily.entity.CommentList;
import com.developer.rimon.zhihudaily.listener.OnGetListener;
import com.developer.rimon.zhihudaily.utils.HttpUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.comment_recyclerview)
    RecyclerView commentRecyclerView;

    private String id;
    public static int shortCommentsCount;
    public static int longCommentsCount;
    private boolean canLoadShortComment = true;

    private ArrayList<Comment> comments = new ArrayList<>();
    private CommentRecyclerAdapter commentAdapter;
    private LinearLayoutManager layoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_detail);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        longCommentsCount = intent.getIntExtra("long_comments", 0);
        shortCommentsCount = intent.getIntExtra("short_comments", 0);
        int commentsCount = intent.getIntExtra("comments", 0);

        toolbar.setTitle(commentsCount + "条点评");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        commentAdapter = new CommentRecyclerAdapter(this, comments);
        layoutManager = new LinearLayoutManager(this);
        commentRecyclerView.setAdapter(commentAdapter);
        commentRecyclerView.setLayoutManager(layoutManager);

        HttpUtil.getLongComment(id, new OnGetListener() {
            @Override
            public void onNext(Object object) {
                CommentList commentList = (CommentList) object;
                commentAdapter.setFooterViewPosition(commentList.comments.size() + 1);
                comments.addAll(commentList.comments);
                commentAdapter.notifyDataSetChanged();
            }
        });


        commentAdapter.setOnCommentLayoutClickListener(new CommentRecyclerAdapter.OnCommentLayoutClickListener() {
            @Override
            public void onClick() {
                if (canLoadShortComment) {
                    canLoadShortComment = false;
                    HttpUtil.getShortComment(id, new OnGetListener() {
                        @Override
                        public void onNext(Object object) {
                            CommentList commentList = (CommentList) object;
                            commentAdapter.setHeaderAndFooterCount(1);
                            comments.add(new Comment());
                            comments.addAll(commentList.comments);
                            commentAdapter.notifyDataSetChanged();
                            commentRecyclerView.scrollBy(0, layoutManager.findViewByPosition((longCommentsCount + 1))
                                    .getTop());
                        }
                    });
                }
            }
        });
    }
}
