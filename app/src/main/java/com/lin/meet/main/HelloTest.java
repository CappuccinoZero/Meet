package com.lin.meet.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.lin.meet.R;
import com.lin.meet.bean.Baike;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;

public class HelloTest extends AppCompatActivity{
    private String regEx1 = "[\\u4e00-\\u9fa5]";
    private ImageView imageView;
    private List<BmobObject> bmobs = new ArrayList<>();
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1111){
                Baike baike = new Baike();
                Bundle data = msg.getData();
                String name = data.getString("name");
                String img = data.getString("img");
                String url = data.getString("url");
                int id = data.getInt("id");
                baike.setUri(url);
                baike.setImageUri(img);
                baike.setId(id);
                baike.setCnName(name);
                baike.setEnName("");
                baike.setBrief("");
                baike.setType("Chong_Wu");
                bmobs.add(baike);
            }
            if(msg.what == 2222){
                new BmobBatch().insertBatch(bmobs).doBatch(new QueryListListener<BatchResult>() {
                    @Override
                    public void done(List<BatchResult> list, BmobException e) {
                        if(e==null){
                            Log.d("测试成功", "done: "+list.size());
                        }else {
                            Log.d("测试失败", "done: ");
                        }
                    }
                });
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_test);
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
