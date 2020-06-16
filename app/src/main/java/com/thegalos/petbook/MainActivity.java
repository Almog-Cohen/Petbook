package com.thegalos.petbook;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.thegalos.petbook.Fragments.AppFeed;
import com.thegalos.petbook.Fragments.Chats;
import com.thegalos.petbook.Fragments.MyPets;
import com.thegalos.petbook.Fragments.Splash;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeLayout;
    CoordinatorLayout coordinatorLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.flFragment, new Splash()).commit();

        swipeLayout = findViewById(R.id.swipedLayout);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                layoutRefresh();
                swipeLayout.setRefreshing(false);
            }
        });

        SmoothBottomBar smoothBottomBar = findViewById(R.id.bottomBar);
        smoothBottomBar.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int position) {
                switch (position) {
                    case 0:
                        handleFragment(new AppFeed());
                        break;
                    case 1:
//                        handleFragment(new HomeFragment());
                        break;
                    case 2:
                        handleFragment(new Chats());
                        break;
                    case 3:
                        handleFragment(new MyPets());
                        break;

                }
                return false;
            }
        });
    }

    void handleFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, fragment);
        ft.commit();
    }

    private void layoutRefresh() {
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ft.replace(R.id.flNews, new MyPets()).commit();
        //TODO refresh only main feed and matches
    }
}