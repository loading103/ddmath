package com.tsinghuabigdata.edu.ddmath.parent.bean;

import java.io.Serializable;

/**
 * 社区文章详情实体类
 * Created by Administrator on 2018/7/10.
 */

public class ArticleBean implements Serializable {

    public static final int UNLIKE = 0;  //不喜欢
    public static final int LIKE = 1;  //喜欢
    private static final long serialVersionUID = -7314445384380873672L;

    private String articleId;
    private String articleName;
    private String content;
    private int    createTIme;
    private int    like;
    private int    likeCount;
    private int    readCount;
    private int    status;

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getCreateTIme() {
        return createTIme;
    }

    public void setCreateTIme(int createTIme) {
        this.createTIme = createTIme;
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
