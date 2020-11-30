package com.kftc.openbankingsample2.biz.main;

import android.widget.ImageView;

public class menuList {
    private ImageView profile;
    private String menuName;
    private String price;

    public menuList(){}

    public ImageView getProfile(){
        return profile;
    }

    public String getMenuName(){
        return menuName;
    }

    public String getPrice(){
        return price;
    }

    public void setProfile(ImageView profile) {
        this.profile = profile;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setPrice(String price){
        this.price = price;
    }
}
