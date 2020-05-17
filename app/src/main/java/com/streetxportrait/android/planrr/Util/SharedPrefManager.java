package com.streetxportrait.android.planrr.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.streetxportrait.android.planrr.Model.PhotoList;
import com.streetxportrait.android.planrr.R;

import java.lang.reflect.Type;

public class SharedPrefManager {

    private static final String TAG = "SharedPrefs";
    SharedPreferences sharedPreferences;
    Context context;

    public SharedPrefManager(Context context) {
        this.context = context;
    }

    public void saveTheme(String choice) {
        sharedPreferences = context.getSharedPreferences("key", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("theme", choice);
        editor.apply();
    }

    public String loadTheme() {
        sharedPreferences = context.getSharedPreferences("key", Context.MODE_PRIVATE);
        String theme = sharedPreferences.getString("theme", null);
        Resources resources = context.getResources();

        if (theme == null) {
            theme = resources.getString(R.string.system_default);
        }
        // change theme
        if (theme.equals(resources.getString(R.string.light_theme))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else if (theme.equals(resources.getString(R.string.dark_theme))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if (theme.equals(resources.getString(R.string.system_default))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

        return theme;
    }

    public void savePhotos(PhotoList photoList) {
        sharedPreferences = context.getSharedPreferences("key", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(photoList);
        editor.putString("Photos", json);
        Log.d(TAG, "savePhotos: saved");
        editor.apply();

    }

    public PhotoList loadPhotos() {
        sharedPreferences = context.getSharedPreferences("key", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Photos", null);
        Type type = new TypeToken<PhotoList>(){}.getType();

        PhotoList photoList = gson.fromJson(json, type);

        if (photoList == null) {
            photoList = new PhotoList();
        }

        return photoList;
    }
}
