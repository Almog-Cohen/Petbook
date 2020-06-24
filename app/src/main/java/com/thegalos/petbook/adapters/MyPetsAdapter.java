package com.thegalos.petbook.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thegalos.petbook.fragments.AddPet;
import com.thegalos.petbook.fragments.Profile;
import com.thegalos.petbook.objects.Pet;
import com.thegalos.petbook.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class MyPetsAdapter extends RecyclerView.Adapter<MyPetsAdapter.MyPetsViewHolder> {
    private final FragmentManager manager;
    private final Context context;
    private List<Pet> petList;
    private myPetsListener listener;

    public interface myPetsListener {
        void onCardLongClicked(int position);
    }

    public void setListener(myPetsListener listener){
        this.listener = listener;
    }

    public MyPetsAdapter(FragmentManager manager, Context context, List<Pet> petList) {
        this.manager = manager;
        this.context = context;
        this.petList = petList;
    }

    public class MyPetsViewHolder extends RecyclerView.ViewHolder{

        final TextView tvPetName;
        final TextView tvAnimalType;
        final TextView tvAge;
        final TextView tvGender;
        final TextView tvBreed;
        final ImageView ivPetType;
        final ImageView ivDelete;
        final CheckedTextView cbVaccine;
        final CheckedTextView cbPureBred;


        MyPetsViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPetName = itemView.findViewById(R.id.tvPetName);
            tvAnimalType = itemView.findViewById(R.id.tvAnimalType);
            tvAge =  itemView.findViewById(R.id.tvAge);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvBreed = itemView.findViewById(R.id.tvBreed);
            ivPetType =  itemView.findViewById(R.id.ivPetType);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            cbPureBred = itemView.findViewById(R.id.cbPureBred);
            cbVaccine = itemView.findViewById(R.id.cbVaccine);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (listener != null)
                        listener.onCardLongClicked(getAdapterPosition());
//                    Toast.makeText(context, "Long clicked position: " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    ivDelete.setVisibility(View.VISIBLE);
                    return true;
                }
            });
        }
    }

    @NonNull
    @Override
    public MyPetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_pet,parent,false);
        return new MyPetsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyPetsViewHolder holder, final int position) {
        final Pet pet = petList.get(position);
        String str = "";
        holder.tvPetName.setText(pet.getName());
        holder.tvAnimalType.setText(pet.getAnimalType());
        str = context.getString(R.string.age_space) + pet.getAge();
        holder.tvAge.setText(str);
        holder.tvGender.setText(pet.getGender());
        holder.tvBreed.setText(pet.getBreed());
        if (pet.getVaccine()){
            holder.cbVaccine.setChecked(true);
            holder.cbVaccine.setCheckMarkDrawable(R.drawable.vector_check);
        }
        if (pet.getPureBred()){
            holder.cbPureBred.setChecked(true);
            holder.cbPureBred.setCheckMarkDrawable(R.drawable.vector_check);
        }

//        if (pet.getCurrentImagePath() == null)
//            Glide.with(holder.ivPetType.getContext()).load(R.drawable.missing).into(holder.ivPetType);
//        else {

        switch (pet.getAnimalType()) {
            case "Horse":
                Glide.with(holder.ivPetType.getContext()).load(R.drawable.icon_horse).into(holder.ivPetType);
                break;
            case "Dog":
                Glide.with(holder.ivPetType.getContext()).load(R.drawable.icon_dog).into(holder.ivPetType);
                break;
            case "Cat":
                Glide.with(holder.ivPetType.getContext()).load(R.drawable.icon_cat).into(holder.ivPetType);
                break;
            default:
                Glide.with(holder.ivPetType.getContext()).load(R.drawable.missing).into(holder.ivPetType);
                break;
        }

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context); //alert for confirm to delete
                builder.setIcon(context.getResources().getDrawable(android.R.drawable.ic_dialog_alert));
                builder.setTitle(R.string.remove_pet);
                builder.setMessage(R.string.related_post_would_be_removed);    //set message
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() { //when click on DELETE
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removePet(position, pet.getPetUID());
                    }
                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        holder.ivDelete.setVisibility(View.INVISIBLE);
                    }
                }).show();  //show alert dialog
            }
        });

    }

    private void removePet(int position, final String petUID) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Gson gson = new Gson();
        String json = sp.getString("PetList", "");
        Type type = new TypeToken<ArrayList<Pet>>() {
        }.getType();
        petList = gson.fromJson(json, type);
        petList.remove(position);
        SharedPreferences.Editor editor = sp.edit();

        json = gson.toJson(petList);
        editor.putString("PetList", json);
        editor.apply();

        Toast.makeText(context, "removed from prefs", Toast.LENGTH_SHORT).show();
        manager.beginTransaction().replace(R.id.flFragment, new Profile(), "Profile").commit();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Pets").child(petUID);
            databaseReference.removeValue();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
            final DatabaseReference finalDatabaseReference = databaseReference;
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            if (snapshot.child("Pet").child("petUID").getValue().equals(petUID)) {
                                String str = snapshot.getKey();
                                finalDatabaseReference.child(str).removeValue();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }


    @Override
    public int getItemCount() {
        return petList.size();
    }
}