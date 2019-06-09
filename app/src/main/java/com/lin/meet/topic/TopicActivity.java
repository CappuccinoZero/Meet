package com.lin.meet.topic;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.MenuItem;
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

import com.lin.meet.R;
import com.lin.meet.bean.ReplyBean;
import com.lin.meet.bean.TopicMain;
import com.lin.meet.bean.topic_comment;
import com.lin.meet.override.EmojiAdapter;
import com.lin.meet.override.ScaleAnim;

import cn.bmob.v3.BmobUser;

public class TopicActivity extends AppCompatActivity implements View.OnClickListener,EmojiAdapter.EmojiCallback,TopicConstract.View, TopicAdapter.TopicCallback {
    private String id;
    private topic_comment currentComment;
    private boolean like = false;
    private boolean isStar = false;
    private ImageView thumb;
    private ImageView star;

    private ImageView useEmoji;
    private boolean isUseEmoji = false;
    private GridView emojiView;
    private EmojiAdapter emojiAdapter;
    private String tempReply = "";
    private String tempSon = "";
    private int useEdit = 0;//1->defalut 2->last = now 3->other
    private int lastPosition = -1;

    private TextView reply;
    private LinearLayoutManager manager;
    private TopicConstract.Presenter presenter;
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
        TopicMain bean = (TopicMain) getIntent().getSerializableExtra("bean");
        id = bean.bean.getId();
        init();
        presenter.initData(id);
        adapter.initAdapter(bean);
    }

    private void init(){
        emojiView = (GridView)findViewById(R.id.emoji_View);
        replyNormal = (RelativeLayout)findViewById(R.id.video_reply_normal);
        replyWrite = (LinearLayout) findViewById(R.id.video_reply_write);
        replyEdit = (EditText)findViewById(R.id.video_reply_edit);
        replyLayout = (LinearLayout)findViewById(R.id.video_reply_layout);
        toolbar = (Toolbar) findViewById(R.id.topic_toolbar);
        recyclerView = (RecyclerView)findViewById(R.id.topic_recyclerView);
        useEmoji = (ImageView)findViewById(R.id.use_emoji);
        reply = (TextView)findViewById(R.id.video_reply_text);
        thumb = (ImageView)findViewById(R.id.thumb);
        star = (ImageView)findViewById(R.id.star);
        star.setOnClickListener(this);
        thumb.setOnClickListener(this);
        reply.setOnClickListener(this);
        replyEdit.setOnClickListener(this);
        replyLayout.setOnClickListener(this);
        useEmoji.setOnClickListener(this);
        presenter = new TopicPresenter(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back_x);
        }

        adapter = new TopicAdapter(this);
        manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);

        emojiAdapter = new EmojiAdapter(this);
        emojiView.setAdapter(emojiAdapter);
        closeEmoji();
    }

    private void closeEmoji(){
        emojiView.setVisibility(View.GONE);
        isUseEmoji = false;
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
                useEdit = 1;
                showEdit(0);
                break;
            case R.id.use_emoji:
                emojiClick();
                break;
            case R.id.video_reply_edit:
                closeEmoji();
                break;
            case R.id.video_reply_text://发送
                if(useEdit==1)
                    presenter.senComment(replyEdit.getText().toString());
                else
                    presenter.onSendOnMessage(currentComment.getFloor(),replyEdit.getText().toString(),lastPosition);
                break;
            case R.id.thumb:
                if(isNoLogin())return;
                like();
                break;
            case R.id.star:
                if(isNoLogin())return;
                onClickStar();
                break;
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
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void getEmoji(String emoji) {
        int index = replyEdit.getSelectionStart();
        Editable editable = replyEdit.getText();
        editable.insert(index,emoji);
    }

    @Override
    public void initResult(int resultCode, TopicMain bean) {
        switch (resultCode){
            case -1:
                toast("错误,请检查网络");
                break;
            case 0:
                break;
            case 1:
                adapter.initThumbCount(bean);
                break;
        }
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public int insertComment(ReplyBean reply,Boolean isRoll) {
        int position = adapter.insertComment(reply);
        if(isRoll){
            manager.scrollToPosition(position);
        }
        tempReply = "";
        hideEdit();
        return position;
    }

    @Override
    public void sendSonResult(int resultCode,int position, int level, String msg,String nickName) {
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
    public void setCommentCount(int count) {
        adapter.setCommentCount(count);
    }

    @Override
    public void setThumbCount(int position) {
        adapter.setThumbCount(position);
    }

    @Override
    public void setCommentLike(int position, int count) {
        adapter.setCommentLikeCount(position,count);
    }

    @Override
    public void likeResult(int resultCode, boolean like) {
        if(resultCode == 1){
            if(like)
                adapter.setThumbCount(adapter.getLikeCount()+1);
            else
                adapter.setThumbCount(adapter.getLikeCount()-1);
            adapter.setLike(like);
        }
    }

    @Override
    public void likeCommentResult(int resultCode, int position, boolean like) {
        if(resultCode == 1){
            adapter.updateCommentLike(position,like);
        }
    }

    @Override
    public void setLikeNoAnim(boolean like) {
        if(like){
            adapter.setLike(like);
            this.like = like;
            thumb.setImageResource(R.drawable.thumb_red);
        }
    }

    @Override
    public void setCommentLike(int position, boolean like) {
        adapter.setCommentLike(position,like);
    }

    @Override
    public void initStart() {
        isStar = true;
        star.setImageResource(R.drawable.star_1);
    }

    @Override
    public void onStartResult(int resultCode, boolean star) {
        if(resultCode == 1){
            isStar = star;
        }
    }

    @Override
    public void showSonEdit(topic_comment comment, int position) {
        useEdit = 2;
        currentComment = comment;
        if(lastPosition!=position&&lastPosition!=-1){
            useEdit = 3;
            replyEdit.setText("");
        }
        showEdit(position);
    }

    @Override
    public void setLike(boolean like) {
        if(isNoLogin())return;
        this.like = !like;
        like();
    }

    @Override
    public void onCommentLike(int position, int floor) {
        if(isNoLogin())return;
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
