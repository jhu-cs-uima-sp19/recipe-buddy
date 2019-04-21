package com.example.recipebuddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.database.Cursor;
import android.widget.ToggleButton;
import android.os.Vibrator;

import com.example.recipebuddy.DBConstants.*;

import java.util.HashMap;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.MyViewHolder> {
    private String[] mDataset;
    private Cursor mCursor;
    public String type;
    CustomItemClickListener listener;
    SparseBooleanArray selectedItems = new SparseBooleanArray();
    HashMap<String, Boolean> selected = new HashMap<String, Boolean>();
    int MODE;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is a view
        public View view;
        public TextView name, description, price;
        public ImageView thumbnail;
        public RelativeLayout viewBackground, viewForeground;
        public ToggleButton toggleButton;
        CustomItemClickListener listener;
        SparseBooleanArray selectedItems = new SparseBooleanArray();

        public MyViewHolder(View v) {
            super(v);
            final View view = v;
            name = v.findViewById(R.id.name);
            thumbnail = v.findViewById(R.id.thumbnail);
            viewBackground = v.findViewById(R.id.view_background);
            viewForeground = v.findViewById(R.id.view_foreground);
            toggleButton = (ToggleButton) v.findViewById(R.id.myToggleButton);
            toggleButton.setChecked(false);
            toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(v.getContext(), R.drawable.ic_baseline_star_border_24px));
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //TODO handle favorited items
                    if (isChecked)
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(),R.drawable.ic_baseline_star_24px));
                    else
                        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.ic_baseline_star_border_24px));
                }
            });
        }
    }

    public DataAdapter(Cursor cursor, int mode) {
        mCursor = cursor;
        type = "Ingredients";
        MODE = mode;
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
                .inflate(R.layout.view_favoritable_ingredient, parent, false);
        if (type.compareTo("RecipeIngredients") == 0) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_recipe_ingredient, parent, false);
        }
        final MyViewHolder mViewHolder = new  DataAdapter.MyViewHolder(v);
        if (MODE == 0) {
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Vibrator v = (Vibrator) view.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(500);
                    }

                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    intent.putExtra("mode", 1);
                    view.getContext().startActivity(intent);
                    ((Activity) view.getContext()).finish();

                    return true;
                }
            });
        } else if (MODE == 1) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    listener.onItemClick(view, mViewHolder.getPosition());
                    if (selectedItems.get(mViewHolder.getAdapterPosition(), false)) {
                        selectedItems.delete(mViewHolder.getAdapterPosition());
                        view.findViewById(R.id.view_foreground).setSelected(false);
                    }
                    else {
                        selectedItems.put(mViewHolder.getAdapterPosition(), true);
                        view.findViewById(R.id.view_foreground).setSelected(true);
                    }
                    mCursor.moveToPosition(mViewHolder.getAdapterPosition());

                    String value = mCursor.getString(mCursor.getColumnIndex("name"));
                    if (selected.get(value) != null && selected.get(value)) {
                        selected.put(value, false);
                        view.findViewById(R.id.view_foreground).setSelected(false);
                    } else {
                        selected.put(value, true);
                        view.findViewById(R.id.view_foreground).setSelected(true);
                    }
//                    String test = "";
//
//                    for (HashMap.Entry<String, Boolean> i : selected.entrySet()) {
//                        String key = i.getKey();
//                        Boolean v = i.getValue();
//                        if (v) {
//                            test += key + " ";
//                        }
//                    }
//
//                    Snackbar snackbar = Snackbar.make(view.getRootView(), test, Snackbar.LENGTH_LONG);
//                    snackbar.show();
                }
            });
        }
        return mViewHolder;
    }

    public HashMap<String, Boolean> getSelected() {
        return selected;
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
