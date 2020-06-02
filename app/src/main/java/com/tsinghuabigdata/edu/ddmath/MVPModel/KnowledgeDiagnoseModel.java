package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.famousteacher.bean.FamousVideoBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.KnowlegeDiagnoseBean;
import com.tsinghuabigdata.edu.ddmath.module.onekeydiagnose.bean.QuestionCountBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.KnowledgeDiagnoseService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.KnowledgeDiagnoseImpl;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/8/13.
 */

public class KnowledgeDiagnoseModel {


    private KnowledgeDiagnoseService mService = new KnowledgeDiagnoseImpl();


    /**
     * 知识诊断
     */
    public void getKnowledgeDiagnose(HashMap<String, String> params, RequestListener requestListener) {
        KnowledgeDiagnoseTask task = new KnowledgeDiagnoseTask(requestListener);
        task.execute(params);
    }

    /**
     * 创建提升练习返回题目数
     */
    public void getQuestionCount(HashMap<String, String> params, RequestListener requestListener) {
        QuestionCountTask task = new QuestionCountTask(requestListener);
        task.execute(params);
    }

    /**
     * 创建提升练习-生成套题
     */
    public void produceQuestionSet(HashMap<String, String> params, RequestListener requestListener) {
        ProduceSetTask task = new ProduceSetTask(requestListener);
        task.execute(params);
    }

    /**
     * 知识诊断
     */
    class KnowledgeDiagnoseTask extends AppAsyncTask<HashMap<String, String>, Void, KnowlegeDiagnoseBean> {

        private RequestListener reqListener;

        public KnowledgeDiagnoseTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected KnowlegeDiagnoseBean doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.getKnowlegeDiagnose(map);
        }

        @Override
        protected void onResult(KnowlegeDiagnoseBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<KnowlegeDiagnoseBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 创建提升练习返回题目数
     */
    class QuestionCountTask extends AppAsyncTask<HashMap<String, String>, Void, QuestionCountBean> {

        private RequestListener reqListener;

        public QuestionCountTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected QuestionCountBean doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.getQuestionCount(map);
        }

        @Override
        protected void onResult(QuestionCountBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<QuestionCountBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 创建提升练习-生成套题
     */
    class ProduceSetTask extends AppAsyncTask<HashMap<String, String>, Void, String> {

        private RequestListener reqListener;

        public ProduceSetTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected String doExecute(HashMap<String, String>... maps) throws Exception {
            HashMap<String, String> map = maps[0];
            return mService.produceQuestionSet(map);
        }

        @Override
        protected void onResult(String bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<String> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }
}
