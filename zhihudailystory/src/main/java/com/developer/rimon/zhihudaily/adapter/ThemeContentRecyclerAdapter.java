package com.developer.rimon.zhihudaily.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.entity.Story;
import com.developer.rimon.zhihudaily.ui.activity.StoryDetailActivity;
import com.developer.rimon.zhihudaily.utils.ImageLoaderUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rimon on 2016/8/26.
 */
public class ThemeContentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private String description;
    private String thumbnail;
    private SharedPreferences sharedPreferences;
    private ArrayList<Story> storyArrayList;

    public ThemeContentRecyclerAdapter(Context mContext, ArrayList<Story> storyArrayList, String description, String thumbnail) {
        this.mContext = mContext;
        this.storyArrayList = storyArrayList;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case Constants.TYPE_HEADER:
                View headView = inflater.inflate(R.layout.item_theme_header, parent, false);
                return new HeaderViewHolder(headView);
            case Constants.TYPE_ITEM:
                View itemView = inflater.inflate(R.layout.item_theme, parent, false);
                return new ItemViewHolder(itemView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof HeaderViewHolder) {
            final HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            Glide.with(mContext).load(thumbnail).into(new SimpleTarget<GlideDrawable>() {
                @Override
                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                    viewHolder.themeImage.setImageDrawable(resource);
                    viewHolder.themeImage.setVisibility(View.VISIBLE);
                    viewHolder.themeDescription.setText(description);
                }
            });
        } else if (holder instanceof ItemViewHolder) {
            sharedPreferences = mContext.getSharedPreferences(Constants.SAVE_FILE_NAME, Context.MODE_PRIVATE);
            final ItemViewHolder viewHolder = (ItemViewHolder) holder;
            final Story story = storyArrayList.get(holder.getAdapterPosition() - 1);
            if (sharedPreferences.getBoolean(story.id, false)){
                viewHolder.titleText.setTextColor(ContextCompat.getColor(mContext,R.color.item_selected_color));
            }
            viewHolder.titleText.setText(story.title);
            if (story.images != null) {
                viewHolder.titleImage.setVisibility(View.VISIBLE);
                ImageLoaderUtils.load(mContext, story.images.get(0), null, null, viewHolder.titleImage);
            } else {
                viewHolder.titleImage.setVisibility(View.GONE);
            }
            viewHolder.cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(story.id, true);
                    editor.apply();
                    viewHolder.titleText.setTextColor(ContextCompat.getColor(mContext,R.color.item_selected_color));
                    Intent intent = new Intent(mContext, StoryDetailActivity.class);
                    intent.putExtra("id", story.id);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return storyArrayList.size() == 0 ? 0 : storyArrayList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Constants.TYPE_HEADER;
        }
        return Constants.TYPE_ITEM;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.theme_image)
        ImageView themeImage;
        @BindView(R.id.theme_description)
        TextView themeDescription;

        HeaderViewHolder(View view) {
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

}
