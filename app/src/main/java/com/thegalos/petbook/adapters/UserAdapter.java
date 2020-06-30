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
import com.thegalos.petbook.objects.User;

import java.util.List;

public class UserAdapter  extends RecyclerView.Adapter<UserAdapter.ViewHolder>  {

    private Context context;
//    private List<String>userNamesList;
    private List <User> userList;
    private MyUserListener myUserListener;
//    private List<String>lastMessage;

    public interface MyUserListener {
        void onUserClicked(int position,View view);
    }


    public void setListener(MyUserListener myUserListener) {
        this.myUserListener=myUserListener;
    }



    public UserAdapter(@NonNull Context context,List<User> userList ) {

        this.context=context;
//        this.userNamesList = userNamesList;
        this.userList = userList ;
//        this.lastMessage = lastMessage;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_chats,parent,false);
        return new UserAdapter.ViewHolder(view);

    }
;
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {




//        User user = userList.get(position);

        holder.userNameTv.setText(userList.get(position).getUserName());
        holder.lastMesssageTv.setText(userList.get(position).getLastMessage());






    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView userNameTv;
        public ImageView profileImage;
        private TextView lastMesssageTv;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userNameTv = itemView.findViewById(R.id.user_name);
            profileImage = itemView.findViewById(R.id.profile_image);
            lastMesssageTv = itemView.findViewById(R.id.last_msg);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (myUserListener!= null)
                        myUserListener.onUserClicked(getAdapterPosition(),v);
                }
            });

        }

    }

    private  void lastMessage(String userId , TextView last_msg) {

    }
}
