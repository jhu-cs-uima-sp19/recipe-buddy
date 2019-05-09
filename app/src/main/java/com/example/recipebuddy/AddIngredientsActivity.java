package com.example.recipebuddy;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.recipebuddy.DBConstants.*;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddIngredientsActivity extends AppCompatActivity {
    private static ImageButton search;
    private ArrayList<ItemsListSingleItem> data;
    private SQLiteDatabase kitchenDB;
    private ItemsListAdapter adapter;
    private EditText editText;
    private MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);

        KitchenDBHandler dbHelper = new KitchenDBHandler(this);
        kitchenDB = dbHelper.getWritableDatabase();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                // if search view closed, list default
                filter("");
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
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
                    if (cursor.getInt(cursor.getColumnIndex("favorited")) == 1) {
                        ingredients.add(0, ingredient);
                    } else {
                        ingredients.add(ingredient);
                    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
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
            Drawable thumb = getResources().getDrawable(getResources().getIdentifier("ing_" + list.get(i).trim().toLowerCase().replaceAll(" ", "_"), "drawable", getPackageName()));
            out.add(new ItemsListSingleItem(
                    i + 1,
                    list.get(i),
                    thumb
            ));
        }
        return out;
    }
}
