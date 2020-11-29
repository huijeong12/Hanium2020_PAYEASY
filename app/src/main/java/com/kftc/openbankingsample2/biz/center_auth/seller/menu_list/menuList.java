package com.kftc.openbankingsample2.biz.center_auth.seller.menu_list;

import android.widget.ImageView;

public class menuList {
    private String profile;
    private String menuName;
    private String price;

    public menuList(){}

    public String getProfile(){
        return profile;
    }

    public String getMenuName(){
        return menuName;
    }

    public String getPrice(){
        return price;
    }


    public void setProfile(String pro) {
        this.profile = pro;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setPrice(String price){
        this.price = price;
    }
}
