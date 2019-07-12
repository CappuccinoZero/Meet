package com.lin.meet.know

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import cn.bmob.v3.BmobUser
import com.lin.meet.R
import com.lin.meet.bean.KnowBean
import com.lin.meet.db_bean.Reply
import com.lin.meet.personal.PersonalActivity

class KnowAdapter(callback:KnowAdapterCallback): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isAuthor = false
    private var comments:ArrayList<Reply> = ArrayList()
    private var head:KnowBean ?= null
    private var context: Context?= null
    private var nickName = ""
    private var header = ""
    private var callback:KnowAdapterCallback = callback
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        if(context==null)
            context = p0.context
        if(p1 == 1){
            val view = LayoutInflater.from(context).inflate(R.layout.know_head_view,p0,false)
            return KnowViewHolder(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.know_comment,p0,false)
            return KnowCommentViewHolder(view)
        }
    }



    override fun getItemViewType(position: Int): Int {
        if(position==0)
            return 1
        return 0
    }

    override fun getItemCount(): Int {
        if(head!=null)
            return 1 + comments.size
        return 0
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(viewHoler: RecyclerView.ViewHolder, i: Int) {
        if(i==0&&viewHoler is KnowViewHolder){
            viewHoler.nickName.text = nickName
            viewHoler.loadHeader(context!!,header)
            viewHoler.content.text = head!!.content
            viewHoler.time.text = head!!.createdAt
            viewHoler.setSolve(head!!.solve)
            viewHoler.setSolveVisible(isAuthor)
            viewHoler.solve.setOnClickListener {
                if(!head!!.solve)
                    callback.onSolve()
            }
            viewHoler.header.setOnClickListener { PersonalActivity.startOther(context as Activity,head!!.uid) }
        }
        else if(viewHoler is KnowCommentViewHolder){
            viewHoler.loadHeader(context!!,comments[i-1].headUri)
            viewHoler.nickName.text = comments[i-1].nickName
            viewHoler.time.text = comments[i-1].bean.createdAt
            viewHoler.content.text = comments[i-1].bean.content
            viewHoler.setAgree(context!!,comments[i-1].like)
            viewHoler.agree_count.text ="" + comments[i-1].likeCount
            viewHoler.agree.setOnClickListener{
                if(BmobUser.isLogin()) {
                    comments[i - 1].like = !comments[i - 1].like
                    comments[i - 1].likeCount = if (comments[i - 1].like) comments[i - 1].likeCount + 1 else comments[i - 1].likeCount - 1
                    viewHoler.agree_count.text = "" + comments[i - 1].likeCount
                    viewHoler.setAgree(context!!, comments[i - 1].like)
                    callback.onAgree(comments[i - 1].bean.id,comments[i - 1].bean.uid,comments[i - 1].like)
                }
            }
            viewHoler.header.setOnClickListener { PersonalActivity.startOther(context as Activity,comments[i-1].bean.uid) }
        }
    }
    fun initMain(bean:KnowBean) {
        this.head = bean
        notifyDataSetChanged()
    }

    fun initAuthor(nickName: String, header: String) {
        this.nickName = nickName
        this.header = header
        notifyDataSetChanged()
    }

    fun insertComment(comment: Reply):Int {
        comments.add(comment)
        notifyDataSetChanged()
        return comments.size
    }

    fun insertComment(comment: Reply,position:Int):Int {
        comments.add(position,comment)
        notifyDataSetChanged()
        return position
    }

    fun updateAgreeCount(position: Int,count:Int){
        comments[position-1].likeCount = count
        notifyDataSetChanged()
    }

    fun updateAgree(position: Int){
        comments[position-1].like = true
        notifyDataSetChanged()
    }

    fun onSolve(){
        head!!.solve = true
        notifyDataSetChanged()
    }

    fun setSolveVisible(visible:Boolean){
        this.isAuthor = visible
        notifyDataSetChanged()
    }


    interface KnowAdapterCallback{
        fun onAgree(parentId:String,parentUid:String,like:Boolean)
        fun onSolve()
    }
}