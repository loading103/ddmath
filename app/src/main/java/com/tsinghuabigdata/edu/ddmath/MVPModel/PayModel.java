package com.tsinghuabigdata.edu.ddmath.MVPModel;

import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ExchangeCardVo;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.AlipaySignBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.NegotiationBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.QrcodeBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.WxSignBean;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.PayService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.PayServiceImpl;

import java.util.Map;

/**
 * 支付模块
 * Created by Administrator on 2017/12/29.
 */

public class PayModel {

    private PayService mPayService = new PayServiceImpl();

    public void getAlipaySign(Map<String, String> params, RequestListener requestListener) {
        AlipaySignTask task = new AlipaySignTask(requestListener);
        task.execute(params);
    }

    public void updateTrade(Map<String, String> params, RequestListener requestListener) {
        UpdateTradeTask task = new UpdateTradeTask(requestListener);
        task.execute(params);
    }

    public void getAlipayQrcode(Map<String, String> params, RequestListener requestListener) {
        AlipayQrcodeTask task = new AlipayQrcodeTask(requestListener);
        task.execute(params);
    }

    public void queryTrade(String tradeNo, RequestListener requestListener) {
        QueryTradeTask task = new QueryTradeTask(requestListener);
        task.execute(tradeNo);
    }

    public void getAlipaySignDeduction(Map<String, String> params, RequestListener requestListener) {
        AlipaySignDeductionTask task = new AlipaySignDeductionTask(requestListener);
        task.execute(params);
    }

    public void getAlipayQrcodeDeduction(Map<String, String> params, RequestListener requestListener) {
        AlipayQrcodeDeductionTask task = new AlipayQrcodeDeductionTask(requestListener);
        task.execute(params);
    }

    public void getWxSign(Map<String, String> params, RequestListener requestListener) {
        WxSignTask task = new WxSignTask(requestListener);
        task.execute(params);
    }

    public void exchangeCard(Map<String, String> params, RequestListener requestListener) {
        ExchangeCardTask task = new ExchangeCardTask(requestListener);
        task.execute(params);
    }

    class AlipaySignTask extends AppAsyncTask<Map<String, String>, Void, AlipaySignBean> {

        private RequestListener reqListener;

        public AlipaySignTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected AlipaySignBean doExecute(Map<String, String>... maps) throws Exception {
            Map<String, String> map = maps[0];
            return mPayService.getAlipaySign(map);
        }

        @Override
        protected void onResult(AlipaySignBean s) {
            reqListener.onSuccess(s);
        }

        @Override
        protected void onFailure(HttpResponse<AlipaySignBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    class UpdateTradeTask extends AppAsyncTask<Map<String, String>, Void, String> {

        private RequestListener reqListener;

        public UpdateTradeTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected String doExecute(Map<String, String>... maps) throws Exception {
            Map<String, String> map = maps[0];
            return mPayService.updateTrade(map);
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

    class AlipayQrcodeTask extends AppAsyncTask<Map<String, String>, Void, QrcodeBean> {

        private RequestListener reqListener;

        public AlipayQrcodeTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected QrcodeBean doExecute(Map<String, String>... maps) throws Exception {
            Map<String, String> map = maps[0];
            return mPayService.getAlipayQrcode(map);
        }

        @Override
        protected void onResult(QrcodeBean s) {
            reqListener.onSuccess(s);
        }

        @Override
        protected void onFailure(HttpResponse<QrcodeBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    class QueryTradeTask extends AppAsyncTask<String, Void, NegotiationBean> {

        private RequestListener reqListener;

        public QueryTradeTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected NegotiationBean doExecute(String... params) throws Exception {
            String tradeNo = params[0];
            return mPayService.queryTrade(tradeNo);
        }

        @Override
        protected void onResult(NegotiationBean s) {
            reqListener.onSuccess(s);
        }

        @Override
        protected void onFailure(HttpResponse<NegotiationBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    class AlipaySignDeductionTask extends AppAsyncTask<Map<String, String>, Void, AlipaySignBean> {

        private RequestListener reqListener;

        public AlipaySignDeductionTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected AlipaySignBean doExecute(Map<String, String>... maps) throws Exception {
            Map<String, String> map = maps[0];
            return mPayService.getAlipaySignDeduction(map);
        }

        @Override
        protected void onResult(AlipaySignBean s) {
            reqListener.onSuccess(s);
        }

        @Override
        protected void onFailure(HttpResponse<AlipaySignBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    class AlipayQrcodeDeductionTask extends AppAsyncTask<Map<String, String>, Void, QrcodeBean> {

        private RequestListener reqListener;

        public AlipayQrcodeDeductionTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected QrcodeBean doExecute(Map<String, String>... maps) throws Exception {
            Map<String, String> map = maps[0];
            return mPayService.getAlipayQrcodeDeduction(map);
        }

        @Override
        protected void onResult(QrcodeBean s) {
            reqListener.onSuccess(s);
        }

        @Override
        protected void onFailure(HttpResponse<QrcodeBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    class WxSignTask extends AppAsyncTask<Map<String, String>, Void, WxSignBean> {

        private RequestListener reqListener;

        public WxSignTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected WxSignBean doExecute(Map<String, String>... maps) throws Exception {
            Map<String, String> map = maps[0];
            return mPayService.getWxSign(map);
        }

        @Override
        protected void onResult(WxSignBean s) {
            reqListener.onSuccess(s);
        }

        @Override
        protected void onFailure(HttpResponse<WxSignBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    class ExchangeCardTask extends AppAsyncTask<Map<String, String>, Void, ExchangeCardVo> {

        private RequestListener reqListener;

        public ExchangeCardTask(RequestListener requestListener) {
            reqListener = requestListener;
        }


        @Override
        protected ExchangeCardVo doExecute(Map<String, String>... maps) throws Exception {
            Map<String, String> map = maps[0];
            return mPayService.exchangeCard(map);
        }

        @Override
        protected void onResult(ExchangeCardVo s) {
            reqListener.onSuccess(s);
        }

        @Override
        protected void onFailure(HttpResponse<ExchangeCardVo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

}
