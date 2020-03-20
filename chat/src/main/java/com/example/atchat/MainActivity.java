package com.example.atchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MainActivity extends AppCompatActivity {

    private EditText editTextHost;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonJoinChat;
    private ProgressBar progressBar;
    private TextView textViewErrorMessage;
    private TextView textViewCreateAccount;
    private TextView textViewCreateAccount1;

    private ActivitiesCoordinator coordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        editTextHost = findViewById(R.id.host);
        editTextUsername = findViewById(R.id.username);
        editTextPassword = findViewById(R.id.password);
        buttonJoinChat = findViewById(R.id.sign_in_button);
        progressBar = findViewById(R.id.logging_progress_bar);
        textViewErrorMessage = findViewById(R.id.error_message);
        textViewCreateAccount = findViewById(R.id.create_account_text);
        textViewCreateAccount1 = findViewById(R.id.create_account_link);

        editTextUsername.addTextChangedListener(loginTextWatcher);
        editTextHost.addTextChangedListener(loginTextWatcher);

        coordinator = ActivitiesCoordinator.getInstance();

        buttonJoinChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = editTextHost.getText().toString();
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (!InputUtils.isValidHost(host)) {
                    Toast.makeText(MainActivity.this, getApplicationContext().getResources().getString(R.string.incorrect_host_toast_message), Toast.LENGTH_LONG).show();
                }
                else if (!InputUtils.isValidUserName(username)) {
                    Toast.makeText(MainActivity.this, getApplicationContext().getResources().getString(R.string.incorrect_username_toast_message), Toast.LENGTH_LONG).show();
                } else {
                    showLoading();
                    coordinator.joinChat(host, username, password, MainActivity.this);
                }
            }
        });

        textViewCreateAccount1.setMovementMethod(LinkMovementMethod.getInstance());
        textViewCreateAccount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        editTextHost.setVisibility(View.VISIBLE);
        editTextUsername.setVisibility(View.VISIBLE);
        editTextPassword.setVisibility(View.VISIBLE);
        buttonJoinChat.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        textViewErrorMessage.setVisibility(View.INVISIBLE);
        textViewCreateAccount.setVisibility(View.VISIBLE);
        textViewCreateAccount1.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
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
        editTextPassword.setVisibility(View.INVISIBLE);
        buttonJoinChat.setVisibility(View.INVISIBLE);
        textViewCreateAccount.setVisibility(View.INVISIBLE);
        textViewCreateAccount1.setVisibility(View.INVISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        showErrorMessage(event.message);
    }

    private void showErrorMessage(String message) {
        progressBar.setVisibility(View.INVISIBLE);
        textViewErrorMessage.setVisibility(View.VISIBLE);
        textViewErrorMessage.setText(message);
        editTextHost.setVisibility(View.VISIBLE);
        editTextUsername.setVisibility(View.VISIBLE);
        buttonJoinChat.setVisibility(View.VISIBLE);
    }

}
