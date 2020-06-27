package com.thegalos.petbook.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.thegalos.petbook.R;
import com.thegalos.petbook.objects.Feed;
import com.thegalos.petbook.objects.Pet;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.MyFeedViewHolder> {

    private final Context context;
    private final List<Feed> feedList;
    private myFeedListener listener;

    public interface myFeedListener {
        void onFeedListener(int position);
//        void onCardLongClicked();
    }

    public void setListener(myFeedListener listener){
        this.listener = listener;
    }

    public FeedAdapter(Context context, List<Feed> feedList) {
        this.context = context;
        this.feedList = feedList;
    }

    public class MyFeedViewHolder extends RecyclerView.ViewHolder{

        final TextView tvPostOwner;
        final TextView tvPostText;
        final TextView tvPay, tvName, tvPetAge, tvPostTime;
        final ImageView ivFeedPhoto;
        final ImageView ivType/*, ivIsFree*/;
        //////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////////////////////

        MyFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPostOwner = itemView.findViewById(R.id.tvPostOwner);
            tvPostText = itemView.findViewById(R.id.tvPostText);
//            tvSelectedPet =  itemView.findViewById(R.id.tvPostTime);
            ivFeedPhoto =  itemView.findViewById(R.id.ivFeedPhoto);
            tvPostTime = itemView.findViewById(R.id.tvPostTime);

            tvPay = itemView.findViewById(R.id.tvPay);
            tvName = itemView.findViewById(R.id.tvName);
            tvPetAge = itemView.findViewById(R.id.tvAge);
            ivType = itemView.findViewById(R.id.ivType);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        listener.onFeedListener(getAdapterPosition());
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public MyFeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_feed,parent,false);

        return new MyFeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyFeedViewHolder holder, int position) {
        final Feed feed = feedList.get(position);
        final Pet pet = feed.getPet();
        String str;
        ////////////PET///////
        if (pet.getAge().equals(""))
            str ="";
        else
            str = "Age: " + pet.getAge();
        holder.tvPetAge.setText(str);
        str = pet.getName();
        holder.tvName.setText(str);
        switch (pet.getAnimalType()) {
            case "Horse":
                Glide.with(context).load(R.drawable.icon_horse).into(holder.ivType);
                break;
            case "Dog":
                Glide.with(context).load(R.drawable.icon_dog).into(holder.ivType);
                break;
            case "Cat":
                Glide.with(context).load(R.drawable.icon_cat).into(holder.ivType);
                break;
            default:
                Glide.with(context).load(R.drawable.missing).into(holder.ivType);
                break;
        }


        /////////////FEED//////NEED 9//////////
        if (!feed.getImageURL().equals("null")) {
            holder.ivFeedPhoto.setVisibility(View.VISIBLE);
            Glide.with(holder.ivFeedPhoto.getContext())
                    .load(feed.getImageURL())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .thumbnail(0.05f).transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.ivFeedPhoto);
        }
        holder.tvPostTime.setText(feed.getTime());
        holder.tvPostOwner.setText(feed.getPostOwner());
        holder.tvPostText.setText(feed.getPostText());
        holder.tvName.setText(feed.getSelectedPet());
        str = feed.getFree();
        if (str.equals("yes")) {
//             item is free
            holder.tvPay.setText(R.string.free);
        } else if (str.equals("Looking")) {
            holder.tvPay.setText(R.string.looking);
        } else {
            if (feed.getWhoPays().equals("user")) {
                holder.tvPay.setText(R.string.pay);
                holder.tvPay.setTextColor(Color.RED);
            } else {
                holder.tvPay.setText(R.string.earn);
                holder.tvPay.setVisibility(View.VISIBLE);
                holder.tvPay.setTextColor(Color.GREEN);
            }
        }
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }
}