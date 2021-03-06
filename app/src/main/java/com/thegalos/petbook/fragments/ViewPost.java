package com.thegalos.petbook.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thegalos.petbook.R;
import com.thegalos.petbook.objects.Feed;
import com.thegalos.petbook.objects.Pet;

import java.lang.reflect.Type;

import me.ibrahimsn.lib.SmoothBottomBar;

public class ViewPost extends Fragment {

    Context context;
    SharedPreferences sp;
    String userId;
    FirebaseUser user ;

    public ViewPost() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);
        smoothBottomBar.setItemActiveIndex(0);
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();
        context = getContext();
        Gson gson = new Gson();
        String json = sp.getString("PetSelected", "");
        Type type = new TypeToken<Feed>() {
        }.getType();
        if (user!= null)
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


    //  Feed //////////////////////////
        TextView amount = view.findViewById(R.id.amount);
        ImageView imageURL = view.findViewById(R.id.imageURL);
        TextView tvChatWith = view.findViewById(R.id.tvChatWith);
        TextView postText = view.findViewById(R.id.postText);
        TextView time = view.findViewById(R.id.time);

    //  Pet //////////////////////////
        TextView age = view.findViewById(R.id.age);
        ImageView animalType = view.findViewById(R.id.animalType);
        TextView breed = view.findViewById(R.id.breed);
        TextView gender = view.findViewById(R.id.gender);
        TextView petName = view.findViewById(R.id.petName);
        TextView pureBred = view.findViewById(R.id.pureBred);
        TextView vaccine = view.findViewById(R.id.vaccine);

        final Feed feed = gson.fromJson(json, type);
        Pet pet  = feed.getPet();
        String str;
        if (feed.getFree().equals("yes"))
            str = getString(R.string.free);
        else {
            if (feed.getWhoPays().equals("user"))
                str = getString(R.string.pay_space) + feed.getAmount();
            else
                str = getString(R.string.earn_space) + feed.getAmount();
        }
        amount.setText(str);
        if (!feed.getImageURL().equals("null"))
            Glide.with(context).load(feed.getImageURL()).into(imageURL);
        postText.setText(feed.getPostText());
        time.setText(feed.getTime());
        petName.setText(pet.getName());
        gender.setText(pet.getGender());
        if (pet.getVaccine()) {
            vaccine.setText(R.string.vaccinated);
            vaccine.setVisibility(View.VISIBLE);
        } if (pet.getPureBred()) {
            pureBred.setText(R.string.purebred);
            pureBred.setVisibility(View.VISIBLE);
        }
        str = getString(R.string.breed_space) + pet.getBreed();
        breed.setText(str);
        str = getString(R.string.age_space) + pet.getAge();
        age.setText(str);
        switch (pet.getAnimalType()) {
            case "Horse":
                Glide.with(context).load(R.drawable.icon_horse).into(animalType);
                break;
            case "Dog":
                Glide.with(context).load(R.drawable.icon_dog).into(animalType);
                break;
            case "Cat":
                Glide.with(context).load(R.drawable.icon_cat).into(animalType);
                break;
            default:
                Glide.with(context).load(R.drawable.missing).into(animalType);
                break;
        }
        final String ownerId = feed.getOwnerUID();

        if (ownerId.equals(userId))
            tvChatWith.setVisibility(View.INVISIBLE);


        tvChatWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Check If Chat with user Is already created, if not CREATE, change fragment,  maybe? mark as contacted
                sp.edit().putString("ownerId", ownerId).apply();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                SmoothBottomBar smoothBottomBar = getActivity().findViewById(R.id.bottomBar);
                smoothBottomBar.setItemActiveIndex(1);
                ft.replace(R.id.flFragment, new Conversation(), "Conversation").addToBackStack("Conversation").commit();

            }
        });

    }
}
