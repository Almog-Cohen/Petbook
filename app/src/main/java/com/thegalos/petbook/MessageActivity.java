package com.thegalos.petbook;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thegalos.petbook.adapters.MessageAdapter;
import com.thegalos.petbook.objects.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {



    DatabaseReference reference;
    TextView userNameTv;

    MessageAdapter messageAdapter;
    List<Chat> chatList;
    RecyclerView recyclerView;

    ImageButton sendBtn;
    EditText messageEt;
    String ownerId;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        userNameTv=findViewById(R.id.user_name_tv);
        messageEt=findViewById(R.id.msg_et);
        sendBtn=findViewById(R.id.btn_send);
        recyclerView = findViewById(R.id.chat_recycler_view);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        ownerId = getIntent().getStringExtra("ownerId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }




        reference = FirebaseDatabase.getInstance().getReference("Users").child(ownerId).child("Details").child("Name");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.getValue(String.class);
                    userNameTv.setText(username);

                    readMessages(userId, ownerId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);*/

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = messageEt.getText().toString();
                if (!msg.equals("")){
                    sendMessage(userId,ownerId,msg);

                }else {
                    Toast.makeText(MessageActivity.this, "You cant send empty message", Toast.LENGTH_SHORT).show();
                }

                messageEt.setText("");

            }
        });








    }

    private void sendMessage(String sender,String receiver, String message){

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

//        FirebaseDatabase.getInstance().getReference().child("Messages").push().setValue(hashMap);
        FirebaseDatabase.getInstance().getReference().child("Messages").child(ownerId).child(userId).push().setValue(hashMap);
        FirebaseDatabase.getInstance().getReference().child("Messages").child(userId).child(ownerId).push().setValue(hashMap);

    }

    private void readMessages(final String myId , final String userid){


        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("Messages");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatList = new ArrayList<>();
                chatList.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = snapshot.getValue(Chat.class);
                        if (chat.getReceiver().equals(myId) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myId)) {
                            chatList.add(chat);
                            Log.d("NAG", "onDataChange: " + chat.getReceiver() +"       SENDER ::" +chat.getSender());
                        }

                        messageAdapter = new MessageAdapter(MessageActivity.this, chatList);
                        recyclerView.setAdapter(messageAdapter);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}