package com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean;

import java.io.Serializable;

/**
 * 名师精讲单个视频体类
 * Created by Administrator on 2018/2/6.
 */

public class SingleVideoBean implements Serializable {


    private  String videoId;
    private  String videoName;
    private  String grade;
    private  String version;
    private  int videoLong;
    private  String factory;
    private  int status;
    private  long broadcastTimes;
    private  String imageUrl;
    private  String chapter;
    private  String section;
    private int price;

    public static final int Exchanged = 1; //已兑换


    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVideoLong() {
        return videoLong;
    }

    public void setVideoLong(int videoLong) {
        this.videoLong = videoLong;
    }

    public String getFactory() {
        return factory;
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getBroadcastTimes() {
        return broadcastTimes;
    }

    public void setBroadcastTimes(long broadcastTimes) {
        this.broadcastTimes = broadcastTimes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }


}
