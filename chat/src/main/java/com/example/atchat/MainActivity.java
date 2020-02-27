package com.example.atchat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    private EditText editTextHost;
    private EditText editTextUsername;
    private Button buttonJoinChat;
    private ProgressBar progressBar;
    private TextView textViewErrorMessage;

    private ActivitiesCoordinator coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextHost = findViewById(R.id.host);
        editTextUsername = findViewById(R.id.username);
        buttonJoinChat = findViewById(R.id.join_button);
        progressBar = findViewById(R.id.logging_progress_bar);
        textViewErrorMessage = findViewById(R.id.error_message);

        editTextUsername.addTextChangedListener(loginTextWatcher);
        editTextHost.addTextChangedListener(loginTextWatcher);

        coordinator = ActivitiesCoordinator.getInstance();

        buttonJoinChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = editTextHost.getText().toString();
                String username = editTextUsername.getText().toString();

                if (!InputUtils.isValidHost(host)) {
                    Toast.makeText(MainActivity.this, getApplicationContext().getResources().getString(R.string.incorrect_host_toast_message), Toast.LENGTH_LONG).show();
                }
                else if (!InputUtils.isValidUserName(username)) {
                    Toast.makeText(MainActivity.this, getApplicationContext().getResources().getString(R.string.incorrect_username_toast_message), Toast.LENGTH_LONG).show();
                } else {
                    showLoading();
                    coordinator.joinChat(host, username, MainActivity.this);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        editTextHost.setVisibility(View.VISIBLE);
        editTextUsername.setVisibility(View.VISIBLE);
        buttonJoinChat.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        textViewErrorMessage.setVisibility(View.INVISIBLE);
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

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        editTextHost.setVisibility(View.INVISIBLE);
        editTextUsername.setVisibility(View.INVISIBLE);
        buttonJoinChat.setVisibility(View.INVISIBLE);
    }

}
