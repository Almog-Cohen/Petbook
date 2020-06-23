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
<<<<<<< HEAD
    private Context context;
    private List<Chat> chatList;

    public MessageAdapter(Context context, List<Chat> chatList) {
        this.chatList = chatList;
        this.context = context;
=======
=======
>>>>>>> mog 24.6

    FirebaseUser firebaseUser;

    private Context context;
    private List<Chat> chatList;

<<<<<<< HEAD
    public MessageAdapter(Context context, List<Chat> chatList){

        this.chatList = chatList;
        this.context=context;
>>>>>>> galos 24/06 fix almog problems
=======
    public MessageAdapter(Context context,List<Chat> chatList){

        this.chatList = chatList;
        this.context=context;
>>>>>>> mog 24.6
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
<<<<<<< HEAD
<<<<<<< HEAD
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        }
=======
=======
>>>>>>> mog 24.6
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        }

<<<<<<< HEAD
>>>>>>> galos 24/06 fix almog problems
=======
>>>>>>> mog 24.6
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
<<<<<<< HEAD
<<<<<<< HEAD
        Chat chat = chatList.get(position);
        holder.showMessageTv.setText(chat.getMessage());
    }

=======
=======
>>>>>>> mog 24.6

        Chat chat = chatList.get(position);
        holder.showMessage.setText(chat.getMessage());

        //TODO If defult use this if not use glide
        /*holder.profileImage.setImageResource(R.mipmap.ic_launcher);*/
    }



<<<<<<< HEAD
>>>>>>> galos 24/06 fix almog problems
=======
>>>>>>> mog 24.6
    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
<<<<<<< HEAD
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
=======
>>>>>>> mog 24.6

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (chatList.get(position).getSender().equals(firebaseUser.getUid())){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView showMessage;
<<<<<<< HEAD
>>>>>>> galos 24/06 fix almog problems
=======
>>>>>>> mog 24.6
        public ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
<<<<<<< HEAD
<<<<<<< HEAD
            showMessageTv = itemView.findViewById(R.id.tvChatMessage);
        }
=======
=======
>>>>>>> mog 24.6

            showMessage = itemView.findViewById(R.id.show_message);
        }

<<<<<<< HEAD
>>>>>>> galos 24/06 fix almog problems
=======
>>>>>>> mog 24.6
    }
}
