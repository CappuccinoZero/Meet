package com.lin.meet.bean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.lin.meet.R;

import java.util.Random;

public class DefaultUtil {
    public static String getRandomAnimalPicture(){
        String []uris = new String[]{
                "http://img2.imgtn.bdimg.com/it/u=3362296653,1244173215&fm=26&gp=0.jpg",
                "http://img1.imgtn.bdimg.com/it/u=26151978,2594024839&fm=26&gp=0.jpg",
                "http://img5.imgtn.bdimg.com/it/u=1522494215,3599539552&fm=26&gp=0.jpg",
                "http://img2.imgtn.bdimg.com/it/u=2748765664,2746799336&fm=26&gp=0.jpg",
                "http://img4.imgtn.bdimg.com/it/u=1132913077,1776478761&fm=26&gp=0.jpg",
                "http://img0.imgtn.bdimg.com/it/u=3810678251,3241344329&fm=26&gp=0.jpg",
                "http://img5.imgtn.bdimg.com/it/u=2257220156,4111853203&fm=26&gp=0.jpg",
                "http://img2.imgtn.bdimg.com/it/u=2207724802,2179312622&fm=26&gp=0.jpg",
                "http://img0.imgtn.bdimg.com/it/u=2022361976,3305754182&fm=26&gp=0.jpg",
                "http://img5.imgtn.bdimg.com/it/u=4163417879,784321072&fm=26&gp=0.jpg"
        };
        return uris[new Random().nextInt(uris.length)];
    }
    public static String createUid(String userPhone){
        StringBuilder builder = new StringBuilder(userPhone);
        StringBuilder uid = new StringBuilder();
        String str = String.valueOf(System.currentTimeMillis());
        builder.append("0");
        builder.append(str);
        int lenth = str.length()%4;
        for(int i=0;i<lenth;i++)
            builder.append("0");
        str = builder.toString();
        for(int i=3;i<str.length();i+=4){
            uid.append(getChar(str.charAt(i-3),str.charAt(i-2),str.charAt(i-1),str.charAt(i)));
        }
        return uid.toString();
    }

    private static char getChar(char a,char b,char c,char d){
        int num = (int)(a)+(int)(b)+(int)(c)+(int)(d);
        num -= ((int)('0'))*4;
        if(num<26){
            return (char)(num+'a');
        }else{
            return (char)((num%26)+'A');
        }
    }

    public static View createBottomView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.loading_item_3,null);
        return view;
    }

    public static View createTopView(Context context){
        View view = LayoutInflater.from(context).inflate(R.layout.loading_item_4,null);
        return view;
    }
}
