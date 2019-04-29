package com.example.recipebuddy;

import android.app.AlertDialog;
import android.content.DialogInterface;
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

    private void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirmation Dialog");
        builder.setMessage("Are you sure you would like to remove selected ingredients?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                HashMap<String, Boolean> selected = getSelected();

                for (HashMap.Entry<String, Boolean> i : selected.entrySet()) {
                    String key = i.getKey();
                    Boolean value = i.getValue();
                    if (value) {
                        kitchenDB.delete(KitchenColumns.TABLE_NAME, "name = ?", new String[] {key});
                    }
                }
                final View v = view;
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("mode", 0);
                startActivity(intent);
                getActivity().finishAffinity();
                getActivity().overridePendingTransition(0,0);

                // delay clickability to prevent double click
                v.setClickable(false);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setClickable(true);
                    }
                }, 500);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final View v = view;
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra("mode", 0);
                startActivity(intent);
                getActivity().finishAffinity();
                getActivity().overridePendingTransition(0,0);

                // delay clickability to prevent double click
                v.setClickable(false);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setClickable(true);
                    }
                }, 500);
            }
        });

        builder.show();
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
        String selected = getArguments().getString("value", "");
        // specify an adapter (see also next example)
        mAdapter = new DataAdapter(kitchenDB, MODE);
        mAdapter.setSelection(selected);
        recyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final View v = view;
                Intent intent = new Intent(getContext(), RecipesActivity.class);
                startActivity(intent);

                // delay clickability to prevent double click
                v.setClickable(false);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setClickable(true);
                    }
                }, 500);
            }
        });
        if (MODE == 1) {
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_delete_24px));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmDialog();
                }
            });
        }
    }

    public HashMap<String, Boolean> getSelected() {
        HashMap<String, Boolean> selected = mAdapter.getSelected();
        return selected;
    }
}
