package com.example.atchat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class    ActivitiesCoordinator {

    private static final String TAG = ActivitiesCoordinator.class.getSimpleName();

    private static ActivitiesCoordinator instance = new ActivitiesCoordinator();
    private ActivitiesCoordinator() {
    }
    public static ActivitiesCoordinator getInstance() {
        return instance;
    }

    private ChatService chatService = null;

    private User currentUser = null;

    private MutableLiveData<List<Message>> messagesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<User>> usersLiveData = new MutableLiveData<>();

    private StreamObserver<Message> messageStreamObserver = new DefaultStreamObserver<Message>() {
        @Override
        public void onNext(Message value) {
            Log.d(TAG, "Received new message: " + value.getText());
            List<Message> currentMessages = messagesLiveData.getValue();
            if (currentMessages == null) {
                Log.e(TAG, "Current messages is null. Aborting updates");
                return;
            }
            currentMessages.add(value);
            messagesLiveData.postValue(currentMessages);
        }
    };
    private StreamObserver<CurrentUsers> usersStreamObserver = new DefaultStreamObserver<CurrentUsers>() {
        @Override
        public void onNext(CurrentUsers value) {
            /*
             Set connection lost countdown to 5 seconds
             Client expects next update in no more that this time
             In case of timeout, lost connection error will be shown
             */
//            connectionLostDelayedTask.delayExecution(5);
            Log.d(TAG, "Received current users update, users count: " + value.getUsersList().size());
            usersLiveData.postValue(value.getUsersList());
        }
    };

    public void registerUser(String host, String username, String password, final Context context) {
        chatService = new ChatService(host, context);

        User user = User.newBuilder().setName(username).setPassword(password).build();
        chatService.register(user, new StreamObserver<User>() {
            @Override
            public void onNext(User value) {
                Intent intentToStartLoginActivity = new Intent(context, LoginActivity.class);
                context.startActivity(intentToStartLoginActivity);
            }

            @Override
            public void onError(Throwable throwable) {
                if (throwable instanceof StatusRuntimeException) {
                    StatusRuntimeException exception = (StatusRuntimeException) throwable;
                    switch (exception.getStatus().getCode()) {
                        case ALREADY_EXISTS:
                            EventBus.getDefault().post(new ErrorEvent(context.getString(R.string.username_exists_message)));
                            return;
                        case UNAUTHENTICATED:
                            EventBus.getDefault().post(new ErrorEvent(context.getString(R.string.incorrect_credentials_message)));
                    }
                }
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void joinChat(String host, String username, String password, final Context context) {

        chatService = new ChatService(host, context);

        currentUser = User.newBuilder().setName(username).setPassword(password).build();
        chatService.getChat(currentUser, new StreamObserver<Chat>() {
            @Override
            public void onNext(Chat value) {
                messagesLiveData.postValue(new ArrayList<>(value.getMessagesList()));
                Intent intentToStartChatActivity = new Intent(context, ChatActivity.class);
                intentToStartChatActivity.putExtra("USER_NAME", currentUser.getName());
                context.startActivity(intentToStartChatActivity);

                chatService.observeMessages(currentUser, messageStreamObserver);
                chatService.observeUsers(currentUser, usersStreamObserver);
            }

            @Override
            public void onError(Throwable throwable) {

                if (throwable instanceof StatusRuntimeException) {
                    StatusRuntimeException exception = (StatusRuntimeException) throwable;
                    switch (exception.getStatus().getCode()) {
                        case UNAVAILABLE:
                            EventBus.getDefault().post(new ErrorEvent("Unable to connect to provided host. Make sure entered host is correct."));
                            Log.d(TAG, "Timeout");
                            return;
                        case UNAUTHENTICATED:
                            EventBus.getDefault().post(new ErrorEvent(context.getString(R.string.incorrect_credentials_message)));
                            return;
                    }
                }
                EventBus.getDefault().post(new ErrorEvent(context.getString(R.string.unknown_error_message)));
            }

            @Override
            public void onCompleted() {

            }
        });
    }

    public void sendMessage(String text) {
        Message message =
                Message.newBuilder()
                        .setUser(currentUser)
                        .setText(text)
                        .setTimestamp(System.currentTimeMillis())
                        .build();

        chatService.sendMessage(message, new StreamObserver<Message>() {
            @Override
            public void onNext(Message value) {
                // Do nothing
            }

            @Override
            public void onError(Throwable throwable) {
               Log.d(TAG, "Unable to send message. Reason: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                // Do nothing
            }
        });

    }

    public void observeChat(LifecycleOwner lifecycleOwner,
                            Observer<List<Message>> messagesObserver,
                            Observer<List<User>> usersObserver) {
        usersLiveData.observe(lifecycleOwner, usersObserver);
        messagesLiveData.observe(lifecycleOwner, messagesObserver);
    }
}
