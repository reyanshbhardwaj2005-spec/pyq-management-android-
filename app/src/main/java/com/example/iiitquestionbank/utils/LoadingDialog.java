package com.example.iiitquestionbank.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import com.example.iiitquestionbank.R;
public class LoadingDialog {
    private AlertDialog dialog;
    public LoadingDialog(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        builder.setView(inflater.inflate(R.layout.dialog_loading, null));
        builder.setCancelable(false);
        dialog = builder.create();
    }
    public void show() {
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }
    public void dismiss() {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
