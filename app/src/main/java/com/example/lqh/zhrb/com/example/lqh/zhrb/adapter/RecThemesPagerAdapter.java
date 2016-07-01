package com.example.lqh.zhrb.com.example.lqh.zhrb.adapter;

import android.app.Activity;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lqh.zhrb.MainActivity;
import com.example.lqh.zhrb.R;
import com.example.lqh.zhrb.domain.ThemesData;
import com.example.lqh.zhrb.domain.ThemesItemContent;
import com.example.lqh.zhrb.utils.CharsetJsonRequest;
import com.example.lqh.zhrb.utils.DimenUtils;
import com.example.lqh.zhrb.utils.ParseJson;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by lqh on 2016/6/10.
 */
public class RecThemesPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private View topThemesView;//主题头布局
    public static final int ITEM_TYPE_HEADER = 0;//头布局标志
    public static final int ITEM_TYPE_CONTENT = 1;//内容布局标志
    private int mHeaderCount=1;//头部View个数
    private MainActivity mActivity;
    public static ThemesData mThemesData = null;
    public static ThemesData loadMoreThemesData = null;
    public Handler handler;//用于头条新闻的轮播
    private boolean isFirst=false;//用于判断进入home界面
    private String themesContentURL="http://news-at.zhihu.com/api/4/theme/";//主题内容URL
    private static RequestQueue mRequestQueue;//请求队列
    public static boolean isLoadMore=false;
    public static String today;//今天的日期字符串形式，如20160608
    public static Date date;//今天的日期date形式
    private static int itemCount;//上一次加载是数据的条数
    private ThemesData.Others other;//当前被选中的菜单选项对应的数据
    private ThemesItemContent themesItemContent;//当前被选中的菜单选项对应的数据
    public RecThemesPagerAdapter(Activity mActivity,ThemesData mThemesData,ThemesItemContent themesItemContent){

        this.mThemesData=mThemesData;
        this.mActivity=(MainActivity) mActivity;
        mRequestQueue = Volley.newRequestQueue(mActivity);//初始化请求队列
        this.themesItemContent=themesItemContent;
        other=mThemesData.others.get(this.mActivity.getCurrMenuItemClick()-1);
    }

    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    //define interface
    public static interface OnRecyclerViewItemClickListener {
        void onItemClick(View view , String data,String id);
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
            //如果是头布局类型就返回头布局的holder,如果用view.inflate加载布局的话会出错，此次应注意
            topThemesView= LayoutInflater.from(mActivity).inflate(R.layout.top_themes, parent, false);
            return new HeaderViewHolder(topThemesView);
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
            ((HeaderViewHolder) holder).tv_desc.setText(other.description);
            //获取屏幕的宽度
            int targetWidth=((Activity)mActivity).getWindowManager().getDefaultDisplay().getWidth();
            //加载图片
            Picasso.with(mActivity).load(other.thumbnail)
                    .resize(targetWidth,DimenUtils.dp2px(mActivity, 250))
                    .centerCrop().into(((HeaderViewHolder) holder).imageView);

        } else if (holder instanceof ContentViewHolder) {
         //   NewData.Stories storie=mNewData.stories.get(position-1);
            if (themesItemContent!=null){


            //设置新闻的标题
            ((ContentViewHolder) holder).tv_item_title.setText(themesItemContent.stories.get(position-1).title);
            int size=DimenUtils.dp2px(mActivity, 80);

              //  Log.d("images",themesItemContent.stories.get(position-1).images.toString());
            //如果有图片的话就设置新闻的图片
                if (null!=themesItemContent.stories.get(position-1).images) {
                    ((ContentViewHolder) holder).iv_item_img.setVisibility(View.VISIBLE);
                     Picasso.with(mActivity).load(themesItemContent.stories.get(position-1).images.get(0))
                    .resize(size,size).centerCrop()
                    .into(((ContentViewHolder) holder).iv_item_img);
                }else{
                    ((ContentViewHolder) holder).iv_item_img.setVisibility(View.INVISIBLE);
                }
//            //获取今天的日期
//            date=new Date();
//            Calendar calendar=Calendar.getInstance();
//            calendar.setTime(date);
//            int year=calendar.get(Calendar.YEAR);
//            int month=calendar.get(Calendar.MONTH);
//            int day=calendar.get(Calendar.DAY_OF_MONTH);
//
//            //如果不是在加载更多的时候才把今天的日期赋值给today
//            if(!isLoadMore){
//                itemCount=mNewData.stories.size();
//                if(day>9){
//                    today=""+year+"0"+(month+1)+day;
//                }else{
//                    today=""+year+"0"+(month+1)+"0"+day;
//                }
//            }
            if (position==1){
                ((ContentViewHolder) holder).tv_item_editor.setVisibility(View.VISIBLE);
                for (int i=0;i<themesItemContent.editors.size();i++){
                    ((ContentViewHolder) holder).tv_item_editor.setText("主编："+themesItemContent.editors.get(i).name+" ");
                }

            }
            //else if (position==itemCount+1){
//                itemCount = mNewData.stories.size();
//                ((ContentViewHolder) holder).tv_item_date.setVisibility(View.VISIBLE);
//                ((ContentViewHolder) holder).tv_item_date.setText(today);
//            }
            else {
                //   if (((ContentViewHolder) holder).tv_item_date.getText().equals("")){
                ((ContentViewHolder) holder).tv_item_editor.setVisibility(View.GONE);
                //    }

            }
            Log.d("count",itemCount+"");
//            if (position==itemCount+1){
//                ((ContentViewHolder) holder).tv_item_date.setVisibility(View.VISIBLE);
//                ((ContentViewHolder) holder).tv_item_date.setText(today);
//            }else {
//                ((ContentViewHolder) holder).tv_item_date.setVisibility(View.GONE);
//            }
//            if(position==itemCount+1&&isLoadMore) {
//                itemCount = mNewData.stories.size();
//                ((ContentViewHolder) holder).tv_item_date.setVisibility(View.VISIBLE);
//                ((ContentViewHolder) holder).tv_item_date.setText(today);
//            }
            }

            //将数据保存在itemView的Tag中，以便点击时进行获取
            ((ContentViewHolder) holder).itemView.setTag(themesItemContent.stories.get(position-1).id);
            curntPosition=position-1;
        }
    }
    @Override
    public int getItemCount() {
        return mHeaderCount + mThemesData.others.size() ;
    }

    //内容 ViewHolder
    class ContentViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_item_title;//新闻标题
        private TextView tv_item_editor;//主题主编
        private ImageView iv_item_img;//新闻图片
        public ContentViewHolder(View itemView) {
            super(itemView);
            tv_item_title=(TextView)itemView.findViewById(R.id.tv_item_title);
            iv_item_img= (ImageView) itemView.findViewById(R.id.iv_item_img);
            tv_item_editor=(TextView)itemView.findViewById(R.id.tv_item_date);
        }
    }
    //头部 ViewHolder
    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private TextView tv_desc;
        public HeaderViewHolder(View itemView) {
            super(itemView);
            imageView=(ImageView)itemView.findViewById(R.id.top_themes_image);
            tv_desc= (TextView) itemView.findViewById(R.id.top_themes_desc);
        }
    }

//    public  void loadMore(){
//        //  loadMoreURL+=today;
//      //  String newUrl=loadMoreURL+today;
//    //    Log.d("today",today);
//        //  today=(Integer.parseInt(today)-1)+"";//计算出前一天的日期
//        //  Log.d("111",today);
//        isLoadMore=true;
//        CharsetJsonRequest jor = new CharsetJsonRequest(newUrl, null,
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        //解析json数据并把数据赋值给mNewData
//                        loadMoreThemesData = ParseJson.paserJson(response, ThemesData.class);
////                        if (mNewData!=null){
////                            srl.setRefreshing(false);//当获取到的数据不为空时表示刷新成功，隐藏刷新布局
////                        }
////                        //设置recycleView的适配器
////                        adapter=new RecHomePagerAdapter(mActivity,mNewData);
////                        rec_homePager.setAdapter(adapter);
//                        if (mNewData!=null&&loadMoreThemesData!=null){
//                            mNewData.stories.addAll(loadMoreThemesData.stories);
//                            RecThemesPagerAdapter.this.notifyDataSetChanged();
//                            today=loadMoreThemesData.date;
//                        }
//
//                        Log.d("111",today);
//                        Log.d("stories",mNewData.stories.toString());
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("error", error.toString());
//            }
//        });
//
//        mRequestQueue.add(jor);
//    }

private int curntPosition;
    @Override
    public void onClick(View v) {
        Log.d("onclick","2222");
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取数据
            mOnItemClickListener.onItemClick(v,v.getTag()+"", mThemesData.others.get(curntPosition).id);
        }
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
