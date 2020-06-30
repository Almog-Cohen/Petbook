package com.thegalos.petbook.fragments;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thegalos.petbook.objects.Pet;
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
    Context context;

    public AddPet() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_pet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view,  Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        context = getContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        final Spinner spnGender = view.findViewById(R.id.spnGender);
        final Spinner spnAge = view.findViewById(R.id.spnAge);
        etBreed = view.findViewById(R.id.etBreed);
        etName = view.findViewById(R.id.etName);
        ctvVaccine = view.findViewById(R.id.ctvVaccine);
        RadioGroup rGroup = view.findViewById(R.id.rGroup);
        ctvPurebred = view.findViewById(R.id.ctvPurebred);
        Button btnFinish = view.findViewById(R.id.btnFinish);
        final List<String> listAge = new ArrayList<>();
        final List<String> listGender = new ArrayList<>();

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


        listAge.add("");
        for (int i = 1; i <= 20; i++)
            listAge.add(i + "");

        listGender.add("");
        listGender.add(getString(R.string.male));
        listGender.add(getString(R.string.female));
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(getActivity(),  R.layout.color_spinner_layout, listGender);
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<>(getActivity(), R.layout.color_spinner_layout, listAge);
        genderAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        ageAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spnGender.setAdapter(genderAdapter);
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

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spnAge.getSelectedItem().toString().equals("")) {
                    Toast.makeText(context, R.string.must_select_age, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (spnGender.getSelectedItem().toString().equals("")) {
                    Toast.makeText(context, R.string.must_select_gender, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (animalType.equals("")) {
                    Toast.makeText(context, R.string.must_select_type, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etBreed.getText().toString().equals("")) {
                    Toast.makeText(context, R.string.must_select_breed, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etName.getText().toString().equals("")) {
                    Toast.makeText(context, R.string.must_select_name, Toast.LENGTH_SHORT).show();
                    return;
                }
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Pets").push();
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
                    ft.replace(R.id.flFragment, new Profile(), "Profile");
                    ft.commit();

                }
            }
        });
    }


    // Save data to Shared Preferences
    private void saveData() {
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(petArrayList);
        editor.putString("PetList", json);
        editor.apply();
    }

    // Load data from Shared Preferences
    private void loadData() {
        Gson gson = new Gson();
        String json = preferences.getString("PetList", null);
        Type type = new TypeToken<ArrayList<Pet>>() {
        }.getType();
        petArrayList = gson.fromJson(json, type);
        if (petArrayList == null)
            petArrayList = new ArrayList<>();
    }
}