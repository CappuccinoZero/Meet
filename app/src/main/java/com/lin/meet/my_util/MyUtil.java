package com.lin.meet.my_util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class MyUtil {

    public static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
            fis.close();
        } else {
            file.createNewFile();
        }
        return size;
    }
    /**
     * 把图片转换为tflite所需的数据格式
     * @param bitmap
     * @param ddims
     * @return
     */
    public static ByteBuffer getScaledMatrix(Bitmap bitmap, int[] ddims) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(ddims[0] * ddims[1] * ddims[2] * ddims[3] * 4);
        imgData.order(ByteOrder.nativeOrder());
        // get image pixel
        int[] pixels = new int[ddims[2] * ddims[3]];
        Bitmap bm = Bitmap.createScaledBitmap(bitmap, ddims[2], ddims[3], false);
        bm.getPixels(pixels, 0, bm.getWidth(), 0, 0, ddims[2], ddims[3]);
        int pixel = 0;
        for (int i = 0; i < ddims[2]; ++i) {
            for (int j = 0; j < ddims[3]; ++j) {
                final int val = pixels[pixel++];
                imgData.putFloat(((((val >> 16) & 0xFF) - 128f) / 128f));
                imgData.putFloat(((((val >> 8) & 0xFF) - 128f) / 128f));
                imgData.putFloat((((val & 0xFF) - 128f) / 128f));
            }
        }

        if (bm.isRecycled()) {
            bm.recycle();
        }
        return imgData;
    }

    /**
     * 打开相册
     * @param activity
     * @param requestCode
     */
    public static void openPhoto(Activity activity, int requestCode){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent,requestCode);
    }

    /**
     * LRU 图片压缩
     * @param path
     * @param context
     * @return
     */
    public  static Bitmap getScaleBitmap(String path,Context context){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(path,options);
        int width=options.outWidth;
        int height=options.outHeight;
        int mWidth=500;
        int mHeight=500;
        options.inSampleSize=1;
        while(width/options.inSampleSize>mWidth||height/options.inSampleSize>mHeight){
            options.inSampleSize*=2;
        }
        options.inJustDecodeBounds=false;
        return BitmapFactory.decodeFile(path,options);
    }

    public int getDp(int a,Context context){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, a,context.getResources().getDisplayMetrics());
    }

    public static String get_path_from_url(Context context,Uri uri){
        String result=null;
        Cursor cursor=context.getContentResolver().query(uri,null,null,null,null);
        if(cursor==null){
            result=uri.getPath();
        }else{
            cursor.moveToFirst();
            int idx=cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result=cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static boolean isSlidetoBottom(RecyclerView recyclerView){
        if(recyclerView==null) return false;
        if(recyclerView.computeVerticalScrollExtent()+recyclerView.computeVerticalScrollOffset()>=recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    public static int getScreenWidth(Activity activity){
        WindowManager manager = activity.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public static int dp2px(Context context, float dpValue) {

        final float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dpValue * scale +0.5f);

    }

    public static String getMD5String(String mdStr){
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(mdStr.getBytes());
            for(byte B:result){
                int number = B&0xff;
                String hex = Integer.toHexString(number);
                if(hex.length()==1)
                    sb.append(0);
                sb.append(hex);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void saveSharedPreferences(Context context, String key, HashMap<String,String> map){
        SharedPreferences.Editor editor = context.getSharedPreferences(key,Context.MODE_PRIVATE).edit();
        for (Map.Entry<String,String> entry:map.entrySet()){
            editor.putString(entry.getKey(),entry.getValue());
        }
        editor.apply();
    }

    public static void saveSharedBooleanPreferences(Context context, String key, HashMap<String,Boolean> map){
        SharedPreferences.Editor editor = context.getSharedPreferences(key,Context.MODE_PRIVATE).edit();
        for (Map.Entry<String,Boolean> entry:map.entrySet()){
            editor.putBoolean(entry.getKey(),entry.getValue());
        }
        editor.apply();
    }


    public static SharedPreferences getShardPreferences(Context context,String key){
        SharedPreferences pre = context.getSharedPreferences(key,Context.MODE_PRIVATE);
        if(pre==null){
            SharedPreferences.Editor editor = pre.edit();
            editor.apply();
        }
        return context.getSharedPreferences(key,Context.MODE_PRIVATE);
    }

    public static boolean checkVideo(String uri){
        String uris[] = new String[]{"avi","wmv","mpeg","mp4","mov","mkv","flv","f4v","m4v","rmvb","rm","3gp","dat","ts","mts","vob"};
        StringBuilder builder = new StringBuilder();
        int start = 0;
        for(int i=uri.length()-1;i>=0;i--){
            if(uri.charAt(i)=='.'){
                start = i+1;
                break;
            }
        }
        for(int i=start;i<uri.length();i++){
            if(uri.charAt(i) == ' ')
                continue;
            builder.append(uri.charAt(i));
        }
        uri = builder.toString();
        for (String uris1 : uris) {
            if (equals(uri, uris1)) {
                return true;
            }
        }
        return false;
    }

    private static boolean equals(String uri1,String uri2){
        if(uri1.length()!=uri2.length())
            return false;
        return uri1.toLowerCase().equals(uri2.toLowerCase());
    }

    public static boolean isEmail(String email){
        return email.matches("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    }
}
