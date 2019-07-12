package com.lin.meet.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.lin.meet.R;
import com.youngfeng.snake.annotations.EnableDragToClose;

@EnableDragToClose
public class WebActivity extends AppCompatActivity {
    public static WebActivity instance = null;
    private WebView webView;
    private String myUrl;
    private long time = 0;
    private View back,close;
    private TextView search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web);
        instance = this;
        Intent intent = getIntent();
        myUrl = intent.getStringExtra("url");
        init();
    }

    private void init(){
        webView = (WebView)findViewById(R.id.webView);
        back = (View)findViewById(R.id.back);
        close = (View)findViewById(R.id.close);
        search = (TextView) findViewById(R.id.search);
        webView.loadUrl(myUrl);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {  //表示按返回键
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });
        back.setOnClickListener(v->{
            if (webView.canGoBack()) {
                webView.goBack();
                webView.goBack();
            }
        });
        close.setOnClickListener(v->{
            finish();
        });
        search.setOnClickListener(v->{
            getWindow().setExitTransition(new Explode());
            Pair<View,String> pair = new Pair<>(search,"search");
            ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation(this,pair);
            Intent intent = new Intent(this, MainSearchActivity.class);
            startActivity(intent,compat.toBundle());
        });
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if(webView!=null){
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }


}
