package com.example.recipebuddy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.ImageButton;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.recipebuddy.DBConstants.*;

import java.util.ArrayList;
import java.util.HashSet;

public class AddIngredientsActivity extends AppCompatActivity {
    private static ImageButton cancel;
    private ArrayList<ItemsListSingleItem> data;
    private HashSet<Integer> selected;
    private SQLiteDatabase kitchenDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_ingredients);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cancel = (ImageButton) findViewById(R.id.imageButton);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //TODO USE ALL POSSIBLE INGREDIENTS DATABASE instead of items array below
        String[] items = {"Chicken", "Beef", "Pork", "Lamb", "Turkey"};

        DBHandlerIngredient ingredientDBHelper = new DBHandlerIngredient(this);
        SQLiteDatabase ingredientDB = ingredientDBHelper.getReadableDatabase();



        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                "name"
        };
        // Filter results WHERE "title" = 'My Title'
        ArrayList<String> ingredients = new ArrayList<>();

        Cursor cursor = ingredientDB.query(
                "ingredients",   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null             // The sort order
        );

        if (cursor.moveToFirst()) {
            do {
                ingredients.add(cursor.getString(cursor.getColumnIndex("name")));
            }while(cursor.moveToNext());

        }

        data = createItemsList(ingredients);
        KitchenDBHandler dbHelper = new KitchenDBHandler(this);
        kitchenDB = dbHelper.getWritableDatabase();

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewIngredients);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        // specify an adapter (see also next example)
        ItemsListAdapter adapter = new ItemsListAdapter(this, data, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {}
        });
        recyclerView.setAdapter(adapter);

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

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SparseBooleanArray selected = ((ItemsListAdapter)recyclerView.getAdapter()).getSelectedItems();
                ArrayList<String> ingredients = new ArrayList<>();
                for (int i = 0; i < selected.size(); i++) {
                    if (selected.valueAt(i)) {
                        ingredients.add(data.get(selected.keyAt(i)).getTitle());
                    }
                }
                for (int i = 0; i < ingredients.size(); i++) {
                    addIngredient(ingredients.get(i));
                }
                finishAffinity();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        fab.setImageBitmap(HelperMethods.textAsBitmap("ADD", 40, Color.WHITE));
    }

    public void addIngredient(String ingredient){
        ContentValues cv = new ContentValues();
        cv.put(KitchenColumns.COLUMN_NAME, ingredient);

        kitchenDB.insert(KitchenColumns.TABLE_NAME, null, cv);
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
