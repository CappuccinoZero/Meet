package com.lin.meet.main.fragment.Home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.jsoup.LoveNewsBean;
import com.lin.meet.recommend.RecommendActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<LoveNewsBean> list;
    private Context context;
    private Activity activity;

    RecommendAdapter(Activity activity){
        list = new ArrayList<>();
        this.activity = activity;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context=viewGroup.getContext();
        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.recommend_item,viewGroup,false);
        return new RecommendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        LoveNewsBean bean = list.get(i);
        if(viewHolder instanceof RecommendViewHolder){
            ((RecommendViewHolder) viewHolder).type.setText(bean.getType());
            ((RecommendViewHolder) viewHolder).title.setText(bean.getTitle());
            ((RecommendViewHolder) viewHolder).author.setText(bean.getAuthor());
            ((RecommendViewHolder) viewHolder).setImageUrl(context,bean.getImg());
            setOnclickListener((RecommendViewHolder) viewHolder,bean);
        }
    }

    private void setOnclickListener(RecommendViewHolder viewHolder,final LoveNewsBean bean){
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getWindow().setExitTransition(new Slide(Gravity.LEFT));
                viewHolder.roundView.setVisibility(View.VISIBLE);
                Intent intent = new Intent(context, RecommendActivity.class);
                intent.putExtra("LoveNewsBean",bean);
                Pair<View,String> pair1 = new Pair<>((View)viewHolder.img, ViewCompat.getTransitionName(viewHolder.img));
                Pair<View,String> pair2 = new Pair<>((View)viewHolder.title, ViewCompat.getTransitionName(viewHolder.title));
                Pair<View,String> pair3 = new Pair<>((View)viewHolder.roundView, ViewCompat.getTransitionName(viewHolder.roundView));
                ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,pair1,pair2,pair3);
                ActivityCompat.startActivity(activity,intent,compat.toBundle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class RecommendViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView type;
        TextView title;
        TextView author;
        ImageView img;
        CircleImageView header;
        View roundView;
        public RecommendViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.recommend_card);
            type = itemView.findViewById(R.id.recommend_item_type);
            title = itemView.findViewById(R.id.recommend_item_title);
            author = itemView.findViewById(R.id.recommend_item_author);
            img = itemView.findViewById(R.id.recommend_item_img);
            header = itemView.findViewById(R.id.recommend_item_header);
            roundView = itemView.findViewById(R.id.roundView);
        }

        public void setImageUrl(Context context,String url){
            Glide.with(context).asDrawable().load(url).into(img);
        }

        public void setHeaderUrl(Context context,String url){
            Glide.with(context).asDrawable().load(url).into(header);
        }
    }

    public void addRecomds(LoveNewsBean bean){
        list.add(bean);
        notifyDataSetChanged();
    }

    public synchronized void addRecomds(int position,LoveNewsBean bean){
        list.add(position,bean);
        notifyDataSetChanged();
    }

    public void refresh(){
        list.clear();
        notifyDataSetChanged();
    }

}
