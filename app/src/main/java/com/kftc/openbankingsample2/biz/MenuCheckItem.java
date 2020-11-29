package com.kftc.openbankingsample2.biz;

public class MenuCheckItem {
    private String price;
    private String info;
    private String count;

    public void setPrice(String _price) {
        price = _price;
    }

    public void setInfo(String _info){
        info = _info;
    }

    public void setCount(String _count) {
        count = _count;
    }

    public String getPrice(){
        return this.price;
    }

    public String getInfo(){
        return this.info;
    }

    public String getCount(){
        return this.count;
    }

}
