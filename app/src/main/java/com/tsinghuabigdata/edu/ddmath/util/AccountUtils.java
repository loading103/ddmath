package com.tsinghuabigdata.edu.ddmath.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.ScorePlanBean;
import com.tsinghuabigdata.edu.ddmath.bean.StudyBean;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserInfo;
import com.tsinghuabigdata.edu.ddmath.commons.AppSessionCache;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.DDWorkDetail;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.bean.LocalPageInfo;
import com.tsinghuabigdata.edu.ddmath.module.login.LoginActivity;
import com.tsinghuabigdata.edu.ddmath.module.login.UserInfoSuplementActivity;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.ClassCodeJoinClassActivity;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.bean.RegRewardBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.ScoreProductBean;
import com.tsinghuabigdata.edu.ddmath.module.myscore.bean.UserScoreBean;
import com.tsinghuabigdata.edu.ddmath.module.product.bean.RechargeCashbackBean;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentMainActivity;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/24.
 * </p>
 */
public class AccountUtils {

    //private static final String TAG = "sky";


    public static void setRechargeCashback(RechargeCashbackBean bean) {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache != null) {
            cache.put(AppConst.SESSION_PRODUCT_CASHBACK, bean);
        }
    }

    public static RechargeCashbackBean getRechargeCashback() {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return null;
        }
        return cache.get(AppConst.SESSION_PRODUCT_CASHBACK);
    }

    public static void setRegRewardBean(RegRewardBean bean) {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache != null) {
            cache.put(AppConst.SESSION_REGISTER_REWARD, bean);
        }
    }

    public static RegRewardBean getRegRewardBean() {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return null;
        }
        return cache.get(AppConst.SESSION_REGISTER_REWARD);
    }


    public static void setStudyBean(StudyBean bean) {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache != null) {
            cache.put(AppConst.SESSION_STUDYBEAN_COUNT, bean);
        }
    }

    public static StudyBean getStudyBean() {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return null;
        }
        return cache.get(AppConst.SESSION_STUDYBEAN_COUNT);
    }

    public static void setLoginUser(LoginInfo loginUser) {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            AppLog.i("AppSessionCache setLoginUser cache = null");
            return;
        }
        cache.remove(AppConst.SESSION_LOGIN_USER);
        if (loginUser != null)
            cache.put(AppConst.SESSION_LOGIN_USER, loginUser);

        //家长与学生是互斥登录关系
        cache.remove(AppConst.SESSION_LOGIN_PARENT);
    }

    public static void setLoginParent(LoginInfo loginUser) {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return;
        }
        cache.remove(AppConst.SESSION_LOGIN_USER);
        cache.remove(AppConst.SESSION_LOGIN_PARENT);
        //家长与学生是互斥登录关系
        if (loginUser != null)
            cache.put(AppConst.SESSION_LOGIN_PARENT, loginUser);
    }

    public static LoginInfo getLoginUser() {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return null;
        }
        return cache.get(AppConst.SESSION_LOGIN_USER);
    }

    //家长登录信息
    public static LoginInfo getLoginParent() {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return null;
        }
        return cache.get(AppConst.SESSION_LOGIN_PARENT);
    }

    //得到家长信息
    public static ParentInfo getParentInfo() {
        LoginInfo loginInfo = getLoginParent();
        if (loginInfo != null && loginInfo.getUserInfos() != null && loginInfo.getUserInfos().size() > 0) {
            UserInfo userInfo = loginInfo.getUserInfos().get(0);
            if (userInfo != null)
                return userInfo.getParentInfoVo();
        }
        return null;
    }
    public static void setParentInfo( ParentInfo parentInfo){
        LoginInfo loginInfo = getLoginParent();
        if (loginInfo != null && loginInfo.getUserInfos() != null && loginInfo.getUserInfos().size() > 0) {
            UserInfo userInfo = loginInfo.getUserInfos().get(0);
            if (userInfo != null)
                userInfo.setParentInfoVo( parentInfo );
        }
        setLoginParent( loginInfo );
    }

    //得到家长端学生详情
    public static UserDetailinfo getParentUserDetailinfo() {
        ParentInfo parentInfo = AccountUtils.getParentInfo();
        UserDetailinfo detailinfo = null;
        if (parentInfo != null && parentInfo.getStudentInfos() != null && parentInfo.getStudentInfos().size() > 0)
            detailinfo = parentInfo.getStudentInfos().get(0);       //默认第一个学生信息
        return detailinfo;
    }

    public static void setUserdetailInfo(UserDetailinfo studentInfo) {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return;
        }
        if (null != studentInfo && null != AccountUtils.getUserdetailInfo()) {
            UserDetailinfo tuserinfo = AccountUtils.getUserdetailInfo();
            if (studentInfo.getStudentId().equals(tuserinfo.getStudentId()))
                studentInfo.setFileAddress(getUserdetailInfo().getFileAddress());
            studentInfo.setQueryTutorClassInfo(getUserdetailInfo().getQueryTutorClassInfo());
        }
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo!=null&&studentInfo!=null ){
            studentInfo.setLoginName( loginInfo.getLoginName() );
        }

        cache.put(AppConst.SESSION_STUDENT_INFO, studentInfo);
    }

    public static UserDetailinfo getUserdetailInfo() {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return null;
        }
        return cache.get(AppConst.SESSION_STUDENT_INFO);
    }

//    public static Boolean getDnsProxy() {
//        AppSessionCache cache = AppSessionCache.getInstance();
//        if (cache == null) {
//            return false;
//        }
//        return cache.get(AppConst.SESSION_USE_PROXY);
//    }


    public static void setLocalPageInfo(LocalPageInfo localPageInfo) {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return;
        }
        cache.put(AppConst.SESSION_LM_PAGEINFO, localPageInfo);
    }

    public static LocalPageInfo getLocalPageInfo() {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return null;
        }
        return cache.get(AppConst.SESSION_LM_PAGEINFO);
    }

    private static boolean isExam = false;
    public static void setExamFlag( boolean exam ) {
        isExam = exam;
    }

    public static boolean getExamFlag() {
        return isExam;
    }
    /**
     * 清空
     */
    public static void clear() {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return;
        }
        // 退出JPush推送
        //退出时耗时太久
        //JPushInterface.stopPush(ZxApplication.getApplication());

        // 删除登录信息
        cache.remove(AppConst.SESSION_LOGIN_USER);
        // 删除笔序列号
        //cache.remove(AppConst.SESSION_SERIAL_NUMS);
        // 删除学生信息
        cache.remove(AppConst.SESSION_STUDENT_INFO);
        // 重置当前班级序号
        //GlobalData.setClassId("");
    }

    /**
     * 判断是否加入了班级
     */
    public static boolean hasClass() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null || detailinfo.getQueryTutorClassInfo() == null || detailinfo.getQueryTutorClassInfo().getClassInfos() == null
                || detailinfo.getQueryTutorClassInfo().getClassInfos().size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 得到第一个班级信息
     */
    public static MyTutorClassInfo getFirstClassInfo(String classtype) {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (classtype == null || detailinfo == null || detailinfo.getQueryTutorClassInfo() == null || detailinfo.getQueryTutorClassInfo().getClassInfos() == null
                || detailinfo.getQueryTutorClassInfo().getClassInfos().size() == 0) {
            return null;
        }

        List<MyTutorClassInfo> classInfos = detailinfo.getQueryTutorClassInfo().getClassInfos();
        for (MyTutorClassInfo classInfo : classInfos) {
            if (classtype.equals(classInfo.getType()))
                return classInfo;
        }
        return null;
    }

    /**
     * 判断是否加入了正式班级
     */
    public static boolean hasFormalClass() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        //个人信息界面
        if (detailinfo == null || detailinfo.getQueryTutorClassInfo() == null || detailinfo.getQueryTutorClassInfo().getClassInfos() == null
                || detailinfo.getQueryTutorClassInfo().getClassInfos().size() == 0) {
            return false;
        } else {
            List<MyTutorClassInfo> classInfos = detailinfo.getQueryTutorClassInfo().getClassInfos();
            for (int i = 0; i < classInfos.size(); i++) {
                MyTutorClassInfo classInfo = classInfos.get(i);
                if (MyTutorClassInfo.TYPE_JOINED.equals(classInfo.getType())) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 获取正式班级列表
     */
    private static List<MyTutorClassInfo> getFormalClassList() {
        List<MyTutorClassInfo> list = new ArrayList<>();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null || detailinfo.getQueryTutorClassInfo() == null || detailinfo.getQueryTutorClassInfo().getClassInfos() == null
                || detailinfo.getQueryTutorClassInfo().getClassInfos().size() == 0) {
        } else {
            List<MyTutorClassInfo> classInfos = detailinfo.getQueryTutorClassInfo().getClassInfos();
            for (int i = 0; i < classInfos.size(); i++) {
                MyTutorClassInfo classInfo = classInfos.get(i);
                if (MyTutorClassInfo.TYPE_JOINED.equals(classInfo.getType())) {
                    list.add(classInfo);
                }
            }
        }
        return list;
    }

    //    /**
    //     * 获取班级列表
    //     */
    //    public static List<MyTutorClassInfo> getTutorClassList() {
    //        if (AccountUtils.getUserdetailInfo() != null) {
    //            QueryTutorClassInfo res = AccountUtils.getUserdetailInfo().getQueryTutorClassInfo();
    //            if (res != null && res.getClassInfos() != null) {
    //                return res.getClassInfos();
    //            }
    //        }
    //        return new ArrayList<>();
    //    }

    /**
     * 获取正式班级列表id
     */
    public static String getFormalClassIds() {
        StringBuilder sb = new StringBuilder();
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null || detailinfo.getQueryTutorClassInfo() == null || detailinfo.getQueryTutorClassInfo().getClassInfos() == null
                || detailinfo.getQueryTutorClassInfo().getClassInfos().size() == 0) {
        } else {
            List<MyTutorClassInfo> classInfos = detailinfo.getQueryTutorClassInfo().getClassInfos();
            for (int i = 0; i < classInfos.size(); i++) {
                MyTutorClassInfo classInfo = classInfos.get(i);
                if (MyTutorClassInfo.TYPE_JOINED.equals(classInfo.getType())) {
                    sb.append(classInfo.getClassId());
                    sb.append(",");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 获取当前班级
     */
    public static MyTutorClassInfo getCurrentClassInfo() {
        if (hasFormalClass()) {
            List<MyTutorClassInfo> formalClassList = getFormalClassList();
            String classId = GlobalData.getCurrClassId();

            if (TextUtils.isEmpty(classId))
                return formalClassList.get(0);

            for (MyTutorClassInfo classInfo : formalClassList) {
                if (classId.equals(classInfo.getClassId()))
                    return classInfo;
            }
        }
        return null;
    }
    public static MyTutorClassInfo getCurrentClassInfoForParent() {

        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        if( detailinfo !=null && detailinfo.getClassInfos()!=null && detailinfo.getClassInfos().size()>0 ){
            ArrayList<MyTutorClassInfo> classList = detailinfo.getClassInfos();
            //返回第一个正式班级
            for( MyTutorClassInfo classInfo : classList ){
                if( !TextUtils.isEmpty(classInfo.getClassId()) )
                    //if( MyTutorClassInfo.TYPE_JOINED.equals(classInfo.getType()) )
                    return classInfo;
            }
        }
        return null;
    }
    public static String getSchoolIdsForParent() {
        String sb = "";
        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        if( detailinfo !=null && detailinfo.getClassInfos()!=null && detailinfo.getClassInfos().size()>0 ){
            ArrayList<MyTutorClassInfo> classList = detailinfo.getClassInfos();
            //返回第一个正式班级
            for( MyTutorClassInfo classInfo : classList ){
                if( TextUtils.isEmpty(sb) ) {
                    sb = classInfo.getSchoolId();
                }else if( !TextUtils.isEmpty(classInfo.getSchoolId()) && !sb.contains( classInfo.getSchoolId() ) ){
                    sb = sb +  "," + classInfo.getSchoolId();
                }
            }
        }
        return sb;
    }

    //得到学生的班级学校信息
    public static String getShoolClassInfos() {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        List<MyTutorClassInfo> classList = AccountUtils.getFormalClassList();
        if( detailinfo == null ){
            detailinfo = AccountUtils.getParentUserDetailinfo();
            if( detailinfo != null )
                classList = detailinfo.getClassInfos();
        }
        if(detailinfo == null ) return "[]";

        if( classList !=null && classList.size()>0 ){

            JSONArray jsonArray = new JSONArray();
            //
            for( MyTutorClassInfo classInfo : classList ){
                JSONObject json = new JSONObject();
                try {
                    json.put( "schoolId", TextUtils.isEmpty(classInfo.getSchoolId())?"":classInfo.getSchoolId() );
                    json.put( "classId", TextUtils.isEmpty(classInfo.getClassId())?"":classInfo.getClassId() );
                    json.put( "studentId", detailinfo.getStudentId() );
                    jsonArray.put(json);
                }catch (JSONException ex){
                    AppLog.d("",ex);
                }
            }
            return jsonArray.toString();
        }else if( !TextUtils.isEmpty(detailinfo.getSchoolId()) ){
            return String.format(Locale.getDefault(),"[{\"studentId\":\"%s\",\"schoolId\":\"%s\"}]",detailinfo.getStudentId(),detailinfo.getSchoolId());
        }else
            return String.format(Locale.getDefault(),"[{\"studentId\":\"%s\"}]",detailinfo.getStudentId());
    }

    /**
     * 获取当前班级id
     */
    public static String getCurrentClassId() {
        return GlobalData.getCurrClassId();
    }

    //    public static int getFormalIndexById(String id) {
    //        if (hasFormalClass()) {
    //            List<MyTutorClassInfo> formalClassList = getFormalClassList();
    //            for (int i = 0; i < formalClassList.size(); i++) {
    //                MyTutorClassInfo formalClass = formalClassList.get(i);
    //                if (id.equals(formalClass.getClassId())) {
    //                    return i;
    //                }
    //            }
    //        }
    //        return -1;
    //    }

    /**
     * 获取验证状态
     */
    public static boolean getVerifyState(Context context, String studentId) {
        return PreferencesUtils.getBoolean(context, studentId + "_verifyState", false);
    }

    /**
     * 获取验证状态
     */
    public static void saveVerifyState(Context context, String studentId) {
        PreferencesUtils.putBoolean(context, studentId + "_verifyState", true);
    }


    public static String getFileServer() {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (loginInfo != null) {
            return loginInfo.getFileServer();
        } else if (AccountUtils.getLoginParent() != null) {
            return AccountUtils.getLoginParent().getFileServer();
        }
        return null;
    }

    //检查是否加校加班, 没有则触发相关流程
    public static void checkJionSchoolClass(Context context) {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo == null)
            return;

        //加入学校
        if (TextUtils.isEmpty(detailinfo.getReallyName()) ||
                TextUtils.isEmpty(detailinfo.getSerial()) ||
                TextUtils.isEmpty(detailinfo.getSchoolId()) ) {
            context.startActivity(new Intent(context, UserInfoSuplementActivity.class));
        }
        //加入班级
        else if (!AccountUtils.hasFormalClass()) {
            Intent intent = new Intent(context, ClassCodeJoinClassActivity.class);
//            Intent intent = new Intent(context, JoinClassActivity.class);
//            intent.putExtra("schoolId", detailinfo.getSchoolId());
//            intent.putExtra("schoolName", detailinfo.getSchoolName());
//            if (!TextUtils.isEmpty(detailinfo.getSerial())) {
//                intent.putExtra("enrolYear", Integer.parseInt(detailinfo.getSerial()));
//            }
            context.startActivity(intent);
        }
    }

    public static int getTotalBean() {
        StudyBean studyBean = getStudyBean();
        if (studyBean != null) {
            return studyBean.getTotalDdAmt();
        }
        return 0;
    }

    public static boolean checkInClass(String classId) {
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        //个人信息界面
        if (detailinfo == null || detailinfo.getQueryTutorClassInfo() == null || detailinfo.getQueryTutorClassInfo().getClassInfos() == null
                || detailinfo.getQueryTutorClassInfo().getClassInfos().size() == 0) {
            return false;
        } else {
            List<MyTutorClassInfo> classInfos = detailinfo.getQueryTutorClassInfo().getClassInfos();
            for (int i = 0; i < classInfos.size(); i++) {
                MyTutorClassInfo classInfo = classInfos.get(i);
                if (classInfo.getClassId().equals(classId)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static void gotoMainActivity(Context context) {

        //家长角色登录，进入家长界面
        if (AccountUtils.getLoginParent() != null) {
            context.startActivity(new Intent(context, ParentMainActivity.class));
        }
        //学生角色登录
        else if (AccountUtils.getLoginUser() != null) {
            context.startActivity(new Intent(context, MainActivity.class));
        }
        //没有登录，进入登录界面
        else {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }

    private static boolean enableAutoRecGc = true;
    public static boolean isEnableAutoRecGc(){ return enableAutoRecGc; }
    public static void initEnableAutoRecGc(){
        LoginModel loginModel = new LoginModel();
        loginModel.queryEnableAutoRecGc(new RequestListener<Boolean>() {
            @Override
            public void onSuccess(Boolean res) {
                enableAutoRecGc = res;
            }

            @Override
            public void onFail(HttpResponse<Boolean> response, Exception ex) { }
        });
    }

    //积分作为全局变量保存
    public static UserScoreBean userScoreBean;
    //定制学
    public static ScorePlanBean mScorePlanBean;

    //得到当前正在使用的挂件对象
    public static ScoreProductBean getPendantInfo(){
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if( detailinfo == null || detailinfo.getHeadPendants() == null ) return null;

        ArrayList<ScoreProductBean> list = detailinfo.getHeadPendants();
        for( ScoreProductBean bean : list ){
            if( bean.getUseStatus() == 1 ) return bean;
        }
        return null;
    }

    //缓存当前荣耀值
    public static void setUserGlory( float glory ){
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache != null) {
            cache.put(AppConst.USER_GLORY, glory );
        }
    }

    public static float getUserGlory() {
        AppSessionCache cache = AppSessionCache.getInstance();
        if (cache == null) {
            return 0;
        }
        return cache.get(AppConst.USER_GLORY);
    }

    //当前作业对象
    public static DDWorkDetail mDDWorkDetail = null;
}
