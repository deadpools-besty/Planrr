package com.streetxportrait.android.planrr.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.streetxportrait.android.planrr.R;
import com.streetxportrait.android.planrr.Util.SharedPrefManager;

import java.util.ArrayList;

public class ThemeSelectionDialog extends DialogFragment {

    private OnFragmentInteractionListener listener;
    private RadioGroup radioGroup;
    private MaterialRadioButton lightRadioButton, darkRadioButton, sysDefRadioButton;
    private static final String TAG = "Theme-Selection";
    private String choice;
    private ArrayList<MaterialRadioButton> radioButtons = new ArrayList<>();
    private SharedPrefManager sharedPrefManager;

    public static ThemeSelectionDialog newInstance(String choice) {
        Bundle args = new Bundle();
        args.putSerializable("theme", choice);
        ThemeSelectionDialog fragment = new ThemeSelectionDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public interface OnFragmentInteractionListener {
        void onSetPressed(String finalChoice);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof  OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        }
        else {
            throw new RuntimeException(context.toString() + "must implement OnFragmentInteractionListener");
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.theme_dialog, null);

        // start shared preferences editor
        sharedPrefManager = new SharedPrefManager(getContext());

        // load current theme
        choice = (String) getArguments().getSerializable("theme");
        Log.d(TAG, "onCreateDialog: " + choice);

        // Bind views
        radioGroup = view.findViewById(R.id.theme_radio_group);
        lightRadioButton = view.findViewById(R.id.light_theme_radio_button);
        darkRadioButton = view.findViewById(R.id.dark_theme_radio_button);
        sysDefRadioButton = view.findViewById(R.id.system_default_radio_button);

        radioButtons.add(lightRadioButton);
        radioButtons.add(darkRadioButton);
        radioButtons.add(sysDefRadioButton);

        for (MaterialRadioButton button : radioButtons) {
            if (choice.equals(button.getText().toString())) {
                button.setChecked(true);
            }
        }


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            MaterialRadioButton selected = view.findViewById(checkedId);
            choice = selected.getText().toString();
            sharedPrefManager.saveTheme(choice);
            changeTheme();
            dismiss();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setTitle("Choose a Theme")
                .create();
    }

    private void changeTheme() {

        Resources resources = getResources();
        // change theme
        if (choice.equals(resources.getString(R.string.light_theme))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else if (choice.equals(resources.getString(R.string.dark_theme))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else if (choice.equals(resources.getString(R.string.system_default))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }

    }
}
