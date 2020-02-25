package com.example.atchat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    private Button sendMessageButton;
    private EditText messageEditText;


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

        coordinator = ActivitiesCoordinator.getInstance();
        coordinator.observeChat(messagesObserver);

        sendMessageButton = findViewById(R.id.send_message_button);
        messageEditText = findViewById(R.id.message_edit_text);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                coordinator.sendMessage(message);
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.chat_recyclerview);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // COMPLETED (41) Set the layoutManager on mRecyclerView
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MessagesAdapter();
        recyclerView.setAdapter(adapter);
    }

    private void updateUsers(List<User> messages) {

    }

    private void updateMessages(List<Message> messages) {
        List<String> messagesListToDisplay = new ArrayList<>();
        if (messages != null) {
            for (Message message : messages) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = format.format(new Date(message.getTimestamp()));

                String messageString =
                        "[" + dateString + "] " + message.getUser().getName() + ": " + message.getText();
                messagesListToDisplay.add(messageString);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setMessages(messagesListToDisplay);
                }
            });

        }
    }
}