package com.tsinghuabigdata.edu.ddmath.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 最近一周周题练和每周培优状态
 * Created by Administrator on 2018/7/5.
 */

public class WeekErrorStatus implements Serializable{

    //状态，0：未分享下载，1：已分享下载
    public static final int NOT_SHARE = 0;
    public static final int SHARED = 1;
    private static final long serialVersionUID = 3334036373998479014L;

    private List<ExclusivePapersBean> exclusivePapers;
    private List<WeekExercisesBean>   weekExercises;

    public List<ExclusivePapersBean> getExclusivePapers() {
        return exclusivePapers;
    }

    public void setExclusivePapers(List<ExclusivePapersBean> exclusivePapers) {
        this.exclusivePapers = exclusivePapers;
    }

    public List<WeekExercisesBean> getWeekExercises() {
        return weekExercises;
    }

    public void setWeekExercises(List<WeekExercisesBean> weekExercises) {
        this.weekExercises = weekExercises;
    }


}
