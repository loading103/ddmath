package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequest;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ExchangeCardVo;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.AlipaySignBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.NegotiationBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.QrcodeBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.WxSignBean;

import org.json.JSONException;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2017/12/29.
 */

public class PayServiceImpl extends BaseService implements PayService {

    @Override
    public AlipaySignBean getAlipaySign(Map<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_ALIPAY_SIGN);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putJsonParam(key, value);
            }
        }
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<AlipaySignBean>() {
        }.getType());
    }

    @Override
    public String updateTrade(Map<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.UPDATE_TRADE);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putJsonParam(key, value);
            }
        }
        AppRequest appRequest = (AppRequest) request.requestJson();
        String res = appRequest.getFullBody();
        return res;
    }

    @Override
    public QrcodeBean getAlipayQrcode(Map<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_ALIPAY_QRCODE);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putJsonParam(key, value);
            }
        }
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<QrcodeBean>() {
        }.getType());
    }

    @Override
    public NegotiationBean queryTrade(String tradeNo) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.QUERY_TRADE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("tradeNo", tradeNo);
        String res = request.request().getDataBody();
        return new Gson().fromJson(res, new TypeToken<NegotiationBean>() {
        }.getType());
    }

    @Override
    public AlipaySignBean getAlipaySignDeduction(Map<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_ALIPAY_SIGN_Deduction);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putJsonParam(key, value);
            }
        }
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<AlipaySignBean>() {
        }.getType());
    }

    @Override
    public QrcodeBean getAlipayQrcodeDeduction(Map<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_ALIPAY_SIGN_Deduction);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putJsonParam(key, value);
            }
        }
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<QrcodeBean>() {
        }.getType());
    }

    @Override
    public WxSignBean getWxSign(Map<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_WX_SIGN);
        if( params!=null && params.containsKey("accountId") ){      //家长端使用
            url = getUrl(AppRequestConst.GET_WX_SIGN_PARENT);
        }
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putJsonParam(key, value);
            }
        }
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<WxSignBean>() {
        }.getType());
    }

    @Override
    public ExchangeCardVo exchangeCard(Map<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.EXCHANGE_CARD);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                request.putJsonParam(key, value);
            }
        }
        String res = request.requestJson().getDataBody();
        return new Gson().fromJson(res, new TypeToken<ExchangeCardVo>() {
        }.getType());
    }
}
