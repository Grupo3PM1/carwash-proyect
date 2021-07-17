package com.aplicacion.elcatrachocarwash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, com.aplicacion.elcatrachocarwash.MainActivity.class);
                startActivity(intent);
            }
        },3000);

    }
}