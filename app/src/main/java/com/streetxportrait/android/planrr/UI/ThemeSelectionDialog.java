package com.streetxportrait.android.planrr.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.radiobutton.MaterialRadioButton;
import com.streetxportrait.android.planrr.R;

public class ThemeSelectionDialog extends DialogFragment {

    private OnFragmentInteractionListener listener;
    private RadioGroup radioGroup;
    private MaterialRadioButton lightRadioButton, darkRadioButton, sysDefRadioButton;
    private String choice;

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

        radioGroup = view.findViewById(R.id.theme_radio_group);
        lightRadioButton = view.findViewById(R.id.light_theme_radio_button);
        darkRadioButton = view.findViewById(R.id.dark_theme_radio_button);
        sysDefRadioButton = view.findViewById(R.id.system_default_radio_button);

        sysDefRadioButton.setChecked(true);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            MaterialRadioButton selected = view.findViewById(checkedId);
            choice = selected.getText().toString();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view)
                .setTitle("Choose a Theme")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Set", (dialog, which) -> {
                    listener.onSetPressed(choice);
                }).create();
    }
}
