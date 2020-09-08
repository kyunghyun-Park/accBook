package com.example.accountbook;

public class TodayData {
    private String price;
    private String time;
    private String place;
    private String usage;

    public String getPrice(){
        return price;
    }

    public void setPrice(String price){
        this.price = price;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getPlace(){
        return place;
    }

    public void setPlace(String place){
        this.place = place;
    }

    public String getUsage(){
        return usage;
    }

    public void setUsage(String usage){
        this.usage = usage;
    }
}
