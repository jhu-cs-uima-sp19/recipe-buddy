package com.example.recipebuddy;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeIngredientsFragment extends Fragment {
    View view;

    public static RecipeIngredientsFragment newInstance(String name) {

        Bundle args = new Bundle();

        RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
        args.putString("name", name);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DBHandlerRecipe dbHelper = new DBHandlerRecipe(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] ingredients = {};

        String COLUMN_NAME_TITLE = "name";

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "ingredients"
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = COLUMN_NAME_TITLE + " = ?";
        String[] selectionArgs = { getArguments().getString("name", "Beef and Potatoes") };

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
            String ingredientsStr = cursor.getString(cursor.getColumnIndex("ingredients"));
            ingredients = ingredientsStr.split(", ");
        }

        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewRecipesIngredients);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        RecipeIngredientsDataAdapter mAdapter = new RecipeIngredientsDataAdapter(ingredients, "RecipeIngredients");
        recyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // view the background view
            }
        };
    }
}
