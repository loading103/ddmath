package com.tsinghuabigdata.edu.ddmath.module.entrance;

/**
 * 图片刷新
 */
public interface RefreshImageListener {
    void onRefresh();
    void addAddTypeImage();
    void removeAddTypeImage();
    void setEditMode( boolean  editMode );
}
