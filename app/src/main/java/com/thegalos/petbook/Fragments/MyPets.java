package com.thegalos.petbook.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thegalos.petbook.Adapters.MyPetsAdapter;
import com.thegalos.petbook.Objects.Pet;
import com.thegalos.petbook.R;


public class MyPets extends Fragment {
    private static List<Pet> petList = new ArrayList<>();

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
            RecyclerView recyclerView = view.findViewById(R.id.rvCards);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//            loadData();
            view.findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Snackbar snackbar = Snackbar.make(view, R.string.disconnected_from_petbook, Snackbar.LENGTH_SHORT);
//                snackbar.setAnchorView(R.id.bottomBar);
                    snackbar.show();
                }
            });
            MyPetsAdapter cardAdapter = new MyPetsAdapter((petList));
            recyclerView.setAdapter(cardAdapter);
            Button btnAddPet = view.findViewById(R.id.btnAddPet);
            btnAddPet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.flFragment, new AddPet(), "AddPet").addToBackStack("AddPet").commit();
                }
            });

        }
    private void loadData() {
        SharedPreferences preferences = this.getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("PetList", null);
        Type type = new TypeToken<ArrayList<Pet>>() {
        }.getType();
        petList = gson.fromJson(json, type);

        if (petList == null) {
            petList = new ArrayList<>();
        }
    }
}

