package com.lin.meet.recommend;

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
import com.lin.meet.bean.recommentBean;
import com.lin.meet.jsoup.LoveNewsBean;
import com.lin.meet.override.ScaleAnim;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private int thumbCount = 0,commentCount = 0;
    private List<RecommendViewBean> list = new ArrayList<>();
    private List<ReplyBean> replys = new ArrayList<>();
    private Context context;
    private RecommendCallback callback;
    private boolean isLike = false;

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context = viewGroup.getContext();
        View view = null;
        if(i==1){
            view = LayoutInflater.from(context).inflate(R.layout.recommend_view,viewGroup,false);
            return new RecommendViewHoloder(view);
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
        if(viewHoloder instanceof RecommendViewHoloder){
            RecommendViewBean bean = list.get(i);
            switch (bean.flag){
                case RecommendViewBean.FLAG_HEAD:
                    ((RecommendViewHoloder)viewHoloder).showLayout(1);
                    ((RecommendViewHoloder)viewHoloder).author.setText(bean.author);
                    ((RecommendViewHoloder)viewHoloder).time.setText(bean.time);
                    break;
                case RecommendViewBean.FLAG_IMAGE:
                    ((RecommendViewHoloder)viewHoloder).showLayout(3);
                    ((RecommendViewHoloder)viewHoloder).loadImage(context,bean.uri);
                    break;
                case RecommendViewBean.FLAG_CONTENT:
                    ((RecommendViewHoloder)viewHoloder).showLayout(2);
                    ((RecommendViewHoloder)viewHoloder).text.setText(bean.content);
                    break;
                case RecommendViewBean.FLAG_CONTENT_BOTTOM:
                    ((RecommendViewHoloder)viewHoloder).setCount(thumbCount,commentCount);
                    ((RecommendViewHoloder)viewHoloder).showLayout(5);
                    ((RecommendViewHoloder)viewHoloder).thumb.setImageResource(isLike?R.drawable.thumb_red:R.drawable.thumb);
                    ((RecommendViewHoloder)viewHoloder).thumb.setOnClickListener((v)->{
                        if(isNoLogin())return;
                        if(isLike){
                            isLike = !isLike;
                            ScaleAnim.startAnim(((RecommendViewHoloder)viewHoloder).thumb,R.drawable.thumb);
                            callback.setIsLike(isLike);
                        }
                        else {
                            isLike = !isLike;
                            ScaleAnim.startAnim(((RecommendViewHoloder)viewHoloder).thumb,R.drawable.thumb_red);
                            callback.setIsLike(isLike);
                        }
                    });
                    break;
                case RecommendViewBean.FLAG_CONTENT_REPLY:
                    ((RecommendViewHoloder)viewHoloder).showLayout(6);
                    break;
            }
        }else if(viewHoloder instanceof ReplyViewHolder) {
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
                callback.showSonEdit(replys.get(index).getBean(),i);
            });
            ((ReplyViewHolder)viewHoloder).like.setOnClickListener(v->{
                if(isNoLogin())return;
                boolean like = ((ReplyViewHolder)viewHoloder).onClickLike();
                replys.get(index).like = like;
                callback.onSonLike(i,replys.get(index).floor,like);
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size()+replys.size();
    }

    public static class RecommendViewHoloder extends RecyclerView.ViewHolder {
        CircleImageView header;
        TextView author;
        TextView time;
        ImageView image;
        TextView text;
        TextView thumbCount;
        TextView commentCount;
        RelativeLayout layout_1;
        LinearLayout layout_2;
        LinearLayout layout_2_bottom;
        LinearLayout layout_3;
        LinearLayout layout_4;
        ImageView thumb;
        public RecommendViewHoloder(@NonNull View itemView) {
            super(itemView);
            header=(CircleImageView)itemView.findViewById(R.id.recommend_view_header);
            author=(TextView) itemView.findViewById(R.id.recommend_view_author);
            time=(TextView) itemView.findViewById(R.id.recommend_view_time);
            image=(ImageView) itemView.findViewById(R.id.recommend_view_image);
            text=(TextView) itemView.findViewById(R.id.recommend_view_content);
            thumbCount = (TextView)itemView.findViewById(R.id.recommend_thumb_count);
            commentCount = (TextView)itemView.findViewById(R.id.recommend_reply_count);
            layout_1 = (RelativeLayout)itemView.findViewById(R.id.recommend_view_layout1);
            layout_2 = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout2);
            layout_2_bottom = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout2_bottom);
            layout_3 = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout3);
            layout_4 = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout4);
            thumb = (ImageView)itemView.findViewById(R.id.recommend_thumb);
        }

        public void loadImage(Context context,String url){
            Glide.with(context).asDrawable().load(url).into(image);
        }

        public void loadHeader(Context context,String url){
            Glide.with(context).asDrawable().load(url).into(header);
        }

        public void showLayout(int i){
                layout_1.setVisibility(i==1?View.VISIBLE:View.GONE);
                layout_2.setVisibility(i==2?View.VISIBLE:View.GONE);
                layout_2_bottom.setVisibility(i==5?View.VISIBLE:View.GONE);
                layout_3.setVisibility(i==3?View.VISIBLE:View.GONE);
                layout_4.setVisibility(i==6?View.VISIBLE:View.GONE);
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
    }

    RecommendAdapter(RecommendCallback callback){
        this.callback = callback;
    }


    public void initAdapter(LoveNewsBean bean){
        if(bean==null)
            return;
        RecommendViewBean reBean= new RecommendViewBean();
        reBean.author = bean.getAuthor();
        reBean.time = bean.getTime();
        reBean.flag = RecommendViewBean.FLAG_HEAD;
        list.add(reBean);
        int max = Math.max(bean.getContents().size()-1,bean.getImgs().size()-1);
        for(int i=0;i<max;i++){
            if(bean.getContents().size()-1>i){
                reBean = new RecommendViewBean();
                reBean.flag = RecommendViewBean.FLAG_CONTENT;
                reBean.content = bean.getContents().get(i);
                list.add(reBean);
            }
            if(bean.getImgs().size()-1>i&&i>0){
                reBean = new RecommendViewBean();
                reBean.flag = RecommendViewBean.FLAG_IMAGE;
                reBean.uri = bean.getImgs().get(i);
                list.add(reBean);
            }
        }
        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_BOTTOM;
        list.add(reBean);
        reBean = new RecommendViewBean();
        reBean.flag = RecommendViewBean.FLAG_CONTENT_REPLY;
        list.add(reBean);
    }

    public void setCommentList(List<ReplyBean> comments){
        replys = comments;
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

    public void setThumbCount(int x){
        thumbCount = x;
        notifyDataSetChanged();
    }

    public void setCommentCount(int x){
        commentCount = x;
        notifyDataSetChanged();
    }

    public void setCount(int thumb,int comment){
        thumbCount = thumb;
        commentCount = comment;
        notifyDataSetChanged();
    }

    public interface RecommendCallback{
        void showSonEdit(recommentBean.recomment_comment comment,int position);
        void setIsLike(Boolean isLike);
        void onSonLike(int position,int floor,Boolean isLike);
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

    public void setLike(Boolean isLike){
        this.isLike = isLike;
    }

    public void setSonLike(int position,int count){
        replys.get(position-list.size()).likeCount = count;
        notifyDataSetChanged();
    }

    public void setCommentLike(int position,boolean isLike){
        replys.get(position-list.size()).like = isLike;
        notifyDataSetChanged();
    }
}
