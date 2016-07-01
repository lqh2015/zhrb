package com.example.lqh.zhrb.domain;

import java.util.ArrayList;

/**
 * Created by lqh on 2016/6/10.
 */
public class ThemesData {
    public  String limit;
    public ArrayList subscribed;
    public ArrayList<Others> others;

    public class Others{
        public  String color;
        public  String thumbnail;
        public  String description;
        public  String id;
        public  String name;
    }
}
