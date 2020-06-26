package com.thegalos.petbook.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thegalos.petbook.R;
import com.thegalos.petbook.adapters.PetAdapter;
import com.thegalos.petbook.objects.Pet;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.ibrahimsn.lib.SmoothBottomBar;


public class Profile extends Fragment {
    private static List<Pet> petList = new ArrayList<>();
    SharedPreferences sp;
    RecyclerView recyclerView;
    PetAdapter petAdapter;
    Boolean downloadedPets;
    FirebaseUser user;
    TextView tvTotalPets;
    String petCount;
    TextView tvMemberSince;
    TextView tvUserName;
    FragmentManager manager;



    public Profile() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view,  Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        downloadedPets = sp.getBoolean("downloadedPets" , false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        tvUserName = view.findViewById(R.id.tvUserName);
        tvTotalPets = view.findViewById(R.id.tvTotalPets);
        tvMemberSince = view.findViewById(R.id.tvMemberSince);
        final Button btnLogout = view.findViewById(R.id.btnLogout);
        final FloatingActionButton btnAddPet = view.findViewById(R.id.btnAddPet);

        manager = getParentFragmentManager();
        if (user != null) {
            tvUserName.setText(user.getDisplayName());
            Date date = new Date(sp.getLong("MemberSince",0));
            SimpleDateFormat sfd = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String text = sfd.format(date);
            tvMemberSince.setText(text);
        }
        recyclerView = view.findViewById(R.id.rvCards);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            loadData();

        // Change to add pet fragment


        btnAddPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.flFragment, new AddPet(), "AddPet").addToBackStack("AddPet").commit();
                } else
                    Toast.makeText(getContext(), "Please Sign in", Toast.LENGTH_SHORT).show();
            }
        });

        // log out from app
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    tvUserName.setText(getString(R.string.details_would_show_once_signed_in));
                    tvTotalPets.setText("");
                    tvMemberSince.setText("");
                    petList.clear();
//                    petAdapter.notifyDataSetChanged();
                    FirebaseAuth.getInstance().signOut();

                    Snackbar snackbar = Snackbar.make(view, R.string.disconnected_from_petbook, Snackbar.LENGTH_SHORT);
                    snackbar.setAnchorView(R.id.bottomBar);
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.flFragment, new MainFeed(), "MainFeed").commit();
                    SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);
                    smoothBottomBar.setItemActiveIndex(0);
                    snackbar.show();
                    sp.edit().clear().apply();

                } else
                    Toast.makeText(getContext(), "Must be logged in order to logout", Toast.LENGTH_SHORT).show();
            }
        });

        //Load User Details

        //load pets - if first time from Firebase else from shared prefs
        if (downloadedPets)
            loadLocalData();
        else
            loadFirebaseData();
    }

    private void loadFirebaseData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Pets");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        petList.clear();
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Pet pet = new Pet();
                            pet.setAge(snapshot.child("age").getValue(String.class));
                            pet.setAnimalType(snapshot.child("animalType").getValue(String.class));
                            pet.setBreed(snapshot.child("breed").getValue(String.class));
                            pet.setGender(snapshot.child("gender").getValue(String.class));
                            pet.setName(snapshot.child("name").getValue(String.class));
                            pet.setPureBred(snapshot.child("pureBred").getValue(Boolean.class));
                            pet.setVaccine(snapshot.child("vaccine").getValue(Boolean.class));
                            pet.setPetUID(snapshot.getKey());
                            petList.add(pet);
                        }
//                        petAdapter = new PetAdapter(manager, getContext(), petList);
//                        recyclerView.setAdapter(petAdapter);
//                        Collections.reverse(petList);
//                        petAdapter.notifyDataSetChanged();
//                        petCount = "Pets: " + petList.size();
//                        tvTotalPets.setText(petCount);
//                        sp.edit().putBoolean("downloadedPets", true).apply();
                        setAdapter(false);
//                        saveLocaly();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.w("firebase", "onCancelled", databaseError.toException());
                }
            });
        }
        Toast.makeText(getContext(), "Loaded from Firebase", Toast.LENGTH_SHORT).show();
    }

    private void saveLocaly() {
        SharedPreferences.Editor editor = sp.edit();
        Gson gson = new Gson();
        String json = gson.toJson(petList);
        editor.putString("PetList", json);
        editor.apply();
    }

    private void loadLocalData() {
        Gson gson = new Gson();
        String json = sp.getString("PetList", "");
        Type type = new TypeToken<ArrayList<Pet>>() {
        }.getType();
        petList = gson.fromJson(json, type);

        if (petList == null) {
            petList = new ArrayList<>();
        }
//        petAdapter = new PetAdapter(manager, getContext(), petList);
//        recyclerView.setAdapter(petAdapter);
//        petAdapter.notifyDataSetChanged();
//        petCount = "Pets: " + petList.size();
//        tvTotalPets.setText(petCount);
//        Toast.makeText(getContext(), "Loaded from Shared Prefs", Toast.LENGTH_SHORT).show();
        setAdapter(true);
    }

    private void setAdapter(boolean b) {
        petAdapter = new PetAdapter(manager, getContext(), petList);
        recyclerView.setAdapter(petAdapter);
        if (!b){
            Collections.reverse(petList);
            Toast.makeText(getContext(), "Loaded from FireBase", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sp.edit();
            Gson gson = new Gson();
            String json = gson.toJson(petList);
            editor.putBoolean("downloadedPets", true);
            editor.putString("PetList", json);
            editor.apply();
        } else
            Toast.makeText(getContext(), "Loaded from Shared Prefs", Toast.LENGTH_SHORT).show();
        petAdapter.notifyDataSetChanged();
        petCount = "Pets: " + petList.size();
        tvTotalPets.setText(petCount);

        petAdapter.setListener(new PetAdapter.myPetsListener() {
            @Override
            public void onCardLongClicked(int position) {
                Toast.makeText(getContext(), "Long clicked position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

    }


//    petAdapter = new PetAdapter(manager, getContext(), petList);
//        recyclerView.setAdapter(petAdapter);
//        petAdapter.notifyDataSetChanged();
//    petCount = "Pets: " + petList.size();
//        tvTotalPets.setText(petCount);
//        Toast.makeText(getContext(), "Loaded from Shared Prefs", Toast.LENGTH_SHORT).show();

    //    petAdapter = new PetAdapter(manager, getContext(), petList);
//                        recyclerView.setAdapter(petAdapter);
//                        Collections.reverse(petList);
//                        petAdapter.notifyDataSetChanged();
//    petCount = "Pets: " + petList.size();
//                        tvTotalPets.setText(petCount);

}

