package com.tsinghuabigdata.edu.ddmath.bean;

import android.text.TextUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 登录实体
 * Created by HP on 2015/10/23.
 */
public class LoginInfo implements Serializable {

    private static final long serialVersionUID = 6196682270844520873L;

    /**
     * access_token	string	登录成功，仅能调用4.11.2 rest接口的授权
     * userId	string	用户Id
     * loginName	string	登录名
     * reallyName	string	真实姓名
     * accountTypeNum	int	账户类别数量，值为1
     * userInfos	Object[]	用户信息数组（一个帐号对应多种角色，如：既是老师，又是家长）
     * accountType	string	账户类型（唯一）
     * accountId	string	账户Id
     * status	int	账户状态
     * headImagePath	string	头像图片名称
     * lastLoginTime	date	最后登录时间
     * createTime	date	创建时间
     */
    private String              accessToken;
    private String              accountId;
    private String              accountType;
    private String              userId;
    private String              loginName;
    private String              reallyName;
    private int                 accountTypeNum;//账户类别数量，值为1
    private ArrayList<UserInfo> userInfos;

    //图片文件服务器地址
    private String fileServer;

    public String getFileServer() {
        if (TextUtils.isEmpty(fileServer)) {
            return "http://www.iclassedu.com/";
        }
        return fileServer;
    }

    public void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getReallyName() {
        return reallyName;
    }

    public void setReallyName(String reallyName) {
        this.reallyName = reallyName;
    }

    public int getAccountTypeNum() {
        return accountTypeNum;
    }

    public void setAccountTypeNum(int accountTypeNum) {
        this.accountTypeNum = accountTypeNum;
    }

    public List<UserInfo> getUserInfos() {
        return userInfos;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setUserInfos(ArrayList<UserInfo> userInfos) {
        this.userInfos = userInfos;
    }
}
