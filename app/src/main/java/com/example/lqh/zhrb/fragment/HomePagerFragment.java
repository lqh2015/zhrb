package com.example.lqh.zhrb.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lqh.zhrb.MainActivity;
import com.example.lqh.zhrb.NewsDetialActivity;
import com.example.lqh.zhrb.R;
import com.example.lqh.zhrb.com.example.lqh.zhrb.adapter.RecHomePagerAdapter;
import com.example.lqh.zhrb.com.example.lqh.zhrb.adapter.TopNewsAdapter;
import com.example.lqh.zhrb.domain.MenuList;
import com.example.lqh.zhrb.domain.NewData;
import com.example.lqh.zhrb.utils.CharsetJsonRequest;
import com.example.lqh.zhrb.utils.DimenUtils;
import com.example.lqh.zhrb.utils.ParseJson;

import org.json.JSONObject;

/**
 * Created by lqh on 2016/6/7.
 */
public class HomePagerFragment extends Fragment {

    private Activity mActivity;
    private RequestQueue mRequestQueue;//请求队列
    private  static final String NEWSTORIES_URL = "http://news-at.zhihu.com/api/4/news/latest";
    public NewData mNewData = null;//新闻数据
    private RecyclerView rec_homePager;//home界面的recycleview
    private SwipeRefreshLayout srl;//下拉刷新
    private LinearLayoutManager mLayoutManage;//设置recycleview的类型
    private int lastVisibleItem;//recycleview显示的最后一个条目
    public  static  RecHomePagerAdapter adapter;//recycleview适配器
    public static boolean isScorllUP=false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        mRequestQueue = Volley.newRequestQueue(mActivity);//初始化请求队列
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=View.inflate(mActivity, R.layout.home_pager,null);
        srl= (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget_home);//获取下拉刷新布局
        srl.setColorSchemeColors(Color.parseColor("#0099cc"));//设置旋转箭头的颜色
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.isLoadMore=false;
                getNewData();
            }
        });


//        // 这句话是为了，第一次进入页面的时候显示加载进度条
//        srl.setProgressViewOffset(false, 0, (int) TypedValue
//                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
//                        .getDisplayMetrics()));

        rec_homePager= (RecyclerView) view.findViewById(R.id.rec_home_pager);
     //   rec_homePager.setAdapter(new RecHomePagerAdapter(topNews));
        mLayoutManage=new LinearLayoutManager(mActivity);
        rec_homePager.setLayoutManager(mLayoutManage);//这里用线性显示 类似于listview
        rec_homePager.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == adapter.getItemCount()) {
                 //   srl.setRefreshing(true);
                  //  RecHomePagerAdapter.isLoadMore=true;
                    adapter.loadMore();
                 //   adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManage.findLastVisibleItemPosition();
                if (dy>0){
                    isScorllUP=false;
                }else{
                    isScorllUP=true;
                }
                Log.d("dy",dy+"");
                Log.d("isScorllUP",isScorllUP+"");
            }

        });

        getNewData();
        return view;
    }
    /**
     * 从网络上获取NewData数据
     */
    private void getNewData() {
        CharsetJsonRequest jor = new CharsetJsonRequest(NEWSTORIES_URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //解析json数据并把数据赋值给mNewData
                        mNewData = ParseJson.paserJson(response, NewData.class);
                        if (mNewData!=null){
                            srl.setRefreshing(false);//当获取到的数据不为空时表示刷新成功，隐藏刷新布局
                        }
                    //    if (!RecHomePagerAdapter.isLoadMore){
                            //设置recycleView的适配器
                            adapter=new RecHomePagerAdapter(mActivity,mNewData);
                            rec_homePager.setAdapter(adapter);
                            adapter.setOnItemClickListener(new RecHomePagerAdapter.OnRecyclerViewItemClickListener() {
                            @Override
                            public void onItemClick(View view, String data) {
                                Intent intent =new Intent(mActivity, NewsDetialActivity.class);
                                intent.putExtra("id",data);
                                mActivity.startActivity(intent);
                                Log.d("id","1111111111111111111");
                            }
                        });
                 //       }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });

        mRequestQueue.add(jor);
    }

}
