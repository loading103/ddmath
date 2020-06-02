package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean;

import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 业内分割线
 */

public class SplitLineBean implements Serializable {
    private static final long serialVersionUID = -7897597233047850731L;

    private float x1;
    private float x2;

    //for 本地保存使用
    public JSONObject getJsonObject(){

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put( "x1", x1 );
            jsonObject.put( "x2", x2 );
        }catch (Exception e){
            AppLog.d( "",e );
        }
        return jsonObject;
    }
    public float getX1() {
        return x1;
    }

    public void setX1(float x1) {
        this.x1 = x1;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }
}
