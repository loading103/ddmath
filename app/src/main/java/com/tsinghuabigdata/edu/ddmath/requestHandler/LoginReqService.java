package com.tsinghuabigdata.edu.ddmath.requestHandler;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.bean.JoinedClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.QueryTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ResultInfo;
import com.tsinghuabigdata.edu.ddmath.bean.School;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
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

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 28205 on 2016/12/14.
 */
public interface LoginReqService {
    /**
     * 登录
     *
     * @param
     * @param password
     * @param deviceId
     * @return
     * @throws Exception
     */
    LoginInfo stulogin(String loginName, String password, String deviceId) throws HttpRequestException, JSONException;

    /**
     * 退出登录
     *
     * @param
     */
    void stulogout(String accessToken) throws HttpRequestException, JSONException;

    /**
     * POST /studying/register
     * nickName	true	String	昵称
     * cellPhoneNumber	true	String	电话号码
     * password	true	String	密码
     * verifyCode	true	String	验证码
     */
    void register(String nickName, String sex, String cellPhoneNumber, String password, String verifyCode, String recPhoneNum) throws HttpRequestException, JSONException;

    /**
     * POST /studying/personal/supplement
     * 个人信息补充
     * access_token	true	String	登录成功后获取的token，添加到请求headers中，不作为接口的请求参数
     * accountId	true	String	注册后返回的accountId
     * reallyName	true	String	学生真实姓名
     * schoolId	true	String	学校Id
     * sex	true	String	性别
     * enrollmentYear     true	Date	入学年份
     */
    void updateExtraPersoninfo(String accountId, String reallyName, String enrollmentYear, String schoolId, String nickName, String sex, String classId) throws HttpRequestException, JSONException;

    /**
     * GET /studying/schools?schoolName=
     * schoolName	true	String	学校名称
     */
    List<School> queryBlurSchool(String schoolName) throws HttpRequestException, JSONException;

    /**
     * GET  /studying/captcha?phone=:phone&type=register
     * 操作类型(register, passwdRetrieve,binding)
     */
    void getVeryfyCode(String phone, String type) throws HttpRequestException, JSONException;

    /**
     * GET /rest/studying/register/code
     * 注册发验证码
     */
    void getRegisterCode(String phone, String type) throws HttpRequestException, JSONException;

    /**
     * 重置密码接口
     * POST  /studying /password/reset
     */
    void resetPass(String phone, String captcha, String newPassword, String confirmPassword, String role) throws HttpRequestException, JSONException;

    /**
     * 修改密码接口
     * POST
     */
    void modifyPass(String accountId, String oldPassword, String newPassword, String confirmPassword) throws HttpRequestException, JSONException;

    ResultInfo isPhoneUsed(String phone) throws HttpRequestException, JSONException;

    /**
     * 获取班级列表
     */
    ArrayList<JoinedClassInfo> getJoinedClassList(String accessToken, String studentId) throws HttpRequestException, JSONException;

    /**
     * 获取辅导班级列表
     */
    QueryTutorClassInfo queryTutorClassinfo(String accessToken, String studentId) throws HttpRequestException, JSONException;

    /**
     * 个人基本信息查询接口
     * GET  /studying/v1 /users/{accountId}
     */
    UserDetailinfo queryUserDetailinfo(String accessToken, String accountId) throws HttpRequestException, JSONException;

    /**
     * 省市查询
     * GET rest/studying/dd/v1/school/area/search
     * @throws HttpRequestException
     * @throws JSONException
     */
    List<AreaBean> queryAreaList() throws HttpRequestException, JSONException;


    /**
     * 学校查询
     * GET /rest/studying/dd/v1/school/search
     */
    List<SchoolBean> querySchoolList(String schoolName, String provinceId, String cityId, int learnPeriod) throws HttpRequestException, JSONException;

    /**
     * 班级查询
     * GET /rest/studying/dd/v1/school/class/search
     */
    List<ReturnClassBean> queryClassList(String schoolId, String serial) throws HttpRequestException, JSONException;

    ClassBean queryClassInfo(String classcode) throws HttpRequestException, JSONException;

    /**
     * 加入班级
     * POST /rest/studying/v1/student/class/relate
     */
    void joinClass(String studentId, String studentName, String classId, String className, String schoolId, String serial) throws HttpRequestException, JSONException;

    /**
     * APK更新
     * GET http://upgrade.iclassedu.com/doudou/upgrade/student/android.json
     */
    UpdateInfo queryApkUpdateinfo() throws HttpRequestException, JSONException;

    /**
     * 绑定手机号
     * http://xx.com/rest/studying/cellphone/binding
     */
    Object bindMobile(/*String access_token,*/ String accountId, String cellphone, String verifycode) throws HttpRequestException, JSONException;

    /**
     * 获取验证状态
     */
    VerifyState queryVerifyState(String studentId) throws HttpRequestException, JSONException;

    /**
     * 强制验证手机号
     */
    VerifyBean verifyMobile(String studentId, String cellphone, String verifycode) throws HttpRequestException, JSONException;

    /**
     * 获取豆豆数学学生注册人数（万为单位，10万起）
     */
    CountBean getRegisteCount() throws HttpRequestException, JSONException;


    /**
     * 使用人次统计（万为单位，10万起）
     */
    UseCountBean getusestatistics() throws HttpRequestException, JSONException;

    /**
     * 获取注册奖励配置
     */
    RegRewardBean getRegisterReward() throws HttpRequestException, JSONException;

    /**
     * 用户特权列表
     */
    UserPrivilegeBean getUsePriviledgeList(String infos) throws HttpRequestException, JSONException;

    /**
     * 是否允许使用自动识别勾叉功能
     */
    boolean enableAutoRecGuocha() throws HttpRequestException, JSONException;
}
