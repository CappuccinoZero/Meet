package com.lin.meet.ar

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import com.google.ar.core.HitResult
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.lin.meet.R
import kotlinx.android.synthetic.main.activity_arcore.*

class ARCoreActivity : AppCompatActivity(), ModelFragment.Callback {

    companion object{
        const val code = 10000
    }
    private var arFragment: ArFragment?= null
    private var models = HashMap<String,ModelRenderable>()
    private var androidRobot:ModelRenderable ?= null//安卓机器人
    private var rooster:ModelRenderable ?= null //公鸡
    private var pig:ModelRenderable ?= null //猪
    private var hatDog:ModelRenderable ?= null //帽子狗
    private var fish:ModelRenderable ?= null //帽子狗
    private var dolphin:ModelRenderable ?= null //帽子狗
    private var dog:ModelRenderable ?= null //帽子狗
    private var modelFragment:ModelFragment ? = null
    private var selectModel = ""//选择的模型
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if(!checkIsSupportedDeviceOrFinish(this)) return
        setContentView(R.layout.activity_arcore)
        initView()
        buildModel()
    }

    private fun initView(){
        selectModel = resources.getString(R.string.model_pig)
        modelFragment = ModelFragment()
        modelFragment!!.setListener(this)
        val transation = supportFragmentManager.beginTransaction()
        transation.add(R.id.container,modelFragment!!)
        transation.hide(modelFragment!!)
        transation.commit()
        container.post { container.pivotY = container.height.toFloat() }
        arFragment = supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment
        arFragment?.setOnTapArPlaneListener { hitResult, plane, motionEvent ->
            when(selectModel){
                resources.getString(R.string.model_androidRobot)->{
                    setModel(androidRobot,hitResult)
                }
                resources.getString(R.string.model_pig)->{
                    setModel(pig,hitResult)
                }
                resources.getString(R.string.model_hatDog)->{
                    setModel(hatDog,hitResult)
                }
                resources.getString(R.string.model_rooster)->{
                    setModel(rooster,hitResult)
                }
                resources.getString(R.string.model_fish)->{
                    setModel(fish,hitResult)
                }
                resources.getString(R.string.model_dolphin)->{
                    setModel(dolphin,hitResult)
                }
                resources.getString(R.string.model_dog)->{
                    setModel(dog,hitResult)
                }
            }
        }
        button.setOnClickListener {
            if(modelFragment!!.isHidden){
                val transation = supportFragmentManager.beginTransaction()
                transation.show(modelFragment!!)
                transation.commit()
                val animation = ObjectAnimator.ofFloat(container,"scaleY",0f,1f)
                animation.duration = 300
                animation.start()
            }else
                onClose()
            }
        back.setOnClickListener { onBackPressed() }
    }

    private fun setModel(model:ModelRenderable?,hitResult:HitResult){
        if(model==null)
            return
        val anchor = hitResult.createAnchor()
        val anchorNode = AnchorNode(anchor)
        anchorNode.setParent(arFragment?.arSceneView?.scene)

        val andy = TransformableNode(arFragment?.transformationSystem)
        andy.setParent(anchorNode)
        andy.renderable = model
        andy.worldScale = Vector3.one()
        andy.select()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun buildModel(){
        ModelRenderable.builder()
                .setSource(this,R.raw.andy)
                .build()
                .thenAccept {
                    androidRobot = it
                    models[resources.getString(R.string.model_androidRobot)] = androidRobot!!
                }
                .exceptionally { return@exceptionally null }

        ModelRenderable.builder()
                .setSource(this,R.raw.rooster)
                .build()
                .thenAccept {
                    rooster = it
                    models[resources.getString(R.string.model_rooster)] = rooster!!
                }
                .exceptionally { return@exceptionally null }

        ModelRenderable.builder()
                .setSource(this,R.raw.pig)
                .build()
                .thenAccept {
                    pig = it
                    models[resources.getString(R.string.model_pig)] = pig!!
                }
                .exceptionally { return@exceptionally null }

        ModelRenderable.builder()
                .setSource(this,R.raw.model)
                .build()
                .thenAccept {
                    hatDog = it
                    models[resources.getString(R.string.model_hatDog)] = hatDog!!
                }
                .exceptionally { return@exceptionally null }

        ModelRenderable.builder()
                .setSource(this,R.raw.amagot)
                .build()
                .thenAccept {
                    fish = it
                    models[resources.getString(R.string.model_fish)] = hatDog!!
                }
                .exceptionally { return@exceptionally null }

        ModelRenderable.builder()
                .setSource(this,R.raw.ddd)
                .build()
                .thenAccept {
                    dolphin = it
                    models[resources.getString(R.string.model_dolphin)] = hatDog!!
                }
                .exceptionally { return@exceptionally null }

        ModelRenderable.builder()
                .setSource(this,R.raw.shibainu_blend)
                .build()
                .thenAccept {
                    dog = it
                    models[resources.getString(R.string.model_dog)] = hatDog!!
                }
                .exceptionally { return@exceptionally null }
    }


    private fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Toast.makeText(activity, "必须Android7.0及以上版本", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }
        val openGlVersionString = (activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .deviceConfigurationInfo
                .glEsVersion
        if (java.lang.Double.parseDouble(openGlVersionString) < 3.0) {
            Toast.makeText(activity, "必须OpenGL ES 3.0及以上版本", Toast.LENGTH_LONG)
                    .show()
            activity.finish()
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==code&&resultCode== code){
            val resultStr = data?.getStringExtra("Model")
            if(resultStr != null && resultStr.isNotEmpty())
                selectModel = resultStr
        }
    }

    override fun onClose() {
        val animation:ObjectAnimator = ObjectAnimator.ofFloat(container,"scaleY",1f,0f)
        animation.duration = 300
        animation.start()
        animation.addListener(object :Animator.AnimatorListener{
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                val transation = supportFragmentManager.beginTransaction()
                transation.hide(modelFragment!!)
                transation.commit()
            }
        })
    }

    override fun onChange(model: String) {
        onClose()
        selectModel = model
    }

    override fun onBackPressed() {
        if(modelFragment!!.isHidden)
            super.onBackPressed()
        else
            onClose()
    }
}
