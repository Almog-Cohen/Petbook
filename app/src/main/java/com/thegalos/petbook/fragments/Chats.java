package com.thegalos.petbook.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.thegalos.petbook.Notifications.Token;
import com.thegalos.petbook.R;
import com.thegalos.petbook.adapters.UserAdapter;

import java.util.ArrayList;
import java.util.List;

public class Chats extends Fragment {

    RecyclerView usersRecyclerView;
    UserAdapter userAdapter;
    Context context;
    private List<String> usersList;
    private List<String> userNamesList;
    private String userId;
    String userIdChat;
    String userNameChat;
    SharedPreferences sp;



    public Chats() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view,  Bundle savedInstanceState) {
        context = getContext();
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        usersRecyclerView = view.findViewById(R.id.rvChats);
        usersList = new ArrayList<>();
        userNamesList = new ArrayList<>();
        usersRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
//        linearLayoutManager.setStackFromEnd(true);
        usersRecyclerView.setLayoutManager(linearLayoutManager);

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        DatabaseReference usersChatRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(userId);
        usersChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();
                userNamesList.clear();

                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        if (!snapshot.getKey().equals("1"))
                            usersList.add(snapshot.getKey());

                        if (snapshot.child("1").getKey().equals("1"))
                            userNamesList.add(snapshot.child("1").getValue(String.class));


                        if (userNamesList != null)
                            userAdapter = new UserAdapter(context, userNamesList);
                            userAdapter.setListener(new UserAdapter.MyUserListener() {
                                @Override
                                public void onUserClicked(int position, View view) {

                                    String userId = usersList.get(position);
                                    sp.edit().putString("ownerId", userId).apply();
                                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                                    ft.replace(R.id.flFragment, new Conversation(), "Conversation").addToBackStack("Conversation").commit();

                                }
                            });

                        usersRecyclerView.setAdapter(userAdapter);

                        //TODO add username as child below userid.
//                           userNameChat. = snapshot.getValue(Chat.class);
//                        Chat chat = snapshot.getValue(Chat.class);
//
//                        if (chat.getSender().equals(userId)) {
//                            usersList.add(chat.getReceiver());
//                            Log.d("SHAG", "onDataChange:sssss " + chat.getSender() +chat.getReceiver());
//                        }
//                        if (chat.getReceiver().equals(userId)) {
//                            usersList.add(chat.getSender());
//                            Log.d("SHAG", "onDataChange:sssss " + chat.getReceiver());
//                        }


                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener((Activity) context, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("MAGI", newToken);
                updateToken(newToken);

            }
        });


    }

    private void updateToken(String newToken) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(newToken);
        reference.child(firebaseUser.getUid()).setValue(token1);

    }
}


