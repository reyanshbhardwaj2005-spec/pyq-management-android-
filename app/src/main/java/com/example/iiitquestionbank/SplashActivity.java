package com.example.iiitquestionbank;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import com.example.iiitquestionbank.activity.MainActivity;
import com.example.iiitquestionbank.fragment.HomeFragment;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            startActivity(new Intent(SplashActivity.this, MainActivity.class));

            finish();

        }, 2500);

    }
}