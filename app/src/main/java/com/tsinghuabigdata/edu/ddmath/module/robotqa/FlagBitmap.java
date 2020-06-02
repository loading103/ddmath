package com.tsinghuabigdata.edu.ddmath.module.robotqa;

import android.graphics.Bitmap;

/**
 * Created by 28205 on 2016/11/17.
 */
public class FlagBitmap {
    private boolean isSelected;
    private Bitmap bitmap;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}