package com.lin.meet.main.fragment.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lin.meet.R;
import com.lin.meet.bean.video_main;
import com.lin.meet.my_util.MyUtil;
import com.lin.meet.picture_observer.PictureObserver;

import java.util.ArrayList;
import java.util.List;

public class PicturesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RequestOptions options = new RequestOptions();
    private List<video_main> pictures = new ArrayList<>();
    private List<Float> scales = new ArrayList<>();
    private Context context;
    public PicturesAdapter(Context context){
        this.context = context;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.pictures_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof ViewHolder){
            video_main bean = pictures.get(i);
            final ViewGroup.LayoutParams params = ((ViewHolder)viewHolder).image.getLayoutParams();
            params.width = MyUtil.getScreenWidth((Activity)context)/2 - MyUtil.dp2px(context,4);
            ((ViewHolder) viewHolder).title.setText(bean.getTltle());
            Glide.with(context).asDrawable().load(bean.getUri()).apply(options).thumbnail(0.01f).into(((ViewHolder) viewHolder).image);
            params.height = (int) (params.width / scales.get(i));
            Glide.with(context).asDrawable().load(bean.getUri()).apply(options).thumbnail(0.01f).into(((ViewHolder) viewHolder).image);

            ((ViewHolder) viewHolder).card.setOnClickListener(v->{
                Intent intent = new Intent(context, PictureObserver.class);
                intent.putExtra("haveContent",true);
                intent.putExtra("bean",pictures.get(i));
                ((Activity)context).getWindow().setExitTransition(new Explode());
                Pair<View,String> pair = new Pair<>(((ViewHolder) viewHolder).card,"picture");
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context,pair);
                context.startActivity(intent,compat.toBundle());
            });
        }
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView image;
        CardView card;
        public ViewHolder(View itemView) {
            super(itemView);
            card = (CardView) itemView.findViewById(R.id.pictureCard);
            title = (TextView) itemView.findViewById(R.id.pictures_title);
            image = (ImageView) itemView.findViewById(R.id.pictures_image);
        }
    }

    public void refreshPicture(){
        pictures.clear();
        scales.clear();
        notifyDataSetChanged();
    }

    void insertPicture(int position,final video_main bean){
        Glide.with(context).load(bean.getUri()).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                float scale = resource.getIntrinsicWidth() / (float) resource.getIntrinsicHeight();
                pictures.add(position,bean);
                scales.add(position,scale);
                notifyDataSetChanged();
            }
        });
        notifyDataSetChanged();
    }

    public void insertPicture(final video_main bean){
        Glide.with(context).load(bean.getUri()).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                float scale = resource.getIntrinsicWidth() / (float) resource.getIntrinsicHeight();
                pictures.add(bean);
                scales.add(scale);
                notifyDataSetChanged();
            }
        });
        notifyDataSetChanged();
    }
}
