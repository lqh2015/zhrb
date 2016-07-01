package com.example.lqh.zhrb.domain;

import java.util.ArrayList;

public class MenuList {

	public int limit;
	public ArrayList<String> subscribed;
	public ArrayList<Other> others;
	
	@Override
	public String toString() {
		return "MenuList [others=" + others + "]";
	}

	public class Other{
		public int color;
		public String thumbnail;
		public String description;
		public int id;
		public String name;
		@Override
		public String toString() {
			return "Other [id=" + id + ", name=" + name + "]";
		}
		
	}
}
