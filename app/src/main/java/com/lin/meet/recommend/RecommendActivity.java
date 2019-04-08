package com.lin.meet.recommend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.jsoup.LoveNews;
import com.lin.meet.jsoup.LoveNewsBean;

public class RecommendActivity extends AppCompatActivity implements View.OnClickListener {
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LoveNews.JSOUP_NEWS_MESSAGE:
                    Bundle data = msg.getData();
                    if(data!=null){
                        LoveNewsBean bean = (LoveNewsBean) data.getSerializable("LoveNewsContent");
                        initContent(bean);
                    }
                    break;
            }
        }
    };
    private TextView title;
    private AppBarLayout appBarLayout;
    private RecyclerView recyclerView;
    private ImageView headImage;
    private RecommendAdapter adapter;
    private RelativeLayout replyNormal;
    private LinearLayout replyWrite;
    private LinearLayout replyLayout;
    private EditText replyEdit;
    private ImageView back;
    private boolean showEdit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        Intent dataIntent = getIntent();
        LoveNewsBean bean = (LoveNewsBean) dataIntent.getSerializableExtra("LoveNewsBean");
        init(bean);
    }

    private void init(LoveNewsBean bean){
        recyclerView = (RecyclerView)findViewById(R.id.recommend_recyclerView);
        headImage = (ImageView)findViewById(R.id.recommend_head_image);
        appBarLayout = (AppBarLayout)findViewById(R.id.recommend_appbarLayout);
        title = (TextView)findViewById(R.id.recommend_title);
        replyNormal = (RelativeLayout)findViewById(R.id.video_reply_normal);
        replyWrite = (LinearLayout) findViewById(R.id.video_reply_write);
        replyEdit = (EditText)findViewById(R.id.video_reply_edit);
        replyLayout = (LinearLayout)findViewById(R.id.video_reply_layout);
        back = (ImageView)findViewById(R.id.recommend_back);
        replyLayout.setOnClickListener(this);
        back.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                title.setTranslationY(i);
            }
        });
        LoveNews.updateNewsContent(handler,bean);
    }

    private void initContent(LoveNewsBean bean){
        adapter = new RecommendAdapter(bean);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        Glide.with(this).asDrawable().load(bean.getImgs().get(0)).into(headImage);
        title.setText(bean.getTitle());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void showEdit(){
        replyEdit.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(replyEdit,0);
        showEdit = true;
        replyWrite.setVisibility(View.VISIBLE);
        replyNormal.setVisibility(View.GONE);
    }

    private void hideEdit(){
        showEdit = false;
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        replyWrite.setVisibility(View.GONE);
        replyNormal.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getY()<replyWrite.getTop()){
            hideEdit();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_reply_layout:
                showEdit();
                break;
            case R.id.recommend_back:
                finish();
                break;
        }
    }
}
