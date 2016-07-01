package com.example.lqh.zhrb;

import android.app.FragmentManager;
import android.app.FragmentTransaction;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.lqh.zhrb.com.example.lqh.zhrb.adapter.MenuAdapter;

import com.example.lqh.zhrb.domain.MenuList;

import com.example.lqh.zhrb.fragment.HomePagerFragment;
import com.example.lqh.zhrb.fragment.ThemesPagerFragment;
import com.example.lqh.zhrb.utils.CharsetJsonRequest;
;
import com.example.lqh.zhrb.utils.ParseJson;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private List<String> menuItemList = new ArrayList<String>();//存放菜单栏文字
    private int curr_select_item;//当前被选中的菜单选项
    //最新的新闻

    private static final String MENULIST_URL = "http://news-at.zhihu.com/api/4/themes";//菜单列表
    private ListView lv_menu;
    private RequestQueue mRequestQueue;
    public MenuList mMenuList = null;
    private ImageButton ib_login;
    private ImageButton ib_collect;
    private ImageButton ib_download;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //菜单
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle("首页");//首先把标题的设置为首页
        mRequestQueue = Volley.newRequestQueue(this);//初始化请求队列
        //抽屉
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //获取菜单栏布局并设置数据
        lv_menu = (ListView) findViewById(R.id.left_menu);
        View heardView=View.inflate(this, R.layout.nav_header_main, null);
        lv_menu.addHeaderView(heardView);
        ib_login = (ImageButton) findViewById(R.id.ib_head);
        ib_collect = (ImageButton) findViewById(R.id.ib_head);
        ib_download = (ImageButton) findViewById(R.id.ib_head);
        ib_login.setOnClickListener(this);//登录按钮
        ib_collect.setOnClickListener(this);//我的收藏
        ib_download.setOnClickListener(this);//离线下载
        lv_menu.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final MenuAdapter adapter = new MenuAdapter(this, menuItemList);
        lv_menu.setAdapter(adapter);
        lv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                curr_select_item = i - 1;
                //通知ListView去重新加载布局，如果数据没有变化就不要用notifyDataSetChanged()，用来也无效;
                lv_menu.invalidateViews();
                //设置标题
                invalidateOptionsMenu();
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (curr_select_item == 0) {
                    MainActivity.this.setTitle("首页");
                    //根据选中的标题去加载数据

                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fl_content, new HomePagerFragment());
                    fragmentTransaction.commit();

                } else {
                    MainActivity.this.setTitle(menuItemList.get(curr_select_item - 1));
                    //根据选中的标题去加载数据
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fm.beginTransaction();
                    fragmentTransaction.replace(R.id.fl_content, new ThemesPagerFragment());
                    fragmentTransaction.commit();

                }

                //点击菜单选项后关闭菜单栏
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);

                }
                Log.d("curr_select_item", curr_select_item + "");
            }
        });
        //初始化的时候默认显示首页的数据
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.fl_content, new HomePagerFragment());
        fragmentTransaction.commit();

        getMenuList();//获取菜单数据

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //根据当前选中的菜单的不同加载不同的toolbar
        if (curr_select_item == 0) {
            getMenuInflater().inflate(R.menu.main, menu);
        } else {
            getMenuInflater().inflate(R.menu.theme, menu);
        }

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
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 获取当前点击的菜单选项
     *
     * @return
     */
    public int getCurrMenuItemClick() {
        return curr_select_item;
    }

    /**
     * 获取菜单栏的标题
     */
    private void getMenuList() {
        CharsetJsonRequest jor = new CharsetJsonRequest(MENULIST_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mMenuList = ParseJson.paserJson(response, MenuList.class);
                        menuItemList = new ArrayList<String>();
                        for (int i = 0; i < mMenuList.others.size(); i++) {
                            menuItemList.add(mMenuList.others.get(i).name);
                        }
                        MenuAdapter adapter = new MenuAdapter(MainActivity.this, menuItemList);
                        Log.d("menulist", menuItemList.toString());
                        lv_menu.setAdapter(adapter);// 给菜单列表设置数据
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });

        mRequestQueue.add(jor);
    }

    //按钮点击监听事件
    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent intent=new Intent(this,LoginActivity.class);
        switch (id){
            case R.id.ib_head:
                Intent intent1=new Intent(this,LoginActivity.class);
                startActivity(intent1);
            break;
            case R.id.ib_collect:
                startActivity(intent);
                break;
            case R.id.ib_download:
                startActivity(intent);
                break;
        }


    }

}
