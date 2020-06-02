package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/1/22.
 */

public class BookListBean implements Serializable {

    private int                    totalCount;
    private List<OriginalBookBean> items;

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public List<OriginalBookBean> getItems() {
        return items;
    }

    public void setItems(List<OriginalBookBean> items) {
        this.items = items;
    }
}
