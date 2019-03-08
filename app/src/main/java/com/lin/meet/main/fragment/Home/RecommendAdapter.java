package com.lin.meet.main.fragment.Home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lin.meet.R;

public class RecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if(context==null)
            context=viewGroup.getContext();
        View view = null;
        view = LayoutInflater.from(context).inflate(R.layout.recommend_item,viewGroup,false);
        return new ViewHolder_0(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        setListener((ViewHolder_0) viewHolder);
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    static class ViewHolder_0 extends RecyclerView.ViewHolder{
        CardView cardView;
        public ViewHolder_0(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.recommend_card);
        }
    }

    private void setListener(ViewHolder_0 viewHolder){
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
