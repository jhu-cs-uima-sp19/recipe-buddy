package com.example.recipebuddy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.recipebuddy.DBConstants.*;

import java.util.ArrayList;
import java.util.HashMap;


public class IngredientsFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    SQLiteDatabase kitchenDB;
    DataAdapter mAdapter;
    ToggleButton toggleButton;
    int MODE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_ingredients, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        KitchenDBHandler dbHelper = new KitchenDBHandler(getContext());
        kitchenDB = dbHelper.getReadableDatabase();

        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerViewIngredients);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        MODE = getArguments().getInt("mode", 0);
        // specify an adapter (see also next example)
        mAdapter = new DataAdapter(getKitchenIngredients(), MODE);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), RecipesActivity.class);
                startActivity(intent);
            }
        });
        fab.setImageBitmap(HelperMethods.textAsBitmap("Let's Cook!", 40, Color.WHITE));
    }

    public HashMap<String, Boolean> getSelected() {
        HashMap<String, Boolean> selected = mAdapter.getSelected();
        return selected;
    }

    public Cursor getKitchenIngredients() {
        return kitchenDB.query(
                KitchenColumns.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                KitchenColumns.COLUMN_TIMESTAMP + " DESC"
        );
    }
}
