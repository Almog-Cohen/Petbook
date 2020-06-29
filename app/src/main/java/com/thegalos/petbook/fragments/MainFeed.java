package com.thegalos.petbook.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
    Context context;
    SharedPreferences.Editor editor;
    int maxProgress = 0;
    SwipeRefreshLayout refreshLayout;
    SharedPreferences sp;
    ConstraintLayout constraintLayout;
    String str;


    public MainFeed() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        final RecyclerView recyclerView = view.findViewById(R.id.rvFeed);
        FloatingActionButton fabFeed = view.findViewById(R.id.fabFeed);
        context = getContext();
        progressBar = view.findViewById(R.id.progressBar2);
        constraintLayout = view.findViewById(R.id.loadingLayout);
        user = FirebaseAuth.getInstance().getCurrentUser();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
        progressBar.bringToFront();
        editor = sp.edit();
        refreshLayout = view.findViewById(R.id.mainLayout);

        if (user == null) {
            str = getString(R.string.app_name);
            fabFeed.setVisibility(View.GONE);
        } else {
            fabFeed.setVisibility(View.VISIBLE);
            str = getString(R.string.app_name) + " - " + user.getDisplayName();
            editor.putString("Name", user.getDisplayName());
            if (sp.getLong("MemberSince", 0) == 0) {
                DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Details");
                db.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            editor.putLong("MemberSince", dataSnapshot.child("MemberSince").getValue(Long.class)).apply();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }

        fabFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sp.contains("PetList")) {
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.flFragment, new AddFeed(), "AddFeed").addToBackStack("AddFeed").commit();
                } else {
                    Toast.makeText(context, R.string.add_pets_first, Toast.LENGTH_SHORT).show();
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
                        feed.setPet(snapshot.child("Pet").getValue(Pet.class));
                        feed.setPostText(snapshot.child("PostText").getValue(String.class));
                        feed.setSelectedPet(snapshot.child("SelectedPet").getValue(String.class));
                        feed.setWhoPays(snapshot.child("WhoPays").getValue(String.class));

                        ///////////////////////TIME////////////////////
                        Date date = new Date(snapshot.child("Time").getValue(Long.class));
                        SimpleDateFormat sfd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                        String text = sfd.format(date);
                        feed.setTime(text);
                        feedList.add(feed);
                    }
                    Collections.reverse(feedList);
                    feedAdapter.notifyDataSetChanged();
                    constraintLayout.setVisibility(View.GONE);

//                    Log.d("progress_galos", "getHeight/feedList is: " + recyclerView.getLayoutManager().getHeight()/(feedList.size()-1));
//                    Log.d("progress_galos", "getMinimumHeight is: " + recyclerView.getLayoutManager().getMinimumHeight());
//                    Log.d("progress_galos", "getMinimumHeight is: " + recyclerView.getMinimumHeight());
//                    Log.d("progress_galos", "computeVerticalScrollRange is: " + recyclerView.computeVerticalScrollRange());
//                    Log.d("progress_galos", "computeVerticalScrollExtent is: " + recyclerView.computeVerticalScrollExtent());
//                    Log.d("progress_galos", "computeVerticalScrollOffset is: " + recyclerView.computeVerticalScrollOffset());

//                    progressBar.setMax(size);
                }
                int height = Resources.getSystem().getDisplayMetrics().heightPixels;
//                Log.d("progress_galos", " Y is: " + (height - 1200));

//                int size = feedList.size();
//                Log.d("progress_galos", " dpToPx is: " + dpToPx(height));
//                Log.d("progress_galos", " pxToDp is: " + pxToDp(height));
//                Log.d("progress_galos", "some math --- is: " + ((height-recyclerView.getHeight())*(size-1)));
//                Log.d("progress_galos", "getHeight is: " + recyclerView.getHeight());
//                maxProgress = (int) ((recyclerView.getHeight() * (recyclerView.getAdapter().getItemCount() - 4)) / 2.7);


                maxProgress = height - 1200;
                progressBar.setMax(maxProgress);
                Log.d("progress_galos", "maxProgress claculated: " + maxProgress);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("firebase", "onCancelled", databaseError.toException());
            }
        });


        //Show label when uncollapsing
        final CollapsingToolbarLayout collapsingToolbarLayout = /*(CollapsingToolbarLayout)*/ view.findViewById(R.id.collapsingToolbar);
        AppBarLayout appBarLayout = /*(AppBarLayout)*/ view.findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(str);
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
                    collapsingToolbarLayout.setTitle("");//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
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

        NestedScrollView nestedScrollView = view.findViewById(R.id.nestedscroll);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > maxProgress) {
                    maxProgress = scrollY;
                    progressBar.setMax(maxProgress);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    progressBar.setProgress(scrollY, true);
                else
                    progressBar.setProgress(scrollY);
                Log.d("progress_galos", "v.getMeasuredHeight() " + v.getMeasuredHeight() + " scrollY " + scrollY + " maxProgress: " + maxProgress);

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
                    Toast.makeText(context, R.string.log_in_to_view_full_post, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    private void startRefresh() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, new MainFeed(), "MainFeed").commit();
    }

}
