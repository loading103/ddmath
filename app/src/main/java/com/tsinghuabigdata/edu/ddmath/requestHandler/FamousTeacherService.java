package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousProductBean;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousVideoBean;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.SingleVideoBean;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

/**
 * 名师精讲 模块
 * Created by Administrator on 2018/3/7.
 */

public interface FamousTeacherService {


    /**
     * 获取名师精讲商品列表
     */
    List<FamousProductBean> getFamousProductList(String studentId, String schoolId) throws HttpRequestException, JSONException;


    /**
     * 学习任务精讲视频列表查询
     */
    FamousVideoBean getVideoList(HashMap<String, String> params) throws HttpRequestException, JSONException;

    /**
     * 查看已兑换视频列表
     */
    List<SingleVideoBean> getExchangedVideoList(String studentId, String productId) throws HttpRequestException, JSONException;


    /**
     * 根据知识点获取视频列表
     */
    List<SingleVideoBean> getVideoListByKnowledge(HashMap<String, String> params) throws HttpRequestException, JSONException;

    /**
     * 记录视频播放
     */
    String recordVideoPlay(HashMap<String, String> params) throws HttpRequestException, JSONException;

}
