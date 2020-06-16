package com.thegalos.petbook.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thegalos.petbook.Objects.Feed;
import com.thegalos.petbook.R;

import java.util.List;


public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyFeedViewHolder> {

    private final List<Feed> feedList;
    private myFeedListener listener;

    interface myFeedListener {
        void onClickListener(int position);
        void onCardLongClicked();
    }

    public void setListener(myFeedListener listener){
        this.listener = listener;
    }

    public FeedAdapter(List<Feed> feedList) {
        this.feedList = feedList;
    }

    public class MyFeedViewHolder extends RecyclerView.ViewHolder{

        final TextView tvPostOwner;
        final TextView tvPostText;
        final TextView tvSelectedPet;
        final ImageView ivFeedPhoto;


        MyFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPostOwner = itemView.findViewById(R.id.tvPostOwner);
            tvPostText = itemView.findViewById(R.id.tvPostText);
            tvSelectedPet =  itemView.findViewById(R.id.tvSelectedPet);
            ivFeedPhoto =  itemView.findViewById(R.id.ivFeedPhoto);

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
    public MyFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_card,parent,false);
        return new MyFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyFeedViewHolder holder, int position) {
        final Feed feed = feedList.get(position);
        holder.tvPostOwner.setText(feed.getPostOwner());
        holder.tvPostText.setText(feed.getPostText());
        holder.tvSelectedPet.setText(feed.getSelectedPet());
        if (feed.getImageURL() == null)
            Glide.with(holder.ivFeedPhoto.getContext()).load(R.drawable.missing).into(holder.ivFeedPhoto);
        else
            Glide.with(holder.ivFeedPhoto.getContext()).load(feed.getImageURL()).into(holder.ivFeedPhoto);
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }
}