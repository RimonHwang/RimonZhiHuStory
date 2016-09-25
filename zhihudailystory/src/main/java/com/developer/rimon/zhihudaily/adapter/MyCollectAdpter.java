package com.developer.rimon.zhihudaily.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.entity.Collect;
import com.developer.rimon.zhihudaily.ui.activity.StoryDetailActivity;
import com.developer.rimon.zhihudaily.utils.ImageLoaderUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rimon on 2016/9/19.
 */
public class MyCollectAdpter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context mContext;
    private ArrayList<Collect> collectArrayList;
    private SharedPreferences sharedPreferences;

    public MyCollectAdpter(Context mContext, ArrayList<Collect> collectArrayList) {
        this.mContext = mContext;
        this.collectArrayList = collectArrayList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View headView = inflater.inflate(R.layout.item_story, parent, false);
        return new ItemViewHolder(headView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemViewHolder viewHolder = (ItemViewHolder) holder;
        final Collect collect = collectArrayList.get(position);
        viewHolder.titleText.setText(collect.getTitle());
        ImageLoaderUtils.load(mContext,collect.getImages().get(0),null,null,viewHolder.titleImage);
        viewHolder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, StoryDetailActivity.class);
                intent.putExtra(Constants.MAIN_TO_STORY_DETAIL_INTENT_KEY_ID, collect.getId());
                intent.putExtra("title",collect.getTitle());
                intent.putExtra("image",collect.getImages().get(0));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return collectArrayList.size();
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
}
