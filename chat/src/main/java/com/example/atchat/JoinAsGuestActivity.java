package com.example.atchat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.atchat.Events.GetChatSuccessfullyEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class JoinAsGuestActivity extends AppCompatActivity {

    private static final String TAG = JoinAsGuestActivity.class.getSimpleName();

    private ActivitiesCoordinator coordinator;

    private EditText hostEditText;
    private Button joinChatButton;
    private TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_as_guest);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        coordinator = ActivitiesCoordinator.getInstance();

        hostEditText = findViewById(R.id.host);
        joinChatButton = findViewById(R.id.join_chat_button);
        errorTextView = findViewById(R.id.error_message);

        joinChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String host = hostEditText.getText().toString();
                String username = JoinAsGuestActivity.this.getString(R.string.guest_username);
                String password = "";

                if (!InputUtils.isValidHost(host)) {
                    Toast.makeText(JoinAsGuestActivity.this, getApplicationContext().getResources().getString(R.string.incorrect_host_toast_message), Toast.LENGTH_LONG).show();
                } else {
//                    showLoading();
                    coordinator.joinChat(host, username, password, true,JoinAsGuestActivity.this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String dataMessage = data.getStringExtra("ERROR_MESSAGE");
            Log.d(TAG, "onActivityResult called, data: " + dataMessage);
            errorTextView.setVisibility(View.VISIBLE);
            errorTextView.setText(dataMessage);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onGetChatSuccessfullyEvent(GetChatSuccessfullyEvent event) {
        Intent intentToStartChatActivity = new Intent(this, ChatActivity.class);
        startActivityForResult(intentToStartChatActivity, 2);
    }


}
