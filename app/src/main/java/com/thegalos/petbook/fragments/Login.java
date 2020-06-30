package com.thegalos.petbook.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.thegalos.petbook.R;
import com.thegalos.petbook.objects.Pet;
import com.thegalos.petbook.objects.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import me.ibrahimsn.lib.SmoothBottomBar;

public class Login extends Fragment {

    private EditText etEmail, etPassword, etName;
    FirebaseAuth mAuth;
    Button btnAction;
    boolean readyToRegister = false;
    SharedPreferences sp;
    TextView tvResetPassword;
    private boolean isForgot = false;
    private boolean isRegister = false;
    Context context;


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

        context = getContext();
        mAuth = FirebaseAuth.getInstance();
        etName = view.findViewById(R.id.etName);
        etName.setVisibility(View.INVISIBLE);
        final MotionLayout motionLayout;
        motionLayout = view.findViewById(R.id.motionLogin);
        sp = PreferenceManager.getDefaultSharedPreferences(context);


        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        tvResetPassword = view.findViewById(R.id.tvResetPassword);
        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                motionLayout.setTransition(R.id.end, R.id.forgot);
                motionLayout.transitionToEnd();
                btnAction.setText(R.string.reset);
                isForgot = true;
            }
        });
        btnAction = view.findViewById(R.id.btnAction);

        OnBackPressedCallback callback = new OnBackPressedCallback(
                true // default to enabled
        ) {
            @Override
            public void handleOnBackPressed() {
                if (isForgot) {
                    motionLayout.setTransition(R.id.forgot, R.id.end);
                    motionLayout.transitionToEnd();
                    btnAction.setText(R.string.action_sign_in_short);
                    isForgot = false;
                } else if (isRegister) {
                    motionLayout.setTransition(R.id.login_name, R.id.end);
                    motionLayout.transitionToEnd();
                    btnAction.setText(R.string.action_sign_in_short);
                    isRegister = false;
                } else {
                    getParentFragmentManager().popBackStack();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(), // LifecycleOwner
                callback);



        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
                    Toast.makeText(context, R.string.email_is_not_valid, Toast.LENGTH_SHORT).show();
                    return;
                }
                String email = etEmail.getText().toString();

                if (isForgot) {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, R.string.password_recovery_email_sent, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    return;
                }

                if (etPassword.getText().toString().length() < 6) {
                    Toast.makeText(context, R.string.password_length_must_be_more, Toast.LENGTH_SHORT).show();
                    return;
                }

                String password = etPassword.getText().toString();

                if (!readyToRegister) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("auth", "signInWithCredential:success");
                                signInTransaction();
                                loadFirebaseDB();
                            } else {
                                Log.d("auth", "signInWithCredential:failure", task.getException());
                                if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                                    Toast.makeText(context, R.string.nu_such_user_please_register, Toast.LENGTH_SHORT).show();
                                    motionLayout.setTransition(R.id.end, R.id.login_name);
                                    motionLayout.transitionToEnd();
                                    isRegister = true;
                                    btnAction.setText(getString(R.string.register));
                                    readyToRegister = true;
                                } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                    Toast.makeText(context, R.string.wrong_code_entered, Toast.LENGTH_SHORT).show();
                                // The verification code entered was invalid
                            }
                        }
                    });
                } else {
                    if (!etName.getText().toString().equals("")) {
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
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
                        Toast.makeText(context, R.string.name_cannot_be_empty, Toast.LENGTH_SHORT).show();

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
    }


    private void signInTransaction() {



        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( getActivity(), new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                Log.e("MAGI", newToken);
                updateToken(newToken);
            }
        });


        SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);
        smoothBottomBar.setItemActiveIndex(0);
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, new MainFeed(), "MainFeed").commit();

    }

    private void updateToken(String newToken) {


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1 = new Token(newToken);
        reference.child(user.getUid()).setValue(token1);

    }

}
