package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.requestHandler.MycenterReqService;

import org.json.JSONException;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by 28205 on 2016/12/14.
 */
public class MycenterReqImpl extends BaseService implements MycenterReqService {

    @Override
    public boolean uploadHeadImage(String accessToken, InputStream stream)
            throws HttpRequestException {
        String url = getUrl(AppRequestConst.POST_HEADIMG_UPLOAD);
        HttpRequest request = AppRequestUtils.from(url);
        request.putHeader("access_token", accessToken).
                putFrom("file", stream, "file").upload();
        String resultInfo = request.getResult(String.class);
        return resultInfo.equals("OK");
    }

    @Override
    public void editPersoninfo(String accessToken, String accountId, String reallyName, String serial, String sex, String nickName, String phone) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_PERSONINFO_EDIT_UPDATE);
        HttpRequest request = AppRequestUtils.post(url);
        request.putHeader("access_token", accessToken)
                .putRestfulParam("accountId", accountId)
                .putJsonParam("reallyName", reallyName)
                .putJsonParam("serial", serial)
                .putJsonParam("sex", sex)
                .putJsonParam("nickName", nickName)
                .putJsonParam("phone", phone)
                .requestJson().getBody();
    }

    @Override
    public String updatePersoninfo(HashMap<String, String> params) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_PERSONINFO_EDIT_UPDATE);
        HttpRequest request = AppRequestUtils.post(url);
        if (params != null) {
            Iterator<String> it = params.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = params.get(key);
                if (key.equals("accountId")) {
                    request.putRestfulParam(key, value);
                } else {
                    request.putJsonParam(key, value);
                }

            }
        }
        request.requestJson();
        String res = request.getDataBody();
        return res;
    }


}

