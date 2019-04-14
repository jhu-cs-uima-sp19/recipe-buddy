package com.example.recipebuddy;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipesListAdapter extends RecyclerView.Adapter<RecipesListAdapter.ViewHolder> {
    ArrayList<ItemsListSingleItem> data;

    Context mContext;
    CustomItemClickListener listener;
    SparseBooleanArray selectedItems = new SparseBooleanArray();

    public RecipesListAdapter(Context context, ArrayList<ItemsListSingleItem> itemList) {
        this.data = itemList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_ingredient, parent, false);
        final ViewHolder mViewHolder = new ViewHolder(mView);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getPosition());
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemTitle.setText(Html.fromHtml(data.get(position).getTitle()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public RecipesListAdapter(Context mContext, ArrayList<ItemsListSingleItem> data, CustomItemClickListener listener) {
        this.data = data;
        this.mContext = mContext;
        this.listener = listener;
    }

    public SparseBooleanArray getSelectedItems() {
        return selectedItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;
        public ImageView thumbnailImage;

        ViewHolder(View v) {
            super(v);
            itemTitle = (TextView) v
                    .findViewById(R.id.name);
            thumbnailImage = (ImageView) v.findViewById(R.id.thumbnail);
        }
    }
}

