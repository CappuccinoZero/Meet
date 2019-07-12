package com.lin.meet.drawer_setting

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import cn.bmob.v3.BmobUser
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.lin.meet.R
import com.lin.meet.main.DataBase
import com.lin.meet.main.DataBaseModel
import com.lin.meet.personal.PersonalSetting
import com.lin.meet.setting.CameraSetting
import com.luck.picture.lib.tools.PictureFileUtils
import com.youngfeng.snake.annotations.EnableDragToClose
import kotlinx.android.synthetic.main.activity_main_setting.*
import java.io.File
import java.math.BigDecimal
import kotlin.system.exitProcess


@EnableDragToClose
class MainSettingActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(v: View?) {
        when(v?.id){
            com.lin.meet.R.id.perSetting->{
                if(!BmobUser.isLogin()){
                    Toast.makeText(this,"用户未登录",Toast.LENGTH_SHORT).show()
                    return
                }
                val intent = Intent(this,PersonalSetting::class.java)
                intent.putExtra("fromMain",true)
                startActivity(Intent(intent))
                overridePendingTransition(R.anim.fade_in,R.anim.fade_out)
            }
            com.lin.meet.R.id.cameraSetting->{
                startActivity(Intent(this,CameraSetting::class.java))
            }
            com.lin.meet.R.id.cleanCache->{
                AlertDialog.Builder(this)
                        .setMessage("是否确定清空本地所有的缓存数据？本地占用${getCacheSize(this)}大小的空间。" +
                                "")
                        .setTitle("清空缓存数据")
                        .setPositiveButton("确定",DialogInterface.OnClickListener{
                            dialog: DialogInterface?,i->
                            run {
                                PictureFileUtils.deleteCacheDirFile(applicationContext)
                                clearImageDiskCache(this)
                                clearImageMemoryCache(this)
                                Toast.makeText(this,"清理缓存成功",Toast.LENGTH_SHORT).show()
                            }
                        })
                        .setNeutralButton("取消",null)
                        .create().show()
            }
            com.lin.meet.R.id.cleanHistory->{
                AlertDialog.Builder(this)
                        .setMessage("是否确定清空本地所有的历史识别记录以及缓存？本地识别缓存共占用${getHistorySize(this)}大小的空间。")
                        .setTitle("清空识别记录")
                        .setPositiveButton("确定",DialogInterface.OnClickListener{
                            dialog: DialogInterface?,i->
                            run {
                                val path = Environment.getExternalStorageDirectory().absoluteFile.toString() + File.separator + "Mybitmap"+File.separator
                                val file = File(path)
                                deleteAllFile(file)
                                var dataBase: DataBase = DataBaseModel()
                                dataBase.deletePhotoAll()
                                Toast.makeText(this,"清理历史记录成功",Toast.LENGTH_SHORT).show()
                            }
                        })
                        .setNeutralButton("取消",null)
                        .create().show()
            }
            com.lin.meet.R.id.exitNumber->{
                if(BmobUser.isLogin()){
                    BmobUser.logOut()
                    Toast.makeText(this,"已注销",Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this,"用户未登录",Toast.LENGTH_SHORT).show()
                }

            }
            com.lin.meet.R.id.aboutApp->{
                startActivity(Intent(this, AboutApp::class.java))
            }
            com.lin.meet.R.id.exit->{
                AlertDialog.Builder(this)
                        .setTitle("确定退出"+resources.getString(R.string.app_name))
                        .setPositiveButton("确定",DialogInterface.OnClickListener{
                            dialog: DialogInterface?,i->
                            run {
                                android.os.Process.killProcess(android.os.Process.myPid())
                                exitProcess(0)
                            }
                        })
                        .setNeutralButton("取消",null)
                        .create().show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
        decorView.systemUiVisibility = option
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        setContentView(com.lin.meet.R.layout.activity_main_setting)
        initView()
    }

    private fun initView(){
        setSupportActionBar(toolbar)
        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeAsUpIndicator(R.mipmap.onback_white)
        }
        perSetting.setOnClickListener(this)
        cameraSetting.setOnClickListener(this)
        cleanCache.setOnClickListener(this)
        cleanHistory.setOnClickListener(this)
        exitNumber.setOnClickListener(this)
        aboutApp.setOnClickListener(this)
        exit.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId==android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    private fun deleteAllFile(file:File){
        val files = file.listFiles()
        if(files!=null&& files.isNotEmpty()){
            for(index in 0 until files.size){
                if(files[index].exists()&&files[index].isDirectory){
                    deleteAllFile(files[index])
                    files[index].delete()
                }else if(files[index].exists()){
                    files[index].delete()
                }
            }
        }
        Toast.makeText(this,"清理识别历史成功",Toast.LENGTH_SHORT).show()
    }

    fun clearImageDiskCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Thread(Runnable {
                    Glide.get(context).clearDiskCache()
                }).start()
            } else {
                Glide.get(context).clearDiskCache()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearImageMemoryCache(context: Context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context).clearMemory()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getCacheSize(context: Context): String {
        try {
            return getFormatSize(getFolderSize(File(context.cacheDir.toString() + "/" + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR)).toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0"
    }

    fun getHistorySize(context: Context): String {
        try {
            val path = Environment.getExternalStorageDirectory().absoluteFile.toString() + File.separator + "Mybitmap"+File.separator
            val file = File(path)
            return getFormatSize(getFolderSize(file).toDouble())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "0"
    }

    @Throws(Exception::class)
    private fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (aFileList in fileList) {
                if (aFileList.isDirectory) {
                    size = size + getFolderSize(aFileList)
                } else {
                    size = size + aFileList.length()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return size
    }

    private fun getFormatSize(size: Double): String {

        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return size.toString() + "Byte"
        }

        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(java.lang.Double.toString(kiloByte))
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }

        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(java.lang.Double.toString(megaByte))
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "MB"
        }

        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(java.lang.Double.toString(gigaByte))
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes)

        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }
}
