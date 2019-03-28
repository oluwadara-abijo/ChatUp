package com.example.chatup;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {

    //Constructor
    MessageAdapter(@NonNull Context context, int resource, @NonNull List<Message> messages) {
        super(context, resource, messages);
    }

    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Message message = getItem(position);

        assert message != null;
        if (message.isPublished()) {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.item_published_message, parent, false);
            TextView sentMessageTextView = convertView.findViewById(R.id.textView_sentMessage);
            sentMessageTextView.setText(message.getMessageText());
        } else {
            convertView = ((Activity) getContext()).getLayoutInflater()
                    .inflate(R.layout.item_subscribed_message, parent, false);
            TextView receivedMessageTextView = convertView.findViewById(R.id.textView_receivedMessage);
            receivedMessageTextView.setText(message.getMessageText());
        }

        return convertView;
    }
}
