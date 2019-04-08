package com.lin.meet.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lin.meet.R;
import com.lin.meet.jsoup.LoveNews;
import com.lin.meet.jsoup.LoveNewsBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupTest extends AppCompatActivity {

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    Bundle data = msg.getData();
                    Log.d("测试", "handleMessage: 收到你的答复:"+data.getString("say"));
                    break;
                case LoveNews.JSOUP_NEWS_MESSAGE:
                    Bundle datas = msg.getData();
                    LoveNewsBean bean = (LoveNewsBean) datas.getSerializable("LoveNews");
                    Log.d("测试", "handleMessage: "+bean.getTitle());
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsoup_test);
        Log.d("测试", "run: "+"start");
        LoveNews news  = new LoveNews();
        news.updateNews(handler,1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document document = null;
                try {
                    document = Jsoup.connect("http://www.lovehhy.net/News/List/XDWSJ").get();
                    Elements elements = document.getElementsByClass("post_recommend_new");
                    for(int i=0;i<elements.size();i++){
                        Element element = elements.get(i);
                        Elements elements1 = element.select("h3");
                        Element element1 = elements1.get(0);
                        Elements elements2 = element1.select("a");
                        Element element2 = elements2.get(0);
                        String[] str = element2.text().split("、",2);
                        Log.d("测试", "run: "+str[1]);
                    }
                } catch (Exception e) {
                    Log.d("测试", "run: "+"BUG");
                    e.printStackTrace();
                }finally {
                    Log.d("测试", "run: "+"final");
                }
            }
        });
        senMsg("我最后一次不会不会不会不喜欢你",handler);
    }

    private void senMsg(final String say,final Handler newHandler){
        if(true)return;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(3*1000);
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("say",say);
                    msg.setData(data);
                    msg.what = 1;
                    newHandler.sendMessage(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
