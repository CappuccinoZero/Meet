package com.lin.meet.main.fragment.Find;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.bumptech.glide.Glide;
import com.lin.meet.R;
import com.lin.meet.bean.MapFlag;
import com.lin.meet.bean.User;
import com.lin.meet.personal.PersonalActivity;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

public class Find extends Fragment implements FindContract.View, View.OnClickListener, BaiduMap.OnMarkerClickListener {
    private FindContract.Presenter presenter;
    private BaiduMapOptions options;
    private View mView = null;
    private MapView mapView = null;
    private BaiduMap map;
    private LocationClient locationClient;
    private double latitude = 0,longitude = 0;
    private LinearLayout mapSend;
    private AlertDialog dialog = null;
    private TextView dialog_cancel,dialog_send;
    private EditText dialog_edit;
    private ImageView dialog_image;
    private String path = null;

    private AlertDialog progressDialog= null;
    private View progressView = null;
    private TextView uploadText =null;

    private boolean isFirst = true;
    private int[] rs = new int[]{R.drawable.h1,R.drawable.h2,R.drawable.h3,R.drawable.h4};
    private List<MapFlag> flags = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    private ImageView map_img;
    private CircleImageView map_head;
    private TextView map_name;
    private TextView map_content;
    private ImageView getLocation;
    private ImageView refreshMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_find, container, false);
        initView(mView);
        initMap();
        presenter.refreshMark();
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.map_send:
                if(BmobUser.isLogin())
                    openDialog();
                else
                    toast("未登录无法发表动态");
                break;
            case R.id.map_dialog_cancel:
                dismisDialog();
                break;
            case R.id.map_dialog_edit:

                break;
            case R.id.map_dialog_image:
                openPhoto();
                break;
            case R.id.map_dialog_send:
                Send();
                break;
            case R.id.cancel_upload:
                closeProgressDialog();
                presenter.cancelUp();
                break;
            case R.id.map_location:
                getLocation();
                break;
            case R .id.map_refresh:
                refreshMap();
                break;
        }
    }

    @Override
    public void toast(@NotNull String msg) {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void SendResult(boolean success) {
        if(success){
            dismisDialog();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Bundle data = marker.getExtraInfo();
        int index = data.getInt("id");
        openContentDialog(index);
        return false;
    }

    @Override
    public void insertFlag(@NotNull MapFlag flag, User user) {
        users.add(user);
        addFlag(flag);
    }

    public class MylocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null || mapView == null){
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(1)
                    .direction(location.getDirection())
                    .latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            map.setMyLocationData(locData);
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            if(isFirst){
                isFirst = false;
                LatLng lng = new LatLng(latitude,longitude);
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(lng);
                map.animateMapStatus(update);
            }
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    private void initView(View view){
        presenter = new FindPresenter(this);
        mapView = (MapView)view.findViewById(R.id.baiduView);
        mapSend = (LinearLayout)view.findViewById(R.id.map_send);
        getLocation = (ImageView)view.findViewById(R.id.map_location);
        refreshMap = (ImageView)view.findViewById(R.id.map_refresh);
        refreshMap.setOnClickListener(this);
        getLocation.setOnClickListener(this);
        mapSend.setOnClickListener(this);
    }

    private void initMap(){
        map = mapView.getMap();
        map.setMyLocationEnabled(true);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);
        option.setCoorType("bd09ll");
        option.setScanSpan(1000);
        locationClient = new LocationClient(getContext());
        locationClient.setLocOption(option);
        locationClient.registerLocationListener(new MylocationListener());
        locationClient.start();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(19f);
        map.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        map.setOnMarkerClickListener(this);

        mapView.removeViewAt(1);
        mapView.showScaleControl(false);
        mapView.showZoomControls(false);
        map.getUiSettings().setCompassEnabled(false);
    }

    @Override
    public void onDestroy() {
        locationClient.stop();
        map.setMyLocationEnabled(false);
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    private void getLocation(){
        LatLng lng = new LatLng(latitude,longitude);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(lng);
        map.animateMapStatus(update);
    }

    public void addFlag(MapFlag flag){
        Bundle data = new Bundle();
        data.putInt("id",flags.size());
        flags.add(flag);

        Random random = new Random();
        LatLng point = new LatLng(flag.getX(),flag.getY());
        final View mView = LayoutInflater.from(getContext()).inflate(R.layout.temp_test_view,null);
        CircleImageView imageView =mView.findViewById(R.id.flag_image);
        imageView.setImageResource(rs[random.nextInt(4)]);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromView(mView);
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .perspective(true)
                .icon(bitmap)
                .flat(true)
                .draggable(true)
                .extraInfo(data);
        map.addOverlay(option);
    }

    public void refreshMap(){
        map.clear();
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.zoom(19f);
        map.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
        LatLng lng = new LatLng(latitude,longitude);
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(lng);
        map.animateMapStatus(update);
        presenter.refreshMark();
    }

    private void openDialog(){
        View mView = getLayoutInflater().inflate(R.layout.send_map_view,null);
        dialog_cancel = mView.findViewById(R.id.map_dialog_cancel);
        dialog_edit = mView.findViewById(R.id.map_dialog_edit);
        dialog_send = mView.findViewById(R.id.map_dialog_send);
        dialog_image = mView.findViewById(R.id.map_dialog_image);
        dialog_cancel.setOnClickListener(this);
        dialog_edit.setOnClickListener(this);
        dialog_send.setOnClickListener(this);
        dialog_image.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setView(mView);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void openContentDialog(int index){
        View mView = getLayoutInflater().inflate(R.layout.map_view,null);
        map_img = mView.findViewById(R.id.map_image);
        map_head = mView.findViewById(R.id.map_header);
        map_name = mView.findViewById(R.id.map_nickName);
        map_content = mView.findViewById(R.id.map_content);
        map_head.setOnClickListener(v-> PersonalActivity.Companion.startOther(getActivity(),users.get(index).getUid()));

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        builder.setView(mView);
        builder.setCancelable(true);
        dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Glide.with(Objects.requireNonNull(getActivity())).load(flags.get(index).getImage()).into(map_img);
        Glide.with(getActivity()).load(users.get(index).getHeaderUri()).into(map_head);
        map_name.setText(users.get(index).getNickName());
        map_content.setText(flags.get(index).getContent());
    }

    private void openPhoto(){
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .previewVideo(true)
                .selectionMode(PictureConfig.SINGLE)
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    private void dismisDialog(){
        if(dialog!=null){
            dialog.dismiss();
            dialog=null;
        }
    }

    private void Send(){
        if(dialog_edit.getText().toString().isEmpty()){
            toast("内容不能为空");
            return;
        }
        if(path==null){
            toast("未选择图片");
            return;
        }
        presenter.onSend(dialog_edit.getText().toString(),path,latitude,longitude);
    }

    @Override
    public void createProgressDialog() {
        progressView = getLayoutInflater().inflate(R.layout.progress_view,null);
        uploadText = progressView.findViewById(R.id.cancel_upload);
        uploadText.setOnClickListener(this);
        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
        build.setCancelable(false);
        build.setView(progressView);
        progressDialog = build.create();
        progressDialog.show();
    }

    @Override
    public void closeProgressDialog() {
        if(progressDialog!=null)
            progressDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode){
                case PictureConfig.CHOOSE_REQUEST:
                    LocalMedia media = PictureSelector.obtainMultipleResult(data).get(0);
                    path = media.getPath();
                    setImage();
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setImage(){
        if(dialog_image!=null&&path!=null){
            Glide.with(getActivity()).load(path).into(dialog_image);
        }
    }
}
