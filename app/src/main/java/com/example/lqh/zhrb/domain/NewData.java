package com.example.lqh.zhrb.domain;

import java.util.ArrayList;
import java.util.Arrays;

public class NewData {

	public String date;
	public ArrayList<Stories> stories;
	public ArrayList<Top_stories> top_stories;

	@Override
	public String toString() {
		return "NewData [date=" + date + ", stories=" + stories
				+ ", top_stories=" + top_stories + "]";
	}

	public class Stories {
		public String title;
		public String ga_prefix;
		public ArrayList<String> images;
		public boolean multipic;
		public int type;
		public int id;
		@Override
		public String toString() {
			return "Stories [title=" + title + "id="
					+ id + "]";
		}

		

	}

	public class Top_stories {
		public String title;
		public String ga_prefix;
		public String image;
		public int type;
		public String id;

		@Override
		public String toString() {
			return "Top_stories [title=" + title + ", images=" + image
					+ ", id=" + id + "]";
		}

	}
}
