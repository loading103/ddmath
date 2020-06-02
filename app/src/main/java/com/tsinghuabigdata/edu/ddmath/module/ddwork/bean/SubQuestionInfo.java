package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 小问结构
 */

public class SubQuestionInfo implements Serializable {

    private static final long serialVersionUID = -4407860413679326332L;

    private String subQuestionId;  // 小问id
    private int subNumber;   // 小问序号
    private String subTitle;    //
    private boolean subCorrect = false; // 小问对错
    private float questionScore;            //子问分数
    //private float studentScore;     //学生得分
    private ArrayList<SubQuestionInfo> children;

    private transient boolean subCorrectCache = false;

    //for 上传使用
    public JSONObject getJson(){
        JSONObject json = new JSONObject();
        try {
            json.put("subQuestionId",subQuestionId);
            json.put("subNumber",subNumber);
            json.put("subCorrect",subCorrect);
            json.put("subTitle",subTitle);
            //if( questionScore>0 ){
                json.put("studentScore",subCorrect? calStudentScore():0);
                json.put("questionScore",questionScore);
            //}

            if( children!=null ){
                JSONArray jsonArray = new JSONArray();
                for( SubQuestionInfo subQuestionInfo:children ){
                    jsonArray.put( subQuestionInfo.getJson() );
                }
                json.put("children",jsonArray);
            }
        }catch (JSONException e){
            AppLog.i("",e);
        }
        return json;
    }

    //for 本地保存
    public JSONObject getJsonObject(){
        JSONObject json = new JSONObject();
        try {
            json.put("subQuestionId",subQuestionId);
            json.put("subNumber",subNumber);
            json.put("subCorrect",subCorrect);
            json.put("subTitle",subTitle);
            json.put("questionScore",questionScore);

            if( children!=null ){
                JSONArray jsonArray = new JSONArray();
                for( SubQuestionInfo subQuestionInfo:children ){
                    jsonArray.put( subQuestionInfo.getJson() );
                }
                json.put("children",jsonArray);
            }
        }catch (JSONException e){
            AppLog.i("",e);
        }
        return json;
    }

    public void copy(SubQuestionInfo subQuestionInfo){
        subQuestionId = subQuestionInfo.subQuestionId;
        subNumber = subQuestionInfo.subNumber;
        subTitle = subQuestionInfo.subTitle;
        questionScore    = subQuestionInfo.questionScore;

        //本地使用 不拷贝，以本地为主 subCorrect = true; // 小问对错
        if( children!=null && subQuestionInfo.children!=null ){
            int min_size = children.size()<subQuestionInfo.children.size()?children.size():subQuestionInfo.children.size();
            for( int i=0; i<min_size; i++ ){
                SubQuestionInfo sub = children.get(i);
                sub.copy( subQuestionInfo.children.get(i));
            }
        }
    }

    /*public*/ void initCacheData(){
        subCorrectCache = subCorrect;
        if( children!=null ){
            for( SubQuestionInfo subQuestionInfo:children ){
                subQuestionInfo.initCacheData();
            }
        }
    }

    /*public*/ void setDeflautCorrectCache(){
        subCorrectCache = true;
        if( children!=null ){
            for( SubQuestionInfo subQuestionInfo:children ){
                subQuestionInfo.setDeflautCorrectCache();
            }
        }
    }

    /*public*/ void useCacheData(){
        subCorrect = subCorrectCache;
        if( children!=null ){
            for( SubQuestionInfo subQuestionInfo:children ){
                subQuestionInfo.useCacheData();
            }
        }
    }

    public void setSubCorrectCache(boolean correct){
        subCorrectCache = correct;
    }

//    public String getSubQuestionId() {
//        return subQuestionId;
//    }
//
//    public void setSubQuestionId(String subQuestionId) {
//        this.subQuestionId = subQuestionId;
//    }
//
//    public int getSubNumber() {
//        return subNumber;
//    }
//
//    public void setSubNumber(int subNumber) {
//        this.subNumber = subNumber;
//    }

//    public boolean isSubCorrect() {
//        return subCorrect;
//    }
    public boolean isSubCorrectCache() {
        return subCorrectCache;
    }

//    public void setSubCorrect(boolean subCorrect) {
//        this.subCorrect = subCorrect;
//    }

    public String getSubTitle() {
        return subTitle;
    }

    public ArrayList<SubQuestionInfo> getChildren() {
        return children;
    }

    public float getQuestionScore() {
        return questionScore;
    }

//    public void setQuestionScore(float questionScore) {
//        this.questionScore = questionScore;
//    }

    /*public*/ float calStudentScore(){

        if( children==null || children.size()==0 ){
            return subCorrect?questionScore:0;          //要么全分，要么零分
        }

        float score = 0;
        for( SubQuestionInfo info : children ){
            score += info.calStudentScore();
        }
        return score;
    }

//    private boolean calStudentCorrect(){
//
//        if( children==null || children.size()==0 ){
//            return subCorrect;          //本身的对错
//        }
//
//        boolean correct = true;
//        for( SubQuestionInfo info : children ){
//            correct = correct && info.calStudentCorrect();
//        }
//        subCorrect = correct;
//        return subCorrect;
//    }

}
