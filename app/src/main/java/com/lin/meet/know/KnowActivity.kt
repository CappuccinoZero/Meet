package com.lin.meet.know

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.hw.ycshareelement.transition.ChangeTextTransition
import com.lin.meet.R
import com.lin.meet.bean.DefaultUtil
import com.lin.meet.bean.KnowBean
import com.lin.meet.db_bean.Reply
import com.youngfeng.snake.annotations.EnableDragToClose
import kotlinx.android.synthetic.main.activity_know.*

@EnableDragToClose
class KnowActivity : AppCompatActivity(), View.OnClickListener,Constarct.View, KnowAdapter.KnowAdapterCallback {
    override fun commentResult(success: Boolean) {
        if(success){
            toast("回答成功")
            comment_edit.setText("")
        }else{
            toast("回答失败")
        }
    }

    override fun isChange() {
        isChange = true
    }

    private var isChange = false
    override fun setSolveVisible(visible: Boolean) {
        adapter!!.setSolveVisible(visible)
    }

    override fun onSolve() {
        AlertDialog.Builder(this)
                .setMessage("确认问题已经得到解决了吗？一旦确认，无法再次修改。")
                .setTitle("确认问题解决")
                .setPositiveButton("确认",DialogInterface.OnClickListener{dialog, which ->
                    adapter!!.onSolve()
                    presenter!!.onSolve()
                })
                .setNeutralButton("取消",null)
                .create()
                .show()
    }

    @SuppressLint("RestrictedApi")
    override fun onBackPressed() {
        open_edit.visibility = View.GONE
        if(isChange)
            setResult(2001)
        super.onBackPressed()
    }

    override fun updateAgree(position: Int) {
        adapter!!.updateAgree(position)
    }

    override fun updateAgreeCount(position: Int, count: Int) {
        adapter!!.updateAgreeCount(position,count)
    }

    override fun onAgree(parentId:String,parentUid:String,like:Boolean) {
        presenter!!.onAgree(parentId,parentUid,like)
    }

    override fun hideEdit() {
        closeEdit()
    }

    override fun clearEdit() {
        comment_edit.setText("")
    }

    override fun insertComment(comment: Reply, roll: Boolean):Int {
        if(roll){
            manager!!.scrollToPosition(adapter!!.insertComment(comment,0))
            return 0
        }else{
            return adapter!!.insertComment(comment)
        }
    }

    override fun toast(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    override fun initMain(bean: KnowBean) {
        adapter!!.initMain(bean)
    }

    override fun initAuthor(nickName: String, header: String) {
        adapter!!.initAuthor(nickName,header)
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.open_edit->{
                showEdit()
            }
            R.id.comment_cancel->{
                closeEdit()
            }
            R.id.comment_send->{
                presenter!!.onSendComment(comment_edit.text.toString())
                closeEdit()
            }
        }
    }

    private var presenter:Constarct.Presenter ?= null
    private var manager:LinearLayoutManager ?= null
    private var adapter:KnowAdapter ?= null
    private var toolbar:Toolbar ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.getWindow()
                .getDecorView()
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_know)
        initView()
        presenter!!.onInitData(intent.getStringExtra("id"),intent.getStringExtra("uid"))
    }

    private fun initTransitionAnimation(){
        val set = TransitionSet()
        set.addTransition(ChangeTransform())
        set.addTransition(ChangeImageTransform())
        set.addTransition(ChangeTextTransition())
        set.addTransition(ChangeBounds())
        set.addTarget(know_img)
        window.sharedElementExitTransition = set
        window.sharedElementEnterTransition = set
    }

    private fun initView(){
        presenter = KnowPresenter(this)
        toolbar = findViewById(R.id.know_toolbar)
        setSupportActionBar(toolbar)
        if(supportActionBar!=null){
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
        var title = intent.getStringExtra("Title")
        if(title==null)
            title = "提问"
        var imgUrl = intent.getStringExtra("img")
        if(imgUrl=="@null"||imgUrl==null){
            imgUrl = DefaultUtil.getRandomAnimalPicture()
        }
        loadHeadImage(imgUrl)
        supportActionBar!!.title = title
        manager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        adapter = KnowAdapter(this)
        know_recycler.layoutManager = manager
        know_recycler.adapter = adapter

        open_edit.setOnClickListener(this)
        comment_cancel.setOnClickListener(this)
        comment_send.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home){
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showEdit(){
        comment_layout.visibility = View.VISIBLE
        comment_edit.setSelection(comment_edit.getText().toString().length)
        comment_edit.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(comment_edit, 0)
    }

    private fun closeEdit(){
        comment_layout.visibility = View.GONE
        hideInputMethod()
    }

    private fun hideInputMethod() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = window.peekDecorView()
        if (null != v) {
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(ev!!.y < comment_layout.top && comment_layout.visibility == View.VISIBLE )
            closeEdit()
        return super.dispatchTouchEvent(ev)
    }

    private fun loadHeadImage(uri:String){
        Glide.with(this).load(uri).into(know_img)
    }


}
