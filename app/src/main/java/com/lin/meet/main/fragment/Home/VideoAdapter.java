package com.lin.meet.main.fragment.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lin.meet.R;
import com.lin.meet.video.VideoActivity;

import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private RequestOptions options;
    private List<VideoBean> list;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context=viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.video_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(viewHolder instanceof ViewHolder){
            VideoBean bean = list.get(i);
            Glide.with(context).asDrawable().load(bean.getUrl()).into(((ViewHolder) viewHolder).video);
            setListener((ViewHolder) viewHolder,i);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView video;
        ImageView video_start;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            video = (ImageView) itemView.findViewById(R.id.jz_video);
            video_start  = (ImageView) itemView.findViewById(R.id.start_video);
        }
    }

    private void setListener(final ViewHolder holder,int i){
        holder.video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.video_start.performClick();
            }
        });
        holder.video_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).startActivity(new Intent((Activity)context, VideoActivity.class));
            }
        });
    }

    public VideoAdapter(){
        list = new ArrayList<>();
    }

    public VideoAdapter(List<VideoBean> list){
        this.list=list;
    }



    public void insertData(VideoBean[] beans){
        for(int i =0;i<beans.length;i++)
            insertData(beans[i]);
    }

    public void insertData(VideoBean bean){
            list.add(bean);
    }
}
