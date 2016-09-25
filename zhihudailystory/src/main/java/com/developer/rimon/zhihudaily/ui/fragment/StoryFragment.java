package com.developer.rimon.zhihudaily.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.MyApplication;
import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.adapter.StoryRecyclerAdapter;
import com.developer.rimon.zhihudaily.entity.DaoMaster;
import com.developer.rimon.zhihudaily.entity.DaoSession;
import com.developer.rimon.zhihudaily.entity.News;
import com.developer.rimon.zhihudaily.entity.NewsDao;
import com.developer.rimon.zhihudaily.entity.Story;
import com.developer.rimon.zhihudaily.entity.TopStory;
import com.developer.rimon.zhihudaily.listener.DoubleClick;
import com.developer.rimon.zhihudaily.listener.OnGetListener;
import com.developer.rimon.zhihudaily.service.GetNewsService;
import com.developer.rimon.zhihudaily.ui.activity.AboutActivity;
import com.developer.rimon.zhihudaily.utils.DateUtil;
import com.developer.rimon.zhihudaily.utils.HttpUtil;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

import static cn.bmob.v3.Bmob.getApplicationContext;

public class StoryFragment extends Fragment {

    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private String TAG = "StoryFragment";
    private boolean loadMore = true;
    private boolean isRefreshing = false;
    private boolean shouldRefresh;
    private int pageNum = 1;
    private int dateViewPosition = 0;
    private int secondDateViewPosition;
    private int lastVisibleItemPosition;
    private int firstVisibleItemPosition;

    private DrawerLayout drawerLayout;
    private RecyclerView drawerRecycler;
    private ArrayList<Story> storyArrayList = new ArrayList<>();
    private ArrayList<TopStory> topStoryList = new ArrayList<>();
    private StoryRecyclerAdapter adapter;
    private LinearLayoutManager layoutManager;

    private NewsBroadcastReceiver receiver;
    private News news;
    private Gson gson;
    private NewsDao newsDao;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_story, container, false);
        ButterKnife.bind(this, view);

        receiver = new NewsBroadcastReceiver();
        layoutManager = new LinearLayoutManager(getContext());
        adapter = new StoryRecyclerAdapter(getContext(), storyArrayList, topStoryList);
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);

        initToolbar();
        loadData();
        changToolbarTitle();
        pullToLoadMore();
        pullToRefresh();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiver, new IntentFilter(Constants.ACTION_SEND));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GetNewsService.stopActionGetNewStory(getContext());
    }

    private void initToolbar() {
        toolbar.inflateMenu(R.menu.menu_main_toolbar);
        toolbar.setTitle("首页");
        drawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
        drawerRecycler = (RecyclerView) getActivity().findViewById(R.id.drawer_recycler);
        toolbar.getMenu().findItem(R.id.night_theme).setTitle(MyApplication.appConfig.isNighTheme() ? "白天模式" : "夜间模式");
        toolbar.setNavigationIcon(R.drawable.ic_format_list_bulleted_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawerLayout.isDrawerOpen(drawerRecycler)) {
                    drawerLayout.openDrawer(drawerRecycler);
                }
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.night_theme:
                        changeTheme();

                        break;
                    case R.id.about:
                        Intent intent = new Intent(getActivity(), AboutActivity.class);
                        getContext().startActivity(intent);
                }
                return true;
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
                int findFirstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                if (findFirstVisibleItemPosition < 9) {
                    recycler.smoothScrollToPosition(0);
                } else {
                    recycler.scrollToPosition(10);
                    recycler.smoothScrollToPosition(0);
                }
            }
        });
    }

    private void loadData() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(getApplicationContext(), "theme.db", null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        DaoSession daoSession = daoMaster.newSession();
        gson = new Gson();
        newsDao = daoSession.getNewsDao();
        List<News> newsList = newsDao.loadAll();
        /*判断本地数据是否为今天的数据，不是则需要更新*/
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.US);
        String date = format.format(new Date());
        if (newsList.size() != 0) {
            shouldRefresh = Integer.parseInt(date) > Integer.parseInt(newsList.get(0).getDate());
        }
        if (newsList.size() == 0 || shouldRefresh || DateUtil.isExpired(newsList.get(0).getCreatedTime())) {
            HttpUtil.getNews(new OnGetListener() {
                @Override
                public void onNext(Object object) {
                    news = (News) object;
                    news.setJsonString(gson.toJson(news));
                    news.setCreatedTime(new Date().getTime());
                    newsDao.deleteAll();
                    newsDao.insert(news);
                    topStoryList.addAll(news.top_stories);
                    storyArrayList.addAll(news.stories);
                    secondDateViewPosition = storyArrayList.size() + 2;
                    adapter.notifyDataSetChanged();
                    //开启定时获取新日报服务
                    if (getContext() != null) {
                        GetNewsService.startActionGetNewStory(getContext(), storyArrayList.get(0).id);
                    }
                }
            });
        } else {
            for (News dbNews : newsList) {
                news = gson.fromJson(dbNews.getJsonString(), News.class);
                topStoryList.addAll(news.top_stories);
                storyArrayList.addAll(news.stories);
            }
            secondDateViewPosition = storyArrayList.size() + 2;
            adapter.notifyDataSetChanged();
            //开启定时获取新日报服务
            if (getContext() != null) {
                GetNewsService.startActionGetNewStory(getContext(), storyArrayList.get(0).id);
            }
        }
    }

    private void changToolbarTitle() {

        final SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        final SimpleDateFormat format2 = new SimpleDateFormat("MM月dd日 EEEE", Locale.CHINA);
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                Date currentDate = null;
                int firstVisibleItemViewType = adapter.getItemViewType(firstVisibleItemPosition);
                int secondVisibleItemViewType = adapter.getItemViewType(firstVisibleItemPosition + 1);
                //上滑改变toolbar标题
                if (firstVisibleItemViewType == Constants.TYPE_DATE || firstVisibleItemViewType ==
                        Constants.TYPE_HEADER_SECOND) {
                    dateViewPosition = firstVisibleItemPosition;
                    try {
                        toolbar.setTitle(firstVisibleItemPosition == 1 ? getString(R.string
                                .main_new_story_indicator)
                                : DateUtil.changeFormat(storyArrayList.get(dateViewPosition - 2).date));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //下滑改变toolbar标题
                } else if (secondVisibleItemViewType == Constants.TYPE_DATE || secondVisibleItemViewType ==
                        Constants.TYPE_HEADER_SECOND) {
                    dateViewPosition = firstVisibleItemPosition + 1;
                    if (dateViewPosition == 1) {
                        toolbar.setTitle(R.string.main_toolbar_main_title);
                    } else {
                        try {
                            currentDate = format1.parse(storyArrayList.get(dateViewPosition - 2).date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        toolbar.setTitle(dateViewPosition == secondDateViewPosition ? getString(R.string
                                .main_new_story_indicator) : DateUtil.getOtherDateString(currentDate, 1, format2));
                    }
                }
            }
        });
    }

    private void pullToLoadMore() {
        recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    if (loadMore && lastVisibleItemPosition == storyArrayList.size() + 1) {
                        loadMore = false;
                        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.US);
                        String getDate = DateUtil.getOtherDateString(null, 1 - pageNum, format);
                        final String showDate = DateUtil.getOtherDateString(null, -pageNum, format);

                        HttpUtil.getBeforeNews(getDate).subscribe(new Subscriber<News>() {
                            @Override
                            public void onCompleted() {
                                loadMore = true;
                                Log.i(TAG, "请求历史文章完成");
                            }

                            @Override
                            public void onError(Throwable e) {
                                loadMore = true;
                                Log.e(TAG, "请求历史文章错误" + e.toString());
                            }

                            @Override
                            public void onNext(News news) {
                                Story story = new Story();
                                story.date = showDate;
                                storyArrayList.add(story);
                                storyArrayList.addAll(news.stories);
                                adapter.notifyDataSetChanged();
                                pageNum++;
                            }
                        });
                    }
                }
            }
        });
    }

    private void pullToRefresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (storyArrayList.size() == 0) {
                    getActivity().recreate();
                } else if (!isRefreshing) {
                    isRefreshing = true;
                    GetNewsService.stopActionGetNewStory(getContext());
                    HttpUtil.getNews().subscribe(new Subscriber<News>() {
                        @Override
                        public void onCompleted() {
                            if (recycler.getChildAt(1) != null) {
                                ImageView redDotIndicator = (ImageView) recycler.getChildAt(1).findViewById(R.id.red_dot_indicator);
                                redDotIndicator.setVisibility(View.GONE);
                            }
                            isRefreshing = false;
                            swipeRefreshLayout.setRefreshing(false);
                            GetNewsService.startActionGetNewStory(getContext(), storyArrayList.get(0).id);
                        }

                        @Override
                        public void onError(Throwable e) {
                            isRefreshing = false;
                            swipeRefreshLayout.setRefreshing(false);
                            Log.e("刷新文章错误", e.toString());
                        }

                        @Override
                        public void onNext(News news) {
                            int newStoryPosition = 0;
                            for (int i = 0; i < news.stories.size(); i++) {
                                if (news.stories.get(i).id.equals(storyArrayList.get(0).id)) {
                                    newStoryPosition = i;
                                    break;
                                }
                            }
                            for (int i = 0; i < newStoryPosition; i++) {
                                storyArrayList.add(0, news.stories.get(newStoryPosition - 1 - i));
                            }
                            if (newStoryPosition != 0) {
                                newsDao.deleteAll();
                                news.setJsonString(gson.toJson(news));
                                news.setCreatedTime(new Date().getTime());
                                newsDao.insert(news);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    private void changeTheme() {
        showAnimation();
        toggleThemeSetting();
        refreshUI();
    }

    private void showAnimation() {
        final View decorView = getActivity().getWindow().getDecorView();
        Bitmap cacheBitmap = getCacheBitmapFromView(decorView);
        if (decorView instanceof ViewGroup && cacheBitmap != null) {
            final View view = new View(getContext());
            view.setBackgroundDrawable(new BitmapDrawable(getResources(), cacheBitmap));
            ViewGroup.LayoutParams layoutParam = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            ((ViewGroup) decorView).addView(view, layoutParam);
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
            objectAnimator.setDuration(300);
            objectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    ((ViewGroup) decorView).removeView(view);
                }
            });
            objectAnimator.start();
        }
    }

    public void toggleThemeSetting() {
        if (MyApplication.appConfig.isNighTheme()) {
            getActivity().setTheme(R.style.DayTheme);
            toolbar.getMenu().findItem(R.id.night_theme).setTitle("夜间模式");
            MyApplication.appConfig.setNightTheme(false);
        } else {
            getActivity().setTheme(R.style.NightTheme);
            toolbar.getMenu().findItem(R.id.night_theme).setTitle("白天模式");
            MyApplication.appConfig.setNightTheme(true);
        }
    }

    private void refreshUI() {

        TypedValue toolbarBackground = new TypedValue();//toolBar背景色
        TypedValue colorBackground = new TypedValue();//recyclerView背景色
        TypedValue cardViewBackground = new TypedValue();//cardView背景色
        TypedValue drawerHeaderBackground = new TypedValue();//抽屉顶部背景色
        TypedValue drawerHomeBackground = new TypedValue();//抽屉首页项背景色
        TypedValue normalBackground = new TypedValue();//主题列表背景色

        TypedValue customTextColor = new TypedValue();//自定义字体颜色

        Resources.Theme theme = getActivity().getTheme();

        theme.resolveAttribute(R.attr.colorPrimary, toolbarBackground, true);
        theme.resolveAttribute(android.R.attr.colorBackground, colorBackground, true);
        theme.resolveAttribute(R.attr.cardViewBackground, cardViewBackground, true);
        theme.resolveAttribute(R.attr.drawerHeaderBackground, drawerHeaderBackground, true);
        theme.resolveAttribute(R.attr.drawerHomeBackground, drawerHomeBackground, true);
        theme.resolveAttribute(R.attr.normalBackground, normalBackground, true);

        theme.resolveAttribute(R.attr.customTextColor, customTextColor, true);


        RelativeLayout relativeLayout = (RelativeLayout) drawerLayout.findViewById(R.id.drawer_header_background);
        TextView homePage = (TextView) drawerLayout.findViewById(R.id.home_page);

        toolbar.setBackgroundResource(toolbarBackground.resourceId);
        relativeLayout.setBackgroundResource(drawerHeaderBackground.resourceId);
        homePage.setBackgroundResource(drawerHomeBackground.resourceId);


        recycler.setBackgroundResource(colorBackground.resourceId);
        int childCount = recycler.getChildCount();
        for (int childIndex = 0; childIndex < childCount; childIndex++) {
            ViewGroup childView = (ViewGroup) recycler.getChildAt(childIndex);
            if (childView instanceof CardView) {
                childView.setBackgroundResource(cardViewBackground.resourceId);
                TextView titleText = (TextView) childView.findViewById(R.id.title_text);
                if (titleText.getCurrentTextColor() != getResources().getColor(R.color.item_selected_color)) {
                    titleText.setTextColor(getResources().getColor(customTextColor.resourceId));
                }
            } else {
                TextView dateIndicator = (TextView) childView.findViewById(R.id.date_indicator);
                if (dateIndicator != null) {
                    dateIndicator.setTextColor(getResources().getColor(customTextColor.resourceId));
                }
            }
        }

        drawerRecycler.setBackgroundResource(normalBackground.resourceId);
        int drawerChildCount = drawerRecycler.getChildCount();
        for (int childIndex = 1; childIndex < drawerChildCount; childIndex++) {
            ViewGroup childView = (ViewGroup) drawerRecycler.getChildAt(childIndex);
            TextView themeName = (TextView) childView.findViewById(R.id.theme_name);
            themeName.setTextColor(getResources().getColor(customTextColor.resourceId));
        }

        /*清除recyclerView中的item缓存*/
        Class<RecyclerView> recyclerViewClass = RecyclerView.class;
        try {
            Field declaredField = recyclerViewClass.getDeclaredField("mRecycler");
            declaredField.setAccessible(true);
            Method declaredMethod = Class.forName(RecyclerView.Recycler.class.getName()).getDeclaredMethod("clear");
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(declaredField.get(recycler));
            RecyclerView.RecycledViewPool recycledViewPool = recycler.getRecycledViewPool();
            recycledViewPool.clear();

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    private Bitmap getCacheBitmapFromView(View view) {
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            bitmap = Bitmap.createBitmap(drawingCache);
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }

    public class NewsBroadcastReceiver extends BroadcastReceiver {

        public NewsBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int storyNum = intent.getIntExtra("NewsNum", 0);
            if (recycler.getChildAt(1) != null) {
                ImageView redDotIndicator = (ImageView) recycler.getChildAt(1).findViewById(R.id.red_dot_indicator);
                if (redDotIndicator != null) {
                    redDotIndicator.setVisibility(storyNum != 0 ? View.VISIBLE : View.GONE);
                }
            }
        }
    }
}
