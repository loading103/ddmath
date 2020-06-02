package com.tsinghuabigdata.edu.ddmath.bean;

import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.RankBean;

import java.io.Serializable;
import java.util.List;

/**
 * 荣耀排行榜-班级排行榜-个人中心
 * Created by Administrator on 2017/11/16.
 */

public class ClassGloryRank implements Serializable {
    private int            totalCount;
    private int            totalPage;
    private RankBean       studentRank;
    private List<RankBean> classRank;

    public RankBean getStudentRank() {
        return studentRank;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public void setStudentRank(RankBean studentRank) {
        this.studentRank = studentRank;
    }

    public List<RankBean> getClassRank() {
        return classRank;
    }

    public void setClassRank(List<RankBean> classRank) {
        this.classRank = classRank;
    }
}
