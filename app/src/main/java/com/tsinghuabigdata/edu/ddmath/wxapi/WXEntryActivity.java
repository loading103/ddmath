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
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	

    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_entry);
        
    	api = WXAPIFactory.createWXAPI(this, AppConst.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		LogUtils.i("onReq, errCode = " + req.transaction);
		if (req.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			LogUtils.i("onReq onPayFinish, errCode = " + req.openId);
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		LogUtils.i("onResp, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			LogUtils.i("onResp onPayFinish, errCode = " + resp.errCode);
		}
	}

	public void goActivity(Class clazz) {
		Intent intent = new Intent(this, clazz);
		startActivity(intent);
	}
}