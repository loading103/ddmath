package com.tsinghuabigdata.edu.ddmath.bean;

import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 用户详细信息
 * Created by HP on 2015/10/23.
 */
public class UserDetailinfo implements Serializable {
    private static final long serialVersionUID = 2477113610337066703L;

    /**
     * name	String	姓名
     * schoolName	String	学校名称
     * serial	Date	入学年份
     * studentId	String	学生ID
     * headImage	String	头像路径
     * phone	String	电话
     * classId	String	班级ID
     * className	String	班级名称
     * schoolId       String 学校Id
     */
    private String reallyName;
    private String nickName;
    private String schoolName;
    private String serial;
    private String studentId;
    private String headImage;
    private String phone;
//    private String classId;
//    private String className;
    private String sex;
    private String fileAddress;
    private String schoolId;
    private String examNumber;
    private String mailingAddress;
    private QueryTutorClassInfo queryTutorClassInfo;
    private String accountId;
    private String channelId;         //注册渠道
    private String loginName;

    private int vipLevel;           //会员等级
    private long vipUseEndTime;            //会员到期时间
    private int vipRemainTime;      //到期剩余天数

    private ArrayList<ScoreProductBean> headPendants;   //挂件列表

    private ArrayList<MyTutorClassInfo> classInfos;     //共家长端使用


    public void useHeadPendent( String productId ){

        if( headPendants == null || headPendants.size() == 0 || TextUtils.isEmpty(productId) ) return;

        //先取消之前的挂件
        for( ScoreProductBean pendent : headPendants ){
            pendent.setUseStatus( productId.equals(pendent.getProductId())?1:0 );
        }
        //缓存
        AccountUtils.setUserdetailInfo( this );
    }

    public void addUserPendent( ScoreProductBean pendent ){
        if( headPendants == null ) headPendants = new ArrayList<>();
        headPendants.add(0, pendent );

        //缓存
        AccountUtils.setUserdetailInfo( this );
    }

    public String getReallyName() {
        return reallyName;
    }

    public void setReallyName(String reallyName) {
        this.reallyName = reallyName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFileAddress() {
        return fileAddress;
    }

    public void setFileAddress(String fileAddress) {
        this.fileAddress = fileAddress;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getExamNumber() {
        return examNumber;
    }

    public void setExamNumber(String examNumber) {
        this.examNumber = examNumber;
    }

    public QueryTutorClassInfo getQueryTutorClassInfo() {
        return queryTutorClassInfo;
    }

    public void setQueryTutorClassInfo(QueryTutorClassInfo queryTutorClassInfo) {
        this.queryTutorClassInfo = queryTutorClassInfo;
    }
    public String getMailAddr(){
        return mailingAddress;
    }

    public ArrayList<MyTutorClassInfo> getClassInfos() {
        return classInfos;
    }

    public String getAccountId() {
        return accountId;
    }

    //用户是否可以购买套餐，不能购买，隐藏
    public boolean enableBuySuite(){
//        return false;
        return TextUtils.isEmpty(channelId) || !( "GTMF8".equals( channelId ) );     //天津渠道
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }

    public ArrayList<ScoreProductBean> getHeadPendants() {
        return headPendants;
    }

    public long getExpire() {
        return vipUseEndTime;
    }

    public int getVipRemainTime() {
        return vipRemainTime;
    }
}
