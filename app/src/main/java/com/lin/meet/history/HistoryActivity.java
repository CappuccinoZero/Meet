package com.lin.meet.history;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.lin.meet.R;
import com.lin.meet.camera_demo.PhotoBean;
import com.lin.meet.demo.SmoothCheckBox;
import com.lin.meet.main.DataBase;
import com.lin.meet.main.DataBaseModel;
import com.lin.meet.my_util.MyUtil;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener,HistoryContract.View{
    private HistoryContract.Presenter presenter;
    private String title = "历史记录";
    private static final String TAG = "HistoryActivity";
    private RecyclerView recyclerView;
    private ImageView imageView;
    private ImageView head_image;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private HistoryAdapter adapter;
    private AppBarLayout appbarLayout;
    private FloatingActionButton actionButton;
    private Toolbar toolbar;
    private int translation = -1;
    private int max_i = -1;
    public boolean isDelete = false;
    private List<PhotoBean> list;
    private DataBase dataBase= new DataBaseModel();
    private SmoothCheckBox select_all;
    private TextView select_number;
    private TextView delete;
    private ImageView home;
    public AlertDialog dialog;
    public boolean deletePhoto = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        initData();
        initView();
        presenter = new HistoryPresenter(this,adapter);
    }

    private void initData(){
        list = dataBase.findAllPhoto();
    }

    private void initView(){
        max_i = (int)(getResources().getDimension(R.dimen.history_appbarlayout)-getResources().getDimension(R.dimen.history_toobar_height));
        translation = (int)(getResources().getDimension(R.dimen.history_head)-getResources().getDimension(R.dimen.history_toobar_height));
        actionButton = (FloatingActionButton) findViewById(R.id.history_delete);
        recyclerView =(RecyclerView) findViewById(R.id.history_recyclerView);
        imageView = (ImageView) findViewById(R.id.card_image);
        head_image = (ImageView) findViewById(R.id.history_imageview);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.history_collapsing);
        appbarLayout = (AppBarLayout) findViewById(R.id.history_appbar);
        toolbar = (Toolbar) findViewById(R.id.history_toolbar);
        select_all = (SmoothCheckBox) findViewById(R.id.select_all);
        delete = (TextView) findViewById(R.id.history_delete_text);
        select_number = (TextView) findViewById(R.id.history_number);
        home = (ImageView) findViewById(R.id.history_back) ;

        delete.setOnClickListener(this);
        actionButton.setOnClickListener(this);
        home.setOnClickListener(this);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.BLACK);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        adapter=new HistoryAdapter(list);
        recyclerView.setAdapter(adapter);
        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                int x = translation*i/max_i;
                head_image.setTranslationY(x);
            }
        });
        select_all.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                if(select_all.isChecked()){
                    presenter.doSelectAll();
                }else{
                    presenter.doCloseAll();
                }
            }
        });

        DefaultItemAnimator animator =new DefaultItemAnimator();
        animator.setAddDuration(800);
        animator.setRemoveDuration(800);
        recyclerView.setItemAnimator(animator);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if(MyUtil.isSlidetoBottom(recyclerView)){
                    Log.d(TAG, "onScrolled: 到达底部");
                adapter.insertItem(HistoryActivity.this);
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }
    

    private int getDp(int a){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a,getResources().getDisplayMetrics());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.history_delete:
                if(!isDelete){
                    select_all.setChecked(false);
                    adapter.doCloseAll();
                    setCount(adapter.getCount());
                    isDelete = true;
                    presenter.openDelete();
                }else {
                    isDelete = false;
                    presenter.closeDelete();
                }
                break;
            case R.id.history_back:
                finish();
                break;
            case R.id.history_delete_text:
                presenter.deletePhoto();
                break;
        }
    }

    @Override
    public void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否删除图片");
        builder.setMessage("此记录将从本地永久删除");
        builder.setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deletePhoto = true;
                showLoadingDialog();
                clickFB();
            }
        });
        builder.show();
    }

    @Override
    public void showLoadingDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.loading_dialog,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        WindowManager.LayoutParams lp=dialog.getWindow().getAttributes();
        lp.width = (int)getResources().getDimension(R.dimen.dialog_loading);
        lp.height = (int)getResources().getDimension(R.dimen.dialog_loading);
        lp.dimAmount =0f;
        dialog.getWindow().setAttributes(lp);
        final Timer t=new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
                t.cancel();
            }
        },1350);
    }

    @Override
    public void setCount(int count) {
        String newNumber= count+"";
        select_number.setText(newNumber);
    }

    @Override
    public void  showActionButtonDelete() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator rotation_0 = ObjectAnimator.ofFloat(actionButton,"rotation",0,90);
        rotation_0.setDuration(100);
        ObjectAnimator rotation_1 = ObjectAnimator.ofFloat(actionButton,"rotation",90,165);
        rotation_1.setDuration(100);
        ObjectAnimator rotation_2 = ObjectAnimator.ofFloat(actionButton,"rotation",165,135);
        rotation_1.setDuration(100);
        rotation_1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                actionButton.setImageResource(R.drawable.delete);
                super.onAnimationStart(animation);
            }
        });
        set.play(rotation_1).before(rotation_2).after(rotation_0);
        set.start();
        collapsingToolbarLayout.setTitle("");
        select_all.setVisibility(View.VISIBLE);
        select_number.setVisibility(View.VISIBLE);
        delete.setVisibility(View.VISIBLE);
        home.setVisibility(View.GONE);
        ((TextView)findViewById(R.id.history_text)).setVisibility(View.VISIBLE);
    }

    @Override
    public void showActionButtonClose() {
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator rotation_0 = ObjectAnimator.ofFloat(actionButton,"rotation",135,0);
        rotation_0.setDuration(150);
        ObjectAnimator rotation_1 = ObjectAnimator.ofFloat(actionButton,"rotation",0,-45);
        rotation_1.setDuration(100);
        ObjectAnimator rotation_2 = ObjectAnimator.ofFloat(actionButton,"rotation",-20,0);
        rotation_1.setDuration(25);
        rotation_1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                actionButton.setImageResource(R.drawable.delete_forevery);
                super.onAnimationStart(animation);
            }
        });
        set.play(rotation_1).before(rotation_2).after(rotation_0);
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(deletePhoto){
                    deletePhoto=false;
                    adapter.deletePhoto();
                }
                super.onAnimationEnd(animation);
            }
        });
        set.start();
        ((TextView)findViewById(R.id.history_text)).setVisibility(View.GONE);
        collapsingToolbarLayout.setTitle(title);
        select_all.setVisibility(View.GONE);
        select_number.setVisibility(View.GONE);
        delete.setVisibility(View.GONE);
        home.setVisibility(View.VISIBLE);
    }


    @Override
    public void clickFB() {
        actionButton.performClick();
    }



    private ColorStateList getColorStateListTest(int colorRes) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };
        int color = ContextCompat.getColor(this, colorRes);
        int[] colors = new int[]{color, color, color, color};
        return new ColorStateList(states, colors);
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }

}
