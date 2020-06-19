package com.thegalos.petbook.Fragments;

import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.thegalos.petbook.MainActivity;
import com.thegalos.petbook.R;

public class Login extends Fragment {

    private EditText etEmail, etPassword, etName;
    FirebaseAuth mAuth;
    Button btnAction;
    boolean readyToRegister = false;

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
                                        SharedPreferences preferences = getContext().getSharedPreferences("pref", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
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

    private void signInTransaction() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, new AppFeed(), "main_fragment").commit();
    }
}
