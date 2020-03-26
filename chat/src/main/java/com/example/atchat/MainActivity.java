package com.example.atchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.button.MaterialButtonToggleGroup;

public class MainActivity extends AppCompatActivity {

    private Button signInButton;
    private Button joinAsGuestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_button);
        joinAsGuestButton = findViewById(R.id.join_as_guest_button);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToStartLoginActivity =
                        new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentToStartLoginActivity);
            }
        });

        joinAsGuestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToStartJoinAsGuestActivity =
                        new Intent(MainActivity.this, JoinAsGuestActivity.class);
                startActivity(intentToStartJoinAsGuestActivity);
            }
        });
    }
}
