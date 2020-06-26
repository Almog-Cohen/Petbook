package com.thegalos.petbook.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.thegalos.petbook.R;
import com.thegalos.petbook.adapters.FeedAdapter;
import com.thegalos.petbook.objects.Feed;
import com.thegalos.petbook.objects.Pet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainFeed extends Fragment {

    private static List<Feed> feedList = new ArrayList<>();
    FirebaseUser user;
    ProgressBar progressBar;
    int keepY;
    Context context;
    SharedPreferences.Editor editor;
    int maxProgress = 0;
    SwipeRefreshLayout refreshLayout;
    SharedPreferences sp;
    ConstraintLayout constraintLayout;


    public MainFeed() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        final RecyclerView recyclerView = view.findViewById(R.id.rvFeed);
        Button btnFeedAction = view.findViewById(R.id.btnFeedAction);
        context = getContext();
        progressBar = view.findViewById(R.id.progressBar2);
        constraintLayout = view.findViewById(R.id.loadingLayout);
        user = FirebaseAuth.getInstance().getCurrentUser();
        sp = PreferenceManager.getDefaultSharedPreferences(context);

        editor = sp.edit();
        refreshLayout = view.findViewById(R.id.mainLayout);

        if (user == null) {
            btnFeedAction.setText(R.string.login);
        } else {
            String str = "Hi " + user.getDisplayName();
            editor.putString("Name", user.getDisplayName());
            if (sp.getLong("MemberSince", 0) == 0) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Details");
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            editor.putLong("MemberSince", dataSnapshot.child("MemberSince").getValue(Long.class)).apply();
                            Toast.makeText(context, "saved MemberSince to prefs", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        btnFeedAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    if (sp.contains("PetList")) {
                        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                        ft.replace(R.id.flFragment, new AddFeed(), "AddFeed").addToBackStack("AddFeed").commit();
                    } else {
                        Toast.makeText(context, "Please add Pets first in Profile screen", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.flFragment, new Login(), "Login").addToBackStack("Login").commit();

                }
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        final FeedAdapter feedAdapter = new FeedAdapter(context, feedList);
        recyclerView.setAdapter(feedAdapter);

//        progressBar.setMax(/*recyclerView.getLayoutManager().getHeight()*/1000);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                feedList.clear();
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Feed feed = new Feed();
                        feed.setAmount(snapshot.child("Amount").getValue(String.class));
                        if (snapshot.hasChild("ImageURL"))
                            feed.setImageURL(snapshot.child("ImageURL").getValue(String.class));
                        else
                            feed.setImageURL("null");
                        if (snapshot.hasChild("IsFree"))
                            feed.setFree(snapshot.child("IsFree").getValue(String.class));
                        else
                            feed.setFree("Looking");
                        feed.setPostOwner(snapshot.child("Owner").getValue(String.class));
                        feed.setOwnerUID(snapshot.child("OwnerUID").getValue(String.class));
//                        if (dataSnapshot.hasChild("Pet")) {
                            feed.setPet(snapshot.child("Pet").getValue(Pet.class));
//                        } else {
//                            Pet pet = new Pet();
//                            pet.setAge("");
//                            pet.setAnimalType("");
//                            pet.setBreed("");
//                            pet.setCurrentImagePath("null");
//                            pet.setGender("");
//                            pet.setName("");
//                            pet.setPetUID("");
//                            pet.setPureBred(false);
//                            pet.setVaccine(false);
//                            feed.setPet(pet);
//
//                        }
                        feed.setPostText(snapshot.child("PostText").getValue(String.class));
                        feed.setSelectedPet(snapshot.child("SelectedPet").getValue(String.class));
                        feed.setWhoPays(snapshot.child("WhoPays").getValue(String.class));

                        ///////////////////////TIME////////////////////
                        Date date = new Date(snapshot.child("Time").getValue(Long.class));
                        SimpleDateFormat sfd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        String text = sfd.format(date);
                        feed.setTime(text);

                        //////////////////////////////////////////
                        feedList.add(feed);
                    }
                    Collections.reverse(feedList);
                    feedAdapter.notifyDataSetChanged();
                    constraintLayout.setVisibility(View.GONE);

//                    Log.d("progress_galos", "range is: " + range);
//                    Log.d("progress_galos", "max is: " + recyclerView.getMeasuredHeight()* (feedList.size()-3));
//                    Log.d("progress_galos", "max * 3 is: " + recyclerView.getHeight()*3);
//                    Log.d("progress_galos", "max is: " + recyclerView.getMeasuredHeightAndState());
//                    Log.d("progress_galos", "max is: " + recyclerView.getMinimumHeight());
//                    Log.d("progress_galos", "max is: " + recyclerView.getLayoutManager().getMinimumHeight());
//                    Log.d("progress_galos", "max is: " + recyclerView.getLayoutManager().getHeight());
//                    Log.d("progress_galos", "max is: " + recyclerView.getLayoutManager().getMinimumHeight());
//                    Log.d("progress_galos", "getHeight/feedList is: " + recyclerView.getLayoutManager().getHeight()/feedList.size());
//                    Log.d("progress_galos", "getMinimumHeight is: " + recyclerView.getLayoutManager().getMinimumHeight());
//                    Log.d("progress_galos", "getMinimumHeight is: " + recyclerView.getMinimumHeight());
//                    Log.d("progress_galos", "computeVerticalScrollRange is: " + recyclerView.computeVerticalScrollRange());
//                    Log.d("progress_galos", "computeVerticalScrollExtent is: " + recyclerView.computeVerticalScrollExtent());
//                    Log.d("progress_galos", "computeVerticalScrollOffset is: " + recyclerView.computeVerticalScrollOffset());

//                    progressBar.setMax(size);
                }
//                recyclerView.scrollToPosition();
                int size = recyclerView.getBottom() * (feedList.size() / 3);
                Log.d("progress_galos", "getBottom is: " + size);
                Log.d("progress_galos", "getHeight is: " + recyclerView.getHeight());
                Log.d("progress_galos", "getHeight is: " + ((recyclerView.getHeight() * recyclerView.getAdapter().getItemCount() - 1)));
                Log.d("progress_galos", "getHeight is: " + ((recyclerView.getHeight() * recyclerView.getAdapter().getItemCount() - 1)) / 2.5);
                maxProgress = (int) ((recyclerView.getHeight() * (recyclerView.getAdapter().getItemCount() - 1)) / 3);
                progressBar.setMax(maxProgress);
                Log.d("progress_galos", "maxProgress: " + maxProgress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "onCancelled", databaseError.toException());
            }
        });


        // Limiters

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startRefresh();
                refreshLayout.setRefreshing(false);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                keepY += dy;
                if (maxProgress < keepY)
                    maxProgress = keepY;
                progressBar.setMax(maxProgress);
                Log.d("progress_galos", ": keepY: " + keepY + " dy: " + dy + " maxProgress: " + maxProgress);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressBar.setProgress(keepY, true);
                } else
                    progressBar.setProgress(keepY);
            }
        });


        feedAdapter.setListener(new FeedAdapter.myFeedListener() {
            @Override
            public void onFeedListener(int position) {
                if (user != null) {
                    SharedPreferences.Editor editor = sp.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(feedList.get(position));
                    editor.putString("PetSelected", json);
                    editor.apply();
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.flFragment, new ViewPost(), "ViewPost").addToBackStack("ViewPost").commit();
                } else {
                    Toast.makeText(context, "Must be logged to view full post", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startRefresh() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, new MainFeed(), "MainFeed").commit();
    }
}
