package com.example.lqh.zhrb.utils;

import org.json.JSONObject;

import com.google.gson.Gson;

public class ParseJson {

	/**
	 * ����json
	 * @param object
	 * @param clazz
	 * @return
	 */
	public static <T> T paserJson(JSONObject json,Class<T> clazz){
		Gson gson=new Gson();
		T t = gson.fromJson(json.toString(), clazz);
		return t;
	}
}
