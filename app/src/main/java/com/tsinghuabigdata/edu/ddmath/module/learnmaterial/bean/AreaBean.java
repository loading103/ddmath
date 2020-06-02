package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean;

import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 区域
 */

public class AreaBean implements Serializable {
        private static final long serialVersionUID = 7415899326846716166L;
        private float x;
        private float y;
        private float width;
        private float height;

    //for 本地保存使用
    public JSONObject getJsonObject(){

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put( "x", x );
            jsonObject.put( "y", y );
            jsonObject.put( "width", width );
            jsonObject.put( "height", height );
        }catch (Exception e){
            AppLog.d( "",e );
        }
        return jsonObject;
    }
    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }
}
