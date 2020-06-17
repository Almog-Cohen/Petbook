package com.thegalos.petbook.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thegalos.petbook.Objects.Pet;
import com.thegalos.petbook.R;

import java.util.List;


public class MyPetsAdapter extends RecyclerView.Adapter<MyPetsAdapter.MyPetsViewHolder> {

    private final List<Pet> petList;
    private myPetsListener listener;

    interface myPetsListener {
        void onClickListener(int position);
        void onCardLongClicked();
    }

    public void setListener(myPetsListener listener){
        this.listener = listener;
    }

    public MyPetsAdapter(List<Pet> petList) {
        this.petList = petList;
    }

    public class MyPetsViewHolder extends RecyclerView.ViewHolder{

        final TextView tvPetName;
        final TextView tvAnimalType;
        final TextView tvAge;
        final TextView tvGender;
        final TextView tvBreed;
        final ImageView ivPetType;
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
            cbPureBred = itemView.findViewById(R.id.cbPureBred);
            cbVaccine = itemView.findViewById(R.id.cbVaccine);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null){
                        listener.onClickListener(getAdapterPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(listener != null)
                        listener.onCardLongClicked();
                    return true;
                }
            });
        }
    }

    @NonNull
    @Override
    public MyPetsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pet_card,parent,false);
        return new MyPetsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyPetsViewHolder holder, int position) {
        Pet pet = petList.get(position);
        holder.tvPetName.setText(pet.getName());
        holder.tvAnimalType.setText(pet.getAnimalType());
        holder.tvAge.setText(pet.getAge());
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

        if (pet.getCurrentImagePath() == null)
//            Glide.with(holder.ivPetType.getContext()).load(R.drawable.missing).into(holder.ivPetType);
//        else {
            if (pet.getAnimalType().equals("Horse"))
                Glide.with(holder.ivPetType.getContext()).load(R.drawable.icon_horse).into(holder.ivPetType);

            else if (pet.getAnimalType().equals("Dog"))
                Glide.with(holder.ivPetType.getContext()).load(R.drawable.icon_dog).into(holder.ivPetType);

            else if (pet.getAnimalType().equals("Cat"))
                Glide.with(holder.ivPetType.getContext()).load(R.drawable.icon_cat).into(holder.ivPetType);
            else
                Glide.with(holder.ivPetType.getContext()).load(R.drawable.missing).into(holder.ivPetType);
    }


    @Override
    public int getItemCount() {
        return petList.size();
    }
}