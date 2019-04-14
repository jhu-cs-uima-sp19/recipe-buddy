package com.example.recipebuddy;

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

import java.util.ArrayList;
import java.util.HashSet;

public class AddIngredientsActivity extends AppCompatActivity {
    private static ImageButton cancel;
    private ArrayList<ItemsListSingleItem> data;
    private HashSet<Integer> selected;

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

        String[] items = {"Chicken", "Beef", "Pork", "Lamb", "Turkey"};
        data = createItemsList(items);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerViewIngredients);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

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
                //TODO USE INGREDIENTS TO UPDATE INGREDIENTS DATABASE
                String out = "";
                for (int i = 0; i < ingredients.size(); i++) {
                    out = out + ingredients.get(i) + " ";
                }
                Snackbar.make(view, out, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        fab.setImageBitmap(HelperMethods.textAsBitmap("ADD", 40, Color.WHITE));
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
