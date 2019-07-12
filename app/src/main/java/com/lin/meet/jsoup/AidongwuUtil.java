package com.lin.meet.jsoup;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class AidongwuUtil {
    public static final int JSOUP_AIDONGWU_CONTENT = 3002;
    public static void onGetAidongwuContent(Handler handler, LoveNewsBean bean){
        new Thread(()-> doHttpConnect(handler,bean)).start();
    }

    private static synchronized void doHttpConnect(Handler handler, LoveNewsBean bean){
        try{
            Document document = Jsoup.connect(bean.getContentUri())
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36")
                    .timeout(30000)
                    .cookie("auth", "token")
                    .get();
            Elements contentList = document.select("div.entrycontent").get(0).select("p");
            for(int i = 0;i < contentList.size();i++){
                Element item = contentList.get(i);
                if(item.select("img").size()>0){
                    bean.addImg(item.select("img").get(0).attr("src"));
                }else{
                    bean.addContent(item.text());
                }
            }
            Message msg = new Message();
            msg.what = JSOUP_AIDONGWU_CONTENT;
            Bundle data = new Bundle();
            data.putSerializable("bean",bean);
            msg.setData(data);
            handler.sendMessage(msg);
        }catch (Exception e){}
    }
}
