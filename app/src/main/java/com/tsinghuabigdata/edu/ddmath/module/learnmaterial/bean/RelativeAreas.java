package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean;

import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 程序分析得到的答题区域
 */

public class RelativeAreas implements Serializable {
    private static final long serialVersionUID = -3021959924401814398L;

    private AreaBean leftArea;
    private AreaBean rightArea;
    private AreaBean topArea;
    private AreaBean bottomArea;

    //for 本地保存使用
    public JSONObject getJsonObject(){

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put( "leftArea", leftArea.getJsonObject() );
            jsonObject.put( "rightArea", rightArea.getJsonObject() );
            jsonObject.put( "topArea", topArea.getJsonObject() );
            jsonObject.put( "bottomArea", bottomArea.getJsonObject() );

        }catch (Exception e){
            AppLog.d( "",e );
        }
        return jsonObject;
    }

    public AreaBean getLeftArea() {
        return leftArea;
    }

    public void setLeftArea(AreaBean leftArea) {
        this.leftArea = leftArea;
    }

    public AreaBean getTopArea() {
        return topArea;
    }

    public void setTopArea(AreaBean topArea) {
        this.topArea = topArea;
    }

    public AreaBean getRightArea() {
        return rightArea;
    }

    public void setRightArea(AreaBean rightArea) {
        this.rightArea = rightArea;
    }

    public AreaBean getBottomArea() {
        return bottomArea;
    }

    public void setBottomArea(AreaBean bottomArea) {
        this.bottomArea = bottomArea;
    }
}
