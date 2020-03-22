package com.example.atchat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    private ActivitiesCoordinator coordinator;
    private RecyclerView recyclerView;
    private MessagesAdapter adapter;

    private Button sendMessageButton;
    private EditText messageEditText;

    private UsersBottomSheetDialog bottomSheet;

    private String loggedUserName;

    private Observer<List<User>> usersLiveDataObserver = users -> {
        Log.d(TAG, "updating users");
        updateUsers(users);
    };

    private Observer<List<Message>> messagesLiveDataObserver = messages -> {
        Log.d(TAG, "updating messages");
        updateMessages(messages);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bottomSheet = new UsersBottomSheetDialog();

        coordinator = ActivitiesCoordinator.getInstance();
        coordinator.observeChat(this, messagesLiveDataObserver, usersLiveDataObserver);

        sendMessageButton = findViewById(R.id.send_message_button);
        messageEditText = findViewById(R.id.message_edit_text);
        messageEditText.addTextChangedListener(messageTextWatcher);

        loggedUserName = getIntent().getExtras().get("USER_NAME").toString();
        Log.d(TAG, "Logged user name: " + loggedUserName);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.chat_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_show_users) {
            bottomSheet.show(getSupportFragmentManager(), null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private TextWatcher messageTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String messageString = messageEditText.getText().toString().trim();
            sendMessageButton.setEnabled(!messageString.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void updateUsers(List<User> users) {
        bottomSheet.setUsers(users);
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
            adapter.setMessages(messagesListToDisplay);
            int itemCount = adapter.getItemCount();
            recyclerView.scrollToPosition(itemCount - 1);
        }
    }
}
