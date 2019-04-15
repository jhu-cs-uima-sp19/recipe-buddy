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
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;

public class RecipesActivity extends AppCompatActivity {

    private static ImageButton cancel;
    private ArrayList<ItemsListSingleItem> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cancel = (ImageButton) findViewById(R.id.closeRecipes);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //TODO USE RECIPES DATABASE instead of items array below


        DBHandlerRecipe dbHelper = new DBHandlerRecipe(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String directions = "";

        String COLUMN_NAME_TITLE = "name";

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "name"
        };

        // Filter results WHERE "title" = 'My Title'
        ArrayList<String> items = new ArrayList<>();
        Cursor cursor = db.query(
                "recipes",   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null             // The sort order
        );

        if (cursor.moveToFirst()) {
            do{
                items.add(cursor.getString(cursor.getColumnIndex("name")));
            }while(cursor.moveToNext());

        }

        data = createItemsList(items);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewRecipes);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

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
            out.add(new ItemsListSingleItem(
                    i + 1,
                    list.get(i),
                    ""
            ));
        }
        return out;
    }
}
