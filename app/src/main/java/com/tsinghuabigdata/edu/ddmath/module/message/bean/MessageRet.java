package com.tsinghuabigdata.edu.ddmath.module.message.bean;

import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageRet implements Serializable {

    private static final long serialVersionUID = 6967460945995503446L;

    private int pageNum;
    private int pageSize;
    private int totalCount;
    private int totalPage;
    private ArrayList<MessageInfo> items;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public ArrayList<MessageInfo> getItems() {
        return items;
    }

    public void setItems(ArrayList<MessageInfo> items) {
        this.items = items;
    }

    /**
     * type token
     *
     * @return
     */
    public final static TypeToken<MessageRet> getMsgListRetToken() {
        return new TypeToken<MessageRet>() {
        };
    }
}
