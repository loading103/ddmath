package com.tsinghuabigdata.edu.ddmath.module.message;


import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30.
 */

public class TopMessageUtils {

    /**
     * 可用弹窗消息
     */
    public static List<MessageInfo> isHomeMsgAvailable(List<MessageInfo> homeMessageList) {

        List<MessageInfo> availableHmsg = new ArrayList<MessageInfo>();
        long curr = System.currentTimeMillis();
        for (MessageInfo hmsg : homeMessageList) {
            //long endtime = DateUtils.parseMilliseconds(hmsg.getFrontPageEnd(), DateUtils.FORMAT_DATA_TIME);
            //是否超过了结束时间
            if (curr > hmsg.getFrontPageEnd() || curr < hmsg.getFrontPageStart()) {
                continue;
            }
            String position = hmsg.getPosition();
            if (!TextUtils.isEmpty(position) && "homeAlert".equals(position)) {
                availableHmsg.add(hmsg);
            }
        }
        LogUtils.i("availableHmsg size= " + availableHmsg.size());
        return availableHmsg;

    }

    /**
     * 可用轮播图消息
     */
    public static List<MessageInfo> isBannerMsgAvailable(List<MessageInfo> homeMessageList) {

        List<MessageInfo> availableHmsg = new ArrayList<MessageInfo>();
        long curr = System.currentTimeMillis();
        for (MessageInfo hmsg : homeMessageList) {
            //long endtime = DateUtils.parseMilliseconds(hmsg.getFrontPageEnd(), DateUtils.FORMAT_DATA_TIME);
            //是否超过了结束时间
            if (curr > hmsg.getFrontPageEnd() || curr < hmsg.getFrontPageStart()) {
                continue;
            }
            String position = hmsg.getPosition();
            if (!TextUtils.isEmpty(position) && "homeHead".equals(position)) {
                availableHmsg.add(hmsg);
            }
        }
        LogUtils.i("BannerMsg size= " + availableHmsg.size());
        return availableHmsg;

    }

    /**
     * 获得我的世界重要通知消息（两条）
     */
    public static List<MessageInfo> getWorkNews(List<MessageInfo> messageInfoList) {
        List<MessageInfo> workList = new ArrayList<>();
        List<MessageInfo> uploaddetailList = getSpecificWorkList(messageInfoList, MessageRemarkType.ACTIVITY_DDWORK_UPDETAIL);
        List<MessageInfo> detailList = getSpecificWorkList(messageInfoList, MessageRemarkType.ACTIVITY_DDWORK_DETAIL);
        workList.addAll(uploaddetailList);
        workList.addAll(detailList);
        LogUtils.i("workList size= " + workList.size());
        return workList;
    }

    private static List<MessageInfo> getSpecificWorkList(List<MessageInfo> messageInfoList, String workYype) {
        List<MessageInfo> workList = new ArrayList<>();
        for (MessageInfo msg : messageInfoList) {
            if (MessageInfo.S_READ.equals(msg.getStatus())) {
                continue;
            }
            String remark = msg.getRemark();
            if (TextUtils.isEmpty(remark) || "{}".equals(remark)) {
                continue;
            }
            try {
                JSONObject json = JSON.parseObject(remark);
                String type = json.getString("type");
                if (workYype.equals(type)) {
                    String workId = json.getString("workId");
                    msg.setWorkId(workId);
                    if (MessageRemarkType.ACTIVITY_DDWORK_UPDETAIL.equals(type)) {
                        msg.setWorkType(MessageInfo.ASSGIN_WORK);
                    } else if (MessageRemarkType.ACTIVITY_DDWORK_DETAIL.equals(type)) {
                        msg.setWorkType(MessageInfo.COREECT_WORK);
                    }
                    workList.add(msg);
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                AppLog.d("", e);
            }
        }
        return workList;
    }


}
