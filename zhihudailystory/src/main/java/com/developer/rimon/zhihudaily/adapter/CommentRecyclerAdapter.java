package com.developer.rimon.zhihudaily.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.developer.rimon.zhihudaily.Constants;
import com.developer.rimon.zhihudaily.R;
import com.developer.rimon.zhihudaily.ui.activity.CommentDetailActivity;
import com.developer.rimon.zhihudaily.entity.Comment;
import com.developer.rimon.zhihudaily.utils.DateUtil;
import com.developer.rimon.zhihudaily.utils.ImageLoaderUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Rimon on 2016/8/26.
 */
public class CommentRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private int headerAndFooterCount = 2;
    private int footerViewPosition = -1;
    private boolean hasClickShortCommentLayout = false;
    private ArrayList<Comment> comments;
    private CommentRecyclerAdapter.OnCommentLayoutClickListener onCommentLayoutClickListener;

    public CommentRecyclerAdapter(Context mContext, ArrayList<Comment> comments) {
        this.mContext = mContext;
        this.comments = comments;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case Constants.TYPE_HEADER:
                View headView = inflater.inflate(R.layout.item_comment_header_footer, parent, false);
                return new HeaderViewHolder(headView);
            case Constants.TYPE_ITEM:
                View itemView = inflater.inflate(R.layout.item_comment, parent, false);
                return new ItemViewHolder(itemView);
            case Constants.TYPE_FOOTER:
                View footView = inflater.inflate(R.layout.item_comment_header_footer, parent, false);
                return new FooterViewHolder(footView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            itemViewHolder.like.setText(String.valueOf(comments.get(position - 1).likes));
            Comment comment = comments.get(position - 1);
            ImageLoaderUtils.load(mContext, comment.avatar, null, null, itemViewHolder.userAvatar);
            itemViewHolder.userName.getPaint().setFakeBoldText(true);
            itemViewHolder.userName.setText(comment.author);
            itemViewHolder.commentContent.setText(comment.content);
            itemViewHolder.commentTime.setText(DateUtil.getDateWithMillis((long) comment.time * 1000));
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
            headerViewHolder.commentCount.setText(CommentDetailActivity.longCommentsCount + "条长评");
        } else {
            final FooterViewHolder footerViewHolder = (FooterViewHolder) holder;

            footerViewHolder.commentCount.setText(hasClickShortCommentLayout ? CommentDetailActivity.shortCommentsCount
                    + "条短评" : "点击查看" + CommentDetailActivity.shortCommentsCount + "条短评");
            footerViewHolder.shortCommentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onCommentLayoutClickListener != null) {
                        hasClickShortCommentLayout = true;
                        onCommentLayoutClickListener.onClick();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return comments.size() + headerAndFooterCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return Constants.TYPE_HEADER;
        } else if (position == footerViewPosition || (comments.size() == 0 && position == 1)) {
            return Constants.TYPE_FOOTER;
        }
        return Constants.TYPE_ITEM;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_avatar)
        ImageView userAvatar;
        @BindView(R.id.user_name)
        TextView userName;
        @BindView(R.id.like)
        TextView like;
        @BindView(R.id.comment_content)
        TextView commentContent;
        @BindView(R.id.comment_time)
        TextView commentTime;

        ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_count)
        TextView commentCount;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_count)
        TextView commentCount;
        @BindView(R.id.short_comment_layout)
        LinearLayout shortCommentLayout;

        FooterViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setHeaderAndFooterCount(int headerAndFooterCount) {
        this.headerAndFooterCount = headerAndFooterCount;
    }

    public void setFooterViewPosition(int footerViewPosition) {
        this.footerViewPosition = footerViewPosition;
    }

    public void setOnCommentLayoutClickListener(OnCommentLayoutClickListener onCommentLayoutClickListener) {
        this.onCommentLayoutClickListener = onCommentLayoutClickListener;
    }

    public interface OnCommentLayoutClickListener {
        public void onClick();
    }
}
