package com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean;

import java.io.Serializable;

/**
 * 城市知识点排名
 */

public class CityRankBean implements Serializable {

    private static final long serialVersionUID = 4254345937899763841L;

    private int level;          //正确率范围 1 2 3 4 5 6
    private int totalCount;     //该范围内的人数

    public CityRankBean( int level, int totalCount ){
        this.level = level;
        this.totalCount = totalCount;
    }

    public int getLevel() {
        return level;
    }

    public int getTotalCount() {
        return totalCount;
    }
}
