package com.tsinghuabigdata.edu.ddmath.module.ddwork.bean;

/**
 * 分享子类
 */

public class ShareBean {

    public static final int TYPE_SERIESHIT  = 0;    //连击
    public static final int TYPE_RIGHTRATE  = 1;    //正确率
    public static final int TYPE_SUBMITWORK = 2;    //提交排名
    public static final int TYPE_CLASSRANK  = 3;    //正确率排名
    public static final int TYPE_RIGHTALL   = 4;    //正确总数

    private int type;           //类型

    private int value;          //值

    private int times;          //仅连击使用
    private String name;        //正确率 or 得分率

    private transient int startpos; //记录在文本中的位置
    private transient int len;      //文本中的长度

    public ShareBean(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStartpos() {
        return startpos;
    }

    public void setStartpos(int startpos) {
        this.startpos = startpos;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }
}
