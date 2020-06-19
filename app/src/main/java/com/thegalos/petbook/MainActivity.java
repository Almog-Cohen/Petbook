package com.thegalos.petbook;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

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
        getSupportFragmentManager().beginTransaction().add(R.id.flFragment, new Splash()).commit();

        SmoothBottomBar smoothBottomBar = findViewById(R.id.bottomBar);
        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int position) {
                switch (position) {
                    case 0:
                        handleFragment(new MainFeed(),"MainFeed");
                        break;
                    case 1:
                        handleFragment(new Chats(), "Chats");
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