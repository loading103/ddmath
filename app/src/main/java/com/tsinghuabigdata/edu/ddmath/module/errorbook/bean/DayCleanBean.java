package com.tsinghuabigdata.edu.ddmath.module.errorbook.bean;

import com.tsinghuabigdata.edu.ddmath.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 日日清任务
 */

public class DayCleanBean implements Serializable {

    private static final long serialVersionUID = 1737793129857846517L;

    private long  wrongQuestionDate;    //错题日期  自己组装作业名称
    private String dayOfWeek;           //错题星期  自己组装作业名称

    private long  newestReviseTime;        //上次订正时间

    private int tobeReviseCount;      //待订正数量
    private int hasReviseCount;         //已订正数量
    private int markingInCount;         //批阅中的数量
    private int reviseRightCount;     //订正正确数量
    private int reviseWrongCount;     //订正错误数量

    private int listNumber;         //列表号

    private transient String title;     //标题

    public long getWrongQuestionDate() {
        return wrongQuestionDate;
    }

    public void setWrongQuestionDate(long wrongQuestionData) {
        this.wrongQuestionDate = wrongQuestionData;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public long getNewestReviseTime() {
        return newestReviseTime;
    }

    public void setNewestReviseTime(long newestReviseTime) {
        this.newestReviseTime = newestReviseTime;
    }

    public int getTobeReviseCount() {
        return tobeReviseCount;
    }

    public void setTobeReviseCount(int tobeReviseCount) {
        this.tobeReviseCount = tobeReviseCount;
    }

    public int getHasReviseCount() {
        return hasReviseCount;
    }

    public void setHasReviseCount(int hasReviseCount) {
        this.hasReviseCount = hasReviseCount;
    }

    public int getReviseRightCount() {
        return reviseRightCount;
    }

    public void setReviseRightCount(int reviseRightCount) {
        this.reviseRightCount = reviseRightCount;
    }

    public int getReviseWrongCount() {
        return reviseWrongCount;
    }

    public void setReviseWrongCount(int reviseWrongCount) {
        this.reviseWrongCount = reviseWrongCount;
    }

    public String getTitle() {
        if( title == null ){
            Date date = new Date();
            date.setTime( wrongQuestionDate );
            title = DateUtils.format(date) + DateUtils.getWeekOfDate(date)+"错题订正";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getListNumber() {
        return listNumber;
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }

    public int getMarkingInCount() {
        return markingInCount;
    }

    public void setMarkingInCount(int markingReviseCount) {
        this.markingInCount = markingReviseCount;
    }
}
