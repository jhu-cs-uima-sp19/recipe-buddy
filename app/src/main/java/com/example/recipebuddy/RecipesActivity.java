package com.example.recipebuddy;

import android.content.Intent;
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

        String[] items = {"Chicken", "Beef", "Pork", "Lamb", "Turkey"};
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

    public ArrayList<ItemsListSingleItem> createItemsList(String[] list) {
        ArrayList<ItemsListSingleItem> out = new ArrayList<>();
        for (int i = 0; i < list.length; i++) {
            out.add(new ItemsListSingleItem(
                    i + 1,
                    list[i],
                    ""
            ));
        }
        return out;
    }
}
