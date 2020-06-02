package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WeekBean implements Serializable {

    private   long dateTime;    //时间
    private String week;      //周五
    private  int status;     //0 正常 1异常
    private  String dayString;   //11-24/25
    private Boolean haveWrongQuestion;//有无错题
    private Boolean isSelected;    //有没有被选中(自己加的)
    private List<WeekDatailBean> datas;//被临时保存的详情数据


    private  long createTime;
    private  String title;
    private  String contentId;       //   recordId /exclusiveId/
    private  Boolean hasPrivilege;  //true有使用权 false没有使用权
    private  long submitTime;        //提交时间
    private  int downloadStatus;        //0 未下载 1已下载
    private  int reviseStatus;        //0 未订正 1已订正
    private  int deadDays;        //1需要今日完成 2需要2天完成 3需要三天完成
    private boolean hasWatch;       //
    private boolean hasDownload; // 是否有一建下载 true 有一建下载 ，false没有一建下载
    private String factory;       //第三方公司名称
    private String  productId;
    private List<TaskBean> taskList;

    public WeekBean(long dateTime, String week, int status, String dayString, Boolean haveWrongQuestion) {
        this.dateTime = dateTime;
        this.week = week;
        this.status = status;
        this.dayString = dayString;
        this.haveWrongQuestion = haveWrongQuestion;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDayString() {
        return dayString;
    }

    public void setDayString(String dayString) {
        this.dayString = dayString;
    }

    public Boolean getHaveWrongQuestion() {
        return haveWrongQuestion;
    }

    public void setHaveWrongQuestion(Boolean haveWrongQuestion) {
        this.haveWrongQuestion = haveWrongQuestion;
    }

    public Boolean getSelected() {
        return isSelected;
    }

    public void setSelected(Boolean selected) {
        isSelected = selected;
    }

    public List<WeekDatailBean> getDatas() {
        return datas;
    }

    public void setDatas(List<WeekDatailBean> datas) {
        this. datas=new ArrayList<>();
        this.datas .addAll(datas) ;
    }
}
