package com.lin.meet.main.fragment.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lin.meet.R;
import com.lin.meet.bean.TopicMain;
import com.lin.meet.db_bean.topic_main;
import com.lin.meet.override.ScaleAnim;
import com.lin.meet.personal.PersonalActivity;
import com.lin.meet.topic.TopicActivity;

import java.util.ArrayList;
import java.util.List;

public class  TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {
    private int flagId = -1;
    private Context context;
    private Fragment fragment;
    private RequestOptions options;
    private List<TopicBean> topics;
    private TopicCallback callback;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context=viewGroup.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.topic_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setHeader(context,topics.get(i).header);
        viewHolder.setImageView(context,topics.get(i).bean.getOne_uri());
        viewHolder.nickName.setText(topics.get(i).nickName);
        viewHolder.time.setText(topics.get(i).bean.getCreatedAt());
        viewHolder.title.setText(topics.get(i).bean.getTitle());
        viewHolder.setThumb(topics.get(i).like);
        viewHolder.item.setOnClickListener(v->{
            TopicMain bean = new TopicMain(topics.get(i).bean);
            bean.setHeaderUri(topics.get(i).header);
            bean.setNickName(topics.get(i).nickName);
            Intent intent = new Intent(context, TopicActivity.class);
            intent.putExtra("bean",bean);
            flagId = i;
            fragment.startActivityForResult(intent,2000);
        });
        viewHolder.comment.setOnClickListener(v->{
            TopicMain bean = new TopicMain(topics.get(i).bean);
            bean.setHeaderUri(topics.get(i).header);
            bean.setNickName(topics.get(i).nickName);
            Intent intent = new Intent(context, TopicActivity.class);
            intent.putExtra("bean",bean);
            intent.putExtra("comment",true);
            flagId = i;
            fragment.startActivityForResult(intent,2000);
        });
        viewHolder.thumb.setOnClickListener(v->{
            topics.get(i).like = !topics.get(i).like;
            callback.onClickLike(topics.get(i).like,topics.get(i).bean.getId(),topics.get(i).bean.getUid(),i);
            viewHolder.onThumbClick(topics.get(i).like);
        });
        viewHolder.header.setOnClickListener(v-> PersonalActivity.Companion.startOther((Activity) context,topics.get(i).bean.getUid()));
    }

    @Override
    public int getItemCount() {
        if(topics!=null)
            return topics.size();
        else
            return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView card;
        CardView item;
        TextView nickName;
        TextView time;
        TextView title;
        ImageView header;
        ImageView image;
        ImageView thumb;
        ImageView comment;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            item = (CardView)itemView.findViewById(R.id.topic_cardView);
            card = (CardView)itemView.findViewById(R.id.imageLayout);
            nickName = (TextView)itemView.findViewById(R.id.nickName);
            time = (TextView)itemView.findViewById(R.id.time);
            title = (TextView)itemView.findViewById(R.id.title);
            image = (ImageView)itemView.findViewById(R.id.topic_image);
            header = (ImageView)itemView.findViewById(R.id.topic_header);
            comment = (ImageView)itemView.findViewById(R.id.comment);
            thumb = (ImageView)itemView.findViewById(R.id.thumb);
        }

        void setImageView(Context context,String uri){
            if("@null".equals(uri)){
                card.setVisibility(View.GONE);
            }else {
                card.setVisibility(View.VISIBLE);
                Glide.with(context).load(uri).into(image);
            }
        }

        void onThumbClick(boolean like){
            if(like){
                ScaleAnim.startAnim(thumb,R.mipmap.like2);
            }
            else {
                ScaleAnim.startAnim(thumb,R.mipmap.like);
            }
        }

        void setThumb(boolean like){
            if(like){
                thumb.setImageResource(R.mipmap.like2);
            }else {
                thumb.setImageResource(R.mipmap.like);
            }
        }

        void setHeader(Context context,String uri){
            Glide.with(context).load(uri).into(header);
        }
    }

    static class TopicBean{
        topic_main bean;
        String header;
        String nickName;
        boolean like = false;
        public TopicBean(topic_main bean){
            this.bean = bean;
        }
    }

    TopicAdapter(TopicCallback callback,Fragment activity){
        this.fragment = activity;
        this.callback = callback;
        options = new RequestOptions();
    }

    void initTopics(){
        if(topics != null){
            topics.clear();
            topics = null;
        }
        topics = new ArrayList<>();
        notifyDataSetChanged();
    }

    int insertTopics(TopicBean bean){
        topics.add(bean);
        notifyDataSetChanged();
        return topics.size()-1;
    }

    int insertTopics(int position,TopicBean bean){
        topics.add(position,bean);
        notifyDataSetChanged();
        return 0;
    }

    public void setLike(int position,boolean like){
        topics.get(position).like = like;
        notifyDataSetChanged();
    }

    public void likeChange(){
        if(flagId!=-1){
            topics.get(flagId).like = !topics.get(flagId).like;
            flagId = -1;
            notifyDataSetChanged();
        }
    }


    interface TopicCallback{
        void onClickLike(boolean like,String id,String uid,int position);
    }
}
