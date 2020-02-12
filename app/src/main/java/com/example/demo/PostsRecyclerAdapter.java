package com.example.demo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo.Model.Posts;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PostsRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;
    private ArrayList<Posts> mPostItems;
    private Context context;

    public PostsRecyclerAdapter(ArrayList<Posts> posts,Context context) {
        this.mPostItems = posts;
        this.context = context;
    }


    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.new_list_posts, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.item_progress, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == mPostItems.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }
    @Override
    public int getItemCount() {
        return mPostItems == null ? 0 : mPostItems.size();
    }
    public void addItems(List<Posts> postItems) {
        mPostItems.addAll(postItems);
        notifyDataSetChanged();
    }
    public void addLoading() {
        isLoaderVisible = true;
        mPostItems.add(new Posts());
        notifyItemInserted(mPostItems.size() - 1);
    }
    public void removeLoading() {
        isLoaderVisible = false;
        int position = mPostItems.size() - 1;
        Posts item = getItem(position);
        if (item != null) {
            mPostItems.remove(position);
            notifyItemRemoved(position);
        }
    }
    public void clear() {
        mPostItems.clear();
        notifyDataSetChanged();
    }
    Posts getItem(int position) {
        return mPostItems.get(position);
    }
    public class ViewHolder extends BaseViewHolder {
        @BindView(R.id.event_Name)
        TextView txtEventName;
        @BindView(R.id.likes)
        TextView txtLikes;
        @BindView(R.id.date)
        TextView txtDate;
        @BindView(R.id.views)
        TextView txtVies;
        @BindView(R.id.shares)
        TextView txtShares;
        @BindView(R.id.imag1)
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        protected void clear() {
        }
        public void onBind(int position) {
            super.onBind(position);
            Posts item = mPostItems.get(position);
            txtEventName.setText(item.getEvent_name());
            txtLikes.setText("Likes: "+String.valueOf(item.getLikes()));
            txtDate.setText(String.valueOf(new SimpleDateFormat("dd:mm:yyyy hh:mm a", Locale.ENGLISH)
            .format(new Date(item.getEvent_Date() * 1000))));
            txtVies.setText("Views: "+String.valueOf(item.getViews()));
            txtShares.setText("Shares: "+String.valueOf(item.getShares()));

            Picasso.with(context)
                    .load(item.getThumbnail_image())
                    .into(imageView);
        }
    }
    public class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
        @Override
        protected void clear() {
        }
    }
}
