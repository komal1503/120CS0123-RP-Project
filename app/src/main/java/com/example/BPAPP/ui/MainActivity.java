package com.example.BPAPP.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.example.BPAPP.R;

//import com.example.ppg_update.ui.PpgActivity;
//import com.example.myapplication1.R;
//import com.example.myapplication1.ScgActivity;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find buttons by their IDs
        findViewById(R.id.scgButton).setOnClickListener(v -> {
            // Start SCG activity
            Intent intent = new Intent(MainActivity.this, ScgActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.ppgButton).setOnClickListener(v -> {
            // Start PPG activity
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                intent = new Intent(MainActivity.this, PpgActivity.class);
            }
            startActivity(intent);
        });

        findViewById(R.id.comboButton).setOnClickListener(v -> {
            // Start PPG activity
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                intent = new Intent(MainActivity.this, Combo.class);
            }
            startActivity(intent);
        });
    }
}
