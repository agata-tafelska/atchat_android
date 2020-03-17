package com.example.atchat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observer;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private ActivitiesCoordinator coordinator;
    private RecyclerView recyclerView;
    private MessagesAdapter adapter;

    private Button showUsersButton;
    private Button sendMessageButton;
    private EditText messageEditText;

    private UsersBottomSheetDialog bottomSheet;

    private String loggedUserName;

    @SuppressWarnings("unchecked")
    private Observer usersObserver =
            (observable, argument) -> {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "updating users in new thread");
                        List users = ((List) argument);
                        updateUsers(users);
                    }
                }).start();
            };

    @SuppressWarnings("unchecked")
    private Observer messagesObserver =
            (observable, argument) -> {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "updating messages in new thread");
                        List messages = ((List) argument);
                        updateMessages(messages);
                    }
                }).start();
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bottomSheet = new UsersBottomSheetDialog();

        coordinator = ActivitiesCoordinator.getInstance();
        coordinator.observeChat(messagesObserver, usersObserver);

        showUsersButton = findViewById(R.id.show_users_button);
        sendMessageButton = findViewById(R.id.send_message_button);
        messageEditText = findViewById(R.id.message_edit_text);

        loggedUserName = getIntent().getExtras().get("USER_NAME").toString();
        Log.d(TAG, "Logged user name: " + loggedUserName);

        showUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.show(getSupportFragmentManager(), null);
                Log.d(TAG, "showUsersButton onClicked called");
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                coordinator.sendMessage(message);
                messageEditText.setText("");
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.chat_recyclerview);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessagesAdapter(loggedUserName);
        recyclerView.setAdapter(adapter);
    }

    private void updateUsers(List<User> users) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bottomSheet.setUsers(users);
            }
        });

    }

    private void updateMessages(List<Message> messages) {
        List<MessageText> messagesListToDisplay = new ArrayList<>();
        if (messages != null) {
            for (Message message : messages) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = format.format(new Date(message.getTimestamp()));
                String userString = "~" + message.getUser().getName();
                String messageString = message.getText();
                MessageText messageText = new MessageText(dateString, userString, messageString);
                messagesListToDisplay.add(messageText);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setMessages(messagesListToDisplay);
                    int itemCount = adapter.getItemCount();
                    recyclerView.scrollToPosition(itemCount - 1);
                }
            });

        }
    }
}
