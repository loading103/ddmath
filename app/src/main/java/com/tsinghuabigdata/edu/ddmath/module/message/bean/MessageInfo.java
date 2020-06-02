package com.tsinghuabigdata.edu.ddmath.module.message.bean;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageRemarkType;

import java.io.Serializable;
import java.util.List;

/**
 * 消息体信息
 */
public class MessageInfo implements Serializable {

    private static final long serialVersionUID = -1743587594919563535L;

    public static final String S_UNREAD    = "SNOR";  //未读
    public static final String S_READ      = "ARRE"; //已读
    public static final String S_SEND_FAIL = "SEOF";  //发送失败
    public static final String S_CLEAR     = "CLEAR"; //清空

    public static final int MSG_TYPE_CAUTION = 0;   //提醒类型
    public static final int MSG_TYPE_WORK    = 1;   //作业
    public static final int MSG_TYPE_REPORT  = 2;   //报告

    public static final int ASSGIN_WORK  = 1;
    public static final int COREECT_WORK = 2;


    private String appName;             //平台
    private String description;         //简单描述
    private String msgTitle;            //消息标题
    private String status;              //消息状态：未读：SNOR；已读ARRE；发送失败：SEOF;清空：CLEAR
    private long   sendTime;              //消息发送时间
    private long   readTime;              //消息读取时间
    private String rowKey;              //rowKey 消息id

    private String remark;              //跳转信息

    //消息栏目id
    private int    msgTypeId;
    //消息栏目（系统消息（sys），我的消息 manual（man），准星推荐recommend（rec）
    private String msgType;
    //消息类别id
    private int    categoryId;
    //消息类别：活动（rwd），精选（sel），小贴士（tip）
    private String category;
    private String content;
    //置顶是（Y）,否（N）
    private String sticky;

    private long   frontPageEnd;
    private String frontPageModel;
    private long   frontPageStart;
    private String localImgPath;

    private boolean isShowed;

    //图片地址
    private String picUrl;
    //消息级别
    private int    msgLevel;

    //用于我的世界重要通知消息
    private String workId;
    private int    workType;

    private String position;

    //本地使用,不需要序列化
    private transient boolean select;
    private transient boolean keyTips     = false;
    private transient int     msgDataType = MSG_TYPE_CAUTION;


    public String getRowKey() {
        return rowKey;
    }

    public void setRowKey(String rowKey) {
        this.rowKey = rowKey;
    }

    public long getSendTime() {
        return sendTime;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getMsgTypeId() {
        return msgTypeId;
    }

    public void setMsgTypeId(int msgTypeId) {
        this.msgTypeId = msgTypeId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSticky() {
        return sticky;
    }

    public void setSticky(String sticky) {
        this.sticky = sticky;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getMsgLevel() {
        return msgLevel;
    }

    public void setMsgLevel(int msgLevel) {
        this.msgLevel = msgLevel;
    }

    public long getFrontPageEnd() {
        return frontPageEnd;
    }

    public void setFrontPageEnd(long frontPageEnd) {
        this.frontPageEnd = frontPageEnd;
    }

    public long getFrontPageStart() {
        return frontPageStart;
    }

    public void setFrontPageStart(long frontPageStart) {
        this.frontPageStart = frontPageStart;
    }

    public String getFrontPageModel() {
        return frontPageModel;
    }

    public void setFrontPageModel(String frontPageModel) {
        this.frontPageModel = frontPageModel;
    }

    public String getLocalImgPath() {
        return localImgPath;
    }

    public void setLocalImgPath(String localImgPath) {
        this.localImgPath = localImgPath;
    }

    public boolean isShowed() {
        return isShowed;
    }

    public void setShowed(boolean showed) {
        isShowed = showed;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getReadTime() {
        return readTime;
    }

    public void setReadTime(long readTime) {
        this.readTime = readTime;
    }

    /**
     * type token
     *
     * @return
     */
    public final static TypeToken<List<MessageInfo>> getMessageInfoListToken() {
        return new TypeToken<List<MessageInfo>>() {
        };
    }

    public final static TypeToken<MessageInfo> getMessageInfoToken() {
        return new TypeToken<MessageInfo>() {
        };
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }


    public boolean isKeyTips() {
        return keyTips;
    }

    public void setKeyTips(boolean keyTips) {
        this.keyTips = keyTips;
    }

    public int getMsgDataType() {
        return msgDataType;
    }

    public void setMsgDataType(int msgDataType) {
        this.msgDataType = msgDataType;
    }

    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public int getWorkType() {
        return workType;
    }

    public void setWorkType(int workType) {
        this.workType = workType;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void initData() {
        //默认
        keyTips = false;
        msgDataType = MSG_TYPE_CAUTION;

        if (TextUtils.isEmpty(remark) || "{}".equals(remark))
            return;

        remark = remark.replace("+", ",");
        JSONObject json = JSON.parseObject(remark);
        String type = json.getString("type");
        if (TextUtils.isEmpty(type))
            return;

        if (type.startsWith("ddmath.report.create")) {
            msgDataType = MSG_TYPE_REPORT;
        } else if (type.startsWith("ddmath.ddwork") || type.startsWith("ddmath.schoolwork")) {
            msgDataType = MSG_TYPE_WORK;
        } else if (type.equals(MessageRemarkType.ENROLEXAM_REPORT_RETRIEVE)) {
            keyTips = true;
        }
    }
}
