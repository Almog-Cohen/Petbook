package com.thegalos.petbook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thegalos.petbook.R;
import com.thegalos.petbook.objects.Chat;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_TYPE_LEFT=0;
    public static final int MSG_TYPE_RIGHT=1;

    FirebaseUser firebaseUser;

    private Context context;
    private List<Chat> chatList;

    public MessageAdapter(Context context, List<Chat> chatList) {

        this.chatList = chatList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = chatList.get(position);
        holder.showMessage.setText(chat.getMessage());

        //TODO If defult use this if not use glide
        /*holder.profileImage.setImageResource(R.mipmap.ic_launcher);*/
    }



    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chatList.get(position).getSender().equals(firebaseUser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView showMessage;
        public ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            showMessage = itemView.findViewById(R.id.tvChatMessage);
        }

    }
}
