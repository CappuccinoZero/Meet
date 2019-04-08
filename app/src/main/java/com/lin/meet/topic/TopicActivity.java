package com.lin.meet.topic;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.lin.meet.R;
import com.lin.meet.jsoup.LoveNewsBean;

import java.util.ArrayList;
import java.util.List;

public class TopicActivity extends AppCompatActivity implements View.OnClickListener {

    private TopicAdapter adapter;
    private RecyclerView recyclerView;
    private RelativeLayout replyNormal;
    private LinearLayout replyWrite;
    private LinearLayout replyLayout;
    private EditText replyEdit;
    private boolean showEdit = false;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        init();
    }

    private void init(){
        replyNormal = (RelativeLayout)findViewById(R.id.video_reply_normal);
        replyWrite = (LinearLayout) findViewById(R.id.video_reply_write);
        replyEdit = (EditText)findViewById(R.id.video_reply_edit);
        replyLayout = (LinearLayout)findViewById(R.id.video_reply_layout);
        toolbar = (Toolbar) findViewById(R.id.topic_toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.topic_recyclerView);
        replyLayout.setOnClickListener(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_x);
        }

        adapter = new TopicAdapter(getTestData());
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
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
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private LoveNewsBean getTestData(){
        LoveNewsBean bean= new LoveNewsBean();
        bean.setTitle("1、越写越自闭！！");
        bean.setAuthor("零神");
        bean.setTime("2019年4月8日");
        List<String> content = new ArrayList<>();
        content.add("真的不知道说什么 -> "+"\n"+getResources().getString(R.string.test));
        List<String> uri = new ArrayList<>();
        for(int i=0;i<4;i++)
            uri.add("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=945740795,1384143909&fm=26&gp=0.jpg");
        bean.setContents(content);
        bean.setImgs(uri);
        return bean;
    }
}
