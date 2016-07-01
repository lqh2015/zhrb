package com.example.lqh.zhrb.domain;


import java.util.ArrayList;

/**
 * Created by lqh on 2016/6/10.
 */
public class ThemesItemContent {
    public ArrayList<Stories> stories;
    public ArrayList<Editor> editors;
    public String description;
    public String background;
    public String color;
    public String name;
    public String image;
    public String image_source;

    public class Stories {
        public ArrayList<String> images;
        public String type;
        public String id;
        public String title;
    }

    public class Editor{
        public String  url;
        public String bio;
        public String id;
        public String avatar;
        public String name;
    }
}
