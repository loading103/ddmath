package com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tsinghuabigdata.edu.commons.http.HttpRequest;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.ZxApplication;
import com.tsinghuabigdata.edu.ddmath.bean.JoinedClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.QueryTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ResultInfo;
import com.tsinghuabigdata.edu.ddmath.bean.School;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequest;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestUtils;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.module.apkupgrade.UpdateInfo;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.AreaBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ClassBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.CountBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.RegRewardBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.ReturnClassBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.SchoolBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.UseCountBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.UserPrivilegeBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.VerifyBean;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.VerifyState;
import com.tsinghuabigdata.edu.ddmath.requestHandler.LoginReqService;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 28205 on 2016/12/14.
 *
 */
public class LoginReqImpl extends BaseService implements LoginReqService {

    @Override
    public LoginInfo stulogin(String loginName, String password, String deviceId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_LOGIN_STUDENT);
        HttpRequest request = AppRequestUtils.post(url);
        request.putRequestParam("loginName", loginName)
                .putRequestParam("password", password)
                .putRequestParam("deviceId", deviceId)
                .putRequestParam("clientType", "android")
                .request().getBody();
        LoginInfo loginInfo = request.getResult(LoginInfo.class);

        final Context context = ZxApplication.getApplication();
        if (context == null) throw new HttpRequestException("context", request);
        //LoginSuccessHandler.loginSuccessHandlerMd5(context, loginName, password);
        AccountUtils.setLoginUser( loginInfo );

        //
        url = getUrl(AppRequestConst.LOGIN_ACCESS);
        request = AppRequestUtils.get(url);
        request.putHeader("access_token", loginInfo.getAccessToken());
        String body = request.requestJson().getBody();
        AppLog.i(" login_access body = " + body);
        JSONObject json = new JSONObject(body);
        if (!json.has("domain")) throw new HttpRequestException("图片服务器地址失败", request);
        String domain = json.getString("domain");
        if (!domain.startsWith("http:")) {
            domain = "http://" + domain;
        }
        loginInfo.setFileServer( domain );
        return loginInfo;
    }

    @Override
    public void stulogout(String accessToken) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_LOGOUT_STUDENT);
        HttpRequest request = AppRequestUtils.post(url);
        request.putHeader("access_token", accessToken)
                .requestJson();
    }

    @Override
    public List<AreaBean> queryAreaList() throws HttpRequestException, JSONException {
        String url = getUrl((AppRequestConst.GET_QUERY_AREA));
        HttpRequest request = AppRequestUtils.get(url);
        String res = request.requestJson().getBody();
        return new Gson().fromJson(res , new TypeToken<List<AreaBean>>(){}.getType());
    }

    @Override
    public List<SchoolBean> querySchoolList(String schoolName, String provinceId, String cityId, int learnPeriod) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_QUERY_SCHOOL_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("schoolName", schoolName)
                .putRequestParam("provinceId", provinceId)
                .putRequestParam("cityId", cityId)
                .putRequestParam("learnPeriod", Integer.toString(learnPeriod))
                .request().getBody();
        return request.getResult(new TypeToken<List<SchoolBean>>(){});
    }


    @Override
    public List<ReturnClassBean> queryClassList(String schoolId, String serial) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_QUERY_CLASS_LIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("schoolIds", schoolId);
        request.putRequestParam("serial", serial);
        String res = request.request().getBody();
        return new Gson().fromJson(res, new TypeToken<List<ReturnClassBean>>(){}.getType());
    }

    @Override
    public ClassBean queryClassInfo(String classCode) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_QUERY_CLASSINFO_CLASSCODE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRestfulParam("classCode", classCode);
        String res = request.request().getBody();
        return new Gson().fromJson(res, new TypeToken<ClassBean>(){}.getType());
    }

    @Override
    public void joinClass(String studentId, String studentName, String classId, String className, String schoolId, String serial) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.JOIN_CLASS);
        HttpRequest request = AppRequestUtils.post(url);
        JSONObject object = new JSONObject();
        object.put("classId", classId);
        object.put("className", className);
        object.put("schoolId", schoolId);
        object.put("serial", serial);
        JSONArray array = new JSONArray();
        array.put(object);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("studentId", studentId);
            jsonObject.put("studentName", studentName);
            jsonObject.put("classInfos", array);
        } catch (Exception e) {
            AppLog.i("", e);
        }
        request.setJsonStringParams(jsonObject.toString()).requestJson().getBody();
    }


    @Override
    public void getRegisterCode(String phone, String type) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.REGISTER_VERIFYCODE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("phone", phone)
                .putRequestParam("type", type)
                .request().getBody();
    }

    @Override
    public void register(String nickName, String sex, String cellPhoneNumber, String password, String verifyCode, String recPhoneNum) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_REGISTER);
        HttpRequest request = AppRequestUtils.post(url);
        request/*.putRequestParam("nickName", nickName)
                .putRequestParam("sex", sex)*/
                .putRequestParam("cellPhoneNumber", cellPhoneNumber)
                .putRequestParam("password", password)
                .putRequestParam("verifyCode", verifyCode)
                .putRequestParam("recPhoneNum", recPhoneNum)
                .putRequestParam("source", "2")
                .putRequestParam("channelId", AppUtils.getChannelId())
                .request();
        request.getBody();
    }

    @Override
    public void updateExtraPersoninfo(String accountId, String reallyName, String enrollmentYear, String schoolId, String nickName, String sex, String classId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_UPDATE_EXTRA_PERSONINFO);
        HttpRequest request = AppRequestUtils.post(url);
        request.putRequestParam("accountId", accountId)
                .putRequestParam("reallyName", reallyName)
                .putRequestParam("enrollmentYear", enrollmentYear)
                .putRequestParam("schoolId", schoolId)
                .putRequestParam("nickName", nickName)
                .putRequestParam("sex", sex);
        if( !TextUtils.isEmpty(classId) )
            request.putRequestParam("classId",classId);
        request.request().getBody();
    }

    @Override
    public List<School> queryBlurSchool(String schoolName) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_QUERY_BLURSCHOOL);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("schoolName", schoolName)
                .request().getBody();
        return request.getResult(new TypeToken<List<School>>() {
        });
    }

    @Override
    public void getVeryfyCode(String phone, String type) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_GET_VERIFYCODE);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("phone", phone)
                .putRequestParam("type", type)
                .request().getBody();
    }

    @Override
    public void resetPass(String phone, String captcha, String newPassword, String confirmPassword, String role) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_RESET_PASS);
        HttpRequest request = AppRequestUtils.post(url);
        request.putRequestParam("phone", phone)
                .putRequestParam("captcha", captcha)
                .putRequestParam("newPassword", newPassword)
                .putRequestParam("confirmPassword", confirmPassword)
                .putRequestParam("accountType",role)
                .request();
        request.getBody();
    }

    @Override
    public void modifyPass(String accountId, String oldPassword, String newPassword, String confirmPassword) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_MODIFY_PASS);
        HttpRequest request = AppRequestUtils.post(url);
        request.putRequestParam("accountId", accountId)
                .putRequestParam("oldPassword", oldPassword)
                .putRequestParam("newPassword", newPassword)
                .putRequestParam("confirmPassword", confirmPassword)
                .request();
        request.getBody();
    }

    @Override
    public ArrayList<JoinedClassInfo> getJoinedClassList(String accessToken, String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_GET_CLASSLIST);
        HttpRequest request = AppRequestUtils.get(url);
        request.putHeader("access_token", accessToken)
                .putRestfulParam("studentId", studentId)
                .request().getBody();
        return request.getResult(new TypeToken<ArrayList<JoinedClassInfo>>() {
        });
    }

    @Override
    public QueryTutorClassInfo queryTutorClassinfo(String accessToken, String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_QUERY_TUTOR_CLASSINFO);
        HttpRequest request = AppRequestUtils.get(url);
        request.putHeader("access_token", accessToken)
                .putRestfulParam("studentId", studentId)
                .request().getBody();
        return request.getResult(new TypeToken<QueryTutorClassInfo>() {
        });
    }

    @Override
    public ResultInfo isPhoneUsed(String phone) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_PHONENUM_ISUSED);
        HttpRequest request = AppRequestUtils.get(url);
        request.putRequestParam("phone", phone)
                .request();
        return request.getResult(ResultInfo.class);
    }

    @Override
    public UserDetailinfo queryUserDetailinfo(String accessToken, String accountId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_USER_DETAILINFO);
        HttpRequest request = AppRequestUtils.get(url);
        request.putHeader("access_token", accessToken)
                .putRestfulParam("accountId", accountId)
                .request().getBody();
        return request.getResult(UserDetailinfo.class);
    }

    @Override
    public UpdateInfo queryApkUpdateinfo() throws HttpRequestException, JSONException {
        String url = getUpgradeApkUrl(AppRequestConst.GET_APK_UPDGRADE);
        String appname = ZxApplication.getApplication().getBaseContext().getResources().getString(R.string.app_name);
        if( "青豆数学".equals( appname ) ){
            url = getUpgradeApkUrl(AppRequestConst.GET_APK_UPDGRADE_QD);
        }else if( "北教豆豆".equals( appname ) ){
            url = getUpgradeApkUrl( AppRequestConst.GET_APK_UPDGRADE_BJDD );
        }
        AppRequest request = (AppRequest) AppRequestUtils.get(url);
        request.request();
        String body = request.getFullBody();
        return new Gson().fromJson(body, UpdateInfo.class);
    }

    @Override
    public Object bindMobile(/*String access_token,*/ String accountId, String cellphone, String verifycode) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.POST_BINDMOBILE);
        HttpRequest request = AppRequestUtils.post(url);
        request/*.putHeader("access_token", access_token)*/
                .putRequestParam("accountId", accountId)
                .putRequestParam("cellphone", cellphone)
                .putRequestParam("verifyCode", verifycode)
                .request();
        return request.getBody();
    }

    @Override
    public VerifyState queryVerifyState(String studentId) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.CONFIRM_PHONENUM);
        HttpRequest request = AppRequestUtils.post(url);
        request.putRequestParam("studentId", studentId)
                .request();
        String body = request.getDataBody();
        return new Gson().fromJson(body, VerifyState.class);
    }

    @Override
    public VerifyBean verifyMobile(String studentId, String cellphone, String verifycode) throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.VERIFY_CODE);
        HttpRequest request = AppRequestUtils.post(url);
        request.putRequestParam("studentId", studentId)
                .putRequestParam("phoneNum", cellphone)
                .putRequestParam("verifyCode", verifycode)
                .request();
        String body = request.getDataBody();
        return new Gson().fromJson(body, VerifyBean.class);
    }

    @Override
    public CountBean getRegisteCount() throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_REGISTE_COUNT);
        HttpRequest request = AppRequestUtils.get(url);
        request.request();
        String body = request.getDataBody();
        return new Gson().fromJson(body, CountBean.class);
    }

    @Override
    public UseCountBean getusestatistics() throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_USE_COUNT);
        HttpRequest request = AppRequestUtils.get(url);
        request.request();
        String body = request.getDataBody();
        return new Gson().fromJson(body, UseCountBean.class);
    }

    @Override
    public RegRewardBean getRegisterReward() throws HttpRequestException, JSONException {
        String url = getUrl(AppRequestConst.GET_REGISTE_REAWRD);
        HttpRequest request = AppRequestUtils.get(url);
        request.request();
        String body = request.getDataBody();
        return new Gson().fromJson(body, RegRewardBean.class);
    }

    /**
     * 用户特权列表
     */
    @Override
    public UserPrivilegeBean getUsePriviledgeList(String infos) throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.PRODUCE_USE_PRIVILEDGE);
        HttpRequest request = AppRequestUtils.post(url);
        //JSONArray jsonArray = new JSONArray(infos);
        request.setJsonStringParams( infos );
        request.requestJson();
        String body = request.getDataBody();
        return new Gson().fromJson(body, UserPrivilegeBean.class);
    }

    @Override
    public boolean enableAutoRecGuocha() throws HttpRequestException, JSONException{
        String url = getUrl(AppRequestConst.ENABLE_AUTO_REC_GUOCHA);
        HttpRequest request = AppRequestUtils.get(url);
        request.request();
        String body = request.getDataBody();
        return TextUtils.isEmpty(body)||body.equalsIgnoreCase("true");
    }
}
