package com.example.recipebuddy;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemsListAdapter extends RecyclerView.Adapter<ItemsListAdapter.ViewHolder> {
    ArrayList<ItemsListSingleItem> data;

    Context mContext;
    CustomItemClickListener listener;
    HashMap<String, Boolean> selected = new HashMap<String, Boolean>();

    public ItemsListAdapter(Context context, ArrayList<ItemsListSingleItem> itemList) {
        this.data = itemList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_ingredient, parent, false);
        final ViewHolder mViewHolder = new ViewHolder(mView);
        mViewHolder.setIsRecyclable(false);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = data.get(mViewHolder.getAdapterPosition()).getTitle();
                if (selected.get(value) != null && selected.get(value)) {
                    selected.put(value, false);
                    view.findViewById(R.id.view_foreground).setSelected(false);
                } else {
                    selected.put(value, true);
                    view.findViewById(R.id.view_foreground).setSelected(true);
                }
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(mContext.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemTitle.setText(Html.fromHtml(data.get(position).getTitle()));
        holder.thumbnail.setImageDrawable(data.get(position).getThumbnail());
        String name = data.get(position).getTitle();
        if (selected.get(name) != null && selected.get(name)) {
            holder.viewForeground.setSelected(true);
        }
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public void filterList(ArrayList<ItemsListSingleItem> filteredList) {
        this.data = filteredList;
        notifyDataSetChanged();

    }

    public ItemsListAdapter(Context mContext, ArrayList<ItemsListSingleItem> data, CustomItemClickListener listener) {
        this.data = data;
        this.mContext = mContext;
        this.listener = listener;
    }

    public HashMap<String, Boolean> getSelectedItems() {
        return selected;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;
        public View view;
        public TextView name, description, price;
        public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;
        public ToggleButton toggleButton;

        ViewHolder(View v) {
            super(v);
            view = v;
            viewBackground = v.findViewById(R.id.view_background);
            viewForeground = v.findViewById(R.id.view_foreground);
            itemTitle = (TextView) v
                    .findViewById(R.id.name);
            thumbnail = (ImageView) v.findViewById(R.id.thumbnail);
        }
    }
}