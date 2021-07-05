package com.chrisvanry.weightlosscontest.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.chrisvanry.weightlosscontest.R;
import com.chrisvanry.weightlosscontest.ui.Login;

public class Splash extends AppCompatActivity {

    // TODO stretched splash image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();

    }

}