package com.example.lqh.zhrb.utils;

import android.content.Context;

/**
 * ��Ļ�ߴ�ת��
 * 
 * @author lqh
 * 
 */
public class DimenUtils {

	/**
	 * ������ת��Ϊdp
	 */
	public static float px2dp(Context context, int px) {
		// ��ȡ��Ļ�ı���
		float density = context.getResources().getDisplayMetrics().density;

		float dp = px / density;// dp��px��ת����ʽ
		return dp;
	}
	/**
	 * ��dpת��Ϊpx
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int dp2px(Context context, float dp) {
		float density = context.getResources().getDisplayMetrics().density;
		int px = (int) (dp * density);
		return px;
	}
}
