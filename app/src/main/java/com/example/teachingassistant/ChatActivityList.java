package com.example.teachingassistant;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class ChatActivityList extends AppCompatActivity {
    private static final String TAG = "ChatActivity";

    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText contentMessage;
    private TextView titleToolbar;
    private ImageButton buttonSend;
    private boolean side = false;
    private DatabaseReference dbTest, mDatabase;
    private String chat_msg, chat_user_name, dayDB, timeDB, fullNameThis;
    private String temp_key;

    Toolbar toolbar;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()) {

            dayDB = (String) ((DataSnapshot) i.next()).getValue();
            chat_msg = (String) ((DataSnapshot) i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot) i.next()).getValue();
            timeDB = (String) ((DataSnapshot) i.next()).getValue();

            //Log.d("TAG", "append_chat_conversation: " + chat_msg + chat_user_name);
            String date = dayDB;
            String time = timeDB;

            Log.d("TAG", "append_chat_conversation: " + fullNameThis + chat_user_name);

            if (!fullNameThis.equals(chat_user_name))


                chatArrayAdapter.add(new ChatMessage("Left", chat_user_name, chat_msg, date, time));
            else
                chatArrayAdapter.add(new ChatMessage("Right", chat_user_name, chat_msg, date, time));
            //đẩy data ra view.
            //chat_conversation.append(chat_user_name +" : "+chat_msg +" \n");
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //      loadData();

        Intent intent = getIntent();

        String userName = intent.getStringExtra("userName");
        String idClass = intent.getStringExtra("idClass");
        String fullName = intent.getStringExtra("fullName");
        fullNameThis = fullName;

        dbTest = FirebaseDatabase.getInstance().getReference().child(idClass);

        dbTest.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        buttonSend = (ImageButton) findViewById(R.id.send);
        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.message_sent);
        listView.setAdapter(chatArrayAdapter);

        contentMessage = (EditText) findViewById(R.id.msg);
        contentMessage.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    return sendChatMessage();
                }
                return false;
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String fullName = intent.getStringExtra("fullName");


                //----------------------------------------------------
                calendar = new MyCalendar();

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
                String currentDateandTime = sdf.format(new Date());

                String date = currentDateandTime;

                sdf = new SimpleDateFormat(" HH:mm");
                currentDateandTime = sdf.format(new Date());

                String time = currentDateandTime;

                String User = "THAY_A";
                String message = contentMessage.getText().toString();

                //    uploadDataToDb(message, date, time);

                //  chatArrayAdapter.add(new ChatMessage("Right", User, message, date, time));
                //----------------------------------------------------

                Map<String, Object> map = new HashMap<String, Object>();
                temp_key = dbTest.push().getKey();
                dbTest.updateChildren(map);

                DatabaseReference message_root = dbTest.child(temp_key);
                Map<String, Object> map2 = new HashMap<String, Object>();
                map2.put("name", fullName);
                String input_msg = contentMessage.getText().toString();

                map2.put("msg", input_msg);
                map2.put("date", date);
                map2.put("time", time);
                message_root.updateChildren(map2);


                sendChatMessage();
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });

        setToolbar();
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);

        titleToolbar = toolbar.findViewById(R.id.titleToolbar);
        TextView subtitle = toolbar.findViewById(R.id.subtitleToolbar);

        ImageButton back = toolbar.findViewById(R.id.back);
        ImageButton save = toolbar.findViewById(R.id.save);

        titleToolbar.setText("CHAT");

        subtitle.setVisibility(View.GONE);

        save.setVisibility(View.INVISIBLE);

        back.setOnClickListener(v -> onBackPressed());
    }

//    private void loadData() {
//
//        Intent intent = getIntent();
//
//        String userName = intent.getStringExtra("userName");
//        String idClass = intent.getStringExtra("idClass");
//        String fullName = intent.getStringExtra("fullName");
//
//
//        mDatabase = FirebaseDatabase.getInstance().getReference("Chat");
//        mDatabase.child(idClass).addListenerForSingleValueEvent(new ValueEventListener() {
//
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                for (DataSnapshot child : snapshot.getChildren()) {
//
//                    String message = child.child("contentMessage").getValue(String.class);
//                    String date = child.child("Date").getValue(String.class);
//                    String time = child.child("Time").getValue(String.class);
//                    String userNameChatOnDb = child.child("userName").getValue(String.class);
//                    String fullNameOnDB = child.child("fullName").getValue(String.class);
//
//                    // loadMessageDB(userNameChatOnDb, message, date, time);
//
//                    Log.d("TAG", "onDataChange: " + userName + "_" + fullNameOnDB);
//
//                    if (userName.equals(userNameChatOnDb))
//                        chatArrayAdapter.add(new ChatMessage("Right", fullNameOnDB, message, date, time));
//                    else
//                        chatArrayAdapter.add(new ChatMessage("Left", fullNameOnDB, message, date, time));
//
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ChatActivityList.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }

    private MyCalendar calendar;

    private boolean sendChatMessage() {
        calendar = new MyCalendar();

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM");
        String currentDateandTime = sdf.format(new Date());

        String date = currentDateandTime;

        sdf = new SimpleDateFormat(" HH:mm");
        currentDateandTime = sdf.format(new Date());

        String time = currentDateandTime;

        String User = "THAY_A";
        String message = contentMessage.getText().toString();

        //    uploadDataToDb(message, date, time);

        //   chatArrayAdapter.add(new ChatMessage("Right", User, message, date, time));
        contentMessage.setText("");


//        side = !side;
        return true;
    }

//    private void uploadDataToDb_1(String Message, String Date, String Time, String userName, String idClass, String fullName, String idStt) {
//
//        mDatabase = FirebaseDatabase.getInstance().getReference("Chat");
//        mDatabase.child(idClass).child(idStt).child("userName").setValue(userName);
//        mDatabase.child(idClass).child(idStt).child("contentMessage").setValue(Message);
//        mDatabase.child(idClass).child(idStt).child("Date").setValue(Date);
//        mDatabase.child(idClass).child(idStt).child("Time").setValue(Time);
//        mDatabase.child(idClass).child(idStt).child("fullName").setValue(fullName);
//
//    }

//    private void uploadDataToDb(String Message, String Date, String Time) {
//
//        Intent intent = getIntent();
//
//        String userName = intent.getStringExtra("userName");
//        String idClass = intent.getStringExtra("idClass");
//        String fullName = intent.getStringExtra("fullName");
//        String idStt = "1";
//
//        mDatabase = FirebaseDatabase.getInstance().getReference("Chat");
//        mDatabase.child(idClass).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int d = 1;
//                for (DataSnapshot child : snapshot.getChildren()) {
//
//                    d++;
//
//                }
//
//                String idStt = String.valueOf(d);
//
//                uploadDataToDb_1(Message, Date, Time, userName, idClass, fullName, idStt);
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(ChatActivityList.this, "Fail to get data.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }


}