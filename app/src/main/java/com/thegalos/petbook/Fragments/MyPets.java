package com.thegalos.petbook.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.thegalos.petbook.Adapters.MyPetsAdapter;
import com.thegalos.petbook.Objects.Pet;
import com.thegalos.petbook.R;


public class MyPets extends Fragment {
    private static List<Pet> petList = new ArrayList<>();
    SharedPreferences sp;
    RecyclerView recyclerView;
    MyPetsAdapter myPetsAdapter;
    Boolean downloadedPets;
    FirebaseUser user;


    public MyPets() {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.my_pets, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view,  Bundle savedInstanceState) {
        sp = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        downloadedPets = sp.getBoolean("downloadedPets" , false);
        user = FirebaseAuth.getInstance().getCurrentUser();
        TextView tvUserName = view.findViewById(R.id.tvUserName);
        if (user != null)
            tvUserName.setText(user.getDisplayName());

        recyclerView = view.findViewById(R.id.rvCards);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            loadData();

        // Change to add pet fragment
        Button btnAddPet = view.findViewById(R.id.btnAddPet);

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
        view.findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    FirebaseAuth.getInstance().signOut();
                    Snackbar snackbar = Snackbar.make(view, R.string.disconnected_from_petbook, Snackbar.LENGTH_SHORT);
//                snackbar.setAnchorView(R.id.bottomBar);
                    snackbar.show();
                } else
                    Toast.makeText(getContext(), "Must be logged in order to logout", Toast.LENGTH_SHORT).show();
            }
        });

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
                public void onDataChange(DataSnapshot dataSnapshot) {
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
                            petList.add(pet);
                        }
                        myPetsAdapter = new MyPetsAdapter((petList));
                        recyclerView.setAdapter(myPetsAdapter);
                        Collections.reverse(petList);
                        myPetsAdapter.notifyDataSetChanged();
                        sp.edit().putBoolean("downloadedPets", true).apply();
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

    private void loadLocalData() {
        SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("PetList", null);
        Type type = new TypeToken<ArrayList<Pet>>() {
        }.getType();
        petList = gson.fromJson(json, type);

        if (petList == null) {
            petList = new ArrayList<>();
        }
        myPetsAdapter = new MyPetsAdapter((petList));
        recyclerView.setAdapter(myPetsAdapter);
        myPetsAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), "Loaded from Shared Prefs", Toast.LENGTH_SHORT).show();
    }
}

