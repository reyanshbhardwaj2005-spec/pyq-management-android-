package com.example.iiitquestionbank.utils;

import android.view.View;
public class ProgressUtil {
    public static void show(View view){
        view.setVisibility(View.VISIBLE);
    }
    public static void hide(View view){
        view.setVisibility(View.GONE);
    }
}
