package com.tsinghuabigdata.edu.ddmath.bean;


import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;

import java.io.Serializable;

/**
 * 用户实体类
 * Created by HP on 2015/10/23.
 */
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 5394813144202454977L;
    /**
     * accountType	string	账户类型（唯一）
     * accountId	string	账户Id
     * status	int	账户状态
     * headImagePath	string	头像图片名称
     * lastLoginTime	date	最后登录时间
     * createTime	date	创建时间
     */

    private String accountType;
    private String accountId;
    private String status;
    private String headImagePath;
    private String lastLoginTime;
    private String createTime;

    private ParentInfo parentInfoVo;        //家长端登录时使用

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getHeadImagePath() {
        return headImagePath;
    }

    public void setHeadImagePath(String headImagePath) {
        this.headImagePath = headImagePath;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public ParentInfo getParentInfoVo() {
        return parentInfoVo;
    }

    public void setParentInfoVo(ParentInfo parentInfoVo) {
        this.parentInfoVo = parentInfoVo;
    }
}