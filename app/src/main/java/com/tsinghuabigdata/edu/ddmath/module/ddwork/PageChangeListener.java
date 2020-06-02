package com.tsinghuabigdata.edu.ddmath.module.ddwork;

/**
 * 滑动时自动翻页处理
 */

public interface PageChangeListener {
    void pageUp();
    void pageDown();
    void pageChange( int firstVisibleItem );
}
