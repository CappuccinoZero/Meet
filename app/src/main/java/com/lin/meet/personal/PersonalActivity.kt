package com.lin.meet.personal
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import cn.bmob.v3.BmobUser
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.hw.ycshareelement.transition.ChangeTextTransition
import com.lin.meet.R
import com.lin.meet.bean.User
import kotlinx.android.synthetic.main.activity_personal.*
import kotlinx.android.synthetic.main.personal_view.*
import java.text.SimpleDateFormat
import java.util.*

class PersonalActivity : AppCompatActivity(), View.OnClickListener,PersonalContract.View {
    override fun setAge(age: String) {
        perAge.setText(age+"岁")
    }

    override fun getAge(): String {
        return perAge.text.toString()
    }


    override fun toast(str: String) {
            Toast.makeText(this,str, Toast.LENGTH_SHORT).show()
    }

    override fun updateImageView(id: Int,path:String) {
        when(id){
            1->{
                setHeader(path)
            }
            2->{
                setHeadBg(path)
            }
        }
    }

    override fun setHeadBg(uri: String) {
        Glide.with(this).asDrawable().apply(option!!).load(uri).into(perImage)
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
        var birth = str.split("/")
        if(birth.size>=3){
            var str_2 = birth.get(0)+"年"+birth.get(1)+"月"+birth.get(2)+"日"
            perBirthday.text = str_2
        }
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
        Glide.with(this).asDrawable().load(uri).apply(option!!).into(perHeader)
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

    private var isChange = false
    private val format = SimpleDateFormat("yyyy/MM/dd")
    private var presenter:PersonalContract.Presenter?=null
    private var option: RequestOptions?= null
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
            R.id.perBack->onBackPressed()
            R.id.perAttentioned->{//取消关注

            }
            R.id.perAttentionHe->{//关注

            }
            R.id.perEdit->{//编辑
                startActivityForResult(Intent(this,PersonalSetting::class.java),1)
            }
        }
    }

    private fun initTransitionAnimation() {
        ViewCompat.setTransitionName(perImage, "main_bg")
        ViewCompat.setTransitionName(perHeader, "main_header")
        ViewCompat.setTransitionName(perUserName2, "main_text")
        ViewCompat.setTransitionName(perLayoutTop,"main_view")
        ViewCompat.setTransitionName(perContentView,"main_item_layout")

        val set = TransitionSet()
        set.addTransition(ChangeBounds())
        set.addTransition(ChangeImageTransform())
        set.addTransition(ChangeTransform())
        set.addTransition(ChangeTextTransition())
        set.addTarget(perImage)
        set.addTarget(perHeader)
        set.addTarget(perUserName2)
        set.addTarget(perLayoutTop)
        set.addTarget(perContentView)
        window.sharedElementEnterTransition = set
        window.sharedElementEnterTransition = set
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal)
        initCalcul()
        initView()
        initLoadView()
        initTransitionAnimation()
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
        presenter = PersonalPresenter(this)
        option = RequestOptions()

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
                    Log.d("测试","UP？")
                    if(perUserLayout.alpha!=1f||perBlackBg.alpha!=1f){
                        perUserLayout.alpha = 1f
                        perBlackBg.alpha = 0f
                    }
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
        animTrans.duration = 300
        animScaleX.duration = 300
        animScaleY.duration = 300
        var transYFloat = distance_default*0.01f
        var animTransBack = ObjectAnimator.ofFloat(perContent,"TranslationY",distance_default,distance_default*0.99f,distance_default)
        var animUserTransBack = ObjectAnimator.ofFloat(perUserLayout,"TranslationY",perUserLayout.translationY,perUserLayout.translationY-transYFloat,perUserLayout.translationY)
        animTransBack.duration = 200
        animUserTransBack.duration = 200
        if(perContent.translationY > distance_max /2+ distance_default/2)
            anmiSet.play(animTrans).with(animScaleX).with(animScaleY).before(animTransBack).before(animUserTransBack)
        else
            anmiSet.play(animTrans).with(animScaleX).with(animScaleY)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode==1){
            initLoadView()
            isChange = true
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun initLoadView(){
        var user: User? = null
        if(User.isLogin()){
            user = BmobUser.getCurrentUser(User::class.java)
        }
        else{
            toast("用户未登录")
            return
        }

        setHeadBg(user.backgroundUri)
        setHeader(user.headerUri)
        setNumber(user.uid.toString())
        setName(user.nickName)
        setSex(user.sex)
        setWork(user.work)
        setBirthday(user.brith)
        setEmail(user.e_mail)
        setFrom(user.area)
        setSignature(user.signature)
        setIntroduce(user.introduce)
        setAge(calculAge(user.brith))
        setConstellation(calculConstellation(user.brith) )
    }

    private fun calculAge(birth:String):String{
        var dates = birth.split("/")
        if(dates.size<3) return "0"
        var year = dates[0].toInt()
        var month = dates[1].toInt()
        var day = dates[2].toInt()

        var date = Date(System.currentTimeMillis())
        var nowTime = format.format(date)

        var nowDates = nowTime.split("/")
        var now_year = nowDates[0].toInt()
        var now_month = nowDates[1].toInt()
        var now_day = nowDates[2].toInt()

        var age = now_year - year -1
        if(now_month>month)
            return (age+1).toString()
        if(now_month==month&&now_day>day)
            return (age+1).toString()
        return age.toString()
    }

    override fun finish() {
        if(isChange)
            setResult(1)
        super.finish()
    }

    private fun calculConstellation(birth:String):String{
        var dates = birth.split("/")
        if(dates.size<3) return "0"
        var month = dates[1].toInt()
        var day = dates[2].toInt()
        var calcul:Float = month.toFloat() + day.toFloat()/100
        when(calcul){
            in 1.20..2.18->{
               return "水瓶座"
            }
            in 2.19..3.20->{
                return "双鱼座"
            }
            in 3.21..4.19->{
                return "白羊座"
            }
            in 4.20..5.20->{
                return "金牛座"
            }
            in 5.21..6.21->{
                return "双子座"
            }
            in 6.22..7.22->{
                return "巨蟹座"
            }
            in 7.23..8.22->{
                return "狮子座"
            }
            in 8.23..9.22->{
                return "处女座"
            }
            in 9.23..10.23->{
                return "天秤座"
            }
            in 10.24..11.22->{
                return "天蝎座"
            }
            in 11.23..12.21->{
                return "射手座"
            }
            in 12.22..1.19->{
                return "摩羯座"
            }
        }
        return ""
    }
}

