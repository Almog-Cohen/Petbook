package com.thegalos.petbook.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.thegalos.petbook.Notifications.APIService;
import com.thegalos.petbook.Notifications.Client;
import com.thegalos.petbook.Notifications.Data;
import com.thegalos.petbook.Notifications.MyResponse;
import com.thegalos.petbook.Notifications.Sender;
import com.thegalos.petbook.Notifications.Token;
import com.thegalos.petbook.R;
import com.thegalos.petbook.adapters.MessageAdapter;
import com.thegalos.petbook.objects.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Conversation extends Fragment {

    DatabaseReference reference;
    TextView userNameTv;
    Context context;
    MessageAdapter messageAdapter;
    List<Chat> chatList;
    RecyclerView recyclerView;
    SharedPreferences sp;
    RelativeLayout sendBtn;
    EditText etMessageText;
    String ownerId;
    String userId;
    String userName;
    String ownerUserName;
    APIService apiService;
    boolean notify = false;


    public Conversation() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.conversation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.conversation);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        context = getContext();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userNameTv = view.findViewById(R.id.tvConversationWith);
        etMessageText = view.findViewById(R.id.etMessageText);
        sendBtn = view.findViewById(R.id.btn_send);
        recyclerView = view.findViewById(R.id.rvChats);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

        ownerId = sp.getString("ownerId", "");

        if (user != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();


        }

        reference = FirebaseDatabase.getInstance().getReference("Users").child(ownerId).child("Details").child("Name");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ownerUserName = dataSnapshot.getValue(String.class);
                    userNameTv.setText(ownerUserName);
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
                notify = true;
                String msg = etMessageText.getText().toString();
                ImageView ivFab = view.findViewById(R.id.ivFab);
                Bitmap img = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_media_play);
                ImageViewAnimatedChange(ivFab, img);
                msg = msg.trim();
                if (!msg.equals("")) {

                    sendMessage(userId,ownerId,msg);
                } else {
                    Toast.makeText(context, R.string.message_cant_be_empty, Toast.LENGTH_SHORT).show();
                }
                etMessageText.setText("");
            }
        });
    }

    private void sendMessage(String sender, final String receiver, String message) {
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        FirebaseDatabase.getInstance().getReference().child("Messages").child(ownerId).child(userId).push().setValue(hashMap);
        FirebaseDatabase.getInstance().getReference().child("Messages").child(userId).child(ownerId).push().setValue(hashMap);


        FirebaseDatabase.getInstance().getReference().child("Messages").child(userId).child(ownerId).child("Last_Message").setValue(message);
        FirebaseDatabase.getInstance().getReference().child("Messages").child(ownerId).child(userId).child("Last_Message").setValue(message);
        FirebaseDatabase.getInstance().getReference().child("Messages").child(ownerId).child(userId).child("Time").setValue(ServerValue.TIMESTAMP);
        FirebaseDatabase.getInstance().getReference().child("Messages").child(userId).child(ownerId).child("Time").setValue(ServerValue.TIMESTAMP);


        FirebaseDatabase.getInstance().getReference().child("Messages").child(ownerId).child(userId).child("1").setValue(userName);
        FirebaseDatabase.getInstance().getReference().child("Messages").child(userId).child(ownerId).child("1").setValue(ownerUserName);

        final String msg = message;

        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (notify) {
                    sendNotification(receiver, userName, msg);

                }
                notify=false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotification(String receiver, final String userName, final String msg) {
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Token token = snapshot.getValue(Token.class);
                    //n.setToken(snapshot.getValue(Token.class));
                    Data data = new Data(userId , R.mipmap.ic_launcher , userName + ": " + msg , "New Message" , ownerId);

                    final Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                    if (response.code() == 200) {

                                        if (response.body().success != 1) {
                                            Toast.makeText(context, "Failed notif", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                    Log.d("PRAG", "onFailure: " + t.toString());
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readMessages(final String myId , final String userid) {

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(myId).child(userid);
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatList = new ArrayList<>();
                chatList.clear();

                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (!snapshot.getKey().equals("1")&&!snapshot.getKey().equals("Last_Message")&&!snapshot.getKey().equals("Time")) {
                            Chat chat = snapshot.getValue(Chat.class);
                            chatList.add(chat);
                            messageAdapter = new MessageAdapter(context, chatList );
                            recyclerView.setAdapter(messageAdapter);

                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference().child("Messages");
//        chatRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                chatList = new ArrayList<>();
//                chatList.clear();
//                if (dataSnapshot.exists()) {
//
//                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                        Chat chat = snapshot.getValue(Chat.class);
//                        if (chat.getReceiver().equals(myId) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myId)) {
//                            chatList.add(chat);
//                            Log.d("NAG", "onDataChange: " + chat.getReceiver() +"       SENDER ::" +chat.getSender());
//                        }
//
//                        messageAdapter = new MessageAdapter(context, chatList);
//                        recyclerView.setAdapter(messageAdapter);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//            }
//        });

    }

    private void ImageViewAnimatedChange(final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
        anim_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationEnd(Animation animation) {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}
                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                    @Override
                    public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }
}