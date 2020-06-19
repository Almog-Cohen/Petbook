package com.thegalos.petbook.Fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thegalos.petbook.Objects.Pet;
import com.thegalos.petbook.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AddPet extends Fragment {

    int whichAnimal = 0;
    String animalType = "";
    EditText etBreed, etName;
    CheckedTextView ctvVaccine, ctvPurebred;
    private static List<Pet> petArrayList = new ArrayList<>();
    SharedPreferences preferences;

    public AddPet() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pet_add, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view,  Bundle savedInstanceState) {
        preferences = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
        final Spinner spnGender = view.findViewById(R.id.spnGender);
        final Spinner spnAge = view.findViewById(R.id.spnAge);
        etBreed = view.findViewById(R.id.etBreed);
        etName = view.findViewById(R.id.etName);
        ctvVaccine = view.findViewById(R.id.ctvVaccine);
        ctvVaccine.setChecked(false);
        ctvVaccine.setCheckMarkDrawable(null);
        ctvVaccine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctvVaccine.isChecked()) {
                    ctvVaccine.setChecked(false);
                    ctvVaccine.setCheckMarkDrawable(null);
                } else {
                    ctvVaccine.setChecked(true);
                    ctvVaccine.setCheckMarkDrawable(R.drawable.vector_check);
                }
            }
        });

        ctvPurebred = view.findViewById(R.id.ctvPurebred);
        ctvPurebred.setChecked(false);
        ctvPurebred.setCheckMarkDrawable(null);
        ctvPurebred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ctvPurebred.isChecked()) {
                    ctvPurebred.setChecked(false);
                    ctvPurebred.setCheckMarkDrawable(null);

                } else {
                    ctvPurebred.setChecked(true);
                    ctvPurebred.setCheckMarkDrawable(R.drawable.vector_check);

                }
            }
        });

        final List<String> listAge = new ArrayList<>();
        final List<String> listGender = new ArrayList<>();
        for (int i = 0; i <= 20; i++) {
            listAge.add(i + "");
        }
        listGender.add(getString(R.string.male));
        listGender.add(getString(R.string.female));
        RadioGroup rGroup = view.findViewById(R.id.rGroup);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, listGender);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnGender.setAdapter(genderAdapter);
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, listAge);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAge.setAdapter(ageAdapter);


        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbHorse) {
                    whichAnimal = 1;
                    animalType = "Horse";
                } else if (checkedId == R.id.rbDog) {
                    whichAnimal = 2;
                    animalType = "Dog";
                } else if (checkedId == R.id.rbCat) {
                    whichAnimal = 3;
                    animalType = "Cat";
                } else {
                    whichAnimal = 0;
                    animalType = "";
                }
            }
        });

        Button btnFinish = view.findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (spnAge.getSelectedItem().toString().equals("0")) {
                    Toast.makeText(getContext(), "Age must not be 0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (animalType.equals("")) {
                    Toast.makeText(getContext(), "Must select animal type", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etBreed.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Breed must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etName.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Name must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Pets").push();
                    Toast.makeText(getContext(), "post sent", Toast.LENGTH_SHORT).show();
                    //TODO if we add option to update name after signup we need to use getUID and make sure to load correct name in fragments
//                    db.child("Owner").setValue(user.getUid());
                    Pet pet = new Pet();
                    pet.setAge(spnAge.getSelectedItem().toString());
                    pet.setGender(spnGender.getSelectedItem().toString());
                    pet.setAnimalType(animalType);
                    pet.setBreed(etBreed.getText().toString());
                    pet.setName(etName.getText().toString());
                    pet.setVaccine(ctvVaccine.isChecked());
                    pet.setPureBred(ctvPurebred.isChecked());
                    pet.setPetUID(db.getKey());
                    db.setValue(pet);
                    loadData();
                    petArrayList.add(pet);
                    saveData();
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.flFragment, new MyPets());
                    ft.commit();

                }
            }
        });
    }


    // save data
    private void saveData() {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(petArrayList);
        editor.putString("PetList", json);
        editor.apply();
    }

    // load data
    private void loadData() {
        Gson gson = new Gson();
        String json = preferences.getString("PetList", null);
        Type type = new TypeToken<ArrayList<Pet>>() {
        }.getType();
        petArrayList = gson.fromJson(json, type);

        if (petArrayList == null) {
            petArrayList = new ArrayList<>();
        }
    }
}