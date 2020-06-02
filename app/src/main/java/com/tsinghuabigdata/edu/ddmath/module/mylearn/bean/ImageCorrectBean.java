package com.tsinghuabigdata.edu.ddmath.module.mylearn.bean;

import java.io.Serializable;

/**
 * 批改结果 信息
 */
public class ImageCorrectBean implements Serializable {

    private static final long serialVersionUID = -1412518511705873501L;

    //status说明
    public static final int CORRECT_RIGHT = 1;          //1=>大正确
    public static final int CORRECT_HALF = 2;           //2=>大半对
    public static final int CORRECT_WRONG = 3;          //3=>大错误
    public static final int CORRECT_SMALL_RIGHT = 4;    //4=>小正确
    public static final int CORRECT_SMALL_HALF = 5;     //5=>小半对
    public static final int CORRECT_SMALL_WRONG = 6;    //6=>小错误
    public static final int CORRECT_DOUBT_WRONG = 7;    //7=>疑问

    private int status;
    private ImagePositionBean pos;
    private ImageSizeBean size;

    //标注信息
    private String commentContent;        //错误原因 标注文本

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public ImagePositionBean getPos() {
        return pos;
    }

    public void setPos(ImagePositionBean pos) {
        this.pos = pos;
    }

    public ImageSizeBean getSize() {
        return size;
    }

    public void setSize(ImageSizeBean size) {
        this.size = size;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String text) {
        this.commentContent = text;
    }
}
