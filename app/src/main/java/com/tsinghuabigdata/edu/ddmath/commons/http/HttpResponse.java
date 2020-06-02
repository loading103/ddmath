package com.tsinghuabigdata.edu.ddmath.commons.http;

import java.io.Serializable;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/21.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName: com.tsinghuabigdata.edu.ddmath.commons.http
 * @createTime: 2015/11/21 11:22
 */
public class HttpResponse<DATA> implements Serializable{

    private static final long serialVersionUID = -9096081709022693564L;
    /**
     * 状态码
     */
    private int code;
    /**
     * 错误描述
     */
    private String errorDescribe;
    /**
     * 信息描述
     */
    private String inform;
    /**
     * 消息
     */
    private String message;
    /**
     * 返回数据
     */
    private transient DATA data; //NOSONAR

    public HttpResponse(){

    }

    public HttpResponse(int code, String errorDescribe, String inform, String message, DATA data) {
        this.code = code;
        this.errorDescribe = errorDescribe;
        this.inform = inform;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorDescribe() {
        return errorDescribe;
    }

    public void setErrorDescribe(String errorDescribe) {
        this.errorDescribe = errorDescribe;
    }

    public String getInform() {
        return inform;
    }

    public void setInform(String inform) {
        this.inform = inform;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DATA getData() {
        return data;
    }

    public void setData(DATA data) {
        this.data = data;
    }
}
