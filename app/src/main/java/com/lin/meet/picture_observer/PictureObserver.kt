package com.lin.meet.picture_observer

import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.bumptech.glide.Glide
import com.hw.ycshareelement.transition.ChangeTextTransition
import com.lin.meet.R
import com.lin.meet.bean.User
import com.lin.meet.bean.video_main
import kotlinx.android.synthetic.main.activity_picture_observer.*

class PictureObserver : AppCompatActivity() ,ObserverContract.View{
    override fun toast(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    override fun setDownloadClickable(clickable: Boolean) {
            download.isClickable = clickable
            download.setImageResource(if(clickable)R.drawable.download else R.drawable.downloading)
    }

    val presenter:ObserverContract.Presenter = ObserverPresenter(this)
    val DEFAULT_NICKNAME = "智能百科"
    override fun updateAhthor(user: User?,showAttention:Boolean) {
        if(!showAttention){
            attention.visibility = View.GONE
            nickName.text = DEFAULT_NICKNAME
        }else{

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_picture_observer)
        initData()
        initTransitionAnimation()
    }

    fun initData(){
        if(intent.getBooleanExtra("haveContent",false)){
            val bean = intent.getSerializableExtra("bean") as video_main
            Glide.with(this).load(bean.uri).into(image)
            name.text = bean.tltle
            content.text = bean.content
            presenter.initAuthorMessage(bean.uid)
        }else{

        }
        back.setOnClickListener { onBackPressed() }
        download.setOnClickListener { presenter.downloadPicture() }
    }

    fun initTransitionAnimation(){
        ViewCompat.setTransitionName(image,"picture")
        val set = TransitionSet()
        set.addTransition(ChangeBounds())
        set.addTransition(ChangeImageTransform())
        set.addTransition(ChangeTransform())
        set.addTransition(ChangeTextTransition())
        window.sharedElementExitTransition = set
        window.sharedElementEnterTransition = set
    }
}
