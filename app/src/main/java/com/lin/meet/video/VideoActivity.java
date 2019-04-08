package com.lin.meet.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.override.JZVideo;
import com.lin.meet.override.MyRelativeLayout;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener, MyRelativeLayout.MyLayoutInterface {
    RecyclerView commentRecyclerView;
    private CommentAdapter adapter;
    private boolean showEdit = false;

    private MyRelativeLayout parentLayout;
    private LinearLayout replyLayout;
    private EditText replyEdit;
    private RelativeLayout replyNormal;
    private LinearLayout replyWrite;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();;
        JZVideo jzVideoPlayerStandard = (JZVideo) findViewById(R.id.videoplayer);
        jzVideoPlayerStandard.setUp("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4"
                , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "测试视频");
        jzVideoPlayerStandard.startButton.performClick();
        Glide.with(this).load("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4").into(jzVideoPlayerStandard.thumbImageView);
        init();
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    private void init(){
        parentLayout = (MyRelativeLayout) findViewById(R.id.parent_layout);
        parentLayout.setLayoutInterface(this);
        commentRecyclerView = (RecyclerView)findViewById(R.id.video_recyclerView);
        adapter = new CommentAdapter();
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        commentRecyclerView.setAdapter(adapter);
        commentRecyclerView.setLayoutManager(manager);
        replyEdit = (EditText)findViewById(R.id.video_reply_edit);
        replyLayout = (LinearLayout)findViewById(R.id.video_reply_layout);
        replyNormal = (RelativeLayout)findViewById(R.id.video_reply_normal);
        replyWrite = (LinearLayout) findViewById(R.id.video_reply_write);
        replyLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_reply_layout:
                showEdit();
        }
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

    private static final String TAG = "测试";

    @Override
    public void touch(MotionEvent event) {
        if(event.getY()<replyWrite.getTop()){
            hideEdit();
        }
    }
}
