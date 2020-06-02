package com.tsinghuabigdata.engine.entity;

/**
 * engine result for detect classify with rect
 * Created by ZhangXiao on 2018/8/30 15:32.
 */
public class ClassifyRectEntity {
    private float x; //x ratio
    private float y; //y ratio
    private float width;// width ratio
    private float height;// height ratio
    private int idx;//class id
    private String classStr;//class str
    public ClassifyRectEntity(float x, float y, float width, float height, int idx, String classStr){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.idx = idx;
        this.classStr = classStr;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getIdx() {
        return idx;
    }

    public String getClassStr() {
        return classStr;
    }
}
