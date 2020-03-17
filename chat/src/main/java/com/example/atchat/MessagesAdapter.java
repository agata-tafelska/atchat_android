package com.example.atchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {

    public MessagesAdapter() {

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
    @NonNull
    @Override
    public MessagesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View messageView = inflater.inflate(R.layout.messages_list_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(messageView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.ViewHolder holder, int position) {
        String messageDate = chatMessages.get(position).getDate();
        String messageUser = chatMessages.get(position).getUser();
        String messageText = chatMessages.get(position).getMessage();
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
