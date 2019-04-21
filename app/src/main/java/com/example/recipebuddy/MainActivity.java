package com.example.recipebuddy;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Script;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.recipebuddy.DBConstants.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    /**
     * The tabs that will display the currently displayed fragment title
     */
    private TabLayout tabLayout;
    private SQLiteDatabase kitchenDB;
    Toolbar toolbar;
    int MODE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MODE = getIntent().getIntExtra("mode", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DBHandlerRecipe recipeDB;
        recipeDB = new DBHandlerRecipe(this);
        DBHandlerIngredient ingredientsDB;
        ingredientsDB = new DBHandlerIngredient(this);

        KitchenDBHandler dbHelper = new KitchenDBHandler(this);
        kitchenDB = dbHelper.getWritableDatabase();

        try {
            recipeDB.createDataBase();
            ingredientsDB.createDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Pass in mode to ingredients fragment
        Bundle bundle = new Bundle();
        bundle.putInt("mode", MODE);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        IngredientsFragment ingredientsFrag = new IngredientsFragment();
        ingredientsFrag.setArguments(bundle);
        mSectionsPagerAdapter.addFragment(ingredientsFrag, "My Ingredients");
        mSectionsPagerAdapter.addFragment(new SavedRecipesFragment(), "Saved Recipes");

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the TabLayout with the sections adapter.
        tabLayout = (TabLayout) findViewById(R.id.tabLayoutMain);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // Get current mode for the activity
        // 0 -> view mode, 1 -> delete mode
        if (MODE == 0) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
        } else if (MODE == 1) {
            getMenuInflater().inflate(R.menu.menu_main_delete, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // Get current mode for the activity
            // 0 -> view mode, 1 -> delete mode
            if (MODE == 0) {
                Intent intent = new Intent(this, AddIngredientsActivity.class);
                startActivity(intent);
            } else if (MODE == 1) {
                SectionsPagerAdapter mSectionsPagerAdapter = ((SectionsPagerAdapter)mViewPager.getAdapter());
                HashMap<String, Boolean> selected = ((IngredientsFragment)mSectionsPagerAdapter.getItem(0)).getSelected();

                String test = "";
                for (HashMap.Entry<String, Boolean> i : selected.entrySet()) {
                    String key = i.getKey();
                    Boolean value = i.getValue();
                    test = test + key + ": " + String.valueOf(value) + " ";
                    if (value) {
                        kitchenDB.delete(KitchenColumns.TABLE_NAME, "name = ?", new String[] {key});
                    }
                }
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("mode", 0);
                startActivity(intent);
                finish();
                //TODO remove animation
            }
        }

        return super.onOptionsItemSelected(item);
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
