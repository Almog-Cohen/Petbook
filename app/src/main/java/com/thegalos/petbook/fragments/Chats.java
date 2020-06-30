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
import com.thegalos.petbook.objects.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Chats extends Fragment {

    RecyclerView usersRecyclerView;
    UserAdapter userAdapter;
    Context context;
    private List<String> usersList;
    private List<String> userNamesList;
    private List <Long> userTimeList;
    private List <String> lastMessageList;
    private List <String> isMessageSeenList;
    private String userId;

    private List<User> userList;

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

        userList = new ArrayList<>();
        lastMessageList = new ArrayList<>();
        userTimeList = new ArrayList<>();
        usersList = new ArrayList<>();
        userNamesList = new ArrayList<>();
        isMessageSeenList = new ArrayList<>();

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


                if (dataSnapshot.exists()) {

                    usersList.clear();
                    userNamesList.clear();
                    userTimeList.clear();
                    userList.clear();
                    lastMessageList.clear();
                    isMessageSeenList.clear();

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        if (!snapshot.getKey().equals("1") && !snapshot.equals("Last_Message"))
                            usersList.add(snapshot.getKey());
//                            userObject.setId(snapshot.getKey());
//                        Log.d("BAGA", "onDataChange: " + userList);


                        if (snapshot.child("1").getKey().equals("1"))
                            userNamesList.add(snapshot.child("1").getValue(String.class));
//                            userObject.setUserName(snapshot.child("1").getValue(String.class));


                        if (snapshot.child("Last_Message").getKey().equals("Last_Message"))
                            lastMessageList.add(snapshot.child("Last_Message").getValue(String.class));

                        if (snapshot.child("Time").getKey().equals("Time"))
                            userTimeList.add(snapshot.child("Time").getValue(Long.class));
////                            userObject.setTime(snapshot.child("Time").getValue(Long.class));
//                        if (snapshot.hasChild("Is_seen"))
////                            if (snapshot.child("Is_seen").getKey().equals("Is_seen"))
//                                isMessageSeenList.add(snapshot.child("Is_seen").getValue(String.class));
//

                    }

                    for (int i = 0 ; i < userNamesList.size() ; i++){

                        User user1 = new User();
                        user1.setTime(userTimeList.get(i));
                        user1.setUserName(userNamesList.get(i));
                        user1.setId(usersList.get(i));
                        user1.setLastMessage(lastMessageList.get(i));
//                        user1.setMessageSeen(isMessageSeenList.get(i));
                        userList.add(user1);


                    }

                    Collections.sort(userList, new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            User u1,u2;
                            u1 = (User)o1;
                            u2 = (User)o2;

                            int x = u1.getTime().compareTo(u2.getTime());
                            return x;
                        }
                    });

                    Collections.reverse(userList);

                    if (userList != null)
                        userAdapter = new UserAdapter(context, userList );
                    userAdapter.setListener(new UserAdapter.MyUserListener() {
                        @Override
                        public void onUserClicked(int position, View view) {

                            String userId = userList.get(position).getId();
                            sp.edit().putString("ownerId", userId).apply();
                            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                            ft.replace(R.id.flFragment, new Conversation(), "Conversation").addToBackStack("Conversation").commit();
                        }
                    });

                    usersRecyclerView.setAdapter(userAdapter);





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


