package com.lin.meet.topic;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.bean.ReplyBean;
import com.lin.meet.bean.ReplyViewHolder;
import com.lin.meet.bean.TopicMain;
import com.lin.meet.bean.topic_comment;
import com.lin.meet.override.ScaleAnim;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private List<RecommendViewBean> list = new ArrayList<>();
    private List<ReplyBean> replys = new ArrayList<>();
    private Context context;
    private TopicCallback callback;
    private int thumbCount = 0,commentCount = 0;
    private boolean like = false;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context = viewGroup.getContext();
        View view = null;
        if(i==1){
            view = LayoutInflater.from(context).inflate(R.layout.topic_view,viewGroup,false);
            return new TopicViewHoloder(view);
        }
        else {
            view = LayoutInflater.from(context).inflate(R.layout.comment_item,viewGroup,false);
            return new ReplyViewHolder(view);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(position<list.size())
            return 1;
        else
            return 0;
    }

    private boolean isNoLogin(){
        return !BmobUser.isLogin();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHoloder, int i) {
        if(viewHoloder instanceof TopicViewHoloder){
            RecommendViewBean bean = list.get(i);
            switch (bean.flag){
                case RecommendViewBean.FLAG_HEAD:
                    ((TopicViewHoloder)viewHoloder).showLayout(1);
                    ((TopicViewHoloder)viewHoloder).author.setText(bean.author);
                    ((TopicViewHoloder)viewHoloder).time.setText(bean.time);
                    ((TopicViewHoloder)viewHoloder).loadHeader(context,bean.uri);
                    break;
                case RecommendViewBean.FLAG_IMAGE:
                    ((TopicViewHoloder)viewHoloder).showLayout(3);
                    ((TopicViewHoloder)viewHoloder).loadImage(context,bean.uri);
                    break;
                case RecommendViewBean.FLAG_CONTENT:
                    ((TopicViewHoloder)viewHoloder).showLayout(2);
                    ((TopicViewHoloder)viewHoloder).text.setText(bean.content);
                    break;
                case RecommendViewBean.FLAG_CONTENT_BOTTOM:
                    ((TopicViewHoloder)viewHoloder).setCount(thumbCount,commentCount);
                    ((TopicViewHoloder)viewHoloder).showLayout(5);
                    ((TopicViewHoloder)viewHoloder).thumb.setImageResource(like?R.drawable.thumb_red:R.drawable.thumb);
                    ((TopicViewHoloder)viewHoloder).thumb.setOnClickListener((v)->{
                        if(isNoLogin())return;
                        if(like){
                            like = false;
                            ScaleAnim.startAnim(((TopicViewHoloder)viewHoloder).thumb,R.drawable.thumb);
                            callback.setLike(like);
                        }
                        else {
                            like = true;
                            ScaleAnim.startAnim(((TopicViewHoloder)viewHoloder).thumb,R.drawable.thumb_red);
                            callback.setLike(like);
                        }
                    });
                    break;
                case RecommendViewBean.FLAG_CONTENT_REPLY:
                    ((TopicViewHoloder)viewHoloder).showLayout(6);
                    break;
                case RecommendViewBean.FLAG_CONTENT_TITLE:
                    ((TopicViewHoloder)viewHoloder).showLayout(4);
                    ((TopicViewHoloder)viewHoloder).title.setText(bean.content);
                    break;
                case RecommendViewBean.FLAG_LOCATION:
                    ((TopicViewHoloder)viewHoloder).showLayout(7);
                    ((TopicViewHoloder)viewHoloder).location.setText(bean.content);
                    break;
            }
        }else if(viewHoloder instanceof ReplyViewHolder){
            int index = i - list.size();
            ((ReplyViewHolder)viewHoloder).content.setText(replys.get(index).content);
            ((ReplyViewHolder)viewHoloder).nickName0.setText(replys.get(index).nickName0);
            ((ReplyViewHolder)viewHoloder).time.setText(replys.get(index).time);
            ((ReplyViewHolder)viewHoloder).loadHeader(context,replys.get(index).header);
            ((ReplyViewHolder)viewHoloder).setLickCount(replys.get(index).likeCount);
            ((ReplyViewHolder)viewHoloder).setLike(replys.get(index).like);
            if(replys.get(index).commentCount>0)
                ((ReplyViewHolder)viewHoloder).replyCount.setText(String.valueOf(replys.get(index).commentCount)+"回复");
            else
                ((ReplyViewHolder)viewHoloder).replyCount.setText("回复");
            ((ReplyViewHolder)viewHoloder).setReplyCount(replys.get(index).commentCount);
            if(replys.get(index).nickName1!=null&&replys.get(index).content1!=null){
                ((ReplyViewHolder)viewHoloder).content1.setText(replys.get(index).content1);
                ((ReplyViewHolder)viewHoloder).nickName1.setText(replys.get(index).nickName1+"：");
            }
            if(replys.get(index).nickName2!=null&&replys.get(index).content2!=null){
                ((ReplyViewHolder)viewHoloder).content2.setText(replys.get(index).content2);
                ((ReplyViewHolder)viewHoloder).nickName2.setText(replys.get(index).nickName2+"：");
            }
            ((ReplyViewHolder)viewHoloder).item.setOnClickListener((v)->{
                callback.showSonEdit(replys.get(index).getTopic(),i);
            });
            ((ReplyViewHolder)viewHoloder).like.setOnClickListener(v->{
                if(isNoLogin())return;
                replys.get(index).like = ((ReplyViewHolder)viewHoloder).onClickLike();
                callback.onCommentLike(i,replys.get(index).floor);
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size()+replys.size();
    }

    public static class TopicViewHoloder extends RecyclerView.ViewHolder {
        CircleImageView header;
        TextView author;
        TextView time;
        ImageView image;
        TextView text;
        TextView title;
        RelativeLayout layout_1;
        LinearLayout layout_2_title;
        LinearLayout layout_2;
        LinearLayout layout_2_bottom;
        LinearLayout layout_3;
        LinearLayout layout_4;
        LinearLayout layout_5;
        TextView thumbCount;
        TextView commentCount;
        TextView location;
        ImageView thumb;
        public TopicViewHoloder(@NonNull View itemView) {
            super(itemView);
            header=(CircleImageView)itemView.findViewById(R.id.topic_view_header);
            author=(TextView) itemView.findViewById(R.id.topic_view_author);
            time=(TextView) itemView.findViewById(R.id.topic_view_time);
            image=(ImageView) itemView.findViewById(R.id.topic_view_image);
            text=(TextView) itemView.findViewById(R.id.topic_view_content);
            title = (TextView) itemView.findViewById(R.id.topic_view_title);
            layout_1 = (RelativeLayout)itemView.findViewById(R.id.topic_view_layout1);
            layout_2_title = (LinearLayout)itemView.findViewById(R.id.topic_view_layout2_title);
            layout_2 = (LinearLayout)itemView.findViewById(R.id.topic_view_layout2);
            layout_2_bottom = (LinearLayout)itemView.findViewById(R.id.topic_view_layout2_bottom);
            layout_3 = (LinearLayout)itemView.findViewById(R.id.topic_view_layout3);
            layout_4 = (LinearLayout)itemView.findViewById(R.id.topic_view_layout4);
            layout_5 = (LinearLayout)itemView.findViewById(R.id.topic_view_location);
            thumbCount = (TextView)itemView.findViewById(R.id.topic_thumb_count);
            commentCount = (TextView)itemView.findViewById(R.id.topic_reply_count);
            location = (TextView)itemView.findViewById(R.id.location_text);
            thumb = (ImageView)itemView.findViewById(R.id.topic_thumb);
        }

        void loadImage(Context context, String url){
            Glide.with(context).asDrawable().load(url).into(image);
        }

        public void loadHeader(Context context,String url){
            Glide.with(context).asDrawable().load(url).into(header);
        }

        void showLayout(int i){
                layout_1.setVisibility(i==1?View.VISIBLE:View.GONE);
                layout_2.setVisibility(i==2?View.VISIBLE:View.GONE);
                layout_2_title.setVisibility(i==4?View.VISIBLE:View.GONE);
                layout_2_bottom.setVisibility(i==5?View.VISIBLE:View.GONE);
                layout_3.setVisibility(i==3?View.VISIBLE:View.GONE);
                layout_4.setVisibility(i==6?View.VISIBLE:View.GONE);
                layout_5.setVisibility(i==7?View.VISIBLE:View.GONE);
        }

        public void setCount(int x,int y){
            thumbCount.setText(String.valueOf(x));
            commentCount.setText(String.valueOf(y));
        }
    }

    public static class RecommendViewBean{
        String uri;
        String content;
        String time;
        String author;
        int flag;
        public static final int FLAG_HEAD = 0;
        public static final int FLAG_IMAGE = 1;
        public static final int FLAG_CONTENT = 2;
        public static final int FLAG_CONTENT_BOTTOM = 3;
        public static final int FLAG_CONTENT_REPLY = 4;
        public static final int FLAG_CONTENT_TITLE = 5;
        static final int FLAG_LOCATION = 6;
    }

    TopicAdapter(TopicCallback callback){
        this.callback = callback;
    }

    public void initAdapter(TopicMain bean){
        if(bean==null)
            return;

        RecommendViewBean reBean= new RecommendViewBean();
        reBean.author = bean.getNickName();
        reBean.time = bean.bean.getCreatedAt();
        reBean.uri = bean.getHeaderUri();
        reBean.flag = RecommendViewBean.FLAG_HEAD;
        list.add(reBean);

        reBean = new RecommendViewBean();
        reBean.content = bean.bean.getTitle();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_TITLE;
        list.add(reBean);

        for(int i=0;i<6;i++){
            if(!bean.images[i].equals("@null")){
                reBean = new RecommendViewBean();
                reBean.flag = RecommendViewBean.FLAG_IMAGE;
                reBean.uri = bean.images[i];
                list.add(reBean);
            }
        }

        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT;
        reBean.content = bean.bean.getContent();
        list.add(reBean);

        if(!bean.bean.getLocation().equals("@null")){
            reBean = new RecommendViewBean();
            reBean.flag = RecommendViewBean.FLAG_LOCATION;
            reBean.content = bean.bean.getLocation();
            list.add(reBean);
        }

        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_BOTTOM;
        list.add(reBean);

        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_REPLY;
        list.add(reBean);
        notifyDataSetChanged();
    }

    void initThumbCount(TopicMain bean){
        thumbCount = bean.getLikeCount();
        notifyDataSetChanged();
    }

    synchronized int insertComment(ReplyBean bean){
        if(replys.size()==0){
            replys.add(bean);
            notifyDataSetChanged();
            return list.size();
        }
        for(int i=0;i<replys.size();i++){
            if(replys.get(i).updateTime<bean.updateTime){
                replys.add(i,bean);
                notifyDataSetChanged();
                return i+list.size();
            }
        }
        replys.add(bean);
        notifyDataSetChanged();
        return replys.size()+list.size()-1;
    }

    void updateSonReply(String msg,String nickName,int position,int floor){
        if(floor == 1){
            replys.get(position-list.size()).content1 = msg;
            replys.get(position-list.size()).nickName1 = nickName;
        }
        else if(floor==2){
            replys.get(position-list.size()).content2 = msg;
            replys.get(position-list.size()).nickName2 = nickName;
        }
        replys.get(position-list.size()).commentCount = floor;
        notifyDataSetChanged();
    }

    interface TopicCallback{
        void showSonEdit(topic_comment comment, int position);
        void setLike(boolean like);
        void onCommentLike(int position,int floor);
    }

    public void setThumbCount(int count){
        this.thumbCount = count;
        notifyDataSetChanged();
    }

    public void setCommentCount(int count){
        this.commentCount = count;
        notifyDataSetChanged();
    }


    int getLikeCount() {
        return thumbCount;
    }

    void updateCommentLike(int position,boolean like){
        replys.get(position-list.size()).like = like;
        if(like)
            replys.get(position-list.size()).likeCount = replys.get(position-list.size()).likeCount +1;
        else
            replys.get(position-list.size()).likeCount = replys.get(position-list.size()).likeCount -1;
        notifyDataSetChanged();
    }


    void setCommentLikeCount(int position,int count){
        replys.get(position - list.size()).likeCount = count;
        notifyDataSetChanged();
    }

    void setCommentLike(int position,boolean like){
        replys.get(position - list.size()).like = like;
        notifyDataSetChanged();
    }

    void setLike(boolean like){
        this.like = like;
        notifyDataSetChanged();
    }
}
