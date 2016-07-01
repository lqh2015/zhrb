package com.example.lqh.zhrb.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lqh.zhrb.MainActivity;
import com.example.lqh.zhrb.R;
import com.example.lqh.zhrb.ThemesDetialActivity;
import com.example.lqh.zhrb.com.example.lqh.zhrb.adapter.RecHomePagerAdapter;
import com.example.lqh.zhrb.com.example.lqh.zhrb.adapter.RecThemesPagerAdapter;
import com.example.lqh.zhrb.domain.NewData;
import com.example.lqh.zhrb.domain.ThemesData;
import com.example.lqh.zhrb.domain.ThemesItemContent;
import com.example.lqh.zhrb.utils.CharsetJsonRequest;
import com.example.lqh.zhrb.utils.ParseJson;

import org.json.JSONObject;

/**
 * Created by lqh on 2016/6/10.
 * 主题页面
 */
public class ThemesPagerFragment extends Fragment {

    private MainActivity mActivity;
    private RequestQueue mRequestQueue;//请求队列
    private static final String themes_URL = "http://news-at.zhihu.com/api/4/themes";
    public ThemesData mThemesData = null;//新闻数据
    private RecyclerView rec_themesPager;//home界面的recycleview
    private SwipeRefreshLayout srl;//下拉刷新
    private LinearLayoutManager mLayoutManage;//设置recycleview的类型
    private int lastVisibleItem;//recycleview显示的最后一个条目
    public static RecThemesPagerAdapter adapter;//recycleview适配器
    private ThemesData.Others other;//当前被选中的菜单选项对应的数据
    private ThemesItemContent themesItemContent;//当前被选中的菜单选项对应的数据
    private String themesContentURL="http://news-at.zhihu.com/api/4/theme/";//主题内容URL
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        mRequestQueue = Volley.newRequestQueue(mActivity);//初始化请求队列
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = View.inflate(mActivity, R.layout.themes_pager, null);
        srl = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);//获取下拉刷新布局
        srl.setColorSchemeColors(Color.parseColor("#0099cc"));//设置旋转箭头的颜色
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                adapter.isLoadMore = false;
                getNewData();
            }
        });

        rec_themesPager = (RecyclerView) view.findViewById(R.id.rec_home_pager);
        //   rec_homePager.setAdapter(new RecHomePagerAdapter(topNews));
        mLayoutManage = new LinearLayoutManager(mActivity);
        rec_themesPager.setLayoutManager(mLayoutManage);//这里用线性显示 类似于listview
        rec_themesPager.setOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,
                                             int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE
                        && lastVisibleItem + 1 == adapter.getItemCount()) {
                 //   adapter.loadMore();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLayoutManage.findLastVisibleItemPosition();
            }

        });
        getNewData();
        return view;
    }

    /**
     * 从网络上获取NewData数据
     */
    private void getNewData() {
        CharsetJsonRequest jor = new CharsetJsonRequest(themes_URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //解析json数据并把数据赋值给mNewData
                        mThemesData = ParseJson.paserJson(response, ThemesData.class);
                        if (mThemesData != null) {
                            srl.setRefreshing(false);//当获取到的数据不为空时表示刷新成功，隐藏刷新布局
                        }
                        //    if (!RecHomePagerAdapter.isLoadMore){
                        //设置recycleView的适配器
                        other=mThemesData.others.get(mActivity.getCurrMenuItemClick()-1);//当前被选中的菜单选项对应的数据
                        themesContentURL+=other.id;//主题内容URL
                        getThemesCountData();


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
    /**
     * 获取主题内容数据
     */
    private void getThemesCountData() {
        CharsetJsonRequest jor = new CharsetJsonRequest(themesContentURL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //解析json数据并把数据赋值给mNewData
                        themesItemContent = ParseJson.paserJson(response, ThemesItemContent.class);

                        if (themesItemContent!=null){
                            adapter = new RecThemesPagerAdapter(mActivity, mThemesData,themesItemContent);
                            rec_themesPager.setAdapter(adapter);
                            adapter.setOnItemClickListener(new RecThemesPagerAdapter.OnRecyclerViewItemClickListener() {
                                @Override
                                public void onItemClick(View view, String data,String themes_id) {
                                    Intent intent =new Intent(mActivity,ThemesDetialActivity.class);
                                    intent.putExtra("id",data);
                                    intent.putExtra("themes_id",themes_id);
                                    mActivity.startActivity(intent);
                                }
                            });
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
}