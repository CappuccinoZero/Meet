package com.lin.meet.jsoup;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnimalBaike {
    private static String regEx1 = "[\\u4e00-\\u9fa5]";
    public static final int BAIKE_WHAT = 4321;
    private static synchronized void loadBaikeBean(Handler handler, String url){
        try {
            BaikeBean bean = new BaikeBean();
            Document document = null;
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36")
                    .timeout(30000)
                    .cookie("auth", "token")
                    .get();
            Element summaryEle = document.select("div.intro-wrap").get(0);
            Element summary = summaryEle.select("div.left-wrap").get(0);
            bean.setSummary(summary.text());

            Element photoEle = document.select("div.photo-wrap").get(0);
            Element imgListEle = photoEle.select("ul.list").get(0);
            Elements imgList = imgListEle.select("li");
            for(int i=0;i<imgList.size();i++){
                Element img = imgList.get(i).select("img").get(0);
                String uri = img.attr("data-url");
                bean.insertImage(uri);
            }

            Element clearEle = document.select("div.info-inner-wrap").get(0);
            Element leftEle = clearEle.select("div.left-wrap").get(0);
            Elements titles = leftEle.select("div.title");
            for(int i=0;i<titles.size();i++){
                Element titleEle = titles.get(i);
                String title = matchResult(Pattern.compile(regEx1),titleEle.text());
                bean.insertTitle(title);
            }

            Elements descs = leftEle.select("div.description");
            for(int i=0;i<descs.size();i++){
                Element descEle = descs.get(i);
                Elements imgEles = descEle.select("img");
                String img = null;
                String content = descEle.text();
                if(imgEles.size()>0){
                    Element imgEle = imgEles.get(0);
                    img = imgEle.attr("src");
                }
                if(img!=null)
                    bean.insertContent(content,img);
                else
                    bean.insertContent(content);
            }

            Bundle data = new Bundle();
            data.putSerializable("Baike",bean);
            Message msg = new Message();
            msg.what = BAIKE_WHAT;
            msg.setData(data);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static synchronized void loadChongWuBean(Handler handler, String url){
        try {
            BaikeBean bean = new BaikeBean();
            Document document = null;
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36")
                    .timeout(30000)
                    .cookie("auth", "token")
                    .get();
            Element content_start = document.select("div.entry_content").get(0);
            Elements contents = content_start.select("div.entry_detail");
            Elements summary_p = contents.get(0).select("p");
            bean.setSummary(summary_p.get(1).text());
            Log.d("测试", "loadChongWuBean: "+contents.size());
            for(int i=1;i<contents.size();i++){
                String title;
                String content;
                String image;
                Elements ps = contents.get(i).select("p");
                content = ps.get(1).text();
                Element imgEle = ps.get(0).select("img").get(0);
                title = imgEle.attr("alt");
                image = imgEle.attr("src");
                boolean ok = bean.insertContent(content);
                if(!ok)
                    continue;
                bean.insertImage(image);
                bean.insertTitle(title);
            }
            Bundle data = new Bundle();
            data.putSerializable("Baike",bean);
            Message msg = new Message();
            msg.what = BAIKE_WHAT;
            msg.setData(data);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getBaikeBean(Handler handler, String url){
        new Thread(()->{
            loadBaikeBean(handler,url);
        }).start();
    }

    public static void getChongWuBean(Handler handler, String url){
        new Thread(()->{
            loadChongWuBean(handler,url);
        }).start();
    }

    public static String matchResult(Pattern p, String str)
    {
        StringBuilder sb = new StringBuilder();
        Matcher m = p.matcher(str);
        while (m.find())
            for (int i = 0; i <= m.groupCount(); i++)
            {
                sb.append(m.group());
            }
        return sb.toString();
    }

}
