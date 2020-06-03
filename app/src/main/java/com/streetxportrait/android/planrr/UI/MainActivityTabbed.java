package com.streetxportrait.android.planrr.UI;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.streetxportrait.android.planrr.R;
import com.streetxportrait.android.planrr.Util.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivityTabbed extends AppCompatActivity implements ThemeSelectionDialog.OnFragmentInteractionListener{

    private static final String TAG = "Main-Tab";
    private static final int WRITE_STORAGE_PERMISSION_RC = 21;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private GridFragment gridFragment;
    private EditFragment editFragment;
    private String currentTheme;
    private SharedPrefManager sharedPrefManager;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_theme_options_menu:
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                DialogFragment fragment = ThemeSelectionDialog.newInstance(currentTheme);
                fragmentTransaction.add(0, fragment);
                fragmentTransaction.commit();

                /*ThemeSelectionDialog themeSelectionDialog = new ThemeSelectionDialog();
                themeSelectionDialog.show(getSupportFragmentManager(), "theme");
        */}

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        checkPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_STORAGE_PERMISSION_RC);
        sharedPrefManager = new SharedPrefManager(this);
        currentTheme = sharedPrefManager.loadTheme();
        Log.d(TAG, "onCreate: " + currentTheme);

        setContentView(R.layout.tabbed_main_activity);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        gridFragment = new GridFragment();
        editFragment = new EditFragment();

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(gridFragment, "Grid");
        viewPagerAdapter.addFragment(editFragment, "Edit");
        viewPager.setAdapter(viewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
               invalidateOptionsMenu();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_grid_on_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_edit_black_24dp);

    }


    private void checkPermissions(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(MainActivityTabbed.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(MainActivityTabbed.this,
                    new String[] { permission },
                    requestCode);
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_STORAGE_PERMISSION_RC) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivityTabbed.this,
                        "Writing Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MainActivityTabbed.this,
                        "Enable storage permission to add and save photos",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

    @Override
    public void onSetPressed(String finalChoice) {
        Resources resources= getResources();
        if (finalChoice.equals(resources.getString(R.string.light_theme))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        else if (finalChoice.equals(resources.getString(R.string.dark_theme))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
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
