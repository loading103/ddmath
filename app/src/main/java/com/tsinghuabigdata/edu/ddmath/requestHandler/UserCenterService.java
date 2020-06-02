package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.TodayStudyAbility;
import com.tsinghuabigdata.edu.ddmath.bean.TotalStudyAbility;
import com.tsinghuabigdata.edu.ddmath.module.backstage.bean.FirstPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ExchangeProductBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ExchangeRecordList;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.MyScoreBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductList;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreRecordResult;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.UserScoreBean;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */

public interface UserCenterService {

    /**
     * 查询我的学豆
     */
    StudyBean queryMyStudyBean(String studentId) throws HttpRequestException, JSONException;

    /**
     * 查询我的学力
     */
    TotalStudyAbility queryMyStudyAbility(String studentId) throws HttpRequestException, JSONException;

    /**
     * 查询我的学力
     */
    TodayStudyAbility queryMyTodayStudyAbility(String studentId) throws HttpRequestException, JSONException;

    /**
     * 意见反馈
     */
    String addAdvice(HashMap<String,String> params) throws HttpRequestException, JSONException;

    /**
     * 查询赠送学豆
     */
    List<RewardBean> queryRewardBean() throws HttpRequestException, JSONException;

    /**
     * 提交兑换码
     */
    String submitRedeemCode(HashMap<String, String> map) throws HttpRequestException, JSONException;

    /**
     * 查询用户首次登陆获得的用户特权信息
     */
    FirstPrivilegeBean queryUserFirstPrivilege(String accountId ) throws HttpRequestException, JSONException;

    /**
     * 查询我的积分
     */
    UserScoreBean queryUserScore(String studentId) throws HttpRequestException, JSONException;

    /**
     * 查询商品列表
     */
    ScoreProductList queryProductList(String studentId, int pageNum, int pageSize) throws HttpRequestException, JSONException;

    /**
     * 查询兑换记录
     */
    ExchangeRecordList queryExchangeRecordList(String studentId, int pageNum, int pageSize) throws HttpRequestException, JSONException;

    /**
     * 查询商品详情
     */
    ScoreProductBean queryScoreProductDetail(String studentId, String productId) throws HttpRequestException, JSONException;

    /**
     * 积分兑换商品
     */
    ExchangeProductBean execExchangeProductAction(String studentId, String productId) throws HttpRequestException, JSONException;

    /**
     * 使用挂件
     */
    int useHeaderPendant(String studentId, String recordId) throws HttpRequestException, JSONException;

    /**
     * 积分推荐列表
     */
    ArrayList<MyScoreBean> getCommandScoreList(String studentId) throws HttpRequestException, JSONException;

    /**
     * 积分记录列表
     */
    ScoreRecordResult getScoreRecordList(String studentId, int pageNum, int pageSize, long starttime, long endtime ) throws HttpRequestException, JSONException;

    /**
     * 增加用户积分
     */
    void addUserScore(String studentId, String eventId, String contentId ) throws HttpRequestException, JSONException;
}
