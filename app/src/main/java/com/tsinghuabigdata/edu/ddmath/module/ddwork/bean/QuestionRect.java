package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

import android.graphics.RectF;

import com.tsinghuabigdata.edu.ddmath.module.errorbook.NumberUtil;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * 题目答案矩形区域
 */
public class QuestionRect implements Serializable {

    private static final long serialVersionUID = 2674146233180358325L;

    //矩形区域
    private float x;
    private float y;
    private float width;
    private float height;

    private QuestionRect figure;

    public JSONObject getJsonObject(){
        JSONObject json = new JSONObject();
       try{
            json.put( "x", Float.valueOf( NumberUtil.double2floatFormat( x, 4 ))  );
            json.put( "y", Float.valueOf( NumberUtil.double2floatFormat( y, 4 )) );
            json.put( "width", Float.valueOf( NumberUtil.double2floatFormat( width, 4 )) );
            json.put( "height", Float.valueOf( NumberUtil.double2floatFormat( height, 4 )) );

            if( figure != null ){
                json.put("figure", figure.getJsonObject() );
            }
        }catch (Exception e){
            AppLog.i("",e);
        }
        return json;
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

    public QuestionRect getFigure() {
        return figure;
    }

    public void setFigure(QuestionRect figure) {
        this.figure = figure;
    }

    public RectF getRectF(){
        return new RectF( x, y, x+width, y+height );
    }
}
