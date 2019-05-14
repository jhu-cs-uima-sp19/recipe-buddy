package com.example.recipebuddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class RecipesActivity extends AppCompatActivity implements AllergyFilterDialog.AllergyDialogListener{

    private static ImageButton filter;
    private ArrayList<ItemsListSingleItem> data;
    private ArrayList<String> allergies;
    private double threshold = 0.5;
    private RecipesListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queryData();
        // clear shared preferences for filters
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        int i = sharedPref.getInt("size", 0);
        for (int j = 0; j < i; j++) {
            editor.remove(Integer.toString(j));
        }
        editor.remove("size");
        editor.commit();
    }

    protected void queryData(){
        query(true);
    }

    protected void query(boolean finish){
        setContentView(R.layout.activity_recipes);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        filter = findViewById(R.id.filterRecipes);
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
                "main_ingredient",
                "ingredients",
                "allergies"
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
                Boolean has_required_ingredients = true;
                String[] requiredIngredients = cursor.getString(cursor.getColumnIndex("main_ingredient")).split(", ");

                for (String item : requiredIngredients) {
                    if (!kitchenItems.contains(item)) {
                        has_required_ingredients = false;
                        break;
                    }
                }

                if(has_required_ingredients){
                    String recipe_allergy = cursor.getString(cursor.getColumnIndex("allergies")).trim().toLowerCase();

                    Boolean check_allergy = false;
                    if(allergies != null){
                        for (String allergy : allergies){
                            if(allergy.toLowerCase().equals(recipe_allergy)){
                                check_allergy = true;
                                break;
                            }
                        }
                    }
                    if(!check_allergy){
                        items.add(cursor.getString(cursor.getColumnIndex("name")));
                    }
                }


            } while (cursor.moveToNext());

        }

        if (items.isEmpty() && finish) {
            Toast.makeText(getApplicationContext(),"No recipes with selected ingredients",Toast.LENGTH_SHORT).show();
            finish();
        } else if (items.isEmpty()) {
            Toast.makeText(getApplicationContext(),"No recipes with selected ingredients",Toast.LENGTH_SHORT).show();
        }

        data = createItemsList(items);

        final RecyclerView recyclerView = findViewById(R.id.recyclerViewRecipes);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));

        // specify an adapter (see also next example)
        adapter = new RecipesListAdapter(this, data, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(v.getContext(), DisplayRecipeActivity.class);
                intent.putExtra("name", data.get(position).getTitle());
                startActivity(intent);
            }
        }, new DBHandlerRecipe(this).getReadableDatabase());
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    public ArrayList<ItemsListSingleItem> createItemsList(ArrayList<String> list) {
        ArrayList<ItemsListSingleItem> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
//            Log.i("recipes activity", list.get(i).toLowerCase().replaceAll(" ", "_"));
            out.add(new ItemsListSingleItem(
                    i + 1,
                    list.get(i),
                    getResources().getDrawable(getResources().getIdentifier("recipe_" + list.get(i).toLowerCase().replaceAll(" ", "_"), "drawable", getPackageName()))
            ));
        }
        return out;
    }

    public void openFilterDialog() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        int i = sharedPref.getInt("size", 0);
        boolean[] selectedArray = new boolean[i];
        for (int j = 0; j < i; j++) {
            int selected = sharedPref.getInt(Integer.toString(j), 0);
            selectedArray[j] = selected == 1;
        }

        AllergyFilterDialog filterDialog = new AllergyFilterDialog();
        Bundle b = new Bundle();
        b.putBooleanArray("selected", selectedArray);
        filterDialog.setArguments(b);
        filterDialog.show(getSupportFragmentManager(), "filter dialog");
    }
    @Override
    public void submitted(ArrayList<String> input){
        allergies = input;
        System.out.println(allergies);
        query(false);
    }
}

