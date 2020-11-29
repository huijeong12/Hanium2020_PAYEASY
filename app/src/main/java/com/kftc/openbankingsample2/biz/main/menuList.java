package com.kftc.openbankingsample2.biz.main;

import android.widget.ImageView;

import com.google.firebase.storage.StorageReference;

public class menuList {
    private StorageReference profile;
    private String menuName;
    private String price;

    public menuList(){}

    public StorageReference getProfile(){
        return profile;
    }

    public String getMenuName(){
        return menuName;
    }

    public String getPrice(){
        return price;
    }

    public void setProfile(StorageReference profile) {
        this.profile = profile;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public void setPrice(String price){
        this.price = price;
    }
}
