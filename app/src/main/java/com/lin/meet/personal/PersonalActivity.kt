package com.lin.meet.personal
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.lin.meet.R
import kotlinx.android.synthetic.main.activity_personal.*
import kotlinx.android.synthetic.main.personal_view.*

class PersonalActivity : AppCompatActivity(), View.OnClickListener,PersonalContract.View {
    override fun setHeadBg(uri: String) {
        Glide.with(this).asDrawable().load(uri).into(perImage)
    }

    override fun setNumber(str: String) {
        perId.setText(str)
    }

    override fun getNumber(): String {
        return perId.text.toString()
    }

    override fun setName(str: String) {
        perName.setText(str)
        perUserName.setText(str)
        perUserName2.text = str
    }

    override fun getName(): String {
        return perName.text.toString()
    }

    override fun setSex(str: String) {
        perSex2.text = str
        perSex.text = str
    }

    override fun getSex(): String {
        return perSex2.text.toString()
    }

    override fun setWork(str: String) {
        perWork.text = str
    }

    override fun getWork(): String {
        return perWork.text.toString()
    }

    override fun setBirthday(str: String) {
        perBirthday.text = str
        perAge.text = "0"+"岁"
    }

    override fun getBirthday(): String {
        return perBirthday.text.toString()
    }

    override fun setEmail(str: String) {
        perEmail.text = str
    }

    override fun getEmail(): String {
        return perEmail.text.toString()
    }

    override fun setConstellation(str: String) {
        perConstellation.text = str
    }

    override fun getConstellation(): String {
        return perConstellation.text.toString()
    }

    override fun setFrom(str: String) {
        perFrom.text = str
        perFrom2.text = str
    }

    override fun getFrom(): String {
        return perFrom2.text.toString()
    }

    override fun setSignature(str: String) {
        perSignature.text = str
    }

    override fun getSignature(): String {
        return perSignature.text.toString()
    }

    override fun setIntroduce(str: String) {
        perIntroduce.text = str
    }

    override fun getIntroduce(): String {
        return perSignature.text.toString()
    }

    override fun setHeader(uri: String) {
        Glide.with(this).asDrawable().load(uri).into(perHeader)
    }

    override fun setAttend(str: String) {
        perAttention.text = "关注:"+str
    }

    override fun getAttend(): String {
        return perAttention.text.toString()
    }

    override fun setFans(str: String) {
        perFan.text = str
    }

    override fun getFans(): String {
        return perFan.text.toString()
    }

    private var isFirst:Boolean = true
    private var isDefault:Boolean = true
    private var lastY:Int = 0
    private var distance_default = 0f
    private var distance_max = 0f
    private var scale_max = 0f
    private var alpha_max = 0.5f
    private var show_title_height = 0f
    private var distance_min = 0f
    private var isAnimation = false
    private var isPointUp = false
    override fun showAttentionHe() {
        perEdit.visibility = View.GONE
        perAttentionHe.visibility = View.VISIBLE
        perAttention.visibility = View.GONE
    }

    override fun showAttentioned() {
        perEdit.visibility = View.GONE
        perAttentionHe.visibility = View.GONE
        perAttention.visibility = View.VISIBLE
    }

    override fun showEdit() {
        perEdit.visibility = View.VISIBLE
        perAttentionHe.visibility = View.GONE
        perAttention.visibility = View.GONE
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.perBack->finish()
            R.id.perAttentioned->{//取消关注

            }
            R.id.perAttentionHe->{//关注

            }
            R.id.perEdit->{//编辑
                startActivity(Intent(this,PersonalSetting::class.java))
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        initCalcul()
        initView()
    }

    private fun initCalcul(){
        var window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT

        distance_default = resources.getDimension(R.dimen.per_roll_default_transY)
        distance_max = resources.getDimension(R.dimen.per_roll_maxdistance)
        show_title_height = resources.getDimension(R.dimen.per_show_title)
        distance_min = resources.getDimension(R.dimen.per_roll_mindistance)
        perContent.translationY = distance_default
        perImage.pivotY = 0f

        var height = resources.getDimension(R.dimen.per_roll_maxdistance)
        var dy = distance_max - distance_default
        scale_max = dy/height*1.5f

        perBlackBg.visibility = View.VISIBLE
        perBlackBg.alpha = 0f
    }

    private fun initView(){
        perAttentionHe.setOnClickListener(this)
        perAttentioned.setOnClickListener(this)
        perEdit.setOnClickListener(this)
        perBack.setOnClickListener(this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(isAnimation||event!!.pointerCount>=2){
            isPointUp = true
            return super.onTouchEvent(event)
        }
        if((perImage.pivotX).toInt()!=(perImage.width/2))
            perImage.pivotX = (perImage.width/2).toFloat()
        when(event.action){
            MotionEvent.ACTION_DOWN->{
                lastY = (event.y).toInt()
            }
            MotionEvent.ACTION_MOVE->{
                if(isPointUp){
                    lastY = (event.y).toInt()
                    isPointUp =false
                    return super.onTouchEvent(event)
                }
                var y = (event.y).toInt()
                var dy = y-lastY
                if (Math.abs(dy)<=distance_min)
                    return super.onTouchEvent(event)
                var translationY = perContent.translationY + dy
                var lastTranslationY = perContent.translationY
                if(translationY<=0)
                    perContent.translationY = 0f
                else if (translationY>=distance_max){
                    perContent.translationY = distance_max
                }else
                    perContent.translationY = translationY
                if(perContent.translationY>=distance_default){
                    if(lastTranslationY<distance_default) lastTranslationY = distance_default
                    var scaleY = ((perContent.translationY-distance_default)/(distance_max-distance_default))*scale_max
                    perImage.scaleY = 1+scaleY
                    perImage.scaleX = 1+scaleY
                    perUserLayout.translationY=0f
                }else{
                    perImage.scaleY = 1f
                    perImage.scaleX = 1f
                    var bg_alpha = (1f-(perContent.translationY/distance_default))*alpha_max
                    perBlackBg.alpha = bg_alpha

                    perUserLayout.translationY=perContent.translationY-distance_default
                    var user_alpha = perContent.translationY/distance_default
                    perUserLayout.alpha = user_alpha
                }
                perUserName.visibility = if(perContent.translationY<=show_title_height) View.VISIBLE else View.GONE
                lastY = y
            }
            MotionEvent.ACTION_UP->{
                if (perContent.translationY>=distance_default){
                    startCardAnimation(event)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun startCardAnimation(event: MotionEvent?){
        var anmiSet = AnimatorSet()
        var animTrans = ObjectAnimator.ofFloat(perContent,"translationY",perContent.translationY,distance_default)
        var animScaleX = ObjectAnimator.ofFloat(perImage,"ScaleX",perImage.scaleX,1f)
        var animScaleY = ObjectAnimator.ofFloat(perImage,"ScaleY",perImage.scaleY,1f)
        anmiSet.play(animTrans).with(animScaleX).with(animScaleY)
        anmiSet.duration = 300
        anmiSet.addListener(object : AnimatorListenerAdapter(){
            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                isAnimation = false
                lastY = (event!!.y).toInt()
            }
            override fun onAnimationCancel(animation: Animator?) {
                super.onAnimationCancel(animation)
                isAnimation = false
                lastY = (event!!.y).toInt()
            }
            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                isAnimation = true
            }
        })
        anmiSet.start()
    }
}

