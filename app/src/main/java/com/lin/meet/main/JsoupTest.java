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
import org.jsoup.select.Elements;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

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
                    Log.d("测试", "handleMessage:??? "+bean.getTitle());
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsoup_test);
        Log.d("测试", "run: "+"start");
        LoveNews news  = new LoveNews();
        //news.updateNews(handler,1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document document = null;
                try {
                    document = Jsoup.connect("http://www.boqii.com/pet-all/dog/")
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36")
                            .timeout(30000)
                            .cookie("auth", "token")
                            .get();
                    Elements elements = document.getElementsByClass("item cf");
                    Log.d("测试", "run: into"+document.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                }
            }
        }).start();
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


    public static void checkQuietly() {
        try {
            HttpsURLConnection
                    .setDefaultHostnameVerifier(new HostnameVerifier() {
                        public boolean verify(String hostname,
                                              SSLSession session) {
                            return true;
                        }
                    });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context
                    .getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
