package com.thegalos.petbook;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.thegalos.petbook.fragments.Chats;
import com.thegalos.petbook.fragments.Conversation;
import com.thegalos.petbook.fragments.MainFeed;
import com.thegalos.petbook.fragments.Profile;
import com.thegalos.petbook.fragments.Splash;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SmoothBottomBar smoothBottomBar = findViewById(R.id.bottomBar);
        smoothBottomBar.setVisibility(View.INVISIBLE);
        //When clicking on chat notification moving to conversation fragment
        boolean msgFragment = getIntent().getBooleanExtra("msg_notif",false);
        if (msgFragment) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.flFragment, new Chats(), "Chats").addToBackStack("Chats").commit();
        }else
        getSupportFragmentManager().beginTransaction().add(R.id.mainLayout, new Splash(),"splash").commit();
        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int position) {
                switch (position) {
                    case 0:
                        handleFragment(new MainFeed(),"MainFeed");
                        getSupportFragmentManager().popBackStack();
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
                        getSupportFragmentManager().popBackStack();
                        break;
                    case 2:
                        handleFragment(new Profile(), "Profile");
                        getSupportFragmentManager().popBackStack();
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

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentByTag("AddFeed") != null
                && getSupportFragmentManager().findFragmentByTag("AddFeed").isVisible())
            getSupportFragmentManager().popBackStack();

        else if (getSupportFragmentManager().findFragmentByTag("AddPet") != null
                && getSupportFragmentManager().findFragmentByTag("AddPet").isVisible())
            getSupportFragmentManager().popBackStack();

        else if (getSupportFragmentManager().findFragmentByTag("Login") != null
                && getSupportFragmentManager().findFragmentByTag("Login").isVisible())
            getSupportFragmentManager().popBackStack();

        else if (getSupportFragmentManager().findFragmentByTag("ViewPost") != null
                && getSupportFragmentManager().findFragmentByTag("ViewPost").isVisible())
            getSupportFragmentManager().popBackStack();

        else if (getSupportFragmentManager().findFragmentByTag("Conversation") != null
                && getSupportFragmentManager().findFragmentByTag("Conversation").isVisible())
            getSupportFragmentManager().popBackStack();

        else if ((getSupportFragmentManager().findFragmentByTag("MainFeed") != null &&
                getSupportFragmentManager().findFragmentByTag("MainFeed").isVisible()) ||
                (getSupportFragmentManager().findFragmentByTag("Chats") != null &&
                        getSupportFragmentManager().findFragmentByTag("Chats").isVisible()) ||
                (getSupportFragmentManager().findFragmentByTag("Profile") != null
                        && getSupportFragmentManager().findFragmentByTag("Profile").isVisible())) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
            } else {
                doubleBackToExitPressedOnce = true;
                Toast.makeText(MainActivity.this, R.string.please_click_back_again, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);
            }
        }

    }
}
