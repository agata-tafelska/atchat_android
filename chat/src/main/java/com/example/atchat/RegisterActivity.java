package com.example.atchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atchat.Events.ErrorEvent;
import com.example.atchat.Events.RegisterSuccessfullyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class RegisterActivity extends AppCompatActivity {

    private ActivitiesCoordinator coordinator;

    private EditText editTextHost;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextPasswordConfirmation;
    private Button buttonRegister;
    private TextView signInTextView;
    private TextView signInTextView2;
    private TextView errorMessageTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        editTextHost = findViewById(R.id.register_host);
        editTextUsername = findViewById(R.id.register_username);
        editTextPassword = findViewById(R.id.register_password);
        editTextPasswordConfirmation = findViewById(R.id.register_password_confirmation);
        buttonRegister = findViewById(R.id.register_button);
        signInTextView = findViewById(R.id.sign_in_link);
        signInTextView2 = findViewById(R.id.sign_in_text);
        errorMessageTextView = findViewById(R.id.error_message);
        progressBar = findViewById(R.id.register_progress_bar);

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

        signInTextView.setMovementMethod(LinkMovementMethod.getInstance());

        signInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
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
        showErrorMessage(event.errorType.messageResourceId);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRegisterSuccessfullyEvent(RegisterSuccessfullyEvent event) {
        Toast.makeText(this, R.string.account_created_toast_message, Toast.LENGTH_LONG).show();
        Intent intentToStartLoginActivity = new Intent(this, LoginActivity.class);
        startActivity(intentToStartLoginActivity);
    }

    private void showErrorMessage(int stringResourceId) {
        String errorMessage = getString(stringResourceId);
        errorMessageTextView.setVisibility(View.VISIBLE);
        errorMessageTextView.setText(errorMessage);

        progressBar.setVisibility(View.INVISIBLE);
        editTextHost.setVisibility(View.VISIBLE);
        editTextUsername.setVisibility(View.VISIBLE);
        editTextPassword.setVisibility(View.VISIBLE);
        editTextPasswordConfirmation.setVisibility(View.VISIBLE);
        buttonRegister.setVisibility(View.VISIBLE);
        signInTextView.setVisibility(View.VISIBLE);
        signInTextView2.setVisibility(View.VISIBLE);
    }
}
