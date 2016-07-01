package com.example.lqh.zhrb;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lqh.zhrb.domain.NewsDetialData;
import com.example.lqh.zhrb.utils.CharsetJsonRequest;
import com.example.lqh.zhrb.utils.ParseJson;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ObservableWebView;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;

import org.json.JSONObject;

public class ThemesDetialActivity extends AppCompatActivity implements ObservableScrollViewCallbacks {

    private ObservableWebView webView;
    private RequestQueue mRequestQueue;
    private String newsDetial= "http://news-at.zhihu.com/api/4/news/";
    private NewsDetialData mNewsDetial;
    private Handler handler;
    private int mFlexibleSpaceImageHeight;
    private int mActionBarSize;
    private View mOverlayView;
    private ImageView mImageView;
    private Toolbar toolbar;
    private AppBarLayout appbarlayout;
    private ImageView topImage;
    private ImageView topImageOverlay;
    private TextView topTitle;
    private TextView imageSource;
    private ObservableScrollView mScrollView;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themesnews_detial);
        mRequestQueue= Volley.newRequestQueue(this);
        topImage= (ImageView) findViewById(R.id.top_image);
        topImageOverlay= (ImageView) findViewById(R.id.top_image_overlay);
        topTitle= (TextView) findViewById(R.id.top_title);
        imageSource= (TextView) findViewById(R.id.tv_image_source);
        appbarlayout= (AppBarLayout) findViewById(R.id.appbarlayout);
//        topImage.setVisibility(View.GONE);
//        topImageOverlay.setVisibility(View.GONE);
//        topTitle.setVisibility(View.GONE);
//        imageSource.setVisibility(View.GONE);
        //菜单
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        //给返回键设置监听事件
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //   finish();
                onBackPressed();
            }
        });
        newsDetial=newsDetial+getIntent().getStringExtra("id");
//final TextView textView= (TextView) findViewById(R.id.tv_newsDetial);
        // textView.setText(Html.fromHtml(""));
        webView= (ObservableWebView) findViewById(R.id.webview);
        // webView的设置对象，webview的一般属性都可以通过WebSettings来设置
        // webView的设置对象，webview的一般属性都可以通过WebSettings来设置
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);// 表示支持js
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启DOM storage API 功能
        settings.setDomStorageEnabled(true);
        // 开启database storage API功能
        settings.setDatabaseEnabled(true);
        // 开启Application Cache功能
        settings.setAppCacheEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            // 网页开始加载的时候被调用
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                // progressBar.setVisibility(View.VISIBLE);//
            }

            // 网页加载结束被调用
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // progressBar.setVisibility(View.GONE);// 加载完成之后让进度条消失
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            // 获得所加载的网页的图标
            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }

            // 获得所加载的网页的标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                System.out.println("网页的标题" + title);
            }

            // 加载网页的进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO Auto-generated method stub
                super.onProgressChanged(view, newProgress);
                System.out.println("加载网页的进度" + newProgress);
            }
        });
//        ActionBar actionBar=getSupportActionBar();
//        if (actionBar != null) {
//            // Show the Up button in the action bar.
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                NewsDetialData newsDetialData= (NewsDetialData) msg.obj;
                //  webView.loadUrl(newsDetialData.share_url);
                webView.loadData(newsDetialData.body, "text/html; charset=UTF-8", null);//这种写法可以正确解码
            }
        };
        getMenuList();
        mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
        mScrollView.setScrollViewCallbacks(this);
        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mActionBarSize = getSupportActionBar().getHeight();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.story, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if (id==R.id.action_notification){
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
            return true;
        }else if (id==android.R.id.home){

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取菜单栏的标题
     */
    private void getMenuList() {
        CharsetJsonRequest jor = new CharsetJsonRequest(newsDetial, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mNewsDetial = ParseJson.paserJson(response, NewsDetialData.class);
                        if (mNewsDetial!=null&&handler!=null){
                            Message message=Message.obtain();
                            message.obj=mNewsDetial;
                            handler.sendMessage(message);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });

        mRequestQueue.add(jor);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
        float flexibleRange = mFlexibleSpaceImageHeight - mActionBarSize;
        int minOverlayTransitionY = mActionBarSize - webView.getHeight();
        //    ViewHelper.setTranslationY(webView, ScrollUtils.getFloat(-scrollY, minOverlayTransitionY, 0));
//        ViewHelper.setTranslationY(topImage, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));
//        ViewHelper.setTranslationY(topImageOverlay, ScrollUtils.getFloat(-scrollY / 2, minOverlayTransitionY, 0));
        ViewHelper.setTranslationY(webView, ScrollUtils.getFloat(-scrollY / 2+5, minOverlayTransitionY, 0));
        //ViewHelper.setAlpha(webView, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0, 1));
        // ViewHelper.setAlpha(appbarlayout, ScrollUtils.getFloat((float) scrollY / flexibleRange, 0,1));
        ViewHelper.setAlpha(appbarlayout, 1-(float) scrollY / (mFlexibleSpaceImageHeight-mActionBarSize-500));
        float scale = 1 + ScrollUtils.getFloat((flexibleRange - scrollY) / flexibleRange, 0, 1);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {

    }
}
