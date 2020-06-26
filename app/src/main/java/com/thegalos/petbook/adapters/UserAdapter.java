package com.thegalos.petbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thegalos.petbook.R;

import java.util.List;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder>  {

    private Context context;
    private List<String>userNamesList;
    private MyUserListener myUserListener;

    public interface MyUserListener {
        void onUserClicked(int position,View view);
    }


    public void setListener(MyUserListener myUserListener){
        this.myUserListener=myUserListener;
    }



    public UserAdapter(@NonNull Context context,List<String> userNamesList) {
        this.context=context;

        this.userNamesList = userNamesList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_chats,parent,false);
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        String userName = userNamesList.get(position);

        holder.userNameTv.setText(userName);






    }

    @Override
    public int getItemCount() {
        return userNamesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userNameTv;
        public ImageView profileImage;
        private TextView lastMsg;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTv = itemView.findViewById(R.id.user_name);
            profileImage = itemView.findViewById(R.id.profile_image);
            lastMsg = itemView.findViewById(R.id.last_msg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (myUserListener!=null){
                        myUserListener.onUserClicked(getAdapterPosition(),v);
                    }
                }
            });

        }

    }

    private  void lastMessage(String userId , TextView last_msg){

    }
}
