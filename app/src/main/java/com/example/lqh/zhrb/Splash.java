package com.example.lqh.zhrb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lqh.zhrb.domain.SplashData;
import com.example.lqh.zhrb.utils.CharsetJsonRequest;
import com.example.lqh.zhrb.utils.DimenUtils;
import com.example.lqh.zhrb.utils.ParseJson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class Splash extends Activity {

	//启动页图片地址
	private String url="http://news-at.zhihu.com/api/4/start-image/1440*2560";
	private SplashData spData;
	private ImageView imageView;
	private RequestQueue mRequestQueue;//请求队列
	private ScaleAnimation animation;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash);
		mRequestQueue = Volley.newRequestQueue(this);//初始化请求队列
		imageView=(ImageView) findViewById(R.id.img_splash);
		getSplashImage();
		animation=new ScaleAnimation(1.2f, 1.3f, 1.2f, 1.3f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(3000);
		animation.setFillAfter(true);
		imageView.setAnimation(animation);
		
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation arg0) {
			}
			
			@Override
			public void onAnimationRepeat(Animation arg0) {
			}
			
			@Override
			public void onAnimationEnd(Animation arg0) {
				Intent intent=new Intent(Splash.this,MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
	}

	public void getSplashImage(){
		CharsetJsonRequest cjr=new CharsetJsonRequest(url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				spData=ParseJson.paserJson(jsonObject, SplashData.class);
				Uri uri = Uri.parse(spData.img);
				//int targetWidth=(Splash.this).getWindowManager().getDefaultDisplay().getWidth();

				Picasso.with(Splash.this).load(uri).resize(1440,2560).centerCrop().into(imageView);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {
				Log.d("error",volleyError.toString());
			}
		});
		mRequestQueue.add(cjr);
	}
}
