package com.tsinghuabigdata.edu.ddmath.parent.requestHandler.requestImpl;

import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.parent.ParentConst;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.requestHandler.ParentCenterService;
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.BaseService;

import org.json.JSONException;

import java.io.InputStream;

/**
 * 家长端
 */
public class ParentCenterImpl extends BaseService implements ParentCenterService {

    @Override
    public LoginInfo login(String loginName, String password, String deviceId) throws HttpRequestException, JSONException {
        String url = getUrl( ParentConst.POST_LOGIN );
        HttpRequest request = AppRequestUtils.post(url);
        request.putRequestParam("loginName", loginName)
                .putRequestParam("password", password)
                .putRequestParam("deviceId", deviceId)
                .putRequestParam("clientType", "android")
                .request().getBody();
        return request.getResult(LoginInfo.class);
    }

    @Override
    public ParentInfo queryParentInfo(String parentId) throws HttpRequestException, JSONException{
        String url = getUrl( ParentConst.GET_PARENT_INFO );
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("parentId", parentId)
                .request().getBody();
        return request.getResult(ParentInfo.class);
    }

    @Override
    public boolean uploadHeadImage(/*String accessToken, */InputStream stream)
            throws HttpRequestException {
        String url = getUrl(ParentConst.POST_UPDATEHEAD);
        HttpRequest request = AppRequestUtils.from(url);
        request./*putHeader("access_token", accessToken).*/
                putFrom("file", stream, "file").upload();
        String resultInfo = request.getResult(String.class);
        return resultInfo.equals("OK");
    }

//    @Override
//    public ArrayList<ProductBean> queryProductSuiteList(String studentId)  throws HttpRequestException, JSONException {
//        String url = getUrl( ParentConst.GET_PARENT_INFO );
//        HttpRequest request = AppRequestUtils.get(url);
//        request.putRequestParam("studentId", studentId)
//                .request().getBody();
//        String res = request.getDataBody();
//        return new Gson().fromJson( res, new TypeToken<ArrayList<ProductBean>>() {}.getType());
//    }
}
