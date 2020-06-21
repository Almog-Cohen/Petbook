package com.thegalos.petbook.fragments;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.thegalos.petbook.R;
import com.thegalos.petbook.adapters.MyPetsAdapter;
import com.thegalos.petbook.objects.Pet;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Login extends Fragment {

    private EditText etEmail, etPassword, etName;
    FirebaseAuth mAuth;
    Button btnAction;
    boolean readyToRegister = false;
    SharedPreferences sp;


    public Login() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        etName = view.findViewById(R.id.etName);
        etName.setVisibility(View.INVISIBLE);
        final MotionLayout motionLayout;
        motionLayout = view.findViewById(R.id.motionLogin);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());


        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        btnAction = view.findViewById(R.id.btnAction);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                    Toast.makeText(getContext(), "Not a valid Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (etPassword.getText().toString().length() < 6) {
                    Toast.makeText(getContext(), "Min pass length is 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                if (!readyToRegister) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("auth", "signInWithCredential:success");
                                signInTransaction();
                                loadFirebaseDB();
                            } else {
                                Log.d("auth", "signInWithCredential:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    Toast.makeText(getContext(), "No such user, please register", Toast.LENGTH_SHORT).show();
                                    motionLayout.setTransition(R.id.end, R.id.login_name);
                                    motionLayout.transitionToEnd();
                                    btnAction.setText(getString(R.string.register));
                                    readyToRegister = true;
                                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                    Toast.makeText(getContext(), "Wrong Code Entered", Toast.LENGTH_SHORT).show();
                                // The verification code entered was invalid
                            }
                        }
                    });
                } else {
                    if (!etName.getText().toString().equals("")) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("auth", "user created successfully");
                                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null) {
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Details").child("Name").setValue(etName.getText().toString());
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Details").child("Email").setValue(etEmail.getText().toString());
                                        FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Details").child("MemberSince").setValue(ServerValue.TIMESTAMP);
                                        SharedPreferences.Editor editor = sp.edit();
                                        editor.putString("Name", etName.getText().toString());
                                        editor.putString("Email", etEmail.getText().toString());
                                        editor.apply();
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(etName.getText().toString())
                                                .build();

                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("firebase", "User profile updated.");
                                                            signInTransaction();
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        });
                    } else
                        Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    private void loadFirebaseDB() {
        boolean downloadedPets = sp.getBoolean("downloadedPets" , false);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Pets");
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        List<Pet> petList = new ArrayList<>();
                        for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Pet pet = snapshot.getValue(Pet.class);
//                            pet.setAge(snapshot.child("age").getValue(String.class));
//                            pet.setAnimalType(snapshot.child("animalType").getValue(String.class));
//                            pet.setBreed(snapshot.child("breed").getValue(String.class));
//                            pet.setGender(snapshot.child("gender").getValue(String.class));
//                            pet.setName(snapshot.child("name").getValue(String.class));
//                            pet.setPureBred(snapshot.child("pureBred").getValue(Boolean.class));
//                            pet.setVaccine(snapshot.child("vaccine").getValue(Boolean.class));
//                            pet.setPetUID(snapshot.getKey());
                            petList.add(pet);
                        }
                        Collections.reverse(petList);

                        Gson gson = new Gson();
                        String json = gson.toJson(petList);
                        sp.edit().putString("PetList", json).apply();
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


    private void signInTransaction() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, new MainFeed(), "main_fragment").commit();

    }
}
