package com.lin.meet.drawer_footprint

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.lin.meet.R
import com.lin.meet.bean.MapFlag
import com.lin.meet.bean.TopSmoothScroller
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_footprint.*
import java.util.*

class FootprintActivity : AppCompatActivity(),FootConstract.View, BaiduMap.OnMarkerClickListener,FootConstract.ItemCallback {

    override fun insertFlag(flag: MapFlag) {
        val i = adapter.insertMapFlag(flag)
        addFlag(flag,i)
    }

    override fun selectItem(position: Int) {
        val flag = adapter.getMapFlag(position)
        val lng = LatLng(flag.x, flag.y)
        val update = MapStatusUpdateFactory.newLatLng(lng)
        map?.animateMapStatus(update)
    }


    override fun onMarkerClick(marker: Marker?): Boolean {
        val data = marker?.extraInfo
        var i: Int? = data?.getInt("id")
        if(i!=null&&i<adapter.itemCount){
            val scroller = TopSmoothScroller(this)
            scroller.targetPosition = i
            manager.startSmoothScroll(scroller)
            adapter.setItemSelect(i)
            return true
        }
        return false
    }

    override fun toast(msg: String) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }

    val locationClient = LocationClient(this)
    var first = true
    var thread:Thread ?= null
    private val rs = intArrayOf(R.drawable.h1, R.drawable.h2, R.drawable.h3, R.drawable.h4)
    var map:BaiduMap ?= null
    val presenter:FootConstract.Presenter = FootprintPresenter(this)
    val adapter = FootAdapter(this)
    val manager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_footprint)
        initStatusBar()
        initView()
        initMapView()
        presenter.initData()
    }

    private fun initStatusBar(){
        window.statusBarColor = resources.getColor(com.lin.meet.R.color.msg_appbar)
        window.decorView.systemUiVisibility =  View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }

    private fun initView(){
        setSupportActionBar(toolbar)
        if(supportActionBar!=null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(com.lin.meet.my_util.MyUtil.isSlidetoBottom(recyclerView))
                    presenter.insertData()
            }
        })
    }

    private fun initMapView(){
        map = mapView.map
        mapView.removeViewAt(1)
        mapView.showScaleControl(false)
        mapView.showZoomControls(false)
        map?.setMyLocationEnabled(true)
        val option = LocationClientOption()
        option.isOpenGps = true
        option.setCoorType("bd09ll")
        option.setScanSpan(1000)
        locationClient.setLocOption(option)
        locationClient.registerLocationListener(MylocationListener())
        locationClient.start()
        map?.uiSettings?.isCompassEnabled = false
        map?.uiSettings?.isRotateGesturesEnabled = false
        map?.uiSettings?.isZoomGesturesEnabled = false
        map?.uiSettings?.isOverlookingGesturesEnabled = false
        val builder = MapStatus.Builder()
        builder.zoom(18f)
        map?.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
        map?.setOnMarkerClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    fun addFlag(flag: MapFlag,position: Int) {
        val data = Bundle()
        data.putInt("id", position)

        val random = Random()
        val point = LatLng(flag.x, flag.y)
        val mView = LayoutInflater.from(this).inflate(R.layout.temp_test_view, null)
        val imageView = mView.findViewById<CircleImageView>(R.id.flag_image)
        imageView.setImageResource(rs[random.nextInt(4)])
        val bitmap = BitmapDescriptorFactory
                .fromView(mView)
        val option = MarkerOptions()
                .position(point)
                .perspective(true)
                .icon(bitmap)
                .flat(true)
                .draggable(true)
                .extraInfo(data)
        map?.addOverlay(option)
    }

    inner class MylocationListener : BDAbstractLocationListener() {

        override fun onReceiveLocation(location: BDLocation?) {
            if (location == null || mapView == null) {
                return
            }
            if (first) {
                first = false
                val lng = LatLng(location.latitude, location.longitude)
                val update = MapStatusUpdateFactory.newLatLng(lng)
                map?.animateMapStatus(update)
                locationClient.stop()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId==android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}
