package com.thegalos.petbook.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.thegalos.petbook.MessageActivity;
import com.thegalos.petbook.R;

import java.util.List;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder>  {

    private Context context;
    private List<String> usersIdList;
    private List<String>userNamesList;


    public UserAdapter(@NonNull Context context, List<String> usersIdList,List<String> userNamesList) {
        this.context=context;
        this.usersIdList = usersIdList;
        this.userNamesList = userNamesList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String userId = usersIdList.get(position);
        String userName = userNamesList.get(position);

        holder.userNameTv.setText(userName);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putString("ownerId", ownerId).apply();

            }
        });

    }

    @Override
    public int getItemCount() {
        return usersIdList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userNameTv;
        public ImageView profileImage;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTv=itemView.findViewById(R.id.user_name);
            profileImage=itemView.findViewById(R.id.profile_image);

        }




    }
}
