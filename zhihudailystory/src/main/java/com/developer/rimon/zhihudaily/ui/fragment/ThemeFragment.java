package com.developer.rimon.zhihudaily.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.adapter.ThemeContentRecyclerAdapter;
import com.developer.rimon.zhihudaily.entity.Story;
import com.developer.rimon.zhihudaily.entity.ThemeStory;
import com.developer.rimon.zhihudaily.listener.DoubleClick;
import com.developer.rimon.zhihudaily.listener.OnGetListener;
import com.developer.rimon.zhihudaily.utils.HttpUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

public class ThemeFragment extends Fragment {

    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String TAG = "ThemeFragment";
    private String id;
    private String name;
    private boolean isRefreshing = false;

    private LinearLayoutManager storyLayoutManager;
    private ThemeContentRecyclerAdapter adapter;
    private ArrayList<Story> mData = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme, container, false);
        ButterKnife.bind(this, view);

        name = getArguments().getString("name");
        id = getArguments().getString("id");
        String description = getArguments().getString("description");
        String thumbnail = getArguments().getString("thumbnail");

        initToolbar();

        adapter = new ThemeContentRecyclerAdapter(getContext(), mData, description, thumbnail);
        storyLayoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(storyLayoutManager);
        recycler.setAdapter(adapter);

        HttpUtil.getThemeStory(id, new OnGetListener() {
            @Override
            public void onNext(Object object) {
                ThemeStory themeStory = (ThemeStory) object;
                mData.addAll(themeStory.stories);
                adapter.notifyDataSetChanged();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isRefreshing) {
                    isRefreshing = true;
                    HttpUtil.getThemeStory(id).subscribe(new Subscriber<ThemeStory>() {
                        @Override
                        public void onCompleted() {
                            Log.e(TAG, "主题日报请求完成");
                            isRefreshing = false;
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, "主题日报请求错误"+e.toString());
                            isRefreshing = false;
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        @Override
                        public void onNext(ThemeStory themeStory) {
                            mData.clear();
                            mData.addAll(themeStory.stories);
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
        return view;
    }

    private void initToolbar() {
        toolbar.setTitle(name);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        //双击toolbar回到顶部
        DoubleClick.registerDoubleClickListener(toolbar, new DoubleClick.OnDoubleClickListener() {
            @Override
            public void OnSingleClick(View v) {
            }

            @Override
            public void OnDoubleClick(View v) {
                //平滑滚回顶部
                int findFirstVisibleItemPosition = storyLayoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition < 9) {
                    recycler.smoothScrollToPosition(0);
                } else {
                    recycler.scrollToPosition(10);
                    recycler.smoothScrollToPosition(0);
                }
            }
        });
    }

    public RecyclerView getRecyclerView() {
        return recycler;
    }
}
