package com.lin.meet.recommend;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;
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
import com.lin.meet.bean.User;
import com.lin.meet.bean.recommentBean;
import com.lin.meet.jsoup.LoveNews;
import com.lin.meet.jsoup.LoveNewsBean;
import com.lin.meet.override.EmojiAdapter;
import com.lin.meet.override.ScaleAnim;

import org.jetbrains.annotations.NotNull;

import cn.bmob.v3.BmobUser;

public class RecommendActivity extends AppCompatActivity implements View.OnClickListener, EmojiAdapter.EmojiCallback,RecommendConstract.View,RecommendAdapter.RecommendCallback {
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
    private RecommendConstract.Presenter presenter;
    private recommentBean.recomment_comment currentComment;
    private boolean isUseEmoji = false;
    private ImageView useEmoji;
    private GridView emojiView;
    private EmojiAdapter emojiAdapter;
    private View roundView;
    private TextView sendComment;
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
    private LinearLayoutManager manager;
    private ImageView star;
    private ImageView thumb;
    private boolean showEdit = false;
    private String tempReply = "";
    private String tempSon = "";
    private boolean isLike = false;
    private boolean isStar = false;
    private int useEdit = 0;//1->defalut 2->last = now 3->other
    private int lastPosition = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_recommend);
        Intent dataIntent = getIntent();
        LoveNewsBean bean = (LoveNewsBean) dataIntent.getSerializableExtra("LoveNewsBean");
        init(bean);
        initTransitionAnimation();
    }

    private void initTransitionAnimation(){
        ViewCompat.setTransitionName(headImage,"recommend_img");
        ViewCompat.setTransitionName(title,"recommend_text");
        ViewCompat.setTransitionName(roundView,"recommend_view");

        TransitionSet set = new TransitionSet();
        set.addTransition(new ChangeBounds());
        set.addTransition(new ChangeImageTransform());
        set.addTransition(new ChangeTransform());
        set.addTransition(new ChangeTextTransition());
        set.addTarget(headImage);
        set.addTarget(title);
        set.addTarget(roundView);

        getWindow().setSharedElementEnterTransition(set);
        getWindow().setSharedElementEnterTransition(set);
    }

    private void init(LoveNewsBean bean){
        presenter = new RecommendPresenter(this);
        recyclerView = (RecyclerView)findViewById(R.id.recommend_recyclerView);
        headImage = (ImageView)findViewById(R.id.recommend_head_image);
        appBarLayout = (AppBarLayout)findViewById(R.id.recommend_appbarLayout);
        title = (TextView)findViewById(R.id.recommend_title);
        replyNormal = (RelativeLayout)findViewById(R.id.video_reply_normal);
        replyWrite = (LinearLayout) findViewById(R.id.video_reply_write);
        replyEdit = (EditText)findViewById(R.id.video_reply_edit);
        replyLayout = (LinearLayout)findViewById(R.id.video_reply_layout);
        back = (ImageView)findViewById(R.id.recommend_back);
        emojiView = (GridView)findViewById(R.id.emoji_View);
        roundView = (View)findViewById(R.id.recommend_roundView);
        useEmoji = (ImageView)findViewById(R.id.use_emoji);
        sendComment = (TextView)findViewById(R.id.video_reply_text);
        star = (ImageView)findViewById(R.id.star);
        thumb = (ImageView)findViewById(R.id.thumb);
        star.setOnClickListener(this);
        thumb.setOnClickListener(this);
        sendComment.setOnClickListener(this);
        useEmoji.setOnClickListener(this);
        replyLayout.setOnClickListener(this);
        back.setOnClickListener(this);
        replyEdit.setOnClickListener(this);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                title.setTranslationY(i);
                roundView.setTranslationY(i);
            }
        });
        adapter = new RecommendAdapter(this);
        LoveNews.updateNewsContent(handler,bean);
        presenter.checkNet(bean.getContentUri());
        emojiAdapter = new EmojiAdapter(this);
        emojiView.setAdapter(emojiAdapter);
        Glide.with(this).asDrawable().load(bean.getImg()).into(headImage);
        title.setText(bean.getTitle());
        closeEmoji();
    }

    private void initContent(LoveNewsBean bean){
        adapter.initAdapter(bean);
        manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    @Override
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

    private void userSonEdit(){

    }

    @Override
    public void hideEdit(){
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
    public void onBackPressed() {
        setResult(0);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.video_reply_layout:
                useEdit = 1;
                showEdit(0);
                break;
            case R.id.recommend_back:
                onBackPressed();
                break;
            case R.id.use_emoji:
                emojiClick();
                break;
            case R.id.video_reply_edit:
                closeEmoji();
                break;
            case R.id.video_reply_text:
                if(useEdit==1)
                    presenter.onSendMessage(replyEdit.getText().toString());
                else
                    presenter.onSendSonMessage(currentComment,replyEdit.getText().toString(),lastPosition);
                break;
            case R.id.star:
                if(isNoLogin())return;
                isStar = !isStar;
                presenter.onStar();
                star(isStar);
                break;
            case R.id.thumb:
                if(isNoLogin())return;
                isLike = !isLike;
                presenter.onLike(isLike);
                adapter.setLike(isLike);
                like(isLike);
                break;
        }
    }

    @Override
    public void getEmoji(String emoji) {
        addEmoji(emoji);
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

    private void closeEmoji(){
        emojiView.setVisibility(View.GONE);
        isUseEmoji = false;
    }

    private void addEmoji(String emoji){
        int index = replyEdit.getSelectionStart();
        Editable editable = replyEdit.getText();
        editable.insert(index,emoji);
    }

    @Override
    public void toast(@NotNull String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void senMessageResult(int resultCode) {
        if(resultCode == 1){
            toast("发送成功");
            hideEdit();
            tempReply = "";
            replyEdit.setText("");
        }else if(resultCode == 0){
            toast("回复错误,请检查网络");
        }
    }

    @Override
    public void setThumn(int count) {
        adapter.setThumbCount(count);
    }

    @Override
    public void setComment(int count) {
        adapter.setCommentCount(count);
    }

    @Override
    public int insertComment(@NotNull ReplyBean bean) {
        return adapter.insertComment(bean);
    }

    @Override
    public void setCount(int thumb, int comment) {
        adapter.setCount(thumb,comment);
    }


    @Override
    public void moveToPosition(int position) {
        appBarLayout.setExpanded(false);
        manager.scrollToPosition(position);
    }

    @Override
    public void sonSendResult(int id,String msg,int ResultCode) {
        switch (id){
            case 1:
                adapter.updateSonReply(msg, BmobUser.getCurrentUser(User.class).getNickName(),ResultCode,id);
                break;
            case 2:
                adapter.updateSonReply(msg, BmobUser.getCurrentUser(User.class).getNickName(),ResultCode,id);
                break;
            default:
                adapter.updateSonReply(msg, BmobUser.getCurrentUser(User.class).getNickName(),ResultCode,id);
                break;
        }
        hideEdit();
        replyEdit.setText("");
        tempSon = "";
    }

    @Override
    public void showSonEdit(recommentBean.recomment_comment comment, int position) {
        useEdit = 2;
        currentComment = comment;
        if(lastPosition!=position&&lastPosition!=-1){
            useEdit = 3;
            replyEdit.setText("");
        }
        showEdit(position);
    }

    @Override
    public void setIsLike(Boolean isLike) {
        like(true);
        this.isLike = isLike;
        presenter.onLike(isLike);
    }

    @Override
    public void onSonLike(int position, int floor, Boolean isLike) {
        presenter.onLikeSon(floor,position,isLike);
    }

    @Override
    public void like(boolean isLike) {
        if(isLike){
            ScaleAnim.startAnim(thumb,R.drawable.thumb_red);
        }else {
            ScaleAnim.startAnim(thumb,R.drawable.thumb);
        }
    }

    @Override
    public void star(boolean isStar) {
        if(isStar){
            ScaleAnim.startAnim(star,R.drawable.star_1);
        }else {
            ScaleAnim.startAnim(star,R.drawable.star_0);
        }
    }

    @Override
    public void likeError() {
        thumb.setImageResource(R.drawable.thumb);
    }

    @Override
    public void starError() {
        star.setImageResource(R.drawable.star_0);
    }

    @Override
    public void setLikeCount(int count) {
        adapter.setThumbCount(count);
    }

    @Override
    public void setlike(boolean isLike) {
        this.isLike = isLike;
        adapter.setLike(isLike);
        thumb.setImageResource(isLike?R.drawable.thumb_red:R.drawable.thumb);
    }

    @Override
    public void lickSonResult(int resultCode, int position, int count) {
        if(resultCode==1){
            adapter.setSonLike(position,count);
        }
    }

    @Override
    public void setLikeComment(int position, boolean like) {
        adapter.setCommentLike(position,like);
    }

    @Override
    public void setStar(boolean isStar) {
        if(isStar){
            isStar = true;
            star.setImageResource(R.drawable.star_1);
        }
        else {
            isStar = false;
            star.setImageResource(R.drawable.star_0);
        }
    }

    private boolean isNoLogin(){
        return !BmobUser.isLogin();
    }
}
