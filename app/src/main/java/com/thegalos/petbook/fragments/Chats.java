package com.thegalos.petbook.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.thegalos.petbook.R;

public class Chats extends Fragment {

    public Chats() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.chats, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view,  Bundle savedInstanceState) {



    }
}

