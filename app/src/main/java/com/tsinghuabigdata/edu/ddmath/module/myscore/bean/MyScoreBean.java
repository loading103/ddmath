package com.tsinghuabigdata.edu.ddmath.module.myscore.bean;

import java.io.Serializable;

/**
 * 积分项列表
 */

public class MyScoreBean implements Serializable{

    private static final long serialVersionUID = 6753147940106524378L;

    public final static int ST_TASK_NONE = 0;       //无任务
    public final static int ST_TASK_FINISHED = 1;       //已完成
    public final static int ST_TASK_UNFINISH = 2;       //待完成

    private String eventId;       //事件ID
    private String eventName;       //事件名称

    private String imgPath;         //

    private int useUnit;        //积分项类型，1：每日一次  规则重复操作限制单位，1：每日，2：每周， 3：每月
    private int useLimit;       //规则重复操作限制次数

    private int pointAmt;        //积分值
    private int status;      //0无任务 1 已完成 2 待完成

    private String classId;     //
    private String className;   // 存在多班的情况，且有新的作业、考试不在当前班级
    private String schoolName;

    private int toUrl;          //0: 跳转到老师布置的作业的诊断结果列表 1: 跳转到自我诊断的列表

    private long createTime;

    //以下是临时使用
    public final static int TYPE_COMMAND_TITLE = 0;         //推荐积分的标题
    public final static int TYPE_COMMAND_ITEM = 1;         //推荐积分的子项
    public final static int TYPE_RECORD_TITLE = 2;          //积分记录的标题
    public final static int TYPE_RECORD_ITEM = 3;          //积分记录的子项
    public final static int TYPE_EMPTY_DATA = 4;          //积分记录的子项

    private transient int type;             //

    public MyScoreBean(){}
    public MyScoreBean( int type ){
        this.type = type;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public String getImgPath() {
        return imgPath;
    }

    public int getUseUnit() {
        return useUnit;
    }
    public String getUseUnitStr(){
        String unit = "日";
        if( useUnit == 2 ) unit = "周";
        else if( useUnit == 3 ) unit = "月";
        return unit;
    }

    public int getUseLimit() {
        return useLimit;
    }

    public int getPointAmt() {
        return pointAmt;
    }

    public int getStatus() {
        return status;
    }

    public String getClassId() {
        return classId;
    }

    public String getClassName() {
        return className;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getToUrl() {
        return toUrl;
    }
}
