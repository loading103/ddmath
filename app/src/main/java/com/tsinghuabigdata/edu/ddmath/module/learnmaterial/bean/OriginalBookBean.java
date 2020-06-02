package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/1/16.
 */

public class OriginalBookBean implements Serializable {

    private static final long serialVersionUID = 1354382911264274354L;
    /**
     * bookId : string
     * bookName : string
     * version : string
     * useTimes : int
     * lastModifyTime : date
     */

    private String bookId;
    private String bookName;
    private String version;
    private String coverPicture;
    private String publishers;
    private int useTimes;
    private long lastModifyTime;
    private float widthHeightRate;

    private boolean select;
    private int useCount;

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCoverPicture() {
        return coverPicture;
    }

    public void setCoverPicture(String coverPicture) {
        this.coverPicture = coverPicture;
    }

    public int getUseTimes() {
        return useTimes;
    }

    public void setUseTimes(int useTimes) {
        this.useTimes = useTimes;
    }

    public long getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(long lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public float getWidthHeightRate() {
        return widthHeightRate;
    }

    public void setWidthHeightRate(float widthHeightRate) {
        this.widthHeightRate = widthHeightRate;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getPublishers() {
        return publishers;
    }

    public void setPublishers(String publishers) {
        this.publishers = publishers;
    }

    public int getUseCount() {
        return useCount;
    }
}
