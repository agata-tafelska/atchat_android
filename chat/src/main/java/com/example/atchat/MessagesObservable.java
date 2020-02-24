package com.example.atchat;

import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;

public class MessagesObservable extends Observable {
    private static final String TAG = MessagesObservable.class.getSimpleName();

    private ArrayList<Message> messages;

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
        notifyObservers();
        Log.d(TAG, "Messages set.");
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyObservers();
    }

    @Override
    public void notifyObservers() {
        super.setChanged();
        notifyObservers(messages);
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }
}
