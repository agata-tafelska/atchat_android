package com.example.atchat;

import android.content.Context;

import io.grpc.android.AndroidChannelBuilder;
import io.grpc.stub.StreamObserver;

public class ChatService {
    private static final int PORT = 9000;

    private ChatServiceGrpc.ChatServiceStub asyncStub;
    private String host;

    public ChatService(String host, Context context) {
        this.host = host;
        this.asyncStub = ChatServiceGrpc.newStub(
                AndroidChannelBuilder.forAddress(host, PORT)
                        .usePlaintext()
                        .context(context)
                        .build()
        );
    }

    public String getHost() {
        return host;
    }

    public void register(User request, StreamObserver<User> responseObserver) {
        asyncStub.register(request, responseObserver);
    }

    public void getChat(User request, StreamObserver<Chat> responseObserver) {
        asyncStub.getChat(request, responseObserver);
    }

    public void unsubscribe(User request, StreamObserver<User> responseObserver) {
        asyncStub.unsubscribe(request, responseObserver);
    }

    public void observeMessages(User request, StreamObserver<Message> responseObserver) {
        asyncStub.observeMessages(request, responseObserver);
    }

    public void observeUsers(User request, StreamObserver<CurrentUsers> responseObserver) {
        asyncStub.observeUsers(request, responseObserver);
    }

    public void sendMessage(Message request, StreamObserver<Message> responseObserver) {
        asyncStub.sendMessage(request, responseObserver);
    }
}
