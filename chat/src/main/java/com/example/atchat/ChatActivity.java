package com.example.atchat;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Observer;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private ActivitiesCoordinator coordinator;

    private TextView textView;

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

        coordinator.printMessages();


        textView = (TextView) findViewById(R.id.textView2);

    }

    private void updateUsers(List<User> messages) {

    }

    @SuppressWarnings("unchecked")
    private void updateMessages(List<Message> messages) {
        if (messages != null) {
            for (Message message : messages) {
                textView.append(message.getText() + "\n");
            }
        }

        Log.d(TAG, "updateMessages: " + messages.toString());

    }
}
