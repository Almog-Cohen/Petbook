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
<<<<<<< HEAD
    private Context context;
    private List<Chat> chatList;

    public MessageAdapter(Context context, List<Chat> chatList) {
        this.chatList = chatList;
        this.context = context;
=======

    FirebaseUser firebaseUser;

    private Context context;
    private List<Chat> chatList;

    public MessageAdapter(Context context, List<Chat> chatList){

        this.chatList = chatList;
        this.context=context;
>>>>>>> galos 24/06 fix almog problems
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
<<<<<<< HEAD
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        }
=======
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        }

>>>>>>> galos 24/06 fix almog problems
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
<<<<<<< HEAD
        Chat chat = chatList.get(position);
        holder.showMessageTv.setText(chat.getMessage());
    }

=======

        Chat chat = chatList.get(position);
        holder.showMessage.setText(chat.getMessage());

        //TODO If defult use this if not use glide
        /*holder.profileImage.setImageResource(R.mipmap.ic_launcher);*/
    }



>>>>>>> galos 24/06 fix almog problems
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
<<<<<<< HEAD
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatList.get(position).getSender().equals(firebaseUser.getUid()))
            return MSG_TYPE_RIGHT;
        else
            return MSG_TYPE_LEFT;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView showMessageTv;
=======

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView showMessage;
>>>>>>> galos 24/06 fix almog problems
        public ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
<<<<<<< HEAD
            showMessageTv = itemView.findViewById(R.id.tvChatMessage);
        }
=======

            showMessage = itemView.findViewById(R.id.show_message);
        }

>>>>>>> galos 24/06 fix almog problems
    }
}
