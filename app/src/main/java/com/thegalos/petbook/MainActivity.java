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
                            Snackbar snackbar = Snackbar.make(smoothBottomBar, R.string.sign_in_first, Snackbar.LENGTH_SHORT);
                            snackbar.setAnchorView(R.id.bottomBar);
                            snackbar.show();
                            smoothBottomBar.setItemActiveIndex(0);
                        }
                        break;
                    case 2:
                        handleFragment(new Profile(), "Profile");
                        break;
                }
                return false;
            }
        });
    }

    void handleFragment(Fragment fragment, String mainFeed) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, fragment, mainFeed);
        ft.commit();
    }
}