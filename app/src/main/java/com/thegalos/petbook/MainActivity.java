package com.thegalos.petbook;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.thegalos.petbook.fragments.Chats;
import com.thegalos.petbook.fragments.MainFeed;
import com.thegalos.petbook.fragments.Profile;
import com.thegalos.petbook.fragments.Splash;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SmoothBottomBar smoothBottomBar = findViewById(R.id.bottomBar);
        smoothBottomBar.setVisibility(View.INVISIBLE);
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, new Splash(),"splash").commit();
        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int position) {
                switch (position) {
                    case 0:
                        handleFragment(new MainFeed(),"MainFeed");
                        break;
                    case 1:
                        if (FirebaseAuth.getInstance().getCurrentUser() != null)
                            handleFragment(new Chats(), "Chats");
                        else {
                            Toast.makeText(MainActivity.this, "Sign in First", Toast.LENGTH_SHORT).show();
                            smoothBottomBar.setItemActiveIndex(0);
                        }
                        break;
                    case 2:
                        if (FirebaseAuth.getInstance().getCurrentUser() != null)
                            handleFragment(new Profile(), "Profile");
                        else {
                            Toast.makeText(MainActivity.this, "Sign in First", Toast.LENGTH_SHORT).show();
                            smoothBottomBar.setItemActiveIndex(0);
                        }
                }
                return false;
            }
        });
    }

    void snakeBar(){
//        View parentLayout = findViewById(android.R.id.content);
//        Snackbar.make(parentLayout, "This is main activity", Snackbar.LENGTH_LONG).show();
//        Snackbar snackbar = Snackbar.make(, R.string.disconnected_from_petbook, Snackbar.LENGTH_SHORT);
//        snackbar.show();

    }
    void handleFragment(Fragment fragment, String mainFeed) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, fragment, mainFeed);
        ft.commit();
    }
}