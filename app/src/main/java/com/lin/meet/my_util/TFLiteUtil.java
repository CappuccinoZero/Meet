package com.lin.meet.my_util;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Environment;

import org.tensorflow.lite.Interpreter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class TFLiteUtil {
    private Interpreter lite=null;
    private boolean load_result=false;
    private Context context;
    public String mylabel[]=new String[400];
    public String elabel[]=new String[400];
    public String myUrl[]=new String[1196];
    private int ddims[]={1,3,224,224};
    public String assets_path="lite_images";
    public String[] describe;

    public TFLiteUtil(Context context){
        this.context=context;
    }
    
    public void init(){
        readCacheLabelFromLocalFile();
        copy_file_from_asset(context,assets_path,Environment.getExternalStorageState()+File.separator+assets_path);
        load_model();
    }

    public float[][] predict_image(String image_path){
        Bitmap bmp=MyUtil.getScaleBitmap(image_path,context);
        ByteBuffer inputData=MyUtil.getScaledMatrix(bmp,ddims);
        float[][] labelP=new float[1][1001];
        long start=System.currentTimeMillis();
        lite.run(inputData,labelP);
        long end=System.currentTimeMillis();
        long time=end-start;
        float[] result=new float[labelP[0].length];
        System.arraycopy(labelP[0],0,result,0,labelP[0].length);
        float r[][]=get_max_result(result);
        return r;
    }
    
    private void copy_file_from_asset(Context context, String oldPath, String newPath) {
        try {
            String[] fileNames = context.getAssets().list(oldPath);
            if (fileNames.length > 0) {
                // directory
                File file = new File(newPath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                // copy recursivelyC
                for (String fileName : fileNames) {
                    copy_file_from_asset(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {
                // file
                File file = new File(newPath);
                // if file exists will never copy
                if (file.exists()) {
                    return;
                }

                // copy file to new path
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load_model(){
        try {
            AssetFileDescriptor fileDescriptor=context.getAssets().openFd("mobilenet_v2_1.4_224.tflite");
            FileInputStream inputStream=new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel channel=inputStream.getChannel();
            long start=fileDescriptor.getStartOffset();
            long len=fileDescriptor.getDeclaredLength();
            lite=new Interpreter(channel.map(FileChannel.MapMode.READ_ONLY,start,len));
            lite.setNumThreads(4);
            load_result=true;
        } catch (IOException e) {
            e.printStackTrace();
            load_result=false;
        }
    }

    private void readCacheLabelFromLocalFile(){//负责读标签

        try {
            int i=0;
            AssetManager assetManager=context.getAssets();
            BufferedReader reader=new BufferedReader(new InputStreamReader(assetManager.open("MyLabel.txt")));
            String readLine=null;
            while((readLine=reader.readLine())!=null){
                mylabel[i++] = readLine;
            }
            reader.close();
            i=0;
            assetManager=context.getAssets();
            reader=new BufferedReader(new InputStreamReader(assetManager.open("e_Label.txt")));
            readLine=null;
            while((readLine=reader.readLine())!=null){
                elabel[i++] = readLine;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readCacheLabelFromDescribe(){//负责读标签
        describe=new String[400];
        try {
            int i=0;
            AssetManager assetManager=context.getAssets();
            BufferedReader reader=new BufferedReader(new InputStreamReader(assetManager.open("describe.txt")));
            String readLine=null;
            while((readLine=reader.readLine())!=null){
                describe[i++] = readLine;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] readAndGetLabel(){//负责读标签
        String []temp=new String[400];
        try {
            int i=0;
            AssetManager assetManager=context.getAssets();
            BufferedReader reader=new BufferedReader(new InputStreamReader(assetManager.open("MyLabel.txt")));
            String readLine=null;
            while((readLine=reader.readLine())!=null){
                temp[i++] = readLine;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;
    }

    public String[] readCacheLabelFromUrl(){//负责读标签
        myUrl=new String[1196];
        try {
            int i=0;
            AssetManager assetManager=context.getAssets();
            BufferedReader reader=new BufferedReader(new InputStreamReader(assetManager.open("picture.txt")));
            String readLine=null;
            while((readLine=reader.readLine())!=null){
                myUrl[i++] = readLine;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return myUrl;
    }

    private float[][] get_max_result(float []result){
        float r[][] = new float[2][400];
        for(int i=1;i<400;i++){
            r[0][i]=i;
            r[1][i]=result[i];
        }
        for(int i=1;i<399;i++){
            for (int j=1;j<399-i;j++){
                if(r[1][j]<r[1][j+1]){
                    float temp=r[0][j];
                    r[0][j]=r[0][j+1];
                    r[0][j+1]=temp;
                    temp=r[1][j];
                    r[1][j]=r[1][j+1];
                    r[1][j+1]=temp;
                }
            }
        }
        return r;
    }
}