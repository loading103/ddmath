package com.tsinghuabigdata.edu.ddmath.module.learnmaterial.bean;

import com.alibaba.fastjson.JSONArray;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 原版教辅答案区域 结构
 */

public class AnswerAreaBean implements Serializable {

    private static final long serialVersionUID = 4701846111107377757L;

    private int pageNum;                    //整本页码
    private int serialNum;                  //该框的序号，序号为1的框必须有textAreas
    private AreaBean manualArea;            //编辑画的答题区域

    //新增
    private ArrayList<AreaBean> realAnswerAreas;    //
    private QuestionNumAreaBean questionNumArea;

    //figure数据不包含以下内容
    private RelativeAreas relativeAreas;    //程序分析得到的答题区域
    private ArrayList<AreaBean> textAreas;  //编辑画的答题区域中的文本框
    private SplitLineBean splitLine;        //该页的分割线

    private boolean moveFlag = false;
    private boolean moveFlagCache = false;

    //for 本地保存使用
    public JSONObject getJsonObject(){

        JSONObject jsonObject = new JSONObject();
        try{
            jsonObject.put( "pageNum", pageNum );
            jsonObject.put( "serialNum", serialNum );
            jsonObject.put( "manualArea", manualArea.getJsonObject() );

            jsonObject.put( "relativeAreas", relativeAreas.getJsonObject() );
            if( textAreas!=null ){
                JSONArray jsonArray = new JSONArray();
                for( AreaBean areaBean : textAreas ){
                    jsonArray.add( areaBean.getJsonObject() );
                }
                jsonObject.put( "textAreas", jsonArray );
            }
            jsonObject.put( "splitLine", splitLine.getJsonObject() );

            if( realAnswerAreas!=null ){
                JSONArray jsonArray = new JSONArray();
                for( AreaBean areaBean : realAnswerAreas ){
                    jsonArray.add( areaBean.getJsonObject() );
                }
                jsonObject.put( "realAnswerAreas", jsonArray );
            }
            if( questionNumArea!=null ) jsonObject.put("questionNumArea",questionNumArea.getJsonObject());

        }catch (Exception e){
            AppLog.d( "",e );
        }
        return jsonObject;
    }
    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public AreaBean getManualArea() {
        return manualArea;
    }

    public void setManualArea(AreaBean manualArea) {
        this.manualArea = manualArea;
    }

    public RelativeAreas getRelativeAreas() {
        return relativeAreas;
    }

    public ArrayList<AreaBean> getTextAreas() {
        return textAreas;
    }

    public SplitLineBean getSplitLine() {
        return splitLine;
    }

    public boolean isMoveFlag() {
        return moveFlag;
    }

    public void setMoveFlagCache(){
        moveFlagCache = true;
    }

    public void initCacheData(){
        //moveFlagCache = moveFlag;       //
    }
    public void useCacheData(){
        moveFlag = moveFlagCache;
    }

    public ArrayList<AreaBean> getRealAnswerAreas() {
        return realAnswerAreas;
    }

    public QuestionNumAreaBean getQuestionNumArea() {
        return questionNumArea;
    }
}
