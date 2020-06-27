package com.thegalos.petbook.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thegalos.petbook.MessageActivity;
import com.thegalos.petbook.R;
import com.thegalos.petbook.objects.Feed;
import com.thegalos.petbook.objects.Pet;

import java.lang.reflect.Type;

public class ViewPost2 extends Fragment {

    Context context;
    SharedPreferences sp;
    Pet pet;
    String str;

    public ViewPost2() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_feed2, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        context = getContext();
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        Gson gson = new Gson();
        String json = sp.getString("PetSelected", "");
        Type type = new TypeToken<Feed>() {
        }.getType();

        Button btnChat = view.findViewById(R.id.btnChat2);

//  Feed //////////////////////////
        TextView amount = view.findViewById(R.id.amount2);
        ImageView imageURL = view.findViewById(R.id.imageURL2);
        TextView ownerName = view.findViewById(R.id.ownerName2);
        TextView postText = view.findViewById(R.id.postText2);
        TextView time = view.findViewById(R.id.time2);

//  Pet //////////////////////////
        TextView age = view.findViewById(R.id.age2);
        ImageView animalType = view.findViewById(R.id.animalType2);
        TextView breed = view.findViewById(R.id.breed2);
        TextView gender = view.findViewById(R.id.gender2);
        TextView petName = view.findViewById(R.id.petName2);
        TextView pureBred = view.findViewById(R.id.pureBred2);
        TextView vaccine = view.findViewById(R.id.vaccine2);

        final Feed feed = gson.fromJson(json, type);
        pet  = feed.getPet();
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
        str = getString(R.string.owned_by_space) + feed.getPostOwner();
        ownerName.setText(str);



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
                str = getString(R.string.horse);
                break;
            case "Dog":
                Glide.with(context).load(R.drawable.icon_dog).into(animalType);
                str = getString(R.string.dog);
                break;
            case "Cat":
                Glide.with(context).load(R.drawable.icon_cat).into(animalType);
                str = getString(R.string.cat);
                break;
            default:
                Glide.with(context).load(R.drawable.missing).into(animalType);
                break;
        }

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Check If Chat with user Is already created, if not CREATE, change fragment,  maybe? mark as contacted
                String ownerId = feed.getOwnerUID();
                /*FirebaseDatabase.getInstance().getReference("Messages").child(ownerId).child(userId).setValue("null");*/

                sp.edit().putString("ownerId", ownerId).apply();
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.replace(R.id.flFragment, new MessageActivity(), "MessageActivity").addToBackStack("MessageActivity").commit();
            }
        });



        //Show label when uncollapsing
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsingToolbar);
        AppBarLayout appBarLayout = (AppBarLayout) view.findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
                    collapsingToolbarLayout.setTitle(str + pet.getName());//careful there should a space between double quote otherwise it wont work
                    isShow = false;
                }
            }
        });
    }
}