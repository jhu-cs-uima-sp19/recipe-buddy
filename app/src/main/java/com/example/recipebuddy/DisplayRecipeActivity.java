package com.example.recipebuddy;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DisplayRecipeActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private DisplayRecipeActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * The tabs that will display the currently displayed fragment title
     */
    private TabLayout tabLayout;
    private String name;

    private static ImageButton toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_recipe);

        Intent launcher = getIntent();
        name = launcher.getStringExtra("name");
        this.setTitle(name);

        DBHandlerRecipe recipeDBHandler = new DBHandlerRecipe(getApplicationContext());
        SQLiteDatabase recipeDB = recipeDBHandler.getWritableDatabase();

        String[] args = {name};

        Cursor c = recipeDB.query(
                "recipes",
                null,
                "name=?",
                args,
                null,
                null,
                null);

        c.moveToFirst();
        int favorited = c.getInt(c.getColumnIndex("favorited"));

        toggle = findViewById(R.id.toggleSavedRecipes);

        if (favorited == 1) {
            //recipe is favorited
            toggle.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_remove_24px));
            toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View view = v;
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Remove from Saved Recipes");
                    builder.setMessage("Do you want to remove this recipe from your saved recipes?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBHandlerRecipe recipeDBHandler = new DBHandlerRecipe(view.getContext());
                            SQLiteDatabase recipeDB = recipeDBHandler.getWritableDatabase();

                            ContentValues cv = new ContentValues();
                            cv.put("favorited", 0);

                            String[] args = {name};

                            recipeDB.update(
                                    "recipes",
                                    cv,
                                    "name=?",
                                    args);
                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(0,0);
                            Toast.makeText(getApplicationContext(), "Removed from saved recipes", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Recipe not removed", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.show();

                }
            });
        } else {
            //recipe is not favorited
            toggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final View view = v;
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Add to Saved Recipes");
                    builder.setMessage("Do you want to save this recipe to your saved recipes?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DBHandlerRecipe recipeDBHandler = new DBHandlerRecipe(view.getContext());
                            SQLiteDatabase recipeDB = recipeDBHandler.getWritableDatabase();

                            ContentValues cv = new ContentValues();
                            cv.put("favorited", 1);

                            String[] args = {name};

                            recipeDB.update(
                                    "recipes",
                                    cv,
                                    "name=?",
                                    args);
                            finish();
                            startActivity(getIntent());
                            overridePendingTransition(0,0);
                            Toast.makeText(getApplicationContext(), "Added to saved recipes", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "Recipe not saved", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.show();

                }
            });
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new DisplayRecipeActivity.SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(RecipeIngredientsFragment.newInstance(name), "Ingredients");
        mSectionsPagerAdapter.addFragment(RecipeDirectionsFragment.newInstance(name), "Directions");

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the TabLayout with the sections adapter.
        tabLayout = findViewById(R.id.tabLayoutMain);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragments = new ArrayList<>();
        private final List<String> fragmentTitles = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }
    }
}
