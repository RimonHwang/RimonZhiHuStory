package com.developer.rimon.zhihudaily.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.entity.ThemeList;
import com.developer.rimon.zhihudaily.ui.activity.LoginActivity;
import com.developer.rimon.zhihudaily.ui.activity.MyCollectActivity;
import com.developer.rimon.zhihudaily.ui.activity.UserActivity;
import com.developer.rimon.zhihudaily.utils.weibo.AccessTokenKeeper;
import com.developer.rimon.zhihudaily.utils.FileUtil;
import com.developer.rimon.zhihudaily.utils.ImageLoaderUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Rimon on 2016/9/12.
 */
public class DrawerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<ThemeList.Other> others;
    private OnThemeItemClickListener onThemeItemClickListener;

    public DrawerRecyclerAdapter(Context mContext, ArrayList<ThemeList.Other> others) {
        this.mContext = mContext;
        this.others = others;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view;
        if (viewType == Constants.TYPE_ITEM) {
            view = inflater.inflate(R.layout.item_drawer, parent, false);
            return new ItemViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.item_drawer_header, parent, false);
            return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (position == 0) {
            final HeaderViewHolder viewHolder = (HeaderViewHolder) holder;

            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Constants.PREFERENCES_NAME_USERINFO, Context.MODE_PRIVATE);
            String userName = sharedPreferences.getString(Constants.KEY_USERINFO_NAME, "");
            if (!TextUtils.isEmpty(userName)) {
                viewHolder.profileName.setText(userName);
                if (!FileUtil.loadLocalImage(mContext,Environment.DIRECTORY_PICTURES,Constants.AVATAR_FILE_NAME,viewHolder.profileImage)){
                    SimpleTarget<Bitmap> target = new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            viewHolder.profileImage.setImageBitmap(resource);
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    FileUtil.saveFileToSDCardPrivateFilesDir(mContext,resource, Environment.DIRECTORY_PICTURES,
                                            Constants.AVATAR_FILE_NAME);
                                }
                            }).start();
                        }
                    };
                    ImageLoaderUtils.load(mContext, sharedPreferences.getString(Constants.KEY_USERINFO_PROFILE_IMAGE_URL, ""), null, null, target);
                }
            }

            viewHolder.profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AccessTokenKeeper.readAccessToken(mContext).isSessionValid()) {
                        Intent intent = new Intent(mContext, UserActivity.class);
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(intent);
                    }
                }
            });
            viewHolder.homePage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onThemeItemClickListener.onClick(null);
                }
            });
            viewHolder.offlineDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "离线功能开发中……", Toast.LENGTH_SHORT).show();
                }
            });
            viewHolder.myFavor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (AccessTokenKeeper.readAccessToken(mContext).isSessionValid()) {
                        Intent intent = new Intent(mContext, MyCollectActivity.class);
                        mContext.startActivity(intent);
                    } else {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(intent);
                    }
                }
            });
        } else {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final ThemeList.Other other = others.get(holder.getAdapterPosition() - 1);
            itemViewHolder.themeName.setText(others.get(position - 1).name);
            itemViewHolder.subscribe.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "订阅功能暂不支持", Toast.LENGTH_SHORT).show();
                }
            });
            itemViewHolder.themeNameLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", other.id);
                    bundle.putString("name", other.name);
                    bundle.putString("description", other.description);
                    bundle.putString("thumbnail", other.thumbnail);
                    onThemeItemClickListener.onClick(bundle);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return others.size() == 0 ? 1 : others.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? Constants.TYPE_HEADER : Constants.TYPE_ITEM;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.profile_image)
        ImageView profileImage;
        @BindView(R.id.profile_name)
        TextView profileName;
        @BindView(R.id.profile_layout)
        LinearLayout profileLayout;
        @BindView(R.id.offline_download)
        TextView offlineDownload;
        @BindView(R.id.my_favor)
        TextView myFavor;
        @BindView(R.id.home_page)
        TextView homePage;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.theme_name)
        TextView themeName;
        @BindView(R.id.theme_name_layout)
        RelativeLayout themeNameLayout;
        @BindView(R.id.subscribe)
        ImageView subscribe;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setOnThemeItemClickListener(OnThemeItemClickListener onThemeItemClickListener) {
        this.onThemeItemClickListener = onThemeItemClickListener;
    }

    public interface OnThemeItemClickListener {
        public void onClick(Bundle bundle);
    }


}
