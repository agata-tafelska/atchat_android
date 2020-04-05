package com.example.atchat;

import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.atchat.Events.ConnectionLostEvent;
import com.example.atchat.Events.LogOutEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

        adapter = new MessagesAdapter(coordinator.getCurrentUser());
        recyclerView.setAdapter(adapter);
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

        if (id == R.id.logout) {
            coordinator.logout();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        Log.d(TAG, "Back pressed.");
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
        adapter.setMessages(createMessagesToDisplay(messages));
        int itemCount = adapter.getItemCount();
        recyclerView.scrollToPosition(itemCount - 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionLostEvent(ConnectionLostEvent event) {
        Log.d(TAG, "Connection lost, onConnectionLostEvent called.");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("ERROR_MESSAGE", event.message);
        setResult(2, returnIntent);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogOutEvent(LogOutEvent event) {
        Log.d(TAG, "Log out");
        finish();
        Toast.makeText(this, R.string.log_out_toast_message, Toast.LENGTH_LONG).show();
    }

    private List<MessageText> createMessagesToDisplay(List<Message> messages) {
        List<MessageText> messagesListToDisplay = new ArrayList<>();
        if (messages != null) {
            for (Message message : messages) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = format.format(new Date(message.getTimestamp()));
                String username;
                if (message.getUser().getIsGuest()) username = message.getUser().getName();
                else username = "~" + message.getUser().getName();
                String userId = message.getUser().getId();
                String messageString = message.getText();
                MessageText messageText = new MessageText(dateString, username, messageString, userId);
                messagesListToDisplay.add(messageText);
            }
        }
        return messagesListToDisplay;
    }
}