package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/1/17.
 */

public class CatalogBean implements Serializable {

    private String                bookName;
    private String                bookId;
    private String                coverPicture;

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    private float                 widthHeightRate;
    private List<SectionListBean> sectionList;

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }


    public float getWidthHeightRate() {
        return widthHeightRate;
    }

    public void setWidthHeightRate(float widthHeightRate) {
        this.widthHeightRate = widthHeightRate;
    }

    public List<SectionListBean> getSectionList() {
        return sectionList;
    }

    public void setSectionList(List<SectionListBean> sectionList) {
        this.sectionList = sectionList;
    }

}
