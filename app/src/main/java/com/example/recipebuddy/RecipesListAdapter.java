package com.example.recipebuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RecipesListAdapter extends RecyclerView.Adapter<RecipesListAdapter.ViewHolder> {
    ArrayList<ItemsListSingleItem> data;

    Context mContext;
    CustomItemClickListener listener;
    SparseBooleanArray selectedItems = new SparseBooleanArray();
    SQLiteDatabase recipesDB;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_favoritable_ingredient, parent, false);
        final ViewHolder mViewHolder = new ViewHolder(mView);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getPosition());
            }
        });
        return mViewHolder;
    }

    public int recipeIsFavorited(String recipe) {
        String [] selArgs = {recipe};

        Cursor c = recipesDB.query(
                "recipes",
                null,
                "name=?",
                selArgs,
                null,
                null,
                null
        );
        c.moveToFirst();
        return c.getInt(c.getColumnIndex("favorited"));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemTitle.setText(Html.fromHtml(data.get(position).getTitle()));
        holder.thumbnailImage.setImageDrawable(data.get(position).getThumbnail());

        // check if this ingredient has been favorited by the user
        int favorited = recipeIsFavorited(data.get(position).getTitle());

        if (favorited == 0) {
            holder.toggleButton.setChecked(false);
        } else {
            holder.toggleButton.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public RecipesListAdapter(Context mContext, ArrayList<ItemsListSingleItem> data, CustomItemClickListener listener, SQLiteDatabase recipesDB) {
        this.data = data;
        this.mContext = mContext;
        this.listener = listener;
        this.recipesDB = recipesDB;
    }

    public SparseBooleanArray getSelectedItems() {
        return selectedItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView itemTitle;
        public ImageView thumbnailImage;
        public ToggleButton toggleButton;

        ViewHolder(View v) {
            super(v);
            view = v;
            itemTitle = v
                    .findViewById(R.id.name);
            thumbnailImage = v.findViewById(R.id.thumbnail);
            toggleButton = v.findViewById(R.id.myToggleButton);
            toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_baseline_star_border_24px));
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DBHandlerRecipe dbHelper = new DBHandlerRecipe(view.getContext());
                    SQLiteDatabase recipesDB = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();

                    if (isChecked) {
                        cv.put(DBConstants.KitchenColumns.COLUMN_FAVORITED, 1);
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_baseline_star_24px));
                        recipesDB.update("recipes", cv, "name" + " = ?", new String[]{itemTitle.getText().toString()});
                    } else {
                        cv.put(DBConstants.KitchenColumns.COLUMN_FAVORITED, 0);
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_baseline_star_border_24px));
                        recipesDB.update("recipes", cv, "name" + " = ?", new String[]{itemTitle.getText().toString()});
                    }
                }
            });
        }
    }
}

