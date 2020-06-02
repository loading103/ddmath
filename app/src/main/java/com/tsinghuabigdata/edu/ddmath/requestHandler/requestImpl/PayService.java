package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ExchangeCardVo;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.AlipaySignBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.NegotiationBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.QrcodeBean;
import com.tsinghuabigdata.edu.ddmath.module.mystudybean.bean.WxSignBean;

import org.json.JSONException;

import java.util.Map;

/**
 * 支付服务
 * Created by Administrator on 2017/12/29.
 */

public interface PayService {
    /**
     * 支付宝APP购买学豆请求签名数据返回
     */
    AlipaySignBean getAlipaySign(Map<String, String> map) throws HttpRequestException, JSONException;

    /**
     * 使用支付宝APP充值-更新充值表状态
     */
    String updateTrade(Map<String, String> map) throws HttpRequestException, JSONException;

    /**
     * 支付宝家长代付生成二维码
     */
    QrcodeBean getAlipayQrcode(Map<String, String> map) throws HttpRequestException, JSONException;

    /**
     * 支付宝支付完成后查询充值表
     */
    NegotiationBean queryTrade(String tradeNo) throws HttpRequestException, JSONException;

    /**
     * 支付宝APP购买学豆请求签名数据返回(13.3新增，可以抵扣学豆)
     */
    AlipaySignBean getAlipaySignDeduction(Map<String, String> map) throws HttpRequestException, JSONException;

    /**
     * 支付宝家长代付生成二维码(13.3新增，可以抵扣学豆)
     */
    QrcodeBean getAlipayQrcodeDeduction(Map<String, String> map) throws HttpRequestException, JSONException;

    /**
     * 微信统一下单接口
     */
    WxSignBean getWxSign(Map<String, String> map) throws HttpRequestException, JSONException;

    /**
     * 兑换vip卡
     */
    ExchangeCardVo exchangeCard(Map<String, String> map) throws HttpRequestException, JSONException;


}
