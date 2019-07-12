package com.lin.meet.ar

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.StaggeredGridLayoutManager
import android.widget.Toast
import com.lin.meet.R
import kotlinx.android.synthetic.main.activity_select_model.*

class SelectModelActivity : AppCompatActivity(),SelectModelAdapter.Callback {
    private var manager = StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
    private var adapter = SelectModelAdapter(this)
    private var selectModel = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_model)
        initView()
        initDate()
    }

    private fun initView(){
        recyclerView.adapter = adapter
        recyclerView.layoutManager = manager
        button.setOnClickListener {
            finish()
        }
    }

    private fun initDate(){
        adapter.addModels(ModelBean(resources.getString(R.string.model_androidRobot),"安卓机器人",R.drawable.default_image))
        adapter.addModels(ModelBean(resources.getString(R.string.model_rooster),"公鸡",R.drawable.default_image))
        adapter.addModels(ModelBean(resources.getString(R.string.model_pig),"猪",R.drawable.default_image))
        adapter.addModels(ModelBean(resources.getString(R.string.model_hatDog),"带帽子的狗",R.drawable.default_image))
    }

    override fun setSelectModel(model:String) {
        selectModel = model
        Toast.makeText(this, "选择了：$model",Toast.LENGTH_SHORT).show()
    }

    private fun exitSelectActivity(){
        val intent = Intent()
        intent.putExtra("Model",selectModel)
        setResult(ARCoreActivity.code,intent)
    }

    override fun finish() {
        exitSelectActivity()
        super.finish()
    }
}
