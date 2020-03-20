package com.example.atchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RegisterActivity extends AppCompatActivity {

    private ActivitiesCoordinator coordinator;

    private EditText editTextHost;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editTextHost = findViewById(R.id.register_host);
        editTextUsername = findViewById(R.id.register_username);
        editTextPassword = findViewById(R.id.register_password);
        buttonRegister = findViewById(R.id.register_button);

        coordinator = ActivitiesCoordinator.getInstance();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = editTextHost.getText().toString();
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();
                String passwordConfirmation = editTextPasswordConfirmation.getText().toString();

                if (!InputUtils.isValidHost(host)) {
                    Toast.makeText(
                            RegisterActivity.this,
                            getApplicationContext().getResources().getString(R.string.incorrect_host_toast_message),
                            Toast.LENGTH_LONG)
                            .show();
                } else if (!InputUtils.isValidUserName(username)) {
                    Toast.makeText(
                            RegisterActivity.this,
                            getApplicationContext().getResources().getString(R.string.incorrect_username_toast_message),
                            Toast.LENGTH_LONG)
                            .show();
                } else if (!InputUtils.isValidPassword(password)) {
                    Toast.makeText(
                            RegisterActivity.this,
                            getApplicationContext().getResources().getString(R.string.incorrect_password_toast_message),
                            Toast.LENGTH_LONG)
                            .show();
                } else if (!password.equals(passwordConfirmation)) {
                    errorMessageTextView.setVisibility(View.VISIBLE);
                    errorMessageTextView.setText(R.string.different_passwords_message);
                } else {
                    coordinator.registerUser(host, username, password, RegisterActivity.this);
                    showLoading();
                }
            }
        });
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

    private void showLoading() {
        editTextHost.setVisibility(View.INVISIBLE);
        editTextUsername.setVisibility(View.INVISIBLE);
        editTextPassword.setVisibility(View.INVISIBLE);
        editTextPasswordConfirmation.setVisibility(View.INVISIBLE);
        errorMessageTextView.setVisibility(View.INVISIBLE);
        buttonRegister.setVisibility(View.INVISIBLE);
        signInTextView.setVisibility(View.INVISIBLE);
        signInTextView2.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        showErrorMessage(event.message);
    }

    private void showErrorMessage(String message) {
        progressBar.setVisibility(View.INVISIBLE);
        editTextHost.setVisibility(View.VISIBLE);
        editTextUsername.setVisibility(View.VISIBLE);
        editTextPassword.setVisibility(View.VISIBLE);
        editTextPasswordConfirmation.setVisibility(View.VISIBLE);
        errorMessageTextView.setVisibility(View.VISIBLE);
        buttonRegister.setVisibility(View.VISIBLE);
        signInTextView.setVisibility(View.VISIBLE);
        signInTextView2.setVisibility(View.VISIBLE);
        errorMessageTextView.setVisibility(View.VISIBLE);
        errorMessageTextView.setText(message);
    }
}
