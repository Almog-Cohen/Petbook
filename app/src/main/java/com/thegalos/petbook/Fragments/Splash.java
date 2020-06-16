package com.thegalos.petbook.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thegalos.petbook.R;

public class Splash extends Fragment {


    public Splash() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.splash, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MotionLayout motionLayout;
        motionLayout = view.findViewById(R.id.splashLayout);
        motionLayout.setTransition(R.id.Splash_start, R.id.Splash_end);
        motionLayout.transitionToEnd();
        motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) { }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) { }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) { signInTransaction(); }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) { }
        });
 }

    private void signInTransaction() {
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.replace(R.id.flFragment, new AppFeed(), "main_fragment");
        ft.commit();
    }
}