package com.lin.meet.camera_demo;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.lin.meet.IntroductionPage.IntroductionActivity;
import com.lin.meet.R;
import com.lin.meet.history.HistoryActivity;
import com.lin.meet.main.DataBase;
import com.lin.meet.main.DataBaseModel;
import com.lin.meet.my_util.TFLiteUtil;
import com.lin.meet.setting.CameraSetting;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class CameraActivity extends Activity implements View.OnClickListener, View.OnTouchListener,MyCameraContract.AccListener,MyCameraContract.GravityListener {

    public final static int REQUEST_CODE = 0x01;

    private boolean touching = false;
    private boolean isPause = false;
    private boolean isLife;
    private DataBase dataBase= new DataBaseModel();
    private CameraView cameraView;
    private View containerLayout;
    private ImageView cameraCrop;
    private AccListener accListener;
    private GravityListener gravityListener;
    private ImageView back;
    private ImageView light;
    private ImageView setting;
    private ImageView history;
    private ImageView photo;
    private FloatingActionButton camera_discern;
    private int isOpen = 0;
    private String imagePath;
    private long save_time = 0;
    private SensorManager sensorManager;
    private Sensor sensor;
    private int id1,id2,id3;
    public boolean isFocusing = false;
    public boolean isAutoFocus = true;
    public  boolean AutoFocusing =false;
    private boolean useIntorduction = false;
    private View view_1;
    private View view_2;
    private View view_3;
    private int values_x[]=new int[]{0,0,0},values_y=0;
    private TFLiteUtil tflite;

    public boolean isDialogShow = false;
    ImageView dialog_image[]=new ImageView[3];
    TextView dialog_text[][]=new TextView[3][3];

    private float roll_x;
    private float roll_y;
    private float sensor_x;
    private float sensor_y;
    private float all_roll_x = 3;
    private float all_roll_y = 2;
    private boolean start_sensor = false;
    private RequestOptions requestOptions = new RequestOptions();

    private float oldDisk = 1f;

    public boolean AutoFocusCamera = true ,noSavePicture = true;

    private long MINTIME = 100;
    private long lastSensorTime = 0;

    public boolean setting_onAutoCamera = true;
    public boolean setting_onTouchCamera = true;
    public boolean setting_onGravity = true;
    public boolean setting_onSave = true;
    public boolean setting_onLocation = true;
    public static void startCameraActivity(Context context) {
        ((Activity) context).startActivityForResult(new Intent(context, CameraActivity.class), REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,
                WindowManager.LayoutParams. FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);
        setSetting();
        init();
    }

    private void init() {
        containerLayout = (LinearLayout) findViewById(R.id.container_layout);
        cameraCrop = (ImageView) findViewById(R.id.camera_crop);
        back = (ImageView) findViewById(R.id.back);
        light = (ImageView) findViewById(R.id.light);
        setting = (ImageView) findViewById(R.id.camera_setting);
        camera_discern = (FloatingActionButton) findViewById(R.id.camera_discern);
        cameraView = (CameraView) findViewById(R.id.camera_view);
        history = (ImageView) findViewById(R.id.open_history);
        photo = (ImageView) findViewById(R.id.open_photo);
        view_1 = (View)findViewById(R.id.view_1);
        view_2 = (View)findViewById(R.id.view_2);
        view_3 = (View)findViewById(R.id.view_3);
        initDialog(view_1,0,true);
        initDialog(view_2,1,true);
        initDialog(view_3,2,false);
        requestOptions.placeholder(R.mipmap.load);
        requestOptions.error(R.mipmap.error);
        back.setOnClickListener(this);
        light.setOnClickListener(this);
        setting.setOnClickListener(this);
        history.setOnClickListener(this);
        photo.setOnClickListener(this);
        camera_discern.setOnClickListener(this);
        cameraView.setInstence(this);
        view_1.setOnClickListener(this);
        view_2.setOnClickListener(this);
        view_3.setOnClickListener(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        tflite=new TFLiteUtil(getApplicationContext());
        tflite.init();
        tflite.readCacheLabelFromUrl();
        tflite.readCacheLabelFromDescribe();

    }


    @Override
    public void onClick(View view) {
        closeDialog();
        switch (view.getId()) {
            case R.id.camera_discern:
                cameraCrop.setBackground(getResources().getDrawable(R.drawable.shape_cornet_green));
                useIntorduction=true;
                cameraView.focus(false);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.camera_setting:
                isLife=false;
                startActivityForResult(new Intent(this, CameraSetting.class),10000);
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                break;
            case R.id.light:
                if (isOpen == 0) {
                    isOpen = 1;
                    light.setImageResource(R.mipmap.light_open);
                    cameraView.openLight();
                } else {
                    isOpen = 0;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(125);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    light.setImageResource(R.mipmap.light_close);
                                    cameraView.closeLight();
                                }
                            });
                        }
                    }).start();
                }
                break;
            case R.id.open_history:
                isLife=false;
                startActivity(new Intent(this, HistoryActivity.class));
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                break;
            case R.id.open_photo:
                Intent intent = new Intent(this, IntroductionActivity.class);
                intent.putExtra("usePhoto",true);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
                isLife=false;
                break;
            case R.id.view_1:
                closeDialog();
                intoIntorductionActivity(0);
                break;
            case R.id.view_2:
                closeDialog();
                intoIntorductionActivity(1);
                break;
            case R.id.view_3:
                closeDialog();
                intoIntorductionActivity(2);
                break;
                default:
                    break;
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (view.getId()) {
            case R.id.camera_discern:

        }
        return false;
    }


    /**
     * 拍照
     */
    public  void takePhoto() {
        cameraView.takePhoto(new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(final byte[] bytes, final Camera camera) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Bitmap bitmap = null;
                        if (bytes != null) {
                            //读取照片
                            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            //camera.stopPreview();
                        }
                        if (bitmap != null) {
                            //bitmap旋转90度
                            Matrix matrix  = new Matrix();
                            matrix.setRotate(90, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
                            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                            //裁剪
                            Bitmap resBitmap = Bitmap.createBitmap(bitmap,
                                    (int) ((float)1/20 * (float) bitmap.getWidth()),
                                    (int) ((float)1/13*(float) (bitmap.getHeight())),
                                    (int) ( (float)19/20*(float) bitmap.getWidth()),
                                    (int) ((float)10/13*(float) (bitmap.getHeight()))
                            );
                            saveBitmap(resBitmap);
                            if(!bitmap.isRecycled()){
                                bitmap.recycle();
                            }

                            if(isPause)return;
                            camera.startPreview();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(isDialogShow&&!CameraActivity.this.isFinishing()&&isLife){
                                        float temp[][]=tflite.predict_image(getImagePath());
                                        if(AutoFocusing){
                                            AutoFocusing=false;
                                            if (temp[1][1]<=0.03){
                                                isDialogShow=false;
                                                return;
                                            }
                                        }
                                        cameraCrop.setBackground(getResources().getDrawable(R.drawable.shape_cornet_green));
                                        id1=(int)temp[0][1];id2=(int)temp[0][2];id3=(int)temp[0][3];
                                        showDialog((int)temp[0][1],temp[1][1],0,view_1,100);
                                        showDialog((int)temp[0][2],temp[1][2],1,view_2,400);
                                        showDialog((int)temp[0][3],temp[1][3],2,view_3,250);
                                        roll_x=0;roll_y=0;
                                        start_sensor = true;
                                        savePhoto(tflite.mylabel[(int)temp[0][1]],getLocation());
                                        List<PhotoBean> lists = dataBase.findAllPhoto();
                                    }
                                    else if(useIntorduction){
                                        useIntorduction = false;
                                        intoIntorductionActivity(0);
                                    }
                                }
                            });
                        }
                        touching = false;
                        isAutoFocus = true;
                        isFocusing = false;
                    }
                }).start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        closeDialog();
        isLife=true;
        isPause = false;
        accListener=new AccListener(this);
        gravityListener=new GravityListener(this);
        sensorManager.registerListener(gravityListener, sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(accListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
        sensorManager.unregisterListener(accListener);
        sensorManager.unregisterListener(gravityListener);
    }

    /**
     * 将图片保存到本地文件夹
     * @param bitmap
     */
    private  void saveBitmap(Bitmap bitmap){
        String path = Environment.getExternalStorageDirectory().getAbsoluteFile()+ File.separator+"Mybitmap";
        File f = new File(path);
        if(!f.exists()){
            f.mkdir();
        }
        String fileName = "tempImage";
        if(setting_onSave){
            save_time = System.currentTimeMillis();
            fileName = ""+save_time;
        }
        imagePath=path+File.separator+fileName+".jpg";
        File imageCache=new File(imagePath);
        if(imageCache.exists()){
            imageCache.delete();
        }
        try {
            FileOutputStream fout = new FileOutputStream(imagePath);
            BufferedOutputStream  bos= new BufferedOutputStream(fout);
            //.compress 把压缩后的图片放入bos中,第二个为100表示不压缩
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getImagePath(){
        return imagePath;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        int[] location = new int[2];
    }

    /**
     * 判断触摸点是否在Image对话框内
     *
     *
     * @param event
     * @return
     */
    public boolean isCropView(MotionEvent event){
        Rect frame = new Rect();
        ((View)findViewById(R.id.camera_top_view)).getHitRect(frame);
        float x = event.getX();
        float y = event.getY();
        if(frame.contains((int)x,(int)y)){
            return false;
        }
        cameraCrop.getHitRect(frame);
        x = event.getX();
        y = event.getY();
        return frame.contains((int)x,(int)y);
    }

    public boolean isDialogView(MotionEvent event,int id){
        Rect frame = new Rect();
        ((View)findViewById(id)).getHitRect(frame);
        float x = event.getX();
        float y = event.getY();
        return frame.contains((int)x,(int)y);
    }

    /**
     * 自动对焦
     */
    private int STATUS = 0;
    private int STATUS_NONE = 0;
    private int STATUS_MOVE = 1;
    private int STATUS_STATIC = 2;
    private int last_x,last_y,last_z;
    private final double moveIs = 1.4;
    private long last_time;
    private long last_time_dialog;
    private long time_dialog;
    private boolean isCanFosus=false;
    private float last_values_X;
    private float last_values_Y;
    private float values_X,values_Y;
    private boolean isFirst = true;

    private void showDialog(int id,float maybe,int flag,View view,float transX){
        Random r = new Random();
        Glide.with(this).load(tflite.myUrl[id*3-r.nextInt(3)]).apply(requestOptions).into(dialog_image[flag]);
        dialog_text[flag][0].setText(""+tflite.mylabel[id]);
        dialog_text[flag][1].setText("相似度:"+String.format("%.2f",(maybe*100))+"%");
        view.setTranslationX(transX);
        view.setTranslationY(0);
        view.setVisibility(View.VISIBLE);
        String str ;


        if(tflite.describe[id].charAt(0)=='1')
            ((TextView)view.findViewById(R.id.posi)).setVisibility(View.VISIBLE);
        else
            ((TextView)view.findViewById(R.id.posi)).setVisibility(View.GONE);
        if(tflite.describe[id].charAt(1)=='1')
            ((TextView)view.findViewById(R.id.danger)).setVisibility(View.VISIBLE);
        else
            ((TextView)view.findViewById(R.id.danger)).setVisibility(View.GONE);
        if(tflite.describe[id].charAt(2)=='1')
            ((TextView)view.findViewById(R.id.rare)).setVisibility(View.VISIBLE);
        else
            ((TextView)view.findViewById(R.id.rare)).setVisibility(View.GONE);

        ((TextView)view.findViewById(R.id.other2)).setVisibility(View.GONE);
        if((id>=1&&id<=101)||(id>=128&&id<=147)){
            ((TextView)view.findViewById(R.id.other1)).setText("卵生动物");
        }else if((id>=102&&id<=107)||(id>=148&&id<=300)||(id>=301&&id<=389)){
            ((TextView)view.findViewById(R.id.other1)).setText("哺乳动物");
            if((id>=1&&id<=101)||(id>=128&&id<=147)||(id>=1&&id<=101)||(id>=128&&id<=147))
                ((TextView)view.findViewById(R.id.other2)).setVisibility(View.VISIBLE);
        }else if((id>=109&&id<=127)||(id>=328&&id<=330)||(id>=390&&id<=398)){
            ((TextView)view.findViewById(R.id.other1)).setText("海洋动物");
        }else
            ((TextView)view.findViewById(R.id.other1)).setText("昆虫类");
    }

    private boolean down,move,up;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(event.getPointerCount()==1)
                    down = true;
                else
                    down = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount()==1&&down)
                    move = true;
                else{
                    move = false;
                    down = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if(event.getPointerCount()==1&&down&&move)
                    up=true;
                down = false;
                move = false;
                break;
        }
        if(event.getPointerCount()>=2){
            switch (event.getAction()&MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_POINTER_DOWN:
                    oldDisk = getFingerSpacing(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    float newDisk = getFingerSpacing(event);
                    if(newDisk>oldDisk){
                        cameraView.handleZoom(true);
                    }else{
                        cameraView.handleZoom(false);
                    }
            }
        }
        else if(event.getPointerCount()==1&&up){
                up=false;
            if (isCropView(event)){
                if(isDialogShow)
                    closeDialog();
                else{
                    if(touching)return super.onTouchEvent(event);
                    touching = true;
                    if(setting_onTouchCamera)
                        cameraView.focus(true);
                    else
                        cameraView.focus();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private static float getFingerSpacing(MotionEvent event){
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x*x+y*y);
    }

    public void closeDialog(){
        isFirst = true;
        cameraCrop.setBackground(getResources().getDrawable(R.drawable.shape_cornet_white));
        if(view_1.getVisibility()==View.VISIBLE||
                view_2.getVisibility()==View.VISIBLE||
                view_3.getVisibility()==View.VISIBLE){
            isDialogShow=false;
            view_1.setVisibility(View.GONE);
            view_2.setVisibility(View.GONE);
            view_3.setVisibility(View.GONE);
        }
    }

    private void initDialog(View view,int flag,boolean isT){
        if(isT){
            dialog_image[flag]=(ImageView)view.findViewById(R.id.oval_image);
            dialog_text[flag][0]=(TextView) view.findViewById(R.id.oval_1);
            dialog_text[flag][1]=(TextView) view.findViewById(R.id.oval_2);
            return;
        }
        dialog_image[flag]=(ImageView)view.findViewById(R.id.dialog_image);
        dialog_text[flag][0]=(TextView) view.findViewById(R.id.label_1);
        dialog_text[flag][1]=(TextView) view.findViewById(R.id.label_2);
    }

    int last_roll[] = new int[]{0,0,0};

    private int getDp(int a){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a,getResources().getDisplayMetrics());
    }

    @Override
    public void AccListener(SensorEvent sensorEvent) {
        if (sensorEvent.sensor == null||isFocusing||!isAutoFocus)
            return;
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            int x = (int) sensorEvent.values[0];
            int y = (int) sensorEvent.values[1];
            int z = (int) sensorEvent.values[2];
            long time = System.currentTimeMillis();
            if (STATUS != STATUS_NONE){
                int dx = Math.abs(x-last_x);
                int dy = Math.abs(y-last_y);
                int dz = Math.abs(z-last_z);
                double value = Math.sqrt(dx*dx+dy*dy+dz*dz);
                if (value > moveIs){
                    STATUS = STATUS_MOVE;
                    isCanFosus = false;
                    if(value >= 3)
                        closeDialog();
                }else{
                    if(STATUS == STATUS_MOVE&&!isDialogShow){
                        if(!isCanFosus){
                            isCanFosus = true;
                            last_time = time;
                        }
                        else{
                            if(time - last_time>2000&&setting_onAutoCamera){
                                AutoFocusing=true;
                                cameraView.focus(true);
                                STATUS = STATUS_STATIC;
                            }
                        }
                    }
                }
            }else{
                last_time = time;
                STATUS = STATUS_STATIC;
            }
            last_x = x;
            last_y = y;
            last_z = z;
        }
    }


    private int gx,gy,last_gx,last_gy;
    private boolean first = true;
    @Override
    public void GravityListener(SensorEvent sensorEvent) {
        if(!setting_onGravity)
            return;
        if(first){
            last_gx = (int)(sensorEvent.values[0]*2);
            last_gy = (int)sensorEvent.values[1];
            first = false;
        }
        gx = (int)(sensorEvent.values[0]*2);
        gy = (int)sensorEvent.values[1];
        if(!isDialogShow)
            return;
        ObjectAnimator animator ;
        int v1=50,v2=50,v3=50;
        if(gx<last_gx){
            Log.d(TAG, "GravityListener: 提示 A ");
            if(view_1.getTranslationX()==0&&view_3.getTranslationX()<150&&view_2.getTranslationX()>=150)
                v1=0;
            else if(view_1.getTranslationX()==0&&view_3.getTranslationX()==0&&view_2.getTranslationX()<=150){
                v1=0;v3=0;
            }
            v1*=Math.min(2,(last_gx-gx));
            v2*=Math.min(2,(last_gx-gx));
            v3*=Math.min(2,(last_gx-gx));
            startAnimationX(view_1,view_1.getTranslationX(),view_1.getTranslationX()+v1);
            startAnimationX(view_2,view_2.getTranslationX(),view_2.getTranslationX()+v2);
            startAnimationX(view_3,view_3.getTranslationX(),view_3.getTranslationX()+v3);
        }else if(gx>last_gx){
            if(view_2.getTranslationX()==(cameraCrop.getWidth()-view_2.getWidth())&&view_3.getTranslationX()==(cameraCrop.getWidth()-view_3.getWidth())&&view_3.getTranslationX()-view_1.getTranslationX()<150){
                v2=0;v3=0;
            }
            else if(view_2.getTranslationX()==(cameraCrop.getWidth()-view_2.getWidth())&&view_3.getTranslationX()<=view_2.getTranslationX()&&view_2.getTranslationX()-view_1.getTranslationX()<300){
                v2=0;
            }
            v1*=Math.min(2,(gx-last_gx));
            v2*=Math.min(2,(gx-last_gx));
            v3*=Math.min(2,(gx-last_gx));
            startAnimationX(view_1,view_1.getTranslationX(),view_1.getTranslationX()-v1);
            startAnimationX(view_2,view_2.getTranslationX(),view_2.getTranslationX()-v2);
            startAnimationX(view_3,view_3.getTranslationX(),view_3.getTranslationX()-v3);
        }
        if(gy>last_gy){
            startAnimationY(1);
        }else if(gy<last_gy){
            startAnimationY(0);
        }
        last_gx = gx;
        last_gy = gy;
    }
    //0 右 1 左
    private void startAnimationX(final View view, float start, float end){
        if(end%50!=0&&end!=start){
            if(end>start&&end%50>25)
                end = 50 * ((int)(end/50)+1);
            else if(end>start&&end%50<=25)
                end = 50 * ((int)(end/50));
            else if (end<start&&end%50>25)
                end = 50 * ((int)(end/50)+1);
            else if (end<start&&end%50<=25)
                end = 50 * ((int)(end/50));
        }
        if(end<cameraCrop.getLeft()){
            end = cameraCrop.getLeft();
        }else if (end>cameraCrop.getRight()-view.getWidth()){
            end = cameraCrop.getRight()-view.getWidth();
        }
        ObjectAnimator animator = ObjectAnimator.ofFloat(view,"translationX",start,end);
        animator.setDuration(200);
        animator.start();
    }

    private void startAnimationY(int flag){
        float e1,e2,e3,v=0;
        Log.d(TAG, "startAnimationY: 调试结果" +view_2.getTop());
        if(view_2.getTranslationY()<=-200&&flag==0){
            v=0;
        }else if (view_3.getTranslationY()>=200&&flag==1){
            v=0;
        }else if (flag==0){
            v=-100;
        }else if (flag==1){
            v=100;
        }
        e1 = view_1.getTranslationY()+v;
        e2 = view_1.getTranslationY()+v;
        e3 = view_1.getTranslationY()+v;
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(view_1,"translationY",view_1.getTranslationY(),e1);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(view_2,"translationY",view_2.getTranslationY(),e1);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(view_3,"translationY",view_3.getTranslationY(),e1);
        AnimatorSet set = new AnimatorSet();
        set.play(animator1).with(animator2).with(animator3);
        set.setDuration(200);
        set.start();
    }

    private void setSetting(){
        SharedPreferences pref = getSharedPreferences("camera_setting",MODE_PRIVATE);
        setting_onAutoCamera = pref.getBoolean("auto_camera",true);
        setting_onTouchCamera = pref.getBoolean("touch_camera",true);
        setting_onGravity = pref.getBoolean("gravity",true);
        setting_onSave = pref.getBoolean("auto_save",true);
        setting_onLocation = pref.getBoolean("location",true);
        if(!setting_onSave)
            setting_onLocation = false;
    }

    private String getLocation(){
        if(!setting_onSave||!setting_onLocation||(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
            return null;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(provider);
        Geocoder geocoder=new Geocoder(this);
        List places = null;
        try {
            places = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 5);
            System.out.println(places.size()+"");
        } catch (Exception e) {
            e.printStackTrace();
        }
        String placename = "";
        if (places != null && places.size() > 0) {
            placename = ((Address) places.get(0)).getAddressLine(2);
        }
        return placename;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isLife = true;
        isFocusing = false;
        isAutoFocus = true;
        if(requestCode == 10000 && resultCode == 10001){
            setting_onAutoCamera = data.getBooleanExtra("box_1",true);
            setting_onTouchCamera = data.getBooleanExtra("box_2",true);
            setting_onGravity = data.getBooleanExtra("box_3",true);
            setting_onSave = data.getBooleanExtra("box_4",true);
            if(setting_onSave)
                setting_onLocation = data.getBooleanExtra("box_5",false);
        }
    }

    private void savePhoto(String maybe,String location){
        if(!setting_onSave)
            return;
        PhotoBean photo=new PhotoBean();
        photo.setPath(imagePath);
        photo.setTime(save_time);
        photo.setMaybe(maybe);
        photo.setLocation(location);
        photo.save();
    }

    @Override
    public void finish(){
        super.finish();
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }

    private void intoIntorductionActivity(int page){
        float temp[][]=tflite.predict_image(getImagePath());
        savePhoto(tflite.mylabel[(int)temp[0][1]],getLocation());
        int id[]=new int[]{(int)temp[0][1],(int)temp[0][2],(int)temp[0][3]};
        float maybe[]=new float[]{temp[1][1],temp[1][2],temp[1][3]};
        Intent intent = new Intent(CameraActivity.this,IntroductionActivity.class);
        intent.putExtra("usePhoto",false);
        intent.putExtra("imagePath",getImagePath());
        intent.putExtra("id",id);
        intent.putExtra("maybe",maybe);
        intent.putExtra("page",page);
        intent.putExtra("time",save_time);
        startActivity(intent);
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }

}