package com.example.recipebuddy;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.database.Cursor;
import com.example.recipebuddy.DBConstants.*;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    private String[] mDataset;
    private Cursor mCursor;
    private String type;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is a view
        public View view;
        public TextView name, description, price;
        public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            thumbnail = v.findViewById(R.id.thumbnail);
            viewBackground = v.findViewById(R.id.view_background);
            viewForeground = v.findViewById(R.id.view_foreground);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)

    public DataAdapter(Cursor cursor) {
        mCursor = cursor;
        type = "Ingredients";
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public DataAdapter(Cursor cursor, String type) {
        mCursor = cursor;
        this.type = type;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DataAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_ingredient, parent, false);
        if (type.compareTo("RecipeIngredients") == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_recipe_ingredient, parent, false);
        }


        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String name = mCursor.getString(mCursor.getColumnIndex(KitchenColumns.COLUMN_NAME));
        holder.name.setText(name);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }
    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }

        mCursor = newCursor;

        if (newCursor != null) {
            notifyDataSetChanged();
        }
    }

}
