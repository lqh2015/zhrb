package com.example.lqh.zhrb.com.example.lqh.zhrb.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lqh.zhrb.MainActivity;
import com.example.lqh.zhrb.R;

/**
 * 菜单栏布局适配器
 */
public class MenuAdapter extends BaseAdapter {
	private MainActivity context;
	private List<String> list;//菜单栏标题数据
	
	public MenuAdapter(Context context,List<String> list) {
		this.context=(MainActivity)context;
		this.list=list;
	}
	
	
	@Override
	public int getCount() {
		return list.size()+1;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	@Override
	public int getItemViewType(int position) {
		if(position==0){
			return 0;
		}else{
			return 1;
		}
	}


	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View view=null;
		ViewHolderNormal holderNormal;
		int type=getItemViewType(position);
		if(convertView==null){
			switch (type) {
			case 0:
				view=View.inflate(context, R.layout.left_menu_item_home, null);
				break;
			case 1:
				holderNormal=new ViewHolderNormal();
				view=View.inflate(context, R.layout.left_menu_item_normal, null);
				holderNormal.tx_item=(TextView) view.findViewById(R.id.tv_item);
				view.setTag(holderNormal);
				holderNormal.tx_item.setText(list.get(position-1));
				break;
			}
		}else{
			switch (type) {
			case 0:
				view=convertView;
				break;
			case 1:
				view=convertView;
				holderNormal=(ViewHolderNormal) view.getTag();

				holderNormal.tx_item.setText(list.get(position-1));
				break;
			}
		}
		Log.d("getCurrMenuItemClick()",context.getCurrMenuItemClick()+"");
		if(context.getCurrMenuItemClick()==position){
			view.setBackgroundColor(Color.parseColor("#f2f2f2"));
		}else{
			view.setBackgroundColor(Color.WHITE);
		}
		return view;
	}
	static class ViewHolderNormal{
		TextView tx_item;
	}
}
