package com.exams.anthopoulos.book4thought.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

public class LoadingFragment extends DialogFragment {


    public LoadingFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog dialog = new ProgressDialog(getActivity());
        this.setStyle(STYLE_NO_TITLE, getTheme()); // You can use styles or inflate a view
        dialog.setMessage("Loading.."); // set your messages if not inflated from XML
        ((ProgressDialog) dialog).setIndeterminate(true);

        dialog.setCancelable(false);

        return dialog;
    }
}
