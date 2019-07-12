package com.lin.meet.comment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import cn.bmob.v3.BmobUser
import com.bumptech.glide.Glide
import com.lin.meet.R
import com.lin.meet.db_bean.Reply
import com.lin.meet.override.EmojiAdapter
import com.lin.meet.override.ScaleAnim
import com.youngfeng.snake.annotations.EnableDragToClose
import kotlinx.android.synthetic.main.activity_comment.*

@EnableDragToClose
class CommentActivity : AppCompatActivity(),ComView ,EmojiAdapter.EmojiCallback, View.OnClickListener {
    override fun sendResult(success: Boolean,reply: Reply) {
        if(success){
            editText = ""
            adapter.insertComment(0,reply)
            CommentCount(++replyCount)
        }
    }

    override fun toast(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show()
    }

    override fun CommentCount(count: Int) {
        if(count>0){
            replyCount = count
            commentCount.text = replyCount.toString() + "条回复"
        }
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.replyLayout->{
                showEdit()
            }
            R.id.emoji->{
                emojiClick()
            }
            R.id.replySend->{
                presenter.sendComment(replyEdit.text.toString())
                hideEdit()
            }
            R.id.thumbBottom ->{
                clickLike()
            }
            R.id.thumbLayout->{
                clickLike()
            }
            R.id.back->{
                onBackPressed()
            }
        }
    }



    override fun getEmoji(emoji: String?) {
        val index = replyEdit.selectionStart
        val editable = replyEdit.text
        editable.insert(index, emoji)
    }

    override fun setContent(str: String) {
        content.text = str
    }

    override fun setLikeStatus(like: Boolean) {
        thumbTop.setImageResource(if(like)R.mipmap.like2 else R.id.thumb)
        thumbBottom.setImageResource(if(like)R.mipmap.like else R.id.thumb)

    }

    override fun setLickCount(count: Int) {
        this.count = count
        initThumbCount()
    }

    override fun setTime(str: String) {
        time.text = str
    }

    override fun setHeader(str: String) {
        Glide.with(this).load(str).into(header)
    }

    override fun setNickName(str: String) {
        nickName.text = str
    }

    override fun refreshData() {
        adapter.refreshComment()
    }

    override fun insertComment(i: Int, reply: Reply) {
        adapter.insertComment(i,reply)
    }

    override fun insertComment(reply: Reply) {
        adapter.insertComment(reply)
    }

    private fun initThumbCount(){
        thumbCount.text = count.toString()
    }

    var like = false
    val manager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
    val emojiAdapter = EmojiAdapter(this)
    val presenter:ComPresenter = CommentPresenter(this)
    val adapter = CommentAdapter()
    var isUseEmoji = false
    var editText = ""
    var count = 0
    var initLike = false
    var initCount = 0
    var replyCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        initView()
        presenter.initData(intent.getStringExtra("id"),intent.getIntExtra("flag",-1))
    }

    fun initView(){
        initLike = intent.getBooleanExtra("Like",false)
        like = initLike
        initCount = intent.getIntExtra("Count",0)
        replyCount = initCount
        emojiView.setAdapter(emojiAdapter)
        closeEmoji()
        replyLayout.setOnClickListener(this)
        emoji.setOnClickListener(this)
        back.setOnClickListener(this)
        thumbLayout.setOnClickListener(this)
        thumbBottom.setOnClickListener(this)
        replySend.setOnClickListener(this)
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        header.setOnClickListener { presenter.goToPersonal(this) }
    }

    private fun closeEmoji() {
        emojiView.visibility = View.GONE
        isUseEmoji = false
    }

    private fun emojiClick() {
        if (!isUseEmoji) {
            hideInputMethod()
            Thread(Runnable { this.openEmoji() }).start()
        } else {
            closeEmoji()
        }
    }

    @Synchronized
    private fun openEmoji() {
        try {
            Thread.sleep(100)
            runOnUiThread { emojiView.visibility = View.VISIBLE }
            isUseEmoji = true
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun hideInputMethod() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window.peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    fun showEdit() {
        replyEdit.setText(editText)
        replyEdit.setSelection(replyEdit.text.toString().length)
        closeEmoji()
        replyEdit.requestFocus()
        emojiView.visibility = View.GONE
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(replyEdit, 0)
        replyWrite.visibility = View.VISIBLE
        replyNormal.visibility = View.GONE
    }

    private fun hideEdit() {
        hideInputMethod()
        replyWrite.visibility = View.GONE
        replyNormal.visibility = View.VISIBLE
        editText = replyEdit.text.toString()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.y < replyWrite.top) {
            hideEdit()
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun clickLike(){
        if(!BmobUser.isLogin()){
            toast("用户未登录")
            return
        }
        like = !like
        if(like){
            count++
            ScaleAnim.startAnim(thumbTop, R.mipmap.like2)
            ScaleAnim.startAnim(thumbBottom, R.mipmap.like2)
        }else{
            count--
            ScaleAnim.startAnim(thumbTop, R.mipmap.like)
            ScaleAnim.startAnim(thumbBottom, R.mipmap.like)
        }
        initThumbCount()
        presenter.likeComment(like)
    }

    override fun onBackPressed() {
        val intent = Intent()
        if(like!=initLike||count!=initCount){
            intent.putExtra("Like",like)
            intent.putExtra("Count",replyCount)
            setResult(2001,intent)
        }
        super.onBackPressed()
        overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out)
    }
}
