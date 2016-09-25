package com.developer.rimon.zhihudaily.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.entity.Story;
import com.developer.rimon.zhihudaily.entity.TopStory;
import com.developer.rimon.zhihudaily.ui.activity.StoryDetailActivity;
import com.developer.rimon.zhihudaily.utils.DateUtil;
import com.developer.rimon.zhihudaily.utils.ImageLoaderUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rimon on 2016/8/26.
 */
public class StoryRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;

    private int headerViewCound = 2;
    private int currentItem; //当前页面
    private ArrayList<Story> storyArrayList;
    private ArrayList<TopStory> topStoryList;

    private ScheduledExecutorService scheduledExecutorService;
    private ViewPager viewPager;
    private Bundle viewpagerPosition = new Bundle();
    private SharedPreferences sharedPreferences;

    public StoryRecyclerAdapter(Context mContext, ArrayList<Story> storyArrayList, ArrayList<TopStory> topStoryList) {
        this.mContext = mContext;
        this.storyArrayList = storyArrayList;
        this.topStoryList = topStoryList;
        sharedPreferences = mContext.getSharedPreferences(Constants.SAVE_FILE_NAME, Activity.MODE_PRIVATE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case Constants.TYPE_HEADER:
                View headView = inflater.inflate(R.layout.item_story_header, parent, false);
                return new HeaderViewHolder(headView);
            case Constants.TYPE_HEADER_SECOND:
                View secondHeadView = inflater.inflate(R.layout.item_story_header_second, parent, false);
                return new SecondHeaderViewHolder(secondHeadView);
            case Constants.TYPE_DATE:
                View dateView = inflater.inflate(R.layout.item_story_date, parent, false);
                return new DateViewHolder(dateView);
            case Constants.TYPE_ITEM:
                View itemView = inflater.inflate(R.layout.item_story, parent, false);
                return new ItemViewHolder(itemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            HeaderPagerAdapter adapter = new HeaderPagerAdapter(mContext, topStoryList);
            viewPager = viewHolder.viewpager;
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(viewpagerPosition.getInt("position", 0));
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    currentItem = position;
                    viewpagerPosition.putInt("position", position);
                }

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

        } else if (holder instanceof SecondHeaderViewHolder){
            SecondHeaderViewHolder viewHolder = (SecondHeaderViewHolder) holder;
        } else if (holder instanceof DateViewHolder) {
            DateViewHolder viewHolder = (DateViewHolder) holder;
            try {
                viewHolder.dateIndicator.setText( DateUtil.changeFormat(storyArrayList.get(position - headerViewCound).date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (holder instanceof ItemViewHolder) {
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
            final Story story = storyArrayList.get(position - headerViewCound);
            if (sharedPreferences.getBoolean(story.id, false)) {
                viewHolder.titleText.setTextColor(ContextCompat.getColor(mContext,R.color.item_selected_color));
            }else {
                Resources.Theme theme = mContext.getTheme();
                TypedValue customTextColor = new TypedValue();//自定义字体颜色
                theme.resolveAttribute(R.attr.customTextColor, customTextColor, true);
                viewHolder.titleText.setTextColor(mContext.getResources().getColor(customTextColor.resourceId));
            }
            viewHolder.titleText.setText(story.title);
            if (story.images != null) {
                ImageLoaderUtils.load(mContext, story.images.get(0), null, null, viewHolder.titleImage);
            }
            viewHolder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(story.id, true);
                    editor.apply();
                    viewHolder.titleText.setTextColor(ContextCompat.getColor(mContext,R.color.item_selected_color));
                    Intent intent = new Intent(mContext, StoryDetailActivity.class);
                    intent.putExtra(Constants.MAIN_TO_STORY_DETAIL_INTENT_KEY_ID, story.id);
                    intent.putExtra("title",story.title);
                    intent.putExtra("image",story.images.get(0));
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return storyArrayList.size() == 0 ? 0 : storyArrayList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Constants.TYPE_HEADER;
        } else if (position == 1) {
            return Constants.TYPE_HEADER_SECOND;
        } else if (TextUtils.isEmpty(storyArrayList.get(position - headerViewCound).title)) {
            return Constants.TYPE_DATE;
        }
        return Constants.TYPE_ITEM;
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if (holder instanceof HeaderViewHolder) {
            scheduledExecutorService.shutdownNow();
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if (holder instanceof HeaderViewHolder) {
            startSchedule();
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.viewpager)
        ViewPager viewpager;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class SecondHeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date_indicator)
        TextView dateIndicator;
        @BindView(R.id.red_dot_indicator)
        ImageView redDotIndicator;

        SecondHeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.date_indicator)
        TextView dateIndicator;

        DateViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.title_text)
        TextView titleText;
        @BindView(R.id.title_image)
        ImageView titleImage;
        @BindView(R.id.cardview)
        CardView cardview;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            viewPager.setCurrentItem(currentItem);
        }
    };

    private void startSchedule() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                currentItem = (currentItem + 1) % topStoryList.size();
                mHandler.obtainMessage().sendToTarget();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }


}
