package com.lin.meet.main.fragment.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lin.meet.R;
import com.lin.meet.video.VideoActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private Context context;
    private RequestOptions options;
    private List<VideoBean> videos;

    @Override
    public int getItemCount() {
        return videos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView video_image;
        ImageView play_video;
        CircleImageView header;
        TextView nickName;
        TextView title;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            video_image = (ImageView) itemView.findViewById(R.id.video_image);
            play_video  = (ImageView) itemView.findViewById(R.id.play_video);
            header = (CircleImageView)itemView.findViewById(R.id.video_header);
            nickName = (TextView)itemView.findViewById(R.id.video_nickName);
            title = (TextView)itemView.findViewById(R.id.video_title);
        }

        void setVideoImage(Context context,String uri){
            Glide.with(context).asDrawable().load(uri).into(video_image);
        }

        void setHeader(Context context,String uri){
            Glide.with(context).load(uri).into(header);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context == null)
            context = viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.video_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.title.setText(videos.get(i).getBean().getTltle());
        viewHolder.nickName.setText(videos.get(i).getNickName());
        viewHolder.setHeader(context,videos.get(i).getHeaderUri());
        viewHolder.setVideoImage(context,videos.get(i).getBean().getUri());
        viewHolder.video_image.setOnClickListener(v-> startPlayVideo(videos.get(i).getBean().getId(),viewHolder.video_image));
        viewHolder.play_video.setOnClickListener(v-> startPlayVideo(videos.get(i).getBean().getId(),viewHolder.video_image));
    }

    private void startPlayVideo(String id,View view){
        Intent intent = new Intent(context, VideoActivity.class);
        intent.putExtra("VIDEO",id);
        ((Activity)context).getWindow().setExitTransition(new Explode());
        context.startActivity(intent);
    }

    public void initVideos(){
        if(videos!=null){
            videos.clear();
            videos = null;
        }
        videos = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void insertVideo(VideoBean bean){
        if(videos==null)
            return;
        videos.add(bean);
        notifyDataSetChanged();
    }

    public void insertVideo(int position,VideoBean bean){
        if(videos==null||position<0)
            return;
        videos.add(position,bean);
        notifyDataSetChanged();
    }

}
