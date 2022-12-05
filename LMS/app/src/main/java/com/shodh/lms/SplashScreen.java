package com.shodh.lms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    private SharedPreferences user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = getSharedPreferences("USER",MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(user.contains("enroll")){
            startActivity(new Intent(this, DashboardActivity.class));
        }else{
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
