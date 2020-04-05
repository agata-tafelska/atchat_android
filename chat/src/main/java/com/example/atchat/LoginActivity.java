package com.example.atchat;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.atchat.Events.ErrorEvent;
import com.example.atchat.Events.GetChatSuccessfullyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = LoginActivity.class.getSimpleName();

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

        coordinator = ActivitiesCoordinator.getInstance();

        buttonJoinChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = editTextHost.getText().toString();
                String username = editTextUsername.getText().toString();
                String password = editTextPassword.getText().toString();

                if (!InputUtils.isValidHost(host)) {
                    Toast.makeText(LoginActivity.this, getApplicationContext().getResources().getString(R.string.incorrect_host_toast_message), Toast.LENGTH_LONG).show();
                }
                else if (!InputUtils.isValidUserName(username)) {
                    Toast.makeText(LoginActivity.this, getApplicationContext().getResources().getString(R.string.incorrect_username_toast_message), Toast.LENGTH_LONG).show();
                } else {
                    showLoading();
                    coordinator.joinChat(host, username, password, false, LoginActivity.this);
                }
            }
        });

        textViewCreateAccount1.setMovementMethod(LinkMovementMethod.getInstance());
        textViewCreateAccount1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
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

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        editTextHost.setVisibility(View.INVISIBLE);
        editTextUsername.setVisibility(View.INVISIBLE);
        editTextPassword.setVisibility(View.INVISIBLE);
        buttonJoinChat.setVisibility(View.INVISIBLE);
        textViewCreateAccount.setVisibility(View.INVISIBLE);
        textViewCreateAccount1.setVisibility(View.INVISIBLE);
        textViewErrorMessage.setVisibility(View.INVISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetChatSuccessfullyEvent(GetChatSuccessfullyEvent event) {
        Intent intentToStartChatActivity = new Intent(this, ChatActivity.class);
        startActivityForResult(intentToStartChatActivity, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String dataMessage = data.getStringExtra("ERROR_MESSAGE");
            Log.d(TAG, "onActivityResult called, data: " + dataMessage);
            textViewErrorMessage.setVisibility(View.VISIBLE);
            textViewErrorMessage.setText(dataMessage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent event) {
        showErrorMessage(event.errorType.messageResourceId);
    }

    private void showErrorMessage(int stringResourceId) {
        String errorMessage = getString(stringResourceId);
        textViewErrorMessage.setVisibility(View.VISIBLE);
        textViewErrorMessage.setText(errorMessage);

        progressBar.setVisibility(View.INVISIBLE);
        editTextHost.setVisibility(View.VISIBLE);
        editTextUsername.setVisibility(View.VISIBLE);
        editTextPassword.setVisibility(View.VISIBLE);
        buttonJoinChat.setVisibility(View.VISIBLE);
    }

}
