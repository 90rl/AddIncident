package com.example.addincident;

import java.sql.Time;
import java.util.Date;

public class Upload {
    private String mName;
    private String mImageUrl;
    private double posX, posY;
    private String mTextMessage;
    private String mDateStr;

    public Upload(){
    //empty constructor needed
    }

    public Upload(String name, String imageUrl, double posXx, double posYy, String mTextMessageE, String date) {
        mName = name;
        mImageUrl = imageUrl;
        posX = posXx;
        posY = posYy;
        mTextMessage = mTextMessageE;
        mDateStr = date;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getText() {
        return mTextMessage;
    }

    public void setText(String mTextMessageE) {
        mTextMessage = mTextMessageE;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posXx) {
        posX = posXx;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posYy) {
        posY = posYy;
    }

    public String getDate() {
        return mDateStr;
    }

    public void setDate(String date) {
        mDateStr = date;
    }

}

