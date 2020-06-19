package com.thegalos.petbook.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import com.thegalos.petbook.Adapters.FeedAdapter;
import com.thegalos.petbook.Objects.Feed;
import com.thegalos.petbook.Objects.Pet;
import com.thegalos.petbook.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppFeed extends Fragment {

    private static List<Feed> feedList = new ArrayList<>();
    FirebaseUser user;
    ProgressBar progressBar;
    int keepY;
    TextView tvFeedUser;
    SharedPreferences preferences;
    Context context;
    SharedPreferences.Editor editor;
    int maxprogress = 0;

    public AppFeed() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.app_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        final RecyclerView recyclerView = view.findViewById(R.id.rvFeed);
        Button btnFeedAction = view.findViewById(R.id.btnFeedAction);
        context = getContext();
        progressBar = view.findViewById(R.id.progressBar2);
        tvFeedUser = view.findViewById(R.id.tvFeedUser);
        user = FirebaseAuth.getInstance().getCurrentUser();
        preferences = context.getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = preferences.edit();




        if (user == null) {
            btnFeedAction.setText(R.string.register);
        } else {
            String str = "Hi " + user.getDisplayName();
            editor.putString("Name", user.getDisplayName());
            tvFeedUser.setText(str);
            if (preferences.getLong("MemberSince", 0) == 0) {
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
        final FeedAdapter feedAdapter = new FeedAdapter(feedList);
        recyclerView.setAdapter(feedAdapter);

//        progressBar.setMax(/*recyclerView.getLayoutManager().getHeight()*/1000);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                keepY += dy;
                if (maxprogress < keepY)
                    maxprogress = keepY;
                progressBar.setMax(maxprogress);
                Log.d("progress_galos", ": keepY: " + keepY + " dy: " + dy + " maxprogress: " + maxprogress);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    progressBar.setProgress(keepY,true);
                } else
                    progressBar.setProgress(keepY);
            }
        });
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                feedList.clear();
                if (dataSnapshot.exists()) {
                    for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Feed feed = new Feed();
                        feed.setPet(snapshot.child("Pet").getValue(Pet.class));
                        feed.setPostText(snapshot.child("PostText").getValue(String.class));
                        feed.setPostOwner(snapshot.child("Owner").getValue(String.class));
                        feed.setOwnerUID(snapshot.child("OwnerUID").getValue(String.class));
                        feed.setImageURL(snapshot.child("ImageURL").getValue(String.class));
                        ///////////////////////////////////////////////

                        Date date = new Date(snapshot.child("Time").getValue(Long.class));
                        SimpleDateFormat sfd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        String text = sfd.format(date);
                        feed.setSelectedPet(text);

                        //////////////////////////////////////////
                        feedList.add(feed);
                    }
                    Collections.reverse(feedList);
                    feedAdapter.notifyDataSetChanged();

//                    Log.d("progress_galos", "range is: " + range);
//                    Log.d("progress_galos", "max is: " + recyclerView.getMeasuredHeight()* (feedList.size()-3));
//                    Log.d("progress_galos", "max * 3 is: " + recyclerView.getHeight()*3);
//                    Log.d("progress_galos", "max is: " + recyclerView.getMeasuredHeightAndState());
//                    Log.d("progress_galos", "max is: " + recyclerView.getMinimumHeight());
//                    Log.d("progress_galos", "max is: " + recyclerView.getLayoutManager().getMinimumHeight());
//                    Log.d("progress_galos", "max is: " + recyclerView.getLayoutManager().getHeight());
//                    Log.d("progress_galos", "max is: " + recyclerView.getLayoutManager().getMinimumHeight());
//                    Log.d("progress_galos", "getHeight/feedlist is: " + recyclerView.getLayoutManager().getHeight()/feedList.size());
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
                Log.d("progress_galos", "getHeight is: " + ((recyclerView.getHeight()*recyclerView.getAdapter().getItemCount()-1)));
                Log.d("progress_galos", "getHeight is: " + ((recyclerView.getHeight()*recyclerView.getAdapter().getItemCount()-1))/2.5);
                maxprogress = (int) ((recyclerView.getHeight()*(recyclerView.getAdapter().getItemCount()-1))/2.8);
                progressBar.setMax(maxprogress);
                Log.d("progress_galos", "maxprogress: " + maxprogress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "onCancelled", databaseError.toException());
            }
        });
    }
}
