package com.example.teachingassistant;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class ChatArrayAdapter extends ArrayAdapter<ChatMessage> {

    private TextView contentMessage, timeMessage, dateMessage, userMessage;
    private List<ChatMessage> chatMessageList = new ArrayList<ChatMessage>();
    private Context context;

    @Override
    public void add(ChatMessage object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public int getCount() {
        return this.chatMessageList.size();
    }

    public ChatMessage getItem(int index) {
        return this.chatMessageList.get(index);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (chatMessageObj.getSideMessage().equals("Right")) {
            row = inflater.inflate(R.layout.message_sent, parent, false);

            contentMessage = row.findViewById(R.id.textMessageSent);
            dateMessage = row.findViewById(R.id.dateMessageSent);
            timeMessage = row.findViewById(R.id.timeMessageSent);

            String Content = chatMessageObj.getContentMessage();
            String date = chatMessageObj.getDateMessage();
            String time = chatMessageObj.getTimeMessage();

            contentMessage.setText(Content);
            dateMessage.setText(date);
            timeMessage.setText(time);

        } else if (chatMessageObj.getSideMessage().equals("Left")) {
            row = inflater.inflate(R.layout.message_received, parent, false);

            userMessage = row.findViewById(R.id.nameMessageReceived);
            String User = chatMessageObj.getUserMessage();
            userMessage.setText(User);

            contentMessage = row.findViewById(R.id.textMessageReceived);
            dateMessage = row.findViewById(R.id.dateMessageReceived);
            timeMessage = row.findViewById(R.id.timeMessageReceived);

            String Content = chatMessageObj.getContentMessage();
            String date = chatMessageObj.getDateMessage();
            String time = chatMessageObj.getTimeMessage();

            contentMessage.setText(Content);
            dateMessage.setText(date);
            timeMessage.setText(time);

        }

        dateMessage.setVisibility(View.GONE);


        return row;

    }

}
