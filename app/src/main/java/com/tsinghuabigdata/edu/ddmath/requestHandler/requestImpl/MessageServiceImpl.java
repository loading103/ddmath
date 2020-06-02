package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequest;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.ExistNewMessage;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageList;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MessageService;

import java.util.ArrayList;
import java.util.List;


/**
 * 消息相关接口实现
 */
public class MessageServiceImpl extends BaseService implements MessageService {

    @Override
    public ArrayList<MessageInfo> queryMessageList(String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_MESSAGE_LIST);
        //url = "http://172.16.1.246:8080/tem-rest-officeSupport/rest/msgApp/message/latterly/:userId";
        //studentId = "STU98E8C8FB3B904782ABA5702FA713EFF4";

        HttpRequest request = AppRequestUtils.get(url)
                .putRestfulParam("userId", studentId)       //
                .putRequestParam("latterly", "7")           //最近天数，缺省值7天
                .request();
        String body = ((AppRequest) request).getFullBody();
        MessageList messageList = JSON.parseObject(body, MessageList.class);
        if (messageList.isSuccess()) {
            return messageList.getMessage();
        } else {
            return null;
        }
    }

    @Override
    public MessageInfo queryUserMsgInfo(String studentId, String rowkey) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_MESSAGE_DETAIL);

        //url = "http://172.16.1.246:8080/tem-rest-officeSupport/rest/msgApp/queryUserMsgInfo";
        //studentId = "STU98E8C8FB3B904782ABA5702FA713EFF4";

        HttpRequest request = AppRequestUtils.get(url)
                .putRequestParam("userId", studentId)
                .putRequestParam("rowKey", rowkey)
                .request();
        String body = ((AppRequest) request).getFullBody();

        JSONObject jsonObject = JSON.parseObject(body);
        Boolean success = jsonObject.getBoolean("success");
        if (success != null && success) {
            return JSON.parseObject(jsonObject.get("message").toString(), MessageInfo.class);
        } else /*if( success != null )*/ {
            //存在success 字段， = false   或者不存在 字段
            return null;
        }
    }

    @Override
    public ExistNewMessage queryNewMsg(String studentId) throws HttpRequestException, org.json.JSONException {

        String url = getUrl(AppRequestConst.GET_MESSAGE_NEW);
        //url = "http://172.16.1.246:8080/tem-rest-officeSupport/rest/msgApp/queryUserNewMsg/:accountId";
        //studentId = "STU98E8C8FB3B904782ABA5702FA713EFF4";

        HttpRequest request = AppRequestUtils.get(url)
                .putRestfulParam("accountId", studentId)
                .putRequestParam("msgColumn", "wdxx")
                .request();
        String body = ((AppRequest) request).getFullBody();

        JSONObject jsonObject = JSON.parseObject(body);
        Boolean success = jsonObject.getBoolean("success");
        if (success != null && success) {
            return JSON.parseObject(jsonObject.get("message").toString(), ExistNewMessage.class);
        } else /*if( success != null )*/ {
            //存在success 字段， = false   或者不存在 字段
            return null;
        }
    }

    @Override
    public List<MessageInfo> queryHomePageMsg(String accessToken, String accountId, String userRole) throws HttpRequestException, org.json.JSONException {
        String url = getUrl(AppRequestConst.GET_QUERY_HOME_MSG_LIST);
        HttpRequest request = AppRequestUtils.get(url)
                .putHeader("access_token", accessToken)
                .putRequestParam("userId", accountId)
                .putRequestParam("userRole", userRole)
                .request();

        String body = ((AppRequest) request).getFullBody();
        MessageList messageList = JSON.parseObject(body, MessageList.class);
        if (messageList != null && messageList.isSuccess()) {
            return messageList.getMessage();
        } else {
            return null;
        }
        //这个接口增加参数userRole，值为：student/patriarch/teacher/audituser 分别表示学生、家长、老师和众包批阅人员
    }

    @Override
    public List<MessageInfo> queryImportantMsg(String accountId) throws HttpRequestException, org.json.JSONException {
        String url = getUrl(AppRequestConst.GET_IMPORTANT_MSG_LIST);
        HttpRequest request = AppRequestUtils.get(url)
                .putRestfulParam("userId", accountId)
                .request();

        String body = ((AppRequest) request).getFullBody();
        MessageList messageList = JSON.parseObject(body, MessageList.class);
        if (messageList != null && messageList.isSuccess()) {
            return messageList.getMessage();
        } else {
            return null;
        }
    }
}
