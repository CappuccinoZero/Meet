package com.lin.meet.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lin.meet.R;
import com.lin.meet.jsoup.BaikeBean;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsoupGet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsoup_get);
        new Thread(()->{
            //doOkGet("http://www.aidongwu.net/xinwen/page/2",null);
            loadBaikeBean("http://www.aidongwu.net/20518.html");
        }).start();
    }

    private static synchronized void loadBaikeBean(String url){
        try {
            BaikeBean bean = new BaikeBean();
            Document document = null;
            document = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36")
                    .timeout(30000)
                    .cookie("auth", "token")
                    .get();
            Elements summaryEle = document.select("div.entry");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String doGet(String url, Map<String,String> map){
        HttpClient httpClient = null;
        HttpGet httpGet = null;
        String result = null;
        try{
            httpClient = new DefaultHttpClient();
            httpGet = new HttpGet();
            List<NameValuePair> list = new ArrayList<>();
            Iterator iterator = map.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String,Object> elem = (Map.Entry<String, Object>) iterator.next();
                list.add(new BasicNameValuePair(elem.getKey(),(String) elem.getValue()));
            }
            if(list.size()>0){
                String param = URLEncodedUtils.format(list,"UTF-8");
                httpGet.setURI(URI.create(url+"?"+param));
            }
            HttpResponse response = httpClient.execute(httpGet);
            String content = getHttpEntityContent(response);
            httpGet.abort();
            return content;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static void doOkGet(String url, Map<String,String> map){
        String uri = null;
        try{
            Request request = new Request.Builder()
                    .get()
                    .url(url)
                    .addHeader("Host","www.aidongwu.net")
                    .addHeader("Upgrade-Insecure-Requests","1")
                    .addHeader("Referer","http://www.aidongwu.net/xinwen")
                    .addHeader("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36")
                    .addHeader("Connection","keep-alive")
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d("测试", "onFailure: ");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //Element element = new Element(response.body().string());
                    //Elements elements = element.select("div.shortcut");
                    //Log.d("测试", "onResponse: "+response.body().string().length());
                    String str = response.body().string();
                    Document document = Jsoup.parse(str);
                    Elements elements = document.select("div.content");
                    i("测试其他", "onResponse: "+elements.toString());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String getHttpEntityContent(HttpResponse response) throws IOException, UnsupportedEncodingException {
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream is = entity.getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line = br.readLine();
            StringBuilder sb = new StringBuilder();
            while (line != null) {
                sb.append(line + "\n");
                line = br.readLine();
            }
            br.close();
            is.close();
            return sb.toString();
        }
        return "";
    }

    public static void i(String tag, String msg) {  //信息太长,分段打印
        //因为String的length是字符数量不是字节数量所以为了防止中文字符过多，
        //  把4*1024的MAX字节打印长度改为2001字符数
        int max_str_length = 2001 - tag.length();
        //大于4000时
        while (msg.length() > max_str_length) {
            Log.i(tag, msg.substring(0, max_str_length));
            msg = msg.substring(max_str_length);
        }
        //剩余部分
        Log.i(tag, msg);
    }
}
