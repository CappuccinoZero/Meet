package com.lin.meet.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.lin.meet.bean.TopicMain;
import com.lin.meet.db_bean.Reply;
import com.lin.meet.db_bean.comment;
import com.lin.meet.override.EmojiAdapter;
import com.lin.meet.override.ScaleAnim;
import com.youngfeng.snake.annotations.EnableDragToClose;

import cn.bmob.v3.BmobUser;

@EnableDragToClose
public class TopicActivity extends AppCompatActivity implements View.OnClickListener,EmojiAdapter.EmojiCallback,TopicConstract.View, TopicAdapter.TopicCallback {
    private String id;
    private comment currentComment;
    private boolean like = false;
    private boolean isStar = false;
    private ImageView thumb;
    private ImageView star;
    private boolean initLike = false;

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
        init();
        adapter.initAdapter(bean);
        if(bean!=null){
            id = bean.bean.getId();
            presenter.initData(id,false,bean.bean.getUid());
        }
        else{
            id = getIntent().getStringExtra("ID");
            presenter.initData(id,getIntent().getBooleanExtra("isSender",true),2);
        }
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
            actionBar.setHomeAsUpIndicator(R.mipmap.onback_black);
        }

        adapter = new TopicAdapter(this);
        manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(manager);
        if(getIntent().getBooleanExtra("comment",false))
            replyLayout.post(()->replyLayout.performClick());
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
                    presenter.onSendOnMessage(currentComment,replyEdit.getText().toString(),lastPosition);
                hideEdit();
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
            ScaleAnim.startAnim(star,R.mipmap.love2);
        }else {
            ScaleAnim.startAnim(star,R.mipmap.love);
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
    public void onBackPressed() {
        if(like!=initLike){
            setResult(2001);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
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
                break;
            case 2:
                adapter.initAdapter(bean);
                adapter.initThumbCount(bean);
                break;
        }
    }

    @Override
    public void toast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public int insertComment(Reply reply, Boolean isRoll) {
        int position = adapter.insertComment(reply);
        if(isRoll){
            manager.scrollToPosition(position);
        }
        tempReply = "";
        return position;
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
    public void setLikeNoAnim(boolean like) {
        initLike = like;
        if(like){
            adapter.setLike(like);
            this.like = like;
            thumb.setImageResource(R.mipmap.like2);
        }
    }

    @Override
    public void initStart() {
        isStar = true;
        star.setImageResource(R.mipmap.like);
    }


    @Override
    public void showSonEdit(comment comment, int position) {
        useEdit = 2;
        currentComment = comment;
        if(lastPosition!=position&&lastPosition!=-1){
            useEdit = 3;
            tempSon = "";
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000&&resultCode==2001){
            adapter.updateLikeStatus(data.getBooleanExtra("Like",false));
            adapter.updateCommentStatus(data.getIntExtra("Count",-1));
        }
    }

    @Override
    public void onCommentLike(String parentId,String parentUid,boolean like) {
        if(isNoLogin())return;
        presenter.onClickCommentLike(parentId,parentUid,like);
    }

    @Override
    public void startComment(Intent intent) {
        startActivityForResult(intent,2000);
        overridePendingTransition(R.anim.bottom_in,R.anim.bottom_out);
    }

    private void like(){
        like = !like;
        if(like){
            ScaleAnim.startAnim(thumb,R.mipmap.like2);
        }else {
            ScaleAnim.startAnim(thumb,R.mipmap.like);
        }
        presenter.onClickLike(like);
        likeResult(1,like);
    }

    private boolean isNoLogin(){
        return !BmobUser.isLogin();
    }

    @Override
    public void senMessageResult(int resultCode,Reply msg) {
        if(resultCode == 1){
            int position = adapter.insertComment(0,msg);
            moveToPosition(position);
            toast("发送成功");
            hideEdit();
            tempReply = "";
            replyEdit.setText("");
        }else if(resultCode == 0){
            toast("回复错误,请检查网络");
        }
    }

    @Override
    public void sonSendResult(int resultCode,int position) {
        if(resultCode==1){
            adapter.addComment(position);
        }
        hideEdit();
        replyEdit.setText("");
        tempSon = "";
    }

    public void moveToPosition(int position) {
        manager.scrollToPosition(position);
    }
}
