package com.example.recipebuddy;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class SavedRecipesDataAdapter extends RecyclerView.Adapter<SavedRecipesDataAdapter.ViewHolder> {
    CustomItemClickListener listener;
    SQLiteDatabase recipesDB;

    public SavedRecipesDataAdapter(SQLiteDatabase recipesDB, CustomItemClickListener listener) {
        this.recipesDB = recipesDB;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_ingredient, parent, false);
        final SavedRecipesDataAdapter.ViewHolder mViewHolder = new SavedRecipesDataAdapter.ViewHolder(mView);
        mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(v, mViewHolder.getPosition());
            }
        });
        return mViewHolder;
    }

    public Cursor getFavoritedRecipes() {
        return recipesDB.query(
                "recipes",
                null,
                "favorited=1",
                null,
                null,
                null,
                null);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cursor mCursor = getFavoritedRecipes();

        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex("name"));
        holder.itemTitle.setText(name);
        Context c = holder.itemView.getContext();
        holder.thumbnailImage.setImageResource(c.getResources().getIdentifier("recipe_" + name.toLowerCase().replaceAll(" ", "_"), "drawable", c.getPackageName()));
    }

    @Override
    public int getItemCount() {
        Cursor mCursor = getFavoritedRecipes();
        return mCursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        public TextView itemTitle;
        public ImageView thumbnailImage;

        ViewHolder(View v) {
            super(v);
            view = v;
            itemTitle = v
                    .findViewById(R.id.name);
            thumbnailImage = v.findViewById(R.id.thumbnail);
        }
    }
}
