package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean;

import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONObject;

import java.io.Serializable;

/**
 *
 */

public class QuestionNumAreaBean implements Serializable {

    private static final long serialVersionUID = 1162808644568095928L;

    private boolean hasBracket;
    private AreaBean location;

    //private ArrayList<Integer> questionDigit;
    private String questionDigit;

    //for 本地保存使用
    public JSONObject getJsonObject(){

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put( "hasBracket", hasBracket );
            jsonObject.put( "location", location.getJsonObject() );
            jsonObject.put( "questionDigit", questionDigit );

//            if( questionDigit!=null ){
//                JSONArray jsonArray = new JSONArray();
//                for( Integer areaBean : questionDigit ){
//                    jsonArray.add( areaBean );
//                }
//                jsonObject.put( "questionDigit", jsonArray );
//            }
        }catch (Exception e){
            AppLog.d( "",e );
        }
        return jsonObject;
    }

    public boolean isHasBracket() {
        return hasBracket;
    }

    public AreaBean getLocation() {
        return location;
    }

    public String getQuestionDigit() {
        return questionDigit;
    }
}
