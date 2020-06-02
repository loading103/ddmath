package com.tsinghuabigdata.edu.ddmath.parent.bean;

import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 家长信息
 */

public class ParentInfo implements Serializable {

    private static final long serialVersionUID = -2593461308622993747L;

    private String realName;          //家长昵称
    private String headImage;           //家长头像
    private String phoneNumber;         //手机号
    private String parentId;            //ID
    private String accountId;


    private ArrayList<UserDetailinfo> studentInfos;   //家长下面的学生信息，以后可以支持多孩

    public String getHeadImage() {
        return headImage;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getParentId() {
        return parentId;
    }

    public ArrayList<UserDetailinfo> getStudentInfos() {
        return studentInfos;
    }

    public String getRealName() {
        return realName;
    }

    public String getAccountId() {
        return accountId;
    }
}
