package com.lin.meet.main.fragment.Home;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.lin.meet.R;
import com.lin.meet.my_util.MyUtil;
import com.lin.meet.my_util.TFLiteUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PicturesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private RequestOptions options = new RequestOptions();
    public static int LOAD_COUNT = 6;//一次加载的数目
    public static int INIT_COUNT = 9;//首次加载的数目
    public boolean cleaning = false;
    public boolean rolling = false;
    public boolean initLoad = true;
    public HomeContract.View parentView;
    private boolean firstLoad = true;
    private boolean isLoaded = false;//防止滑动加载过多图片
    private Context context;
    private int count = 0;
    private List<PicturesBean> list = new ArrayList<>();
    private boolean loading;
    private String urls[],labels[];
    private int labels_id[];
    private int loadCount = 0;//要加载的数目
    private int beanId[];
    private static int cacheCount = 0;//已经缓存数目
    public int roll_dy = 0;
    public PicturesAdapter(Context context){
        this.context = context;
        loading = false;
        options.placeholder(R.drawable.default_image);
        options.error(R.drawable.load_error);
        options.diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        dataInit();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.pictures_item,viewGroup,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if(cleaning||rolling||initLoad)
            return;
        isLoaded = true;
        if(viewHolder instanceof ViewHolder){
            final PicturesBean bean = list.get(beanId[i]);
            final ViewGroup.LayoutParams params = ((ViewHolder)viewHolder).image.getLayoutParams();
            params.width = MyUtil.getScreenWidth((Activity)context)/2 - MyUtil.dp2px(context,4);
            int temp = (int)Math.ceil((bean.getLabel()/3.0f));
            ((ViewHolder) viewHolder).title.setText(labels[temp]);
            if(bean.getScale()!=0) {
                params.height = (int) (params.width / bean.getScale());
                Glide.with(context).asDrawable().load(bean.getPath()).apply(options).thumbnail(0.01f).into(((ViewHolder) viewHolder).image);
            }else {
                params.height = params.width;
                Glide.with(context).asDrawable().load(bean.getPath()).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
                        float scale = resource.getIntrinsicWidth() / (float) resource.getIntrinsicHeight();
                        bean.setScale(scale);
                        notifyDataSetChanged();
                        cacheCount++;
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        if(initLoad)
            return 0;
        return loadCount;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        ImageView image;
        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.pictures_title);
            image = (ImageView) itemView.findViewById(R.id.pictures_image);
        }
    }


    private void dataInit(){
        loading = true;
        if(firstLoad){
            firstLoad = false;
            TFLiteUtil util = new TFLiteUtil(context);
            urls = util.readCacheLabelFromUrl();
            labels = util.readAndGetLabel();
            labels_id = new int[urls.length];
            beanId = new int[urls.length];
            for(int k=1;k<urls.length;k++)
                labels_id[k] = k;
            beanId = labels_id;
        }
        list.clear();
        for(int i=1;i<urls.length;i++){
            final PicturesBean bean =new PicturesBean(urls[i],labels_id[i]);
            list.add(bean);
        }
        loadCount = INIT_COUNT;
    }


    private void refreshData(){
        for (int i=0;i<list.size()-1;i++){
            Random random = new Random();
            int num = random.nextInt(list.size()-1);
            int temp = beanId[i];
            beanId[i] = beanId[num];
            beanId[num] = temp;
        }
    }


    public void insertData(final Activity activity){
        if(!isLoaded)
            return;
        notifyDataSetChanged();
        final Timer tr = new Timer();
        tr.schedule(new TimerTask() {
            @Override
            public void run() {
                tr.cancel();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(loadCount>=list.size()-1)
                            return;
                        else{
                            isLoaded = false;
                            if(loadCount+LOAD_COUNT>=list.size()-1)
                                loadCount=list.size()-1;
                            else
                                loadCount+=LOAD_COUNT;
                        }
                        notifyDataSetChanged();
                    }
                });
            }
        },1000);
        notifyDataSetChanged();
    }

    public void refresh(final Home home){

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                cleaning =true;
                clean();
                refreshData();
                cleaning = false;
                notifyDataSetChanged();
            }
        });

    }

    public void setRolling(boolean State){
        if(State&&roll_dy>0)
            rolling = true;
        else{
            rolling = false;
            notifyDataSetChanged();
        }
    }

    public void clean(){
        Glide.get(context).clearMemory();
    }

    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                    while (true){
                        try{
                            Thread.sleep(500);
                                initLoad=false;
                                if(parentView!=null)
                                    parentView.isShowLoad(false);
                                refresh(null);
                                break;
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
            }
        }).start();
    }
}
