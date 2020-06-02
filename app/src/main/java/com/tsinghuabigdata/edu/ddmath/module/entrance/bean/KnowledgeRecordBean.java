package com.tsinghuabigdata.edu.ddmath.module.entrance.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 知识分析结果记录
 */
public class KnowledgeRecordBean implements Serializable{

    private static final long serialVersionUID = 7701107932087141585L;

    private String  recordId;
    private ArrayList<String> images;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
