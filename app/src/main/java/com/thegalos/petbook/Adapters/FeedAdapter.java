package com.thegalos.petbook.Adapters;

import android.util.Log;
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
import com.thegalos.petbook.Objects.Feed;
import com.thegalos.petbook.Objects.Pet;
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
        final TextView tvSelectedPet, tvPurebred, tvName, tvPetAge;
        final ImageView ivFeedPhoto;
        final ImageView ivType;
        //////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////////////////////////////////////////////////////////

        MyFeedViewHolder(@NonNull View itemView) {
            super(itemView);

            tvPostOwner = itemView.findViewById(R.id.tvPostOwner);
            tvPostText = itemView.findViewById(R.id.tvPostText);
            tvSelectedPet =  itemView.findViewById(R.id.tvPostTime);
            ivFeedPhoto =  itemView.findViewById(R.id.ivFeedPhoto);

            tvPurebred = itemView.findViewById(R.id.tvPurebred);
            tvName = itemView.findViewById(R.id.tvName);
            tvPetAge = itemView.findViewById(R.id.tvAge);
            ivType = itemView.findViewById(R.id.ivType);

            itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            int height = itemView.getMeasuredHeight();
            Log.d("Galos_height", String.valueOf(height));


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
        final Pet pet = feed.getPet();
        holder.tvPostOwner.setText(feed.getPostOwner());
        holder.tvPostText.setText(feed.getPostText());
        holder.tvSelectedPet.setText(feed.getSelectedPet());
        String str = "Age: " + pet.getAge();
        holder.tvPetAge.setText(str);
        str = pet.getName();
        holder.tvName.setText(str);
        if (pet.getPureBred())
            str = "Purebred";
        else
            str = "";
        holder.tvPurebred.setText(str);
        if (feed.getPet().getAnimalType().equals("Horse"))
            Glide.with(holder.ivType.getContext()).load(R.drawable.icon_horse).into(holder.ivType);

        else if (feed.getPet().getAnimalType().equals("Dog"))
            Glide.with(holder.ivType.getContext()).load(R.drawable.icon_dog).into(holder.ivType);

        else if (feed.getPet().getAnimalType().equals("Cat"))
            Glide.with(holder.ivType.getContext()).load(R.drawable.icon_cat).into(holder.ivType);
        else
            Glide.with(holder.ivType.getContext()).load(R.drawable.missing).into(holder.ivType);


//        RequestOptions options = new RequestOptions()
//                .skipMemoryCache(true)
//                .centerInside()
//                .placeholder(R.drawable.restaurant_icon)
//                .transform(new CircleCrop());


        if (feed.getImageURL() == null)
            Glide.with(holder.ivFeedPhoto.getContext()).load(R.drawable.missing).into(holder.ivFeedPhoto);
        else
            Glide.with(holder.ivFeedPhoto.getContext())
                    .load(feed.getImageURL())
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .thumbnail(0.05f).transition(DrawableTransitionOptions.withCrossFade())
                    .into(holder.ivFeedPhoto);
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }
}