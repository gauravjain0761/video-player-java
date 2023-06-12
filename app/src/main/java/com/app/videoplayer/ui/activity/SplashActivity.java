package com.app.videoplayer.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moveToNext();
    }

    private void moveToNext() {
        try {
            new Handler().postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 2000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}