package com.example.atchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.button.MaterialButtonToggleGroup;

public class MainActivity extends AppCompatActivity {

    private Button signInButton;
    private Button joinAsGuestButton;
    private TextView textViewCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signInButton = findViewById(R.id.sign_in_button);
        joinAsGuestButton = findViewById(R.id.join_as_guest_button);
        textViewCreateAccount = findViewById(R.id.create_account_link);

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

        textViewCreateAccount.setMovementMethod(LinkMovementMethod.getInstance());
        textViewCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
