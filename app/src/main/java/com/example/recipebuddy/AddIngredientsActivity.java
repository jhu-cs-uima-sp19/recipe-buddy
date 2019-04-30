package com.example.recipebuddy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.recipebuddy.DBConstants.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class AddIngredientsActivity extends AppCompatActivity {
    private static ImageButton cancel;
    private ArrayList<ItemsListSingleItem> data;
    private HashSet<Integer> selected;
    private SQLiteDatabase kitchenDB;
    private ItemsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);

        KitchenDBHandler dbHelper = new KitchenDBHandler(this);
        kitchenDB = dbHelper.getWritableDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cancel = findViewById(R.id.imageButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        DBHandlerIngredient ingredientDBHelper = new DBHandlerIngredient(this);
        SQLiteDatabase ingredientDB = ingredientDBHelper.getReadableDatabase();

        // Filter results WHERE "title" = 'My Title'
        ArrayList<String> ingredients = new ArrayList<>();

        Cursor cursor = ingredientDB.query(
                "ingredients",            // The table to query
                null,                       // The array of columns to return (pass null to get all)
                null,                  // The columns for the WHERE clause
                null,               // The values for the WHERE clause
                null,                  // don't group the rows
                null,                    // don't filter by row groups
                null                   // The sort order
        );

        Cursor ingredientsCursor = kitchenDB.query(
                KitchenColumns.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<String> alreadyInKitchen = new ArrayList<>();

        if (ingredientsCursor.moveToFirst()) {
            do {
                alreadyInKitchen.add(ingredientsCursor.getString(ingredientsCursor.getColumnIndex("name")));
            } while(ingredientsCursor.moveToNext());
        }

        if (cursor.moveToFirst()) {
            do {
                String ingredient = cursor.getString(cursor.getColumnIndex("name"));
                if (!alreadyInKitchen.contains(ingredient)) {
                    ingredients.add(ingredient);
                }
            } while(cursor.moveToNext());
        }

        data = createItemsList(ingredients);

        final RecyclerView recyclerView = findViewById(R.id.recyclerViewIngredients);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        // specify an adapter (see also next example)
        adapter = new ItemsListAdapter(this, data, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {}
        });
        recyclerView.setAdapter(adapter);

        // edit text as search bar
        EditText editText = findViewById(R.id.edittextIngredients);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Boolean> selected = ((ItemsListAdapter)recyclerView.getAdapter()).getSelectedItems();
                ArrayList<String> ingredients = new ArrayList<>();
                for (Map.Entry<String, Boolean> entry: selected.entrySet()) {
                    String key = entry.getKey();
                    Boolean val = entry.getValue();
                    if (val) {
                        addIngredient(key);
                    }
                }
                finishAffinity();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void filter(String text) {
        ArrayList<ItemsListSingleItem> filteredList = new ArrayList<>();

        for (ItemsListSingleItem item : data) {
            if (item.getTitle().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filterList(filteredList);
    }

    public void addIngredient(String ingredient){
        ContentValues cv = new ContentValues();
        cv.put(KitchenColumns.COLUMN_NAME, ingredient);

        kitchenDB.insert(KitchenColumns.TABLE_NAME, null, cv);
    }

    public ArrayList<ItemsListSingleItem> createItemsList(ArrayList<String> list) {
        ArrayList<ItemsListSingleItem> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Log.i("createItemsList", list.get(i));
            Drawable thumb = getResources().getDrawable(getResources().getIdentifier("ing_" + list.get(i).toLowerCase().replaceAll(" ", "_"), "drawable", getPackageName()));
            out.add(new ItemsListSingleItem(
                    i + 1,
                    list.get(i),
//                    getResources().getDrawable(getResources().getIdentifier("th_apple", "drawable", getPackageName()))
                    thumb
            ));
        }
        return out;
    }
}
