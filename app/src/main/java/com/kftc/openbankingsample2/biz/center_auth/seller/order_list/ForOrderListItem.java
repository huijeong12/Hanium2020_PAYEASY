package com.kftc.openbankingsample2.biz.center_auth.seller.order_list;

import android.graphics.drawable.Drawable;

public class ForOrderListItem {
    private String price;
    private String count;
    private String name;

    public void setPrice(String price1){
       price =price1;
    }

    public void setCount(String count1){
        count =count1;
    }
    public void setName(String name1){
        name = name1;
    }

    public String getPrice(){
        return this.price;
    }

    public String getCount(){
        return this.count;
    }

    public String getName(){
        return this.name;
    }







}
