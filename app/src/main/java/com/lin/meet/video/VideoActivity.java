package com.lin.meet.video;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.hw.ycshareelement.transition.ChangeTextTransition;
import com.lin.meet.R;
import com.lin.meet.bean.ReplyBean;
import com.lin.meet.bean.video_comment;
import com.lin.meet.override.EmojiAdapter;
import com.lin.meet.override.JZVideo;
import com.lin.meet.override.MyRelativeLayout;
import com.lin.meet.override.ScaleAnim;

import cn.bmob.v3.BmobUser;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import de.hdodenhof.circleimageview.CircleImageView;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener, MyRelativeLayout.MyLayoutInterface,EmojiAdapter.EmojiCallback,VideoContract.View, CommentAdapter.VideoCallback {
    private ImageView useEmoji;
    private boolean isUseEmoji = false;
    private GridView emojiView;
    private EmojiAdapter emojiAdapter;
    private int useEdit = 0;//1->defalut 2->last = now 3->other
    private boolean like = false;
    private video_comment currentComment;
    private boolean isStar = false;
    RecyclerView commentRecyclerView;
    private CommentAdapter adapter;
    private boolean showEdit = false;
    private ImageView thumb;


    private ImageView star;
    private TextView send;
    private CircleImageView header;
    private TextView nickName;
    private VideoContract.presenter presenter;
    private LinearLayoutManager manager;
    private MyRelativeLayout parentLayout;
    private LinearLayout replyLayout;
    private EditText replyEdit;
    private RelativeLayout replyNormal;
    private LinearLayout replyWrite;
    private int lastPosition = -1;
    private String tempReply = "";
    private String tempSon = "";

    JZVideo player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        init();
        Intent intent = getIntent();
        presenter.initData(intent.getStringExtra("VIDEO"));
        //initTranstionAnimation();
    }

    private void initTranstionAnimation(){
        ViewCompat.setTransitionName(player,"video");
        TransitionSet set = new TransitionSet();
        set.addTransition(new ChangeBounds());
        set.addTransition(new ChangeImageTransform());
        set.addTransition(new ChangeTransform());
        set.addTransition(new ChangeTextTransition());
        set.addTarget(player);

        getWindow().setSharedElementEnterTransition(set);
        getWindow().setSharedElementEnterTransition(set);
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
        presenter = new VideoPresenter(this);
        emojiView = (GridView)findViewById(R.id.emoji_View);
        useEmoji = (ImageView)findViewById(R.id.use_emoji);
        useEmoji.setOnClickListener(this);
        emojiAdapter = new EmojiAdapter(this);
        emojiView.setAdapter(emojiAdapter);
        closeEmoji();

        star = (ImageView)findViewById(R.id.star);
        thumb = (ImageView)findViewById(R.id.video_thumb);
        send = (TextView)findViewById(R.id.video_reply_text);
        header = (CircleImageView)findViewById(R.id.video_header);
        nickName = (TextView)findViewById(R.id.nickName);
        player = (JZVideo) findViewById(R.id.player);
        parentLayout = (MyRelativeLayout) findViewById(R.id.parent_layout);
        parentLayout.setLayoutInterface(this);
        commentRecyclerView = (RecyclerView)findViewById(R.id.video_recyclerView);
        adapter = new CommentAdapter(this);
        manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        commentRecyclerView.setAdapter(adapter);
        commentRecyclerView.setLayoutManager(manager);
        replyEdit = (EditText)findViewById(R.id.video_reply_edit);
        replyLayout = (LinearLayout)findViewById(R.id.video_reply_layout);
        replyNormal = (RelativeLayout)findViewById(R.id.video_reply_normal);
        replyWrite = (LinearLayout) findViewById(R.id.video_reply_write);
        replyLayout.setOnClickListener(this);
        replyEdit.setOnClickListener(this);
        send.setOnClickListener(this);
        thumb.setOnClickListener(this);
        star.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_reply_layout:
                useEdit = 1;
                showEdit(0);
                break;
            case R.id.use_emoji:
                emojiClick();
                break;
            case R.id.video_reply_edit:
                closeEmoji();
                break;
            case R.id.video_reply_text:
                if(useEdit==1)
                    presenter.senComment(replyEdit.getText().toString());
                else
                    presenter.onSendOnMessage(currentComment.getFloor(),replyEdit.getText().toString(),lastPosition);
                break;
            case R.id.video_thumb:
                if(isNoLogin())return;
                Log.d(TAG, "onClick: 测试 Like ");
                like();
                break;
            case R.id.star:
                if(isNoLogin())return;
                onClickStar();
        }
    }

    private void onClickStar(){
        isStar = !isStar;
        if(isStar){
            ScaleAnim.startAnim(star,R.drawable.star_1);
        }else {
            ScaleAnim.startAnim(star,R.drawable.star_0);
        }
        presenter.onStar();
    }

    public void showEdit(int postion){
        if(useEdit==2){
            replyEdit.setText(tempSon);
            lastPosition = postion;
        }
        else if(useEdit==1){
            replyEdit.setText(tempReply);
        }
        else{
            replyEdit.setText("");
            lastPosition = postion;
        }
        replyEdit.setSelection(replyEdit.getText().toString().length());
        closeEmoji();
        replyEdit.requestFocus();
        emojiView.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(replyEdit,0);
        showEdit = true;
        replyWrite.setVisibility(View.VISIBLE);
        replyNormal.setVisibility(View.GONE);
    }

    private void hideEdit(){
        showEdit = false;
        hideInputMethod();
        replyWrite.setVisibility(View.GONE);
        replyNormal.setVisibility(View.VISIBLE);
        if(useEdit==2)
            tempSon = replyEdit.getText().toString();
        else if(useEdit==1)
            tempReply = replyEdit.getText().toString();
        useEdit = 0;
    }

    private static final String TAG = "测试";

    @Override
    public void touch(MotionEvent event) {
        if(event.getY()<replyWrite.getTop()){
            hideEdit();
        }
    }

    @Override
    public void getEmoji(String emoji) {
        int index = replyEdit.getSelectionStart();
        Editable editable = replyEdit.getText();
        editable.insert(index,emoji);
    }

    private void closeEmoji(){
        emojiView.setVisibility(View.GONE);
        isUseEmoji = false;
    }

    private void emojiClick(){
        if(!isUseEmoji){
            hideInputMethod();
            new Thread(this::openEmoji).start();
        }else {
            closeEmoji();
        }
    }

    private synchronized void openEmoji(){
        try {
            Thread.sleep(100);
            runOnUiThread(()->emojiView.setVisibility(View.VISIBLE));
            isUseEmoji = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void hideInputMethod(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    @Override
    public void playVideo(String uri,String title) {
        Glide.with(this).load(uri).into(player.thumbImageView);
        player.setUp(uri,JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL,title);
        player.startButton.performClick();
    }

    @Override
    public void setHeader(String uri) {
        Glide.with(this).load(uri).into(header);
    }

    @Override
    public void setNickName(String nickName) {
        this.nickName.setText(nickName);
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public int insertComment(ReplyBean reply, Boolean isRoll) {
        int position = adapter.insertComment(reply);
        if(isRoll){
            manager.scrollToPosition(position);
        }
        tempReply = "";
        hideEdit();
        return position;
    }

    @Override
    public void sendSonResult(int ResultCode, int position, int level, String msg, String nickName) {
        switch (level){
            case 1:
                adapter.updateSonReply(msg,nickName,position,level);
                break;
            case 2:
                adapter.updateSonReply(msg,nickName,position,level);
                break;
            default:
                adapter.updateSonReply(msg,nickName,position,level);
                break;
        }
        hideEdit();
        replyEdit.setText("");
        tempSon = "";
    }

    @Override
    public void likeResult(int resultCode, boolean like) {
        if(resultCode == 1){
            this.like = like;
            if(like)
                thumb.setImageResource(R.drawable.thumb_red);
            else
                thumb.setImageResource(R.drawable.thumb);
        }
    }

    @Override
    public void onStarResult(int resultCode, boolean star) {
        if(resultCode == 1){
            isStar = star;
            if(star)
                this.star.setImageResource(R.drawable.star_1);
            else
                this.star.setImageResource(R.drawable.star_0);
        }
    }

    @Override
    public void likeCommentResult(int resultCode, int position, boolean like) {
        if(resultCode == 1){
            adapter.updateCommentLike(position,like);
        }
    }

    @Override
    public void setCommentLikeCount(int position, int count) {
        adapter.setCommentLikeCount(position,count);
    }

    @Override
    public void setCommentLike(int position, boolean like) {
        adapter.setCommentLike(position,like);
    }

    @Override
    public void showSonEdit(video_comment comment, int position) {
        useEdit = 2;
        currentComment = comment;
        if(lastPosition!=position&&lastPosition!=-1){
            useEdit = 3;
            replyEdit.setText("");
        }
        showEdit(position);
    }

    @Override
    public void onCommentLike(int position, int floor) {
        presenter.onClickCommentLike(floor,position);
    }

    private void like(){
        like = !like;
        if(like){
            ScaleAnim.startAnim(thumb,R.drawable.thumb_red);
        }else {
            ScaleAnim.startAnim(thumb,R.drawable.thumb);
        }
        presenter.onClickLike();
    }

    private boolean isNoLogin(){
        return !BmobUser.isLogin();
    }
}
