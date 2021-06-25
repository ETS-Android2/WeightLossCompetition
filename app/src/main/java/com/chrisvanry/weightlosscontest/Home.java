package com.chrisvanry.weightlosscontest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Home extends AppCompatActivity {

    // TODO hamburger menu?
    // TODO back button should return to HOME
    // TODO "For logout, you just have to call finish() and an intent to call login"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}