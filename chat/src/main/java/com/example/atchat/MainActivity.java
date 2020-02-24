package com.example.atchat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    private EditText editTextHost;
    private EditText editTextUsername;
    private Button buttonJoinChat;
    private ActivitiesCoordinator coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextHost = findViewById(R.id.host);
        editTextUsername = findViewById(R.id.username);
        buttonJoinChat = findViewById(R.id.join_button);

        editTextUsername.addTextChangedListener(loginTextWatcher);
        editTextHost.addTextChangedListener(loginTextWatcher);

        coordinator = ActivitiesCoordinator.getInstance();

        buttonJoinChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = editTextHost.getText().toString();
                String username = editTextUsername.getText().toString();
                coordinator.joinChat(host, username, MainActivity.this);
            }
        });

    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String hostString = editTextHost.getText().toString().trim();
            String usernameString = editTextUsername.getText().toString().trim();
            buttonJoinChat.setEnabled(!hostString.isEmpty() && !usernameString.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


}
