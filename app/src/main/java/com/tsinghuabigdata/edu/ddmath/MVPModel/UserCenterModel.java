package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.bean.RewardBean;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.TodayStudyAbility;
import com.tsinghuabigdata.edu.ddmath.bean.TotalStudyAbility;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.backstage.bean.FirstPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ExchangeProductBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ExchangeRecordList;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.MyScoreBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductList;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreRecordResult;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.UserScoreBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.UserCenterService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.UserCenterServiceImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2017/9/11.
 */

public class UserCenterModel {

    private UserCenterService mUserCenterService = new UserCenterServiceImpl();

    /**
     * 查询我的学豆
     */
    private class MyStudyBeanTask extends AppAsyncTask<String, Void, StudyBean> {

        private RequestListener<StudyBean> requestListener;

        /*public*/ MyStudyBeanTask(RequestListener<StudyBean> requestListener) {
            this.requestListener = requestListener;
        }

        @Override
        protected StudyBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mUserCenterService.queryMyStudyBean(studentId);
        }

        @Override
        protected void onResult(StudyBean studyBean) {
            AccountUtils.setStudyBean( studyBean );
            if(requestListener!=null)requestListener.onSuccess(studyBean);
        }

        @Override
        protected void onFailure(HttpResponse<StudyBean> response, Exception ex) {
            if(requestListener!=null)requestListener.onFail(response, ex);
        }
    }

    /**
     * 查询赠送学豆
     */
    class RewardBeanTask extends AppAsyncTask<Void, Void, List<RewardBean> > {

        private RequestListener reqListener;

        public RewardBeanTask(RequestListener reqListener) {
            this.reqListener = reqListener;
        }

        @Override
        protected List<RewardBean> doExecute(Void... params) throws Exception {
            return mUserCenterService.queryRewardBean();
        }

        @Override
        protected void onResult(List<RewardBean> rewardBeen) {
            if(reqListener!=null)reqListener.onSuccess(rewardBeen);
        }

        @Override
        protected void onFailure(HttpResponse<List<RewardBean>> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 提交兑换码
     */
    class SubmitRedeemCode extends AppAsyncTask<HashMap<String, String>, Void, String> {

        private RequestListener reqListener;

        public SubmitRedeemCode(RequestListener reqListener) {
            this.reqListener = reqListener;
        }

        @Override
        protected String doExecute(HashMap<String, String>... params) throws Exception {
            HashMap<String, String> map = params[0];
            return mUserCenterService.submitRedeemCode(map);
        }

        @Override
        protected void onResult(String s) {
            reqListener.onSuccess(s);
        }

        @Override
        protected void onFailure(HttpResponse<String> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询我的学力
     */
    class TotalAbilityTask extends AppAsyncTask<String, Void, TotalStudyAbility> {

        private RequestListener reqListener;

        public TotalAbilityTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected TotalStudyAbility doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mUserCenterService.queryMyStudyAbility(studentId);
        }

        @Override
        protected void onResult(TotalStudyAbility totalStudyAbility) {
            reqListener.onSuccess(totalStudyAbility);
        }

        @Override
        protected void onFailure(HttpResponse<TotalStudyAbility> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 查询我的学力
     */
    class TodayAbilityTask extends AppAsyncTask<String, Void, TodayStudyAbility> {

        private RequestListener reqListener;

        public TodayAbilityTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected TodayStudyAbility doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mUserCenterService.queryMyTodayStudyAbility(studentId);
        }

        @Override
        protected void onResult(TodayStudyAbility todayStudyAbility) {
            reqListener.onSuccess(todayStudyAbility);
        }

        @Override
        protected void onFailure(HttpResponse<TodayStudyAbility> response, Exception ex) {
            reqListener.onFail(response, ex);
        }


    }

    /**
     * 查询我的积分
     */
    class UserScoreTask extends AppAsyncTask<String, Void, UserScoreBean> {

        private RequestListener<UserScoreBean> reqListener;

        public UserScoreTask(RequestListener<UserScoreBean> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected UserScoreBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mUserCenterService.queryUserScore(studentId);
        }

        @Override
        protected void onResult(UserScoreBean todayStudyAbility) {
            if( reqListener!=null)reqListener.onSuccess(todayStudyAbility);
        }

        @Override
        protected void onFailure(HttpResponse<UserScoreBean> response, Exception ex) {
            if( reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询我的积分
     */
    private class QueryScoreProductTask extends AppAsyncTask<String, Void, ScoreProductList> {

        private RequestListener<ScoreProductList> reqListener;

        public QueryScoreProductTask(RequestListener<ScoreProductList> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ScoreProductList doExecute(String... params) throws Exception {
            String studentId = params[0];
            int pageNum = Integer.valueOf( params[1] );
            int pageSize = Integer.valueOf( params[2] );
            return mUserCenterService.queryProductList(studentId, pageNum,pageSize);
        }

        @Override
        protected void onResult(ScoreProductList todayStudyAbility) {
            if( reqListener!=null)reqListener.onSuccess(todayStudyAbility);
        }

        @Override
        protected void onFailure(HttpResponse<ScoreProductList> response, Exception ex) {
            if( reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询积分兑换记录
     */
    private class QueryExchangeRecordTask extends AppAsyncTask<String, Void, ExchangeRecordList> {

        private RequestListener<ExchangeRecordList> reqListener;

        public QueryExchangeRecordTask(RequestListener<ExchangeRecordList> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ExchangeRecordList doExecute(String... params) throws Exception {
            String studentId = params[0];
            int pageNum = Integer.valueOf( params[1] );
            int pageSize = Integer.valueOf( params[2] );
            return mUserCenterService.queryExchangeRecordList(studentId, pageNum,pageSize);
        }

        @Override
        protected void onResult(ExchangeRecordList todayStudyAbility) {
            if( reqListener!=null)reqListener.onSuccess(todayStudyAbility);
        }

        @Override
        protected void onFailure(HttpResponse<ExchangeRecordList> response, Exception ex) {
            if( reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询商品详情
     */
    private class QueryProductDetailTask extends AppAsyncTask<String, Void, ScoreProductBean> {

        private RequestListener<ScoreProductBean> reqListener;

        public QueryProductDetailTask(RequestListener<ScoreProductBean> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ScoreProductBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            String productId = params[1];
            return mUserCenterService.queryScoreProductDetail(studentId, productId);
        }

        @Override
        protected void onResult(ScoreProductBean todayStudyAbility) {
            if( reqListener!=null)reqListener.onSuccess(todayStudyAbility);
        }

        @Override
        protected void onFailure(HttpResponse<ScoreProductBean> response, Exception ex) {
            if( reqListener!=null)reqListener.onFail(response, ex);
        }
    }
    /**
     * 查询商品详情
     */
    private class ExecExchangeProductTask extends AppAsyncTask<String, Void, ExchangeProductBean> {

        private RequestListener<ExchangeProductBean> reqListener;

        public ExecExchangeProductTask(RequestListener<ExchangeProductBean> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ExchangeProductBean doExecute(String... params) throws Exception {
            String studentId = params[0];
            String productId = params[1];
            return mUserCenterService.execExchangeProductAction(studentId, productId);
        }

        @Override
        protected void onResult(ExchangeProductBean todayStudyAbility) {
            if( reqListener!=null)reqListener.onSuccess(todayStudyAbility);
        }

        @Override
        protected void onFailure(HttpResponse<ExchangeProductBean> response, Exception ex) {
            if( reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询商品详情
     */
    private class UseHeaderPendantTask extends AppAsyncTask<String, Void, Integer> {

        private RequestListener<Integer> reqListener;

        /*public*/ UseHeaderPendantTask(RequestListener<Integer> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected Integer doExecute(String... params) throws Exception {
            String studentId = params[0];
            String recordId = params[1];
            return mUserCenterService.useHeaderPendant(studentId, recordId);
        }

        @Override
        protected void onResult(Integer todayStudyAbility) {
            if( reqListener!=null)reqListener.onSuccess(todayStudyAbility);
        }

        @Override
        protected void onFailure(HttpResponse<Integer> response, Exception ex) {
            if( reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询推荐积分
     */
    private class QueryCommandScoreListTask extends AppAsyncTask<String, Void, ArrayList<MyScoreBean>> {

        private RequestListener<ArrayList<MyScoreBean>> reqListener;

        public QueryCommandScoreListTask(RequestListener<ArrayList<MyScoreBean>> requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ArrayList<MyScoreBean> doExecute(String... params) throws Exception {
            String studentId = params[0];
            return mUserCenterService.getCommandScoreList(studentId);
        }

        @Override
        protected void onResult(ArrayList<MyScoreBean> todayStudyAbility) {
            if( reqListener!=null)reqListener.onSuccess(todayStudyAbility);
        }

        @Override
        protected void onFailure(HttpResponse<ArrayList<MyScoreBean>> response, Exception ex) {
            if( reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询积分记录
     */
    private class QueryScoreRecordTask extends AppAsyncTask<String, Void, ScoreRecordResult> {

        private RequestListener<ScoreRecordResult> reqListener;

        public QueryScoreRecordTask(RequestListener<ScoreRecordResult> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected ScoreRecordResult doExecute(String... params) throws Exception {
            String studentId = params[0];
            int pageNum = Integer.valueOf( params[1] );
            int pageSize = Integer.valueOf( params[2] );
            long startTime = Long.valueOf( params[3] );
            long endTime = Long.valueOf( params[4] );
            return mUserCenterService.getScoreRecordList(studentId, pageNum, pageSize, startTime, endTime);
        }

        @Override
        protected void onResult(ScoreRecordResult todayStudyAbility) {
            if( reqListener!=null)reqListener.onSuccess(todayStudyAbility);
        }

        @Override
        protected void onFailure(HttpResponse<ScoreRecordResult> response, Exception ex) {
            if( reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询积分记录
     */
    private class AddUserScoreTask extends AppAsyncTask<String, Void, Void> {

        private RequestListener<Void> reqListener;

        /*public*/ AddUserScoreTask(RequestListener<Void> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected Void doExecute(String... params) throws Exception {
            String studentId = params[0];
            String eventId = params[1];
            String contentId = params[2];
            mUserCenterService.addUserScore(studentId, eventId, contentId );
            return null;
        }

        @Override
        protected void onResult(Void v) {
            if( reqListener!=null)reqListener.onSuccess(v);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            if( reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 查询首次登陆获得的免费特权
     */
    private class FirstPrivilegeTask extends AppAsyncTask<String, Void, FirstPrivilegeBean> {

        private RequestListener<FirstPrivilegeBean> reqListener;

        /*public*/ FirstPrivilegeTask(RequestListener<FirstPrivilegeBean> requestListener) {
            reqListener = requestListener;
        }

        @Override
        protected FirstPrivilegeBean doExecute(String... params) throws Exception {
            String accountId = params[0];
            return mUserCenterService.queryUserFirstPrivilege(accountId);
        }

        @Override
        protected void onResult(FirstPrivilegeBean todayStudyAbility) {
            reqListener.onSuccess(todayStudyAbility);
        }

        @Override
        protected void onFailure(HttpResponse<FirstPrivilegeBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 意见反馈
     */
    class AddAdviceTask extends AppAsyncTask<HashMap<String, String>, Void, String> {

        private RequestListener reqListener;

        public AddAdviceTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected String doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mUserCenterService.addAdvice(map);
        }

        @Override
        protected void onResult(String s) {
            reqListener.onSuccess(s);
        }

        @Override
        protected void onFailure(HttpResponse<String> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    public void queryTotalAbility(String studentId, RequestListener requestListener) {
        TotalAbilityTask task = new TotalAbilityTask(requestListener);
        task.executeMulti(studentId);
    }

    public void queryTodayAbilityTask(String studentId, RequestListener requestListener) {
        TodayAbilityTask task = new TodayAbilityTask(requestListener);
        task.executeMulti(studentId);
    }

    public void addAdvice(HashMap<String, String> params, RequestListener requestListener) {
        AddAdviceTask task = new AddAdviceTask(requestListener);
        task.executeMulti(params);
    }

    public void queryMyStudyBean(String studentId, RequestListener<StudyBean> requestListener) {
        MyStudyBeanTask task = new MyStudyBeanTask(requestListener);
        task.executeMulti(studentId);
    }

    public void queryRewardBean(RequestListener requestListener) {
        RewardBeanTask task = new RewardBeanTask(requestListener);
        task.executeMulti();
    }

    public void submitRedeemCode(HashMap<String, String> params, RequestListener requestListener) {
        SubmitRedeemCode submitRedeemCode = new SubmitRedeemCode(requestListener);
        submitRedeemCode.executeMulti(params);
    }

    public void queryUserFirstPrivilege(String accountId, RequestListener<FirstPrivilegeBean> requestListener) {
        FirstPrivilegeTask task = new FirstPrivilegeTask(requestListener);
        task.executeMulti(accountId);
    }

    public void queryUserScore(String studentId, RequestListener<UserScoreBean> requestListener) {
        UserScoreTask task = new UserScoreTask(requestListener);
        task.executeMulti(studentId);
    }

    public void queryProductList(String studentId, int pageNum, int pageSize, RequestListener<ScoreProductList> requestListener) {
        QueryScoreProductTask task = new QueryScoreProductTask(requestListener);
        task.executeMulti(studentId, String.valueOf(pageNum), String.valueOf(pageSize));
    }

    public void queryExchangeRecordList(String studentId, int pageNum, int pageSize, RequestListener<ExchangeRecordList> requestListener) {
        QueryExchangeRecordTask task = new QueryExchangeRecordTask(requestListener);
        task.executeMulti(studentId, String.valueOf(pageNum), String.valueOf(pageSize));
    }

    public void queryProductDetail(String studentId, String productId, RequestListener<ScoreProductBean> requestListener) {
        QueryProductDetailTask task = new QueryProductDetailTask(requestListener);
        task.executeMulti(studentId, productId);
    }

    public void execExchangeProductAction(String studentId, String productId, RequestListener<ExchangeProductBean> requestListener) {
        ExecExchangeProductTask task = new ExecExchangeProductTask(requestListener);
        task.executeMulti(studentId, productId);
    }

    public void useHeaderPendant(String studentId, String recordId, RequestListener<Integer> requestListener) {
        UseHeaderPendantTask task = new UseHeaderPendantTask(requestListener);
        task.executeMulti(studentId, recordId);
    }

    public void queryCommandScoreList(String studentId, RequestListener<ArrayList<MyScoreBean>> requestListener) {
        QueryCommandScoreListTask task = new QueryCommandScoreListTask(requestListener);
        task.executeMulti(studentId);
    }
    public void queryScoreRecordList(String studentId, int pageNum, int pageSize, long startTime, long endTime, RequestListener<ScoreRecordResult> requestListener) {
        QueryScoreRecordTask task = new QueryScoreRecordTask(requestListener);
        task.executeMulti(studentId, String.valueOf(pageNum), String.valueOf(pageSize), String.valueOf(startTime), String.valueOf(endTime));
    }

    public void addUserScore(String studentId, String eventId, String contentId, RequestListener<Void> requestListener) {
        AddUserScoreTask task = new AddUserScoreTask(requestListener);
        task.executeMulti(studentId, eventId, contentId);
    }

}
