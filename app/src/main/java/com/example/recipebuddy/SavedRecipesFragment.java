package com.example.recipebuddy;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SavedRecipesFragment extends Fragment {
    View view;
    RecyclerView recyclerView;
    SQLiteDatabase recipesDB;
    SavedRecipesDataAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_saved_recipes, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        DBHandlerRecipe recipeDBHandler = new DBHandlerRecipe(getContext());
        recipesDB = recipeDBHandler.getReadableDatabase();

        super.onViewCreated(view, savedInstanceState);

        recyclerView = getView().findViewById(R.id.recyclerViewSavedRecipes);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));

        // specify an adapter (see also next example)
        mAdapter = new SavedRecipesDataAdapter(recipesDB, new CustomItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(v.getContext(), DisplayRecipeActivity.class);
                intent.putExtra("name", ((TextView) v.findViewById(R.id.name)).getText().toString());
                startActivity(intent);
            }});
        recyclerView.setAdapter(mAdapter);
    }
}
