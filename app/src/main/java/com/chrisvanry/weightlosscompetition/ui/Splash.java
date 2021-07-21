package com.chrisvanry.weightlosscompetition.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.chrisvanry.weightlosscompetition.R;
import com.chrisvanry.weightlosscompetition.activities.Login;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();

    }

}