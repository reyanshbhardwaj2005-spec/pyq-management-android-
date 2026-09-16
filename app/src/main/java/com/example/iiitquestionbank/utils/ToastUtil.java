package com.example.iiitquestionbank.utils;

import android.content.Context;
import android.widget.Toast;
public class ToastUtil {
    public static void show(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
