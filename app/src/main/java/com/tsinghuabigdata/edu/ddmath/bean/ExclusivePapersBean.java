package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/5.
 */

public class ExclusivePapersBean implements Serializable{

    private static final long serialVersionUID = 7143800779290161328L;
    private String title;
    private int    status;
    private String excluId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExcluId() {
        return excluId;
    }

    public void setExcluId(String excluId) {
        this.excluId = excluId;
    }

}
