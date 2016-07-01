package com.example.lqh.zhrb.com.example.lqh.zhrb.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lqh.zhrb.NewsDetialActivity;
import com.example.lqh.zhrb.R;
import com.example.lqh.zhrb.domain.NewData;
import com.example.lqh.zhrb.utils.CharsetJsonRequest;
import com.example.lqh.zhrb.utils.DimenUtils;
import com.example.lqh.zhrb.utils.ParseJson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by lqh on 2016/6/7.
 * 首页适配器
 */
public class RecHomePagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private View topNewsView;//头条新闻布局
    public static final int ITEM_TYPE_HEADER = 0;//头布局标志
    public static final int ITEM_TYPE_CONTENT = 1;//内容布局标志
    private int mHeaderCount=1;//头部View个数
    private Activity mActivity;
    public static NewData mNewData = null;
    public static NewData loadMoreNewData = null;
    public Handler handler;//用于头条新闻的轮播
    private boolean isFirst=false;//用于判断进入home界面
    private static String loadMoreURL="http://news.at.zhihu.com/api/4/news/before/";//加载更多地址
    private static RequestQueue mRequestQueue;//请求队列
    public static boolean isLoadMore=false;
    public static String today;//今天的日期字符串形式，如20160608
    public static Date date;//今天的日期date形式
    private static int itemCount;//上一次加载是数据的条数
    public RecHomePagerAdapter(Activity mActivity,NewData mNewData){

        this.mNewData=mNewData;
        this.mActivity=mActivity;
        mRequestQueue = Volley.newRequestQueue(mActivity);//初始化请求队列
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data);
    }
    //判断当前item类型
    @Override
    public int getItemViewType(int position) {
        if (position ==0) {
        //头部View
            return ITEM_TYPE_HEADER;
        } else {
        //内容View
            return ITEM_TYPE_CONTENT;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType ==ITEM_TYPE_HEADER) {
            //如果是头布局类型就返回头布局的holder
            topNewsView=LayoutInflater.from(mActivity).inflate(R.layout.top_news, parent, false);
            return new HeaderViewHolder(topNewsView);
        } else if (viewType == mHeaderCount) {
            //如果是内容布局类型就返回内容布局的holder
            View view=LayoutInflater.from(mActivity).inflate(R.layout.rec_item, parent, false);
            //将创建的View注册点击事件
            view.setOnClickListener(this);
            return new ContentViewHolder(view);
        }
        else {
            return null;
        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            //给头条新闻设置适配器
            ((HeaderViewHolder)holder).pager.setAdapter(new TopNewsAdapter(mActivity,mNewData));
            //加个标记，不加标记会重复执行如下代码，会导致轮播错误，圆点也会重复添加
            if (!isFirst){
                isFirst=true;
                //设置头条新闻的滑动监听
                ((HeaderViewHolder)holder).pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }
                    @Override
                    public void onPageSelected(int position) {
                        for(int i=0;i<((HeaderViewHolder)holder).ll_point.getChildCount();i++){
                            if(i==position){
                                View view = ((HeaderViewHolder)holder).ll_point.getChildAt(position);
                                view.setBackgroundResource(R.drawable.top_news_point_white);
                            }else{
                                View view = ((HeaderViewHolder)holder).ll_point.getChildAt(i);
                                view.setBackgroundResource(R.drawable.top_news_point_gray);
                            }
                        }
                    }
                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                // 让头条新闻轮播条滚动
                if (handler == null) {
                    handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            int currentItem =  ((HeaderViewHolder)holder).pager.getCurrentItem();
                            if (currentItem < mNewData.top_stories.size() - 1) {// 如果没有轮播到最后一条新闻，就让当前的currentItem+1
                                ((HeaderViewHolder)holder).pager.setCurrentItem(currentItem + 1);
                            } else {// 如果轮播到了最后一条新闻，就从第一条开始轮播

                                ((HeaderViewHolder)holder).pager.setCurrentItem(0);
                            }
                            handler.sendEmptyMessageDelayed(0, 3000);// 再次发送消息，让头条新闻不停的轮播，类似于递归
                        }
                    };
                }

                handler.sendEmptyMessageDelayed(0, 3000);// 3秒滚动一次
                //动态的添加头布局的圆点
                for(int i=0;i<mNewData.top_stories.size();i++) {
                    View view = new View(mActivity);
                    view.setBackgroundResource(R.drawable.top_news_point_gray);
                    // 给要添加的view设置属性,view 将要添加到哪个布局文件中就用哪个包下的LayoutParams类
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
                            (DimenUtils.dp2px(mActivity, 6),
                                    DimenUtils.dp2px(mActivity, 6));// 设置view的宽高
                    if (i != 0) {
                        // 从第二个圆点开始 每个圆点离左边的圆点6个像素
                        params.leftMargin = 6;
                    } else {
                        view.setBackgroundResource(R.drawable.top_news_point_white);
                    }
                    view.setLayoutParams(params);
                    ((HeaderViewHolder)holder).ll_point.addView(view);
                }
            }
           // ((ContentViewHolder) holder).itemView.setTag(mNewData.stories.get(position-1).id);

        } else if (holder instanceof ContentViewHolder) {
            NewData.Stories storie=mNewData.stories.get(position-1);
            //设置新闻的标题
            ((ContentViewHolder) holder).tv_item_title.setText(storie.title);
            int size=DimenUtils.dp2px(mActivity, 80);
            //设置新闻的图片
            Picasso.with(mActivity).load(storie.images.get(0)).resize(size,size).centerCrop()
                    .into(((ContentViewHolder) holder).iv_item_img);
            //获取今天的日期
            date=new Date();
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            int year=calendar.get(Calendar.YEAR);
            int month=calendar.get(Calendar.MONTH);
            int day=calendar.get(Calendar.DAY_OF_MONTH);

            //如果不是在加载更多的时候才把今天的日期赋值给today
            if(!isLoadMore){
                itemCount=mNewData.stories.size();
                if(day>9){
                    today=""+year+"0"+(month+1)+day;
                }else{
                    today=""+year+"0"+(month+1)+"0"+day;
                }
            }
            if (position==1){
                ((ContentViewHolder) holder).tv_item_date.setVisibility(View.VISIBLE);
                ((ContentViewHolder) holder).tv_item_date.setText("今日热闻");
            }else if (position==itemCount+1){
                itemCount = mNewData.stories.size();
                ((ContentViewHolder) holder).tv_item_date.setVisibility(View.VISIBLE);
                ((ContentViewHolder) holder).tv_item_date.setText(today);
            }
            else {
             //   if (((ContentViewHolder) holder).tv_item_date.getText().equals("")){
                    ((ContentViewHolder) holder).tv_item_date.setVisibility(View.GONE);
            //    }

            }
            Log.d("count",itemCount+"");
//            if (position==itemCount+1){
//                ((ContentViewHolder) holder).tv_item_date.setVisibility(View.VISIBLE);
//                ((ContentViewHolder) holder).tv_item_date.setText(today);
//            }else {
//                ((ContentViewHolder) holder).tv_item_date.setVisibility(View.GONE);
//            }
            if(position==itemCount+1&&isLoadMore) {
                itemCount = mNewData.stories.size();
                ((ContentViewHolder) holder).tv_item_date.setVisibility(View.VISIBLE);
                ((ContentViewHolder) holder).tv_item_date.setText(today);
            }

            //将数据保存在itemView的Tag中，以便点击时进行获取
            ((ContentViewHolder) holder).itemView.setTag(mNewData.stories.get(position-1).id);
        }
    }
    @Override
    public int getItemCount() {
        return mHeaderCount + mNewData.stories.size() ;
    }

    //内容 ViewHolder
    class ContentViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_item_title;//新闻标题
        private TextView tv_item_date;//新闻日期
        private ImageView iv_item_img;//新闻图片
        public ContentViewHolder(View itemView) {
            super(itemView);
            tv_item_title=(TextView)itemView.findViewById(R.id.tv_item_title);
            iv_item_img= (ImageView) itemView.findViewById(R.id.iv_item_img);
            tv_item_date=(TextView)itemView.findViewById(R.id.tv_item_date);
        }
    }
    //头部 ViewHolder
    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private ViewPager pager;
        private LinearLayout ll_point;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            pager=(ViewPager)itemView.findViewById(R.id.vp_topnews);
            ll_point= (LinearLayout) topNewsView.findViewById(R.id.ll_point);
        }
    }

    public  void loadMore(){
      //  loadMoreURL+=today;
        String newUrl=loadMoreURL+today;
        Log.d("today",today);
      //  today=(Integer.parseInt(today)-1)+"";//计算出前一天的日期
      //  Log.d("111",today);
        isLoadMore=true;
        CharsetJsonRequest jor = new CharsetJsonRequest(newUrl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //解析json数据并把数据赋值给mNewData
                        loadMoreNewData = ParseJson.paserJson(response, NewData.class);
//                        if (mNewData!=null){
//                            srl.setRefreshing(false);//当获取到的数据不为空时表示刷新成功，隐藏刷新布局
//                        }
//                        //设置recycleView的适配器
//                        adapter=new RecHomePagerAdapter(mActivity,mNewData);
//                        rec_homePager.setAdapter(adapter);
                        if (mNewData!=null&&loadMoreNewData!=null){
                            mNewData.stories.addAll(loadMoreNewData.stories);
                            RecHomePagerAdapter.this.notifyDataSetChanged();
                            today=loadMoreNewData.date;
                        }

                        Log.d("111",today);
                        Log.d("stories",mNewData.stories.toString());
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
    public void onClick(View v) {
        Log.d("onclick","2222");
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,v.getTag()+"");
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
