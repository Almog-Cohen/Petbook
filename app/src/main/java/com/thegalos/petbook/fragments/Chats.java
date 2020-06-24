package com.thegalos.petbook.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thegalos.petbook.MessageActivity;
import com.thegalos.petbook.R;
import com.thegalos.petbook.objects.Chat;

import java.util.ArrayList;
import java.util.List;

public class Chats extends Fragment {

    private ListView listView;
Context context;
    private List<String> usersList;
    private String userId;
    String userIdChat;
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

        listView = view.findViewById(R.id.list_view);
        usersList = new ArrayList<>();

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        DatabaseReference usersChatRef = FirebaseDatabase.getInstance().getReference().child("Messages").child(userId);
        usersChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                usersList.clear();

                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        if (!snapshot.getKey().equals("message")&&!snapshot.getKey().equals("receiver")&&!snapshot.getKey().equals("sender"))
                        usersList.add(snapshot.getKey());
//                        Chat chat = snapshot.getValue(Chat.class);
//
//                        if (chat.getSender().equals(userId)){
//                            usersList.add(chat.getReceiver());
//                            Log.d("SHAG", "onDataChange:sssss " + chat.getSender() +chat.getReceiver());
//                        }
//                        if (chat.getReceiver().equals(userId)){
//                            usersList.add(chat.getSender());
//                            Log.d("SHAG", "onDataChange:sssss " + chat.getReceiver());
//                        }

                        if (getActivity() != null) {

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, usersList);

                            listView.setAdapter(adapter);
                        }
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                userIdChat = usersList.get(position);


                sp.edit().putString("ownerId", userIdChat).apply();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.flFragment, new MessageActivity(), "MessageActivity").addToBackStack("MessageActivity").commit();


            }
        });
    }
}


