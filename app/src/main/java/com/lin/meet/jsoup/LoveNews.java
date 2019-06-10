package com.lin.meet.jsoup;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * from http://www.lovehhy.net/
 */
public class LoveNews {
    public static final int JSOUP_NEWS_MESSAGE  = 3000;
    public static final int JSOUP_NEWS_MESSAGE_TOP  = 3001;
    private static int MAX_PAGE = 3;
    private static String URL_START = "http://www.lovehhy.net";
    private static String URL = "http://www.lovehhy.net/News/List/XDWSJ/";
    private static String ALL = "?ALL=1";
    public LoveNews(){

    }

    public static synchronized void updateNews(final Handler handler, final int i,boolean top){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document document = null;
                List<LoveNewsBean> newsBeans = new ArrayList<>();
                try {
                    document = Jsoup.connect("http://www.lovehhy.net/News/List/XDWSJ/"+i)
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36")
                            .timeout(30000)
                            .cookie("auth", "token")
                            .get();
                    Elements elements = document.getElementsByClass("post_recommend_new");
                    for(int i=0;i<elements.size();i++){
                        LoveNewsBean bean = new LoveNewsBean();
                        Element recommend = elements.get(i);

                        Elements imgs = recommend.select("img");
                        String img_uri = imgs.attr("data-src");
                        bean.setImg(img_uri);

                        Elements h3 = recommend.select("h3");
                        Elements contents = h3.get(0).select("a");
                        String content_uri = URL_START + contents.attr("href")+ALL;
                        String content_title = contents.text();
                        bean.setTitle(content_title);
                        bean.setContentUri(content_uri);

                        Elements types_1 = recommend.select("div.post_recommend_channel");
                        Elements types_2 = types_1.select("a");
                        String type = types_2.get(0).text();
                        String author = types_2.get(1).text();
                        bean.setType(type);
                        bean.setAuthor(author);

                        Elements times = recommend.select("div.post_recommend_time");
                        String time = times.text();
                        bean.setTime(time);

                        Message msg = new Message();
                        if(top)
                            msg.what = JSOUP_NEWS_MESSAGE_TOP;
                        else
                            msg.what = JSOUP_NEWS_MESSAGE;
                        Bundle data = new Bundle();
                        data.putSerializable("LoveNews",bean);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static synchronized void updateNewsContent(final Handler handler,final LoveNewsBean bean){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String content_uri = bean.getContentUri();
                    Document docContent = Jsoup.connect(content_uri)
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36")
                            .timeout(30000)
                            .cookie("auth", "token")
                            .get();
                    Element eleContent = docContent.getElementById("fontzoom");
                    Elements elesContent = eleContent.select("p");
                    for(int j=0;j<elesContent.size();j++){
                        if(elesContent.get(j).text().length()>2)
                            bean.addContent(elesContent.get(j).text());
                    }
                    Elements elesImgDiv = eleContent.select("div");
                    for(int j=0;j<elesImgDiv.size();j++){
                        Elements elesImg = elesImgDiv.get(j).select("img");
                        bean.addImg(elesImg.attr("data-src"));
                    }

                    Message msg = new Message();
                    msg.what = JSOUP_NEWS_MESSAGE;
                    Bundle data = new Bundle();
                    data.putSerializable("LoveNewsContent",bean);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
