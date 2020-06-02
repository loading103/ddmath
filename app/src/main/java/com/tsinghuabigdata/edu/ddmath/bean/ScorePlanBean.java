package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

public class ScorePlanBean implements Serializable {
    private static final long serialVersionUID = 5915451910797146578L;

    private boolean finishAllTasks;    //true:任务全部完成，false:有未完成的任务
    private boolean confirmJoin;      //true:已加入定制学，flase:未加入定制学
    private boolean right;  // true:有权限，false:无权限
    private String productSuitId;   //推荐购买的套餐Id，right为false时返回
    private int userNum;             //使用定制学的用户数
    private int dayWeek;             //1~7:周一到周日
    private String remindContent;    //提示内容
    private String customizedPrivilegeIds;

    public boolean isFinishAllTasks() {
        return finishAllTasks;
    }

    public boolean isConfirmJoin() {
        return confirmJoin;
    }

    public boolean isRight() {
        return right;
    }

    public String getProductSuitId() {
        return productSuitId;
    }

    public int getUserNum() {
        return userNum;
    }

    public int getDayWeek() {
        return dayWeek;
    }

    public String getRemindContent() {
        return remindContent;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public String getCustomizedPrivilegeIds() {
        return customizedPrivilegeIds;
    }
}
