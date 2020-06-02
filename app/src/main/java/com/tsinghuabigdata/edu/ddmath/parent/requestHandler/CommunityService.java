package com.tsinghuabigdata.edu.ddmath.parent.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ArticleBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ArticleListBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.DayReportListBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.WeekReportListBean;

import org.json.JSONException;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/7/10.
 */

public interface CommunityService {


    /**
     * 文章列表
     */
    ArticleListBean queryArticleList(HashMap<String, String> params) throws HttpRequestException, JSONException;

    /**
     * 社区界面详情
     */
    ArticleBean queryArticleDetail(HashMap<String, String> params) throws HttpRequestException, JSONException;

    /**
     * 更新点赞转发阅读记录
     */
    String operateArticle(HashMap<String, String> params) throws HttpRequestException, JSONException;
}
