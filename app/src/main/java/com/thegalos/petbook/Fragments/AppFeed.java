package com.thegalos.petbook.Fragments;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thegalos.petbook.Objects.Feed;
import com.thegalos.petbook.Adapters.FeedAdapter;
import com.thegalos.petbook.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AppFeed extends Fragment {

    private static List<Feed> feedList = new ArrayList<>();
    FirebaseUser user;
    ProgressBar progressBar;
    int keepY;

    public AppFeed() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,  Bundle savedInstanceState) {
        final RecyclerView recyclerView = view.findViewById(R.id.rvFeed);
        Button btnFeedAction = view.findViewById(R.id.btnFeedAction);
        progressBar = view.findViewById(R.id.progressBar2);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            btnFeedAction.setText(R.string.register);
        } else {
            TextView tvFeedUser = view.findViewById(R.id.tvFeedUser);
            tvFeedUser.setText("Hi, " + user.getDisplayName());

        }
        
//        Button btnLogout = view.findViewById(R.id.btnLogout);
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//                //TODO show disconnected change in UI
//            }
//        });

        btnFeedAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null){
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.flFragment, new AddFeed(), "AddFeed").addToBackStack("AddFeed").commit();
                } else {
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.flFragment, new Login(), "Login").addToBackStack("Login").commit();

                }
               }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        final FeedAdapter feedAdapter = new FeedAdapter((feedList));
        recyclerView.setAdapter(feedAdapter);
Log.d("progress_galos", "max is: " + recyclerView.getLayoutManager().getHeight());
        progressBar.setMax(/*recyclerView.getLayoutManager().getHeight()*/1000);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                keepY += dy;
                Log.d("progress_galos", ": keepY: " + keepY + " dy: " + dy);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressBar.setProgress(keepY,true);
                } else
                    progressBar.setProgress(keepY);
            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot,  String s) {
//                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(dataSnapshot.child("Owner").getValue(String.class));
//                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
////                                feedList.clear();
//                        if (dataSnapshot.exists()) {
//                            String name = dataSnapshot.child("Details").child("Name").getValue(String.class);
////                                    holder.tvPostOwner.setText(dataSnapshot.child("postText").getValue(String.class)/*feed.getPostOwner()*/);
//                            Feed feed = new Feed();
//                            feed.setPostText(dataSnapshot.child("postText").getValue(String.class));
//                            feed.setPostOwner(name);
//                            feed.setSelectedPet(dataSnapshot.child("selectedPet").getValue(String.class));
//                            feed.setImageURL(dataSnapshot.child("ImageURL").getValue(String.class));
//                            feedList.add(feed);
//                            feedAdapter.notifyDataSetChanged();
//
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                        Log.w("firebase", "onCancelled", databaseError.toException());
//                    }
//                });
//
//                //////////////////////////////////////////////////////
////                        Feed feed = new Feed();
////                        feed.setPostText(snapshot.child("postText").getValue(String.class));
////                        feed.setPostOwner(snapshot.child("Owner").getValue(String.class));
////                        feed.setSelectedPet(snapshot.child("selectedPet").getValue(String.class));
////                        feed.setImageURL(snapshot.child("ImageUrl").getValue(String.class));
////                        feedList.add(feed);
//
//            }
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot,  String s) { }
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) { }
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot,  String s) { }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) { }
//        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                feedList.clear();
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Feed feed = new Feed();
                        feed.setPostText(snapshot.child("postText").getValue(String.class));
                        feed.setPostOwner(snapshot.child("Owner").getValue(String.class));
                        feed.setSelectedPet(snapshot.child("selectedPet").getValue(String.class));
                        feed.setImageURL(snapshot.child("ImageURL").getValue(String.class));
                        feedList.add(feed);
                    }
                    Collections.reverse(feedList);
                    feedAdapter.notifyDataSetChanged();
                }
                Log.d("progress_galos", "getHeight is: " + recyclerView.getLayoutManager().getHeight());
//                Log.d("progress_galos", "getHeight/feedlist is: " + recyclerView.getLayoutManager().getHeight()/feedList.size());
//                Log.d("progress_galos", "getHeightMode/getLayoutManager().getHeight() is: " + recyclerView.getLayoutManager().getHeightMode()/recyclerView.getLayoutManager().getHeight());
//                Log.d("progress_galos", "getMinimumHeight is: " + recyclerView.getLayoutManager().getMinimumHeight());
//                Log.d("progress_galos", "getHeight is: " + recyclerView.getHeight());
//                Log.d("progress_galos", "getMinimumHeight is: " + recyclerView.getMinimumHeight());
//                Log.d("progress_galos", "computeVerticalScrollRange is: " + recyclerView.computeVerticalScrollRange());
//                Log.d("progress_galos", "computeVerticalScrollExtent is: " + recyclerView.computeVerticalScrollExtent());
//                Log.d("progress_galos", "computeVerticalScrollOffset is: " + recyclerView.computeVerticalScrollOffset());
//                Log.d("progress_galos", "computeHorizontalScrollOffset is: " + recyclerView.computeHorizontalScrollOffset());
                Log.d("progress_galos", "getBottom is: " + recyclerView.getBottom());
                Log.d("progress_galos", "both is: " + (recyclerView.getBottom() + recyclerView.getLayoutManager().getHeight()));
                progressBar.setMax((recyclerView.getBottom() + recyclerView.getLayoutManager().getHeight()));
//                Log.d("progress_galos", "getPaddingBottom is: " + recyclerView.getPaddingBottom());


//                recyclerView.scrollToPosition(recyclerView.getAdapter().getItemCount()-1);
//                recyclerView.scrollTo(1,1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "onCancelled", databaseError.toException());
            }
        });
    }
}
