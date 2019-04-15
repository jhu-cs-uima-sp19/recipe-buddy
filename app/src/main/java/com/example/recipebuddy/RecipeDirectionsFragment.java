package com.example.recipebuddy;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class RecipeDirectionsFragment extends Fragment {
    View view;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recipe_directions, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        //TODO get directions from recipes database
        DBHandler dbHelper = new DBHandler(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String directions = "";

        String COLUMN_NAME_TITLE = "name";

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "directions"
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { "Beef and Potatoes" };

        Cursor cursor = db.query(
                "recipes",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                 null             // The sort order
        );

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            directions = cursor.getString(cursor.getColumnIndex("directions"));
        }


        super.onViewCreated(view, savedInstanceState);
        TextView textView = (TextView) getView().findViewById(R.id.textView);
        textView.setText(directions);
//        textView.setText("hi");
    }
}
