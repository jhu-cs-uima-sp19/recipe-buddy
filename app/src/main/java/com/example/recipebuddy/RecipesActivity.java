package com.example.recipebuddy;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

public class RecipesActivity extends AppCompatActivity implements RecipeFilterActivity.ExampleDialogListener {

    private static ImageButton filter;
    private ArrayList<ItemsListSingleItem> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filter = (ImageButton) findViewById(R.id.filterRecipes);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterDialog();
            }
        });

        KitchenDBHandler kitchenDBhelper = new KitchenDBHandler(this);
        SQLiteDatabase kitchenDB = kitchenDBhelper.getReadableDatabase();
        DBHandlerRecipe dbHelper = new DBHandlerRecipe(this);
        SQLiteDatabase recipeDB = dbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "name"
        };
        String[] projectionRecipe = {
                "name",
                "main_ingredient"
        };

        // Filter results WHERE "title" = 'My Title'
        ArrayList<String> items = new ArrayList<>();
        ArrayList<String> kitchenItems = new ArrayList<>();
        Cursor cursor = recipeDB.query(
                "recipes",   // The table to query
                projectionRecipe,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null             // The sort order
        );
        Cursor kitchenCursor = kitchenDB.query(
                "kitchen",   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null             // The sort order
        );

        if (kitchenCursor.moveToFirst()) {
            do {
                kitchenItems.add(kitchenCursor.getString(kitchenCursor.getColumnIndex("name")));
            } while (kitchenCursor.moveToNext());

        }

        if (cursor.moveToFirst()) {
            do {
                for (String item : kitchenItems) {
                    if (cursor.getString(cursor.getColumnIndex("main_ingredient")).contains(item.toLowerCase())) {
                        items.add(cursor.getString(cursor.getColumnIndex("name")));
                    }
                }

            } while (cursor.moveToNext());

        }

        data = createItemsList(items);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewRecipes);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        // specify an adapter (see also next example)
        RecipesListAdapter adapter = new RecipesListAdapter(this, data, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(v.getContext(), DisplayRecipeActivity.class);
                intent.putExtra("name", data.get(position).getTitle());
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    public ArrayList<ItemsListSingleItem> createItemsList(ArrayList<String> list) {
        ArrayList<ItemsListSingleItem> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            Log.i("recipes activity", list.get(i).toLowerCase().replaceAll(" ", "_"));
            out.add(new ItemsListSingleItem(
                    i + 1,
                    list.get(i),
                    getResources().getDrawable(getResources().getIdentifier("recipe_" + list.get(i).toLowerCase().replaceAll(" ", "_"), "drawable", getPackageName()))
            ));
        }
        return out;
    }

    public void openFilterDialog() {
        RecipeFilterActivity exampleDialog = new RecipeFilterActivity();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");

    }
    @Override
    public void applyTexts(String username, String password) {
        return;
    }
}

