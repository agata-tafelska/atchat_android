package com.example.atchat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Observer;

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

    private MessagesObservable messagesObservable = new MessagesObservable();
    private UsersObservable usersObservable = new UsersObservable();

    private StreamObserver<Message> messageStreamObserver = new DefaultStreamObserver<Message>() {
        @Override
        public void onNext(Message value) {
            messagesObservable.addMessage(value);
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
            usersObservable.updateUsers(value.getUsersList());
        }
    };

    public void joinChat(String host, String username, final Context context) {

        chatService = new ChatService(host, context);

        currentUser = User.newBuilder().setName(username).build();
        chatService.getChat(currentUser, new StreamObserver<Chat>() {
            @Override
            public void onNext(Chat value) {
                messagesObservable.setMessages(new ArrayList<>(value.getMessagesList()));
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
                        case ALREADY_EXISTS:
                            EventBus.getDefault().post(new ErrorEvent("Username already exists. Please try again."));
                            return;
                    }
                }
//                showError(null, ERROR_UNKNOWN);
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

    public void observeChat(Observer messagesObserver, Observer usersObserver) {
        usersObservable.addObserver(usersObserver);
        messagesObservable.addObserver(messagesObserver);
//        Log.d(TAG, "observe chat messages: " + messagesObservable.getMessages().toString());

        usersObservable.notifyObservers();
        messagesObservable.notifyObservers();
    }
}
