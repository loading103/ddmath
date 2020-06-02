package com.tsinghuabigdata.edu.ddmath.module.floatwindow.bean;

/**
 * 对话气泡 结构
 */

public class BubbleBean {

    public final static int TYPE_COMMON = 0;        //通用
    public final static int TYPE_SPEC   = 1;        //界面专属
    public final static int TYPE_DYNAMIC = 2;       //动态类信息

    public final static String UI_APP = "all";     //应用内所有

    private int type;        //类型
    private String data;        //内容
    private String uiname;      //UI名称

    private Object extend;      //扩展,用于跳转结构


    public BubbleBean( int type, String name, String data ){
        this.type = type;
        this.uiname = name;
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUiname() {
        return uiname;
    }

    public void setUiname(String uiname) {
        this.uiname = uiname;
    }

    public Object getExtend() {
        return extend;
    }

    public void setExtend(Object extend) {
        this.extend = extend;
    }
}
