package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean;

import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalQuestionInfo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 选择题号
 */

public class SelectQuestionBean implements Serializable {

    private static final long serialVersionUID = -4344136153719632051L;

    private String type;
    private ArrayList<LocalQuestionInfo> list;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<LocalQuestionInfo> getList() {
        return list;
    }

    public void setList(ArrayList<LocalQuestionInfo> list) {
        this.list = list;
    }
}
