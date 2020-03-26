package com.example.atchat;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    private static final String TAG = MessagesAdapter.class.getSimpleName();

    private static int TYPE_MY_USER = 1;
    private static int TYPE_OTHER_USERS = 2;

    private String loggedUser;

    public MessagesAdapter(String loggedUser) {
        this.loggedUser = loggedUser;
    }

    private List<MessageText> chatMessages;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView dateTextView;
        private TextView userTextView;
        private TextView messageTextView;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            dateTextView = (TextView) itemView.findViewById(R.id.message_text_view_date);
            userTextView = (TextView) itemView.findViewById(R.id.message_text_view_user);
            messageTextView = (TextView) itemView.findViewById(R.id.message_text_view_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "Logged user name: " + loggedUser);
        if (chatMessages.get(position).getUser().equals("~" + loggedUser)) {
            Log.d(TAG, "getItemViewType: TYPE_MY_USER");
            return TYPE_MY_USER;
        } else {
            Log.d(TAG, "getItemViewType: TYPE_OTHER_USERS");
            return TYPE_OTHER_USERS;
        }
    }

    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View messageView;

        if (viewType == TYPE_MY_USER) {
            Log.d(TAG, "Type: My user" );
            messageView = inflater.inflate(R.layout.messages_list_item_my_user, parent, false);
        } else {
            Log.d(TAG, "Type: Other users" );
            messageView = inflater.inflate(R.layout.messages_list_item_other_users, parent, false);
        }

        ViewHolder viewHolder = new ViewHolder(messageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        String messageDate = chatMessages.get(position).getDate();
        String messageUser = chatMessages.get(position).getUser();
        String messageText = chatMessages.get(position).getMessage();
        if (messageUser.equals("guest")) {
            holder.userTextView.setTypeface(null, Typeface.NORMAL);
        }
        holder.dateTextView.setText(messageDate);
        holder.userTextView.setText(messageUser);
        holder.messageTextView.setText(messageText);
    }

    @Override
    public int getItemCount() {
        if (chatMessages == null) return 0;
        return chatMessages.size();
    }

    public void setMessages (List<MessageText> messages) {
        chatMessages = messages;
        notifyDataSetChanged();
    }
}
