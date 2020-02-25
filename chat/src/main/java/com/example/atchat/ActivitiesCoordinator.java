package com.example.atchat;

import android.content.Context;
import android.content.Intent;
import android.util.Log;


import java.util.ArrayList;
import java.util.Observer;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

public class ActivitiesCoordinator {

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

    public void joinChat(String host, String username, final Context context) {

        chatService = new ChatService(host, context);

        currentUser = User.newBuilder().setName(username).build();
        chatService.getChat(currentUser, new StreamObserver<Chat>() {
            @Override
            public void onNext(Chat value) {
                messagesObservable.setMessages(new ArrayList<>(value.getMessagesList()));
                Intent intentToStartChatActivity = new Intent(context, ChatActivity.class);
                context.startActivity(intentToStartChatActivity);

                chatService.observeMessages(currentUser, messageStreamObserver);
            }

            @Override
            public void onError(Throwable throwable) {

//                if (throwable instanceof StatusRuntimeException) {
//                    StatusRuntimeException exception = (StatusRuntimeException) throwable;
//                    switch (exception.getStatus().getCode()) {
//                        case UNAVAILABLE:
//                            showError(ERROR_SERVICE_UNAVAILABLE, null);
//                            return;
//                        case ALREADY_EXISTS:
//                            showError(null, ERROR_DUPLICATED_USER);
//                            return;
//                    }
//                }
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

    public void observeChat(Observer messagesObserver) {
//        usersObservable.addObserver(usersObserver);
        messagesObservable.addObserver(messagesObserver);
//        Log.d(TAG, "observe chat messages: " + messagesObservable.getMessages().toString());

//        usersObservable.notifyObservers();
        messagesObservable.notifyObservers();
    }
}