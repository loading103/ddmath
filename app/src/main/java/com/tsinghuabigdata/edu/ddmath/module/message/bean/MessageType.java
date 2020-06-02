/*
 * Copyright (c) 2016. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.module.message.bean;

/**
 * 消息类型
 * Created by yanshen on 2016/4/15.
 */
public class MessageType {
    /**
     * 解析失败
     */
    public static final String PARSE_FAILED = "parse_failed";
    /**
     * 加班班级
     */
    public static final String JOINED = "joined";
    /**
     * 做题统计
     */
    public static final String QUES_WEEK = "ques_week";
    /**
     * 家庭作业
     */
    public static final String HOMEWORK = "homework.s.paper";
    /**
     * 作业详情
     */
    public static final String HOMEWORK_DETAIL = "homework.t.detail";
    /**
     * 考试
     */
    public static final String EXAMINATION = "examination.s.analysis";
    /**
     * 考试试卷详情
     */
    public static final String EXAMINATION_DETAIL = "examination.s.paper";
    /**
     * 练习题
     */
    public static final String TRAINING = "training.t.studentAnalysis";
    /**
     * 练习创建
     */
    public static final String EXER_HOME_CREATE = "exerhome.create";
    /**
     * 周分析
     */
    public static final String HOMEWORK_WEEK_ANALYSIS = "homework.s.weekAnalysis";
}
