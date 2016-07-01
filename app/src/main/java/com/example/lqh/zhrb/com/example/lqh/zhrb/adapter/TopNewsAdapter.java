package com.example.lqh.zhrb.com.example.lqh.zhrb.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lqh.zhrb.MainActivity;
import com.example.lqh.zhrb.NewsDetialActivity;
import com.example.lqh.zhrb.R;
import com.example.lqh.zhrb.domain.NewData;
import com.example.lqh.zhrb.utils.DimenUtils;
import com.squareup.picasso.Picasso;


public class TopNewsAdapter extends PagerAdapter{
	Activity context;
	NewData mNewData;
	public TopNewsAdapter(Activity context, NewData newData) {
		this.context=context;
		this.mNewData=newData;
	}
	
	@Override
	public int getCount() {
//		Log.d("newData",mNewData.toString());
		return mNewData.top_stories.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0==arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}

	@Override
	public Object instantiateItem(final ViewGroup container, final int position) {
		
		View view=View.inflate(context, R.layout.top_news_item, null);
		
		ImageView top_image=(ImageView) view.findViewById(R.id.top_image);
		TextView  top_title=(TextView) view.findViewById(R.id.top_title);
		
		top_title.setText(mNewData.top_stories.get(position).title);
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent=new Intent(context, NewsDetialActivity.class);
				intent.putExtra("id",mNewData.top_stories.get(position).id);
				Log.d("id",mNewData.top_stories.get(position).id);
				context.startActivity(intent);
			}
		});
		
		Uri uri = Uri.parse(mNewData.top_stories.get(position).image);
		int targetWidth=((Activity)context).getWindowManager().getDefaultDisplay().getWidth();
		
		Picasso.with(context).load(uri).resize(targetWidth, DimenUtils.dp2px(context, 250)).centerCrop().into(top_image);

		//��ʾͼƬ������  
//        DisplayImageOptions options = new DisplayImageOptions.Builder()  
////                .showImageOnLoading(R.drawable.ic_stub)  
////                .showImageOnFail(R.drawable.ic_error)  
//        		
//                .cacheInMemory(true)  
//                .cacheOnDisc(true)
//                .bitmapConfig(Bitmap.Config.ARGB_8888)  
//                .build();  
//          
//        ImageLoader.getInstance().displayImage(mNewData.top_stories.get(position).image, top_image, options); 
//

		container.addView(view);
		return view;
	}
	
	
}