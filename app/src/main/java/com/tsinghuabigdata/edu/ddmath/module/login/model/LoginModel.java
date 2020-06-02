package com.tsinghuabigdata.edu.ddmath.module.login.model;

import android.content.Context;
import android.os.AsyncTask;

import com.tsinghuabigdata.edu.commons.codec.MD5Utils;
import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.bean.JoinedClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.QueryTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ResultInfo;
import com.tsinghuabigdata.edu.ddmath.bean.School;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppAsyncTask;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
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
import com.tsinghuabigdata.edu.ddmath.requestHandler.requestImpl.LoginReqImpl;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AsyncTaskCancel;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 28205 on 2016/12/14.
 */
public class LoginModel {
    private LoginReqService loginReqService = new LoginReqImpl();
    private List<AsyncTask> runningTasks    = new ArrayList<AsyncTask>();

    /**
     * 获取班级列表
     */
    public void getUsePriviledgeList( String infos, RequestListener<UserPrivilegeBean> requestListener) {
        UsePriviledgeTask task = new UsePriviledgeTask(requestListener);
        task.executeMulti(infos);
        runningTasks.add(task);
    }


    private class UsePriviledgeTask extends AppAsyncTask<String, Void, UserPrivilegeBean> {
        private RequestListener<UserPrivilegeBean> reqListener;

        /*public*/ UsePriviledgeTask(RequestListener<UserPrivilegeBean> listener) {
            this.reqListener = listener;
        }

        @Override
        protected UserPrivilegeBean doExecute(String... params) throws Exception {
            String infos = params[0];
            return loginReqService.getUsePriviledgeList( infos );
        }

        @Override
        protected void onResult(UserPrivilegeBean bean) {
            reqListener.onSuccess(bean);
        }

        @Override
        protected void onFailure(HttpResponse<UserPrivilegeBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 退出登录
     */
    class StuLogoutTask extends AppAsyncTask<String, Void, Void> {
        private RequestListener reqListener;

        public StuLogoutTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected Void doExecute(String... params) throws Exception {
            String accessToken = params[0];
            //loginReqService.stulogout(accessToken);
            return null;
        }

        @Override
        protected void onResult(Void resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }


    /**
     * 查询省市数据
     */
    class QueryProCityTask extends AppAsyncTask<Void, Void, List<AreaBean>> {

        private RequestListener requestListener;

        public QueryProCityTask(RequestListener requestListener) {
            this.requestListener = requestListener;
        }

        @Override
        protected List<AreaBean> doExecute(Void... voids) throws Exception {
            return loginReqService.queryAreaList();
        }

        @Override
        protected void onResult(List<AreaBean> areaBeans) {
            if (requestListener != null) {
                requestListener.onSuccess(areaBeans);
            }
        }

        @Override
        protected void onFailure(HttpResponse<List<AreaBean>> response, Exception ex) {
            if (requestListener != null) {
                requestListener.onFail(response, ex);
            }
        }
    }

    /**
     * 学校查询
     */
    class QuerySchoolTask extends AppAsyncTask<String, Void, List<SchoolBean>> {

        private RequestListener reqListener;

        public QuerySchoolTask(RequestListener reqListener) {
            this.reqListener = reqListener;
        }

        @Override
        protected List<SchoolBean> doExecute(String... strings) throws Exception {
            String schoolName = strings[0];
            String provinceId = strings[1];
            String cityId = strings[2];
            int learnPeriod = Integer.parseInt(strings[3]);
            return loginReqService.querySchoolList(schoolName, provinceId, cityId, learnPeriod);
        }

        @Override
        protected void onResult(List<SchoolBean> schoolBeans) {
            reqListener.onSuccess(schoolBeans);
        }

        @Override
        protected void onFailure(HttpResponse<List<SchoolBean>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }


    /**
     * 查询班级列表
     */
    class QueryClassListTask extends AppAsyncTask<String, Void, List<ReturnClassBean>> {

        private RequestListener requestListener;

        public QueryClassListTask(RequestListener requestListener) {
            this.requestListener = requestListener;
        }

        @Override
        protected List<ReturnClassBean> doExecute(String... strings) throws Exception {
            String schoolId = strings[0];
            String serial = strings[1];
            return loginReqService.queryClassList(schoolId, serial);
        }

        @Override
        protected void onResult(List<ReturnClassBean> returnClassBean) {
            requestListener.onSuccess(returnClassBean);
        }

        @Override
        protected void onFailure(HttpResponse<List<ReturnClassBean>> response, Exception ex) {
            requestListener.onFail(response, ex);
        }
    }
    //通过班级码获取班级信息
    private class QueryClassInfoTask extends AppAsyncTask<String, Void, ClassBean> {

        private RequestListener<ClassBean> requestListener;

        QueryClassInfoTask(RequestListener<ClassBean> requestListener) {
            this.requestListener = requestListener;
        }

        @Override
        protected ClassBean doExecute(String... strings) throws Exception {
            String classcode = strings[0];
            return loginReqService.queryClassInfo(classcode);
        }

        @Override
        protected void onResult(ClassBean returnClassBean) {
            if( requestListener!=null )requestListener.onSuccess(returnClassBean);
        }

        @Override
        protected void onFailure(HttpResponse<ClassBean> response, Exception ex) {
            if( requestListener!=null )requestListener.onFail(response, ex);
        }
    }

    /**
     * 加入班级
     */
    class JoinClassTask extends AppAsyncTask<String, Void, Void> {

        private RequestListener reqListener;

        public JoinClassTask(RequestListener reqListener) {
            this.reqListener = reqListener;
        }

        @Override
        protected Void doExecute(String... strings) throws Exception {
            String studentId = strings[0];
            String studentName = strings[1];
            String classId = strings[2];
            String className = strings[3];
            String schoolId = strings[4];
            String serial = strings[5];
            loginReqService.joinClass(studentId, studentName, classId, className, schoolId, serial);
            return null;
        }

        @Override
        protected void onResult(Void classBeans) {
            reqListener.onSuccess(classBeans);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 注册
     */
    class RegisterTask extends AppAsyncTask<String, Void, Void> {

        private RequestListener reqListener;

        public RegisterTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected Void doExecute(String... params) throws Exception {
            String nickName = params[0];
            String sex = params[1];
            String cellPhoneNumber = params[2];
            String password = params[3];
            String verifyCode = params[4];
            String recPhoneNum = params[5];
            loginReqService.register(nickName, sex, cellPhoneNumber, password, verifyCode, recPhoneNum);
            return null;
        }

        @Override
        protected void onResult(Void resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 检查APK更新
     */
    class CheckApkupdateTask extends AppAsyncTask<String, Void, UpdateInfo> {
        private RequestListener reqListener;

        public CheckApkupdateTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected UpdateInfo doExecute(String... params) throws Exception {
            return loginReqService.queryApkUpdateinfo();
        }

        @Override
        protected void onResult(UpdateInfo updateInfo) {
            if(reqListener!=null)reqListener.onSuccess(updateInfo);
        }

        @Override
        protected void onFailure(HttpResponse<UpdateInfo> response, Exception ex) {
            if(reqListener!=null)reqListener.onFail(response, ex);
        }
    }

    /**
     * 个人信息补充
     */
    class UpdateExtraPersoninfoTask extends AppAsyncTask<String, Void, Void> {
        private RequestListener reqListener;

        /*public*/ UpdateExtraPersoninfoTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected Void doExecute(String... params) throws Exception {
            String accountId = params[0];
            String reallyName = params[1];
            String enrollmentYear = params[2];
            String schoolId = params[3];
            String nickName  = params[4];
            String sex  = params[5];
            String classId = params[6];
            loginReqService.updateExtraPersoninfo(accountId, reallyName, enrollmentYear, schoolId, nickName,sex,classId);
            return null;
        }

        @Override
        protected void onResult(Void resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 学校模糊查询
     */
    public class QueryBlurSchoolTask extends AppAsyncTask<String, Void, List<School>> {
        private RequestListener reqListener;

        public QueryBlurSchoolTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected List<School> doExecute(String... params) throws Exception {
            String schoolName = params[0];
            return loginReqService.queryBlurSchool(schoolName);
        }

        @Override
        protected void onResult(List<School> resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<List<School>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取验证码
     */
    class GetVerifyCodeTask extends AppAsyncTask<String, Void, Void> {
        private RequestListener reqListener;

        public GetVerifyCodeTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected Void doExecute(String... params) throws Exception {
            String phone = params[0];
            String type = params[1];
            loginReqService.getVeryfyCode(phone, type);
            return null;
        }

        @Override
        protected void onResult(Void verifycode) {
            reqListener.onSuccess(verifycode);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 注册、修改密码获得验证码
     */
    class RegisterVerifyCodeTask extends AppAsyncTask<String, Void, Void> {
        private RequestListener reqListener;

        public RegisterVerifyCodeTask(RequestListener reqListener) {
            this.reqListener = reqListener;
        }

        @Override
        protected Void doExecute(String... strings) throws Exception {
            String phone = strings[0];
            String type = strings[1];
            loginReqService.getRegisterCode(phone, type);
            return null;
        }

        @Override
        protected void onResult(Void aVoid) {
            reqListener.onSuccess(aVoid);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 重置密码
     */
    class ResetPassTask extends AppAsyncTask<String, Void, Void> {
        private RequestListener reqListener;

        public ResetPassTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected Void doExecute(String... params) throws Exception {
            String phone = params[0];
            String verifycode = params[1];
            String newPasswd = params[2];
            String confirmPasswd = params[3];
            String role = params[4];
            loginReqService.resetPass(phone, verifycode, newPasswd, confirmPasswd, role);
            return null;
        }

        @Override
        protected void onResult(Void resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 修改密码
     */
    class ModifyPassTask extends AppAsyncTask<String, Void, Void> {
        private RequestListener reqListener;

        public ModifyPassTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected Void doExecute(String... params) throws Exception {
            String phone = params[0];
            String verifycode = params[1];
            String newPasswd = params[2];
            String confirmPasswd = params[3];
            loginReqService.modifyPass(phone, verifycode, newPasswd, confirmPasswd);
            return null;
        }

        @Override
        protected void onResult(Void resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Void> response, Exception ex) {
            reqListener.onFail(response, ex);
        }

    }

    /**
     * 检查手机是否使用
     */
    class IsPhoneUsedTask extends AppAsyncTask<String, Void, ResultInfo> {
        private RequestListener reqListener;

        public IsPhoneUsedTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected ResultInfo doExecute(String... params) throws Exception {
            String phone = params[0];
            return loginReqService.isPhoneUsed(phone);
        }

        @Override
        protected void onResult(ResultInfo resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<ResultInfo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 检查手机是否使用
     */
    class BindMobileTask extends AppAsyncTask<String, Void, Object> {
        private RequestListener reqListener;

        /*public*/ BindMobileTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected Object doExecute(String... params) throws Exception {
            //String accessToken = params[0];
            String accountId = params[0];
            String cellphone = params[1];
            String verifycode = params[2];
            return loginReqService.bindMobile(/*accessToken,*/ accountId, cellphone, verifycode);
        }

        @Override
        protected void onResult(Object resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<Object> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取班级列表
     */
    class GetJoinedClassTask extends AppAsyncTask<String, Void, ArrayList<JoinedClassInfo>> {
        private RequestListener reqListener;

        public GetJoinedClassTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected ArrayList<JoinedClassInfo> doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            return loginReqService.getJoinedClassList(accessToken, studentId);
        }

        @Override
        protected void onResult(ArrayList<JoinedClassInfo> resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<ArrayList<JoinedClassInfo>> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取辅导班级列表
     */
    class TutorClassTask extends AppAsyncTask<String, Void, QueryTutorClassInfo> {
        private RequestListener reqListener;

        public TutorClassTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected QueryTutorClassInfo doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String studentId = params[1];
            return loginReqService.queryTutorClassinfo(accessToken, studentId);
        }

        @Override
        protected void onResult(QueryTutorClassInfo resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<QueryTutorClassInfo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 个人基本信息查询
     */
    class QueryUserdetailInfoTask extends AppAsyncTask<String, Void, UserDetailinfo> {
        private RequestListener reqListener;

        public QueryUserdetailInfoTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected UserDetailinfo doExecute(String... params) throws Exception {
            String accessToken = params[0];
            String accountId = params[1];
            return loginReqService.queryUserDetailinfo(accessToken, accountId);
        }

        @Override
        protected void onResult(UserDetailinfo resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<UserDetailinfo> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取验证状态
     */
    class VerifyStateTask extends AppAsyncTask<String, Void, VerifyState> {
        private RequestListener reqListener;

        public VerifyStateTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected VerifyState doExecute(String... params) throws Exception {
            String studentId = params[0];
            return loginReqService.queryVerifyState(studentId);
        }

        @Override
        protected void onResult(VerifyState resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<VerifyState> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 强制验证手机号
     */
    class VerifyMobileTask extends AppAsyncTask<String, Void, VerifyBean> {
        private RequestListener reqListener;

        public VerifyMobileTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected VerifyBean doExecute(String... params) throws Exception {
            String accountId = params[0];
            String cellphone = params[1];
            String verifycode = params[2];
            return loginReqService.verifyMobile(accountId, cellphone, verifycode);
        }

        @Override
        protected void onResult(VerifyBean resultInfo) {
            reqListener.onSuccess(resultInfo);
        }

        @Override
        protected void onFailure(HttpResponse<VerifyBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取豆豆数学学生注册人数
     */
    class RegisteCountTask extends AppAsyncTask<String, Void, CountBean> {
        private RequestListener reqListener;

        public RegisteCountTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected CountBean doExecute(String... params) throws Exception {
            return loginReqService.getRegisteCount();
        }

        @Override
        protected void onResult(CountBean countBean) {
            reqListener.onSuccess(countBean);
        }

        @Override
        protected void onFailure(HttpResponse<CountBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 使用人次统计
     */
    class UseCountTask extends AppAsyncTask<String, Void, UseCountBean> {
        private RequestListener reqListener;

        public UseCountTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected UseCountBean doExecute(String... params) throws Exception {
            return loginReqService.getusestatistics();
        }

        @Override
        protected void onResult(UseCountBean countBean) {
            reqListener.onSuccess(countBean);
        }

        @Override
        protected void onFailure(HttpResponse<UseCountBean> response, Exception ex) {
            reqListener.onFail(response, ex);
        }
    }

    /**
     * 获取注册奖励配置
     */
    private class RegisteRewardTask extends AppAsyncTask<String, Void, RegRewardBean> {
        private RequestListener reqListener;

        private RegisteRewardTask(RequestListener listener) {
            this.reqListener = listener;
        }

        @Override
        protected RegRewardBean doExecute(String... params) throws Exception {
            RegRewardBean bean = loginReqService.getRegisterReward();
            //备份
            AccountUtils.setRegRewardBean(bean);
            return bean;
        }

        @Override
        protected void onResult(RegRewardBean bean) {
            if (reqListener != null) {
                reqListener.onSuccess(bean);
            }
        }

        @Override
        protected void onFailure(HttpResponse<RegRewardBean> response, Exception ex) {
            if (reqListener != null) {
                reqListener.onFail(response, ex);
            }
        }
    }

    private class EnableAutoRecGcTask extends AppAsyncTask<Void, Void, Boolean> {
        private RequestListener<Boolean> reqListener;

        private EnableAutoRecGcTask(RequestListener<Boolean> listener) {
            this.reqListener = listener;
        }

        @Override
        protected Boolean doExecute(Void... voids) throws Exception {
            return loginReqService.enableAutoRecGuocha();
        }

        @Override
        protected void onResult(Boolean bean) {
            if (reqListener != null) {
                reqListener.onSuccess(bean);
            }
        }

        @Override
        protected void onFailure(HttpResponse<Boolean> response, Exception ex) {
            if (reqListener != null) {
                reqListener.onFail(response, ex);
            }
        }
    }

    public void queryEnableAutoRecGc( RequestListener<Boolean> requestListener ){
        EnableAutoRecGcTask task = new EnableAutoRecGcTask(requestListener);
        task.executeMulti();
    }

    /**
     * 登录调用方法
     *
     * @param userName
     * @param password
     */
    public void login(Context context, String userName, String password, RequestListener requestListener) {
        LoginTask loginTask = new LoginTask(loginReqService, context, requestListener);
        loginTask.execute(userName, MD5Utils.stringToMD5(password));
        runningTasks.add(loginTask);
    }

    /**
     * 检查手机是否使用
     */
    public void isPhoneUsed(String phone, RequestListener requestListener) {
        IsPhoneUsedTask isPhoneUsedTask = new IsPhoneUsedTask(requestListener);
        isPhoneUsedTask.execute(phone);
        runningTasks.add(isPhoneUsedTask);
    }

    /**
     * 退出登录调用方法
     */
    public void logout(String accessToken, RequestListener requestListener) {
        StuLogoutTask stuLogoutTask = new StuLogoutTask(requestListener);
        stuLogoutTask.execute(accessToken);
        runningTasks.add(stuLogoutTask);
    }

    /**
     * 注册
     * register(nickName, sex, cellPhoneNumber, password, verifyCode, recPhoneNum);
     * sex : female 女， male 男
     */
    public void register(String nickName, String sex, String cellPhoneNumber, String password, String verifyCode, String recPhoneNum, RequestListener requestListener) {
        RegisterTask registerTask = new RegisterTask(requestListener);
        registerTask.execute(nickName, sex, cellPhoneNumber, MD5Utils.stringToMD5(password), verifyCode, recPhoneNum);
        runningTasks.add(registerTask);
    }

    /**
     * 个人信息补充
     * loginReqService.updateExtraPersoninfo(accessToken, accountId, reallyName, schoolId, sex, enrollmentYear);
     */
    public void updateExtraPersoninfo(String accountId, String reallyName,
                                      String enrollmentYear, String schoolId, String nickName, String sex, String classId, RequestListener requestListener) {
        UpdateExtraPersoninfoTask updateExtraPersoninfoTask = new UpdateExtraPersoninfoTask(requestListener);
        updateExtraPersoninfoTask.execute(accountId, reallyName, enrollmentYear, schoolId, nickName, sex,classId);
        runningTasks.add(updateExtraPersoninfoTask);
    }

    /**
     * 学校模糊查询
     */
    public void queryBlurSchool(String schoolName, RequestListener requestListener) {
        QueryBlurSchoolTask queryBlurSchoolTask = new QueryBlurSchoolTask(requestListener);
        queryBlurSchoolTask.execute(schoolName);
        runningTasks.add(queryBlurSchoolTask);
    }

    /**
     * 获取验证码
     * type 操作类型(register, passwdRetrieve,binding)
     */
    public void getVerifycode(String phone, String type, RequestListener requestListener) {
        GetVerifyCodeTask getVerifyCodeTask = new GetVerifyCodeTask(requestListener);
        getVerifyCodeTask.execute(phone, type);
        runningTasks.add(getVerifyCodeTask);
    }


    public void getRegisterCode(String phone, String type, RequestListener requestListener) {
        RegisterVerifyCodeTask task = new RegisterVerifyCodeTask(requestListener);
        task.execute(phone, type);
        runningTasks.add(task);
    }

    /**
     * 绑定手机
     */
    public void bindMobile(/*String accessToken, */String accountId, String phone, String verifycode, RequestListener requestListener) {
        BindMobileTask bindMobileTask = new BindMobileTask(requestListener);
        bindMobileTask.execute(/*accessToken,*/ accountId, phone, verifycode);
        runningTasks.add(bindMobileTask);
    }

    /**
     * 检查APK更新
     */
    public void checkApkupdate(RequestListener requestListener) {
        CheckApkupdateTask checkApkupdateTask = new CheckApkupdateTask(requestListener);
        checkApkupdateTask.execute();
        runningTasks.add(checkApkupdateTask);
    }

    /**
     * 获取班级列表
     */
    public void getJoinedClassList(String accessToken, String studentId, RequestListener requestListener) {
        GetJoinedClassTask getJoinedClassTask = new GetJoinedClassTask(requestListener);
        getJoinedClassTask.execute(accessToken, studentId);
        runningTasks.add(getJoinedClassTask);
    }

    /**
     * 获取辅导班级列表
     */
    public void getTutorClassList(String accessToken, String studentId, RequestListener requestListener) {
        TutorClassTask task = new TutorClassTask(requestListener);
        task.execute(accessToken, studentId);
        runningTasks.add(task);
    }


    /**
     * 重置密码
     * phone = params[0];
     * String verifycode = params[1];
     * String newPasswd = params[2];
     * String confirmPasswd = params[3];
     */
    public void resetPass(String phone, String verifycode, String newpass,
                          String confirmpass, String role, RequestListener requestListener) {
        ResetPassTask resetPassTask = new ResetPassTask(requestListener);
        resetPassTask.execute(phone, verifycode, MD5Utils.stringToMD5(newpass), MD5Utils.stringToMD5(confirmpass), role);
        runningTasks.add(resetPassTask);
    }

    /**
     * 修改密码
     */
    public void modifyPass(String accountId, String oldPassword, String newPassword, String confirmPassword, RequestListener requestListener) {
        ModifyPassTask task = new ModifyPassTask(requestListener);
        task.execute(accountId, MD5Utils.stringToMD5(oldPassword), MD5Utils.stringToMD5(newPassword), MD5Utils.stringToMD5(confirmPassword));
        runningTasks.add(task);
    }

    /**
     * 查询省市
     */
    public void queryProCity(RequestListener requestListener) {
        QueryProCityTask queryProCityTask = new QueryProCityTask(requestListener);
        queryProCityTask.execute();
        runningTasks.add(queryProCityTask);
    }

    /**
     * 查询学校
     */
    public void querySchool(String schoolName, String provinceId, String cityId, String learnPeriod, RequestListener requestListener) {
        QuerySchoolTask querySchoolTask = new QuerySchoolTask(requestListener);
        querySchoolTask.execute(schoolName, provinceId, cityId, learnPeriod);
        runningTasks.add(querySchoolTask);
    }

    /**
     * 查询班级
     */
    public void queryClassList(String schoolId, String serial, RequestListener requestListener) {
        QueryClassListTask classListTask = new QueryClassListTask(requestListener);
        classListTask.execute(schoolId, serial);
        runningTasks.add(classListTask);
    }
    public void queryClassInfo(String classcode, RequestListener<ClassBean> requestListener) {
        QueryClassInfoTask classListTask = new QueryClassInfoTask(requestListener);
        classListTask.execute(classcode);
        runningTasks.add(classListTask);
    }

    /**
     * 加入班级
     */
    public void joinClass(String studentId, String studentName, String classId, String className, String schoolId, String serial, RequestListener requestListener) {
        JoinClassTask joinClassTask = new JoinClassTask(requestListener);
        joinClassTask.execute(studentId, studentName, classId, className, schoolId, serial);
        runningTasks.add(joinClassTask);
    }

    /**
     * 个人基本信息查询
     */
    public void queryUserdetailInfo(String accessToken, String accountId, RequestListener requestListener) {
        QueryUserdetailInfoTask queryUserdetailInfoTask = new QueryUserdetailInfoTask(requestListener);
        queryUserdetailInfoTask.execute(accessToken, accountId);
        runningTasks.add(queryUserdetailInfoTask);
    }

    /**
     * 个人基本信息查询
     */
    public UserDetailinfo queryUserdetailInfo2(String accessToken, String accountId) throws JSONException, HttpRequestException {
        UserDetailinfo userDetailinfo = loginReqService.queryUserDetailinfo(accessToken, accountId);
        return userDetailinfo;
    }

    /**
     * 获取验证状态
     */
    public void queryVerifyState(String studentId, RequestListener requestListener) {
        VerifyStateTask task = new VerifyStateTask(requestListener);
        task.execute(studentId);
        runningTasks.add(task);
    }

    /**
     * 强制验证手机号
     */
    public void verifyMobile(String studentId, String cellphone, String verifycode, RequestListener requestListener) {
        VerifyMobileTask task = new VerifyMobileTask(requestListener);
        task.execute(studentId, cellphone, verifycode);
        runningTasks.add(task);
    }

    /**
     * 获取豆豆数学学生注册人数
     */
    public void getRegisteCount(RequestListener requestListener) {
        RegisteCountTask task = new RegisteCountTask(requestListener);
        task.execute();
        runningTasks.add(task);
    }

    /**
     * 获取豆豆数学学生注册人数
     */
    public void getUseCount(RequestListener requestListener) {
        UseCountTask task = new UseCountTask(requestListener);
        task.execute();
        runningTasks.add(task);
    }

    /**
     * 获取注册奖励配置
     */
    public void getRegisteReward(RequestListener requestListener) {
        RegisteRewardTask task = new RegisteRewardTask(requestListener);
        task.execute();
        runningTasks.add(task);
    }

    public void stopRunningTasks() {
        if (runningTasks.size() > 0) {
            AsyncTaskCancel.cancel(runningTasks);
        }
    }
}
