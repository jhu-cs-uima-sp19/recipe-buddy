package com.example.recipebuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    SQLiteDatabase ingredDB;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_favoritable_ingredient, parent, false);
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
                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        return mViewHolder;
    }

    public int getIngredient(String ingred) {
        String [] selArgs = {ingred};

        Cursor c = ingredDB.query(
                "ingredients",
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
        holder.thumbnail.setImageDrawable(data.get(position).getThumbnail());
        String name = data.get(position).getTitle();
        if (selected.get(name) != null && selected.get(name)) {
            holder.viewForeground.setSelected(true);
        }

        // check if this ingredient has been favorited by the user
        int favorited = getIngredient(name);

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

    public void filterList(ArrayList<ItemsListSingleItem> filteredList) {
        this.data = filteredList;
        notifyDataSetChanged();

    }

    public ItemsListAdapter(Context mContext, ArrayList<ItemsListSingleItem> data, CustomItemClickListener listener) {
        this.data = data;
        this.mContext = mContext;
        this.listener = listener;
        DBHandlerIngredient ingreDBHandler = new DBHandlerIngredient(mContext);
        this.ingredDB = ingreDBHandler.getReadableDatabase();
    }

    public HashMap<String, Boolean> getSelectedItems() {
        return selected;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemTitle;
        public View view;
        public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;
        public ToggleButton toggleButton;

        ViewHolder(View v) {
            super(v);
            view = v;
            viewBackground = v.findViewById(R.id.view_background);
            viewForeground = v.findViewById(R.id.view_foreground);
            itemTitle = v
                    .findViewById(R.id.name);
            thumbnail = v.findViewById(R.id.thumbnail);
            toggleButton = v.findViewById(R.id.myToggleButton);
            toggleButton = v.findViewById(R.id.myToggleButton);
            toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_baseline_star_border_24px));
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DBHandlerIngredient dbHelper = new DBHandlerIngredient(view.getContext());
                    SQLiteDatabase ingreDB = dbHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();

                    if (isChecked) {
                        cv.put(DBConstants.KitchenColumns.COLUMN_FAVORITED, 1);
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_baseline_star_24px));
                        ingreDB.update("ingredients", cv, "name" + " = ?", new String[]{itemTitle.getText().toString()});
                    } else {
                        cv.put(DBConstants.KitchenColumns.COLUMN_FAVORITED, 0);
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_baseline_star_border_24px));
                        ingreDB.update("ingredients", cv, "name" + " = ?", new String[]{itemTitle.getText().toString()});
                    }
                }
            });
        }
    }
}