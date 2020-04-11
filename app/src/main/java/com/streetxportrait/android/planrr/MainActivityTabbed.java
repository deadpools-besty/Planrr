package com.streetxportrait.android.planrr;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivityTabbed extends AppCompatActivity {

    private static final String TAG = "Main-Tab";
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private GridFragment gridFragment;
    private EditFragment editFragment;
    private SharedPreferences sharedPreferences;
    private PhotoList photoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tabbed_main_activity);
        getPhotos();
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        setSupportActionBar(toolbar);

        gridFragment = new GridFragment(photoList);
        editFragment = new EditFragment();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(gridFragment, "Grid");
        viewPagerAdapter.addFragment(editFragment, "Edit");
        viewPager.setAdapter(viewPagerAdapter);
/*
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_grid_on_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_edit_black_24dp);
*/
    }

    private void getPhotos() {
        sharedPreferences = getSharedPreferences("key", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Photos", null);
        Type type = new TypeToken<PhotoList>(){}.getType();

        photoList = gson.fromJson(json, type);

        if (photoList == null) {
            photoList = new PhotoList();
        }
    }



    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitles = new ArrayList<>();



        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitles.add(title);
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
