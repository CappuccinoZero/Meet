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
import com.lin.meet.bean.recommentBean;
import com.lin.meet.jsoup.LoveNewsBean;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecommendAdapter extends RecyclerView.Adapter<RecommendAdapter.RecommendViewHoloder>  {
    private List<RecommendViewBean> list = new ArrayList<>();
    private List<recommentBean.recomment_comment> replys = new ArrayList<>();
    private Context context;
    @NonNull
    @Override
    public RecommendViewHoloder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context = viewGroup.getContext();
        View view = null;
        if(i==0)
            view = LayoutInflater.from(context).inflate(R.layout.recommend_view,viewGroup,false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.comment_item,viewGroup,false);
        return new RecommendViewHoloder(view);
    }


    @Override
    public int getItemViewType(int position) {
        if(position>=list.size())
            return 1;
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendViewHoloder viewHoloder, int i) {
        if(i<list.size()){
            RecommendViewBean bean = list.get(i);
            switch (bean.flag){
                case RecommendViewBean.FLAG_HEAD:
                    viewHoloder.showLayout(1);
                    viewHoloder.author.setText(bean.author);
                    viewHoloder.time.setText(bean.time);
                    break;
                case RecommendViewBean.FLAG_IMAGE:
                    viewHoloder.showLayout(3);
                    viewHoloder.loadImage(context,bean.uri);
                    break;
                case RecommendViewBean.FLAG_CONTENT:
                    viewHoloder.showLayout(2);
                    viewHoloder.text.setText(bean.content);
                    break;
                case RecommendViewBean.FLAG_CONTENT_BOTTOM:
                    viewHoloder.showLayout(5);
                    break;
                case RecommendViewBean.FLAG_CONTENT_REPLY:
                    viewHoloder.showLayout(6);
                    break;
            }
        }else {

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
        RelativeLayout layout_1;
        LinearLayout layout_2;
        LinearLayout layout_2_bottom;
        LinearLayout layout_3;
        LinearLayout layout_4;
        public RecommendViewHoloder(@NonNull View itemView) {
            super(itemView);
            header=(CircleImageView)itemView.findViewById(R.id.recommend_view_header);
            author=(TextView) itemView.findViewById(R.id.recommend_view_author);
            time=(TextView) itemView.findViewById(R.id.recommend_view_time);
            image=(ImageView) itemView.findViewById(R.id.recommend_view_image);
            text=(TextView) itemView.findViewById(R.id.recommend_view_content);
            layout_1 = (RelativeLayout)itemView.findViewById(R.id.recommend_view_layout1);
            layout_2 = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout2);
            layout_2_bottom = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout2_bottom);
            layout_3 = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout3);
            layout_4 = (LinearLayout)itemView.findViewById(R.id.recommend_view_layout4);
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

    RecommendAdapter(LoveNewsBean bean){
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

        /*回复测试*/
//        for(int i=0;i<5;i++){
//            ReplyBean replyBean = new ReplyBean();
//            replys.add(replyBean);
//        }
    }
}
