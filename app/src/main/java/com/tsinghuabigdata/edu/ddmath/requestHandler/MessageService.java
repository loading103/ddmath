package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.ExistNewMessage;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2016/2/1.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName:
 * @createTime: 2016/2/1 14:45
 */
public interface MessageService {

    /**
     * 得到消息列表
     * @param studentId
     * @return list
     */
    ArrayList<MessageInfo> queryMessageList(String studentId ) throws HttpRequestException,JSONException;

    /**
     * 消息详情
     * @param studentId ID
     * @param rowkey    消息key
     * @return 详情
     */
    MessageInfo queryUserMsgInfo( String studentId, String rowkey ) throws HttpRequestException, JSONException;

    /**
     * 查询是否有新消息，消息列表拉取时间开始
     * @param studentId ID
     * @return true 新消息
     */
    ExistNewMessage queryNewMsg(String studentId ) throws HttpRequestException, JSONException;

    List<MessageInfo> queryHomePageMsg(String accessToken, String accountId, String userRole) throws HttpRequestException, JSONException;

    /**
     * 获取首页轮播消息
     */
    List<MessageInfo> queryImportantMsg(String accountId) throws HttpRequestException, JSONException;

//    /**
//     * 读取消息
//     * @param token
//     * @param sutdentId
//     * @param messageId
//     * @throws HttpRequestException
//     */
//    void readMessage(String token, String sutdentId, String messageId) throws HttpRequestException;
//
//    List<MessageInfo> queryHomePageMsg(String accessToken, String accountId) throws HttpRequestException, JSONException;
//    MessageRet queryUserMsg(String accessToken, String accountId, String msgColumn, int pageNum, int pageSize) throws HttpRequestException, JSONException;
//    MessageInfo queryUserMsgInfo(String accessToken, String accountId, String rowkey) throws HttpRequestException, JSONException;
//    ResultInfo cleanUserMsg(String accessToken, String accountId, String[] rowkeys) throws HttpRequestException, JSONException;

}
