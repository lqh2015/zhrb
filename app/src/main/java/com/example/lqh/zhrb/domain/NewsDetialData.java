package com.example.lqh.zhrb.domain;

import java.util.ArrayList;

/**
 * Created by lqh on 2016/6/12.
 */
public class NewsDetialData {
    public String body;
    public String image_source;
    public String title;
    public String image;
    public String share_url;
    public String ga_prefix;
    public String type;
    public String id;
    public ArrayList<String> js;
    public ArrayList<Recommenders> recommenders;
    public Section section;
    public ArrayList<String> images;
    public ArrayList<String> css;

    public class Recommenders{
        public String avatar;
    }

    public class Section{
        public String thumbnail;
        public String id;
        public String name;
    }
}
