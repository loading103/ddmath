package com.tsinghuabigdata.edu.ddmath.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.WxPayResultEvent;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;

import org.greenrobot.eventbus.EventBus;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {


    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);

        api = WXAPIFactory.createWXAPI(this, AppConst.APP_ID);
        api.handleIntent(getIntent(), this);
        LogUtils.i("WXPayEntryActivity onCreate ");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
        LogUtils.i("WXPayEntryActivity onNewIntent ");
    }

    @Override
    public void onReq(BaseReq req) {
        LogUtils.i("onReq onPayFinish, openId = " + req.openId);
    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtils.i("onResp onPayFinish, errCode = " + resp.errCode + " errStr = " + resp.errStr);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            WxPayResultEvent wxPayResultEvent = new WxPayResultEvent(resp.errCode, resp.errStr);
            EventBus.getDefault().post(wxPayResultEvent);
        }
        finish();
    }

    /*@Override
    public void onResp(BaseResp resp) {
        LogUtils.i("onResp onPayFinish, errCode = " + resp.errCode + " errStr = " + resp.errStr);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            if (resp.errCode == 0) {
                ToastUtils.showShort(WXPayEntryActivity.this, "支付成功");
            } else if (resp.errCode == -2) {
                ToastUtils.showShort(WXPayEntryActivity.this, "已取消支付");
            } else {
                ToastUtils.showShort(WXPayEntryActivity.this, "支付出现错误");
            }
        }
    }*/
}