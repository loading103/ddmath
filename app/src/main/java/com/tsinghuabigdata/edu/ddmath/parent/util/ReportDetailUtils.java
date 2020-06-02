package com.tsinghuabigdata.edu.ddmath.parent.util;

import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.sys.InstallUtil;
import com.tsinghuabigdata.edu.ddmath.parent.bean.DayReportItemBean;
import com.tsinghuabigdata.edu.ddmath.parent.bean.WeekReportItemBean;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils.getRestUrl;

/**
 * Created by Administrator on 2018/7/9.
 */

public class ReportDetailUtils {

    private static void putTokenVersion(HashMap<String, String> suffixParams) {
        String token = getParentToken();
        if (!TextUtils.isEmpty(token)) {
            try {
                suffixParams.put("access_token", URLEncoder.encode(token, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                AppLog.w(ErrTag.TAG_ENCODE, "err", e);
            }
        }
        String versionName = InstallUtil.getVersionName();
        if (!TextUtils.isEmpty(versionName)) {
            suffixParams.put("v", versionName);
        }
    }

    private static String getParentToken() {
        LoginInfo loginInfo = AccountUtils.getLoginParent();
        if (loginInfo != null) {
            return loginInfo.getAccessToken();
        }
        return null;
    }

    public static String getArticleShareUrl(String parentId, String articleId) {
        String baseUrl = AppRequestConst.WEB_ADDRESS + AppRequestConst.ARTICLE_SHARE_DETAIL;
        Map<String, String> restfulParams = new HashMap<>();
        if (!TextUtils.isEmpty(parentId)) {
            restfulParams.put("parentId", parentId);
        }
        if (!TextUtils.isEmpty(articleId)) {
            restfulParams.put("articleId", articleId);
        }
        HashMap<String, String> suffixParams = new LinkedHashMap<>();
        putTokenVersion(suffixParams);
        String url = getRestUrl(baseUrl, restfulParams) + StringUtil.getUrl(suffixParams);
        return url;
    }

    public static String getDayReportUrl(DayReportItemBean bean, boolean share) {
        String baseUrl = AppRequestConst.WEB_ADDRESS + AppRequestConst.DAY_REPORT_DETAIL;
        if( isUseNewReportTemplate(bean.getSourceType()) ){
            baseUrl = AppRequestConst.WEB_ADDRESS + AppRequestConst.DAY_REPORT_DETAIL_NEW;
        }
        
        Map<String, String> restfulParams = new HashMap<>();
        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        if (detailinfo != null && !TextUtils.isEmpty(detailinfo.getStudentId())) {
            restfulParams.put("studentId", detailinfo.getStudentId());
        }
        HashMap<String, String> suffixParams = new LinkedHashMap<>();
        if (!TextUtils.isEmpty(bean.getClassId())) {
            suffixParams.put("classId", bean.getClassId());
        }
        if (!TextUtils.isEmpty(bean.getExamId())) {
            suffixParams.put("examId", bean.getExamId());
        }
        if (share) {
            suffixParams.put("status", "share");
        }
        putTokenVersion(suffixParams);
        return getRestUrl(baseUrl, restfulParams) + StringUtil.getUrl(suffixParams);
    }

    //变式训练 和错题精炼 目前无法区分
    public static String getDayReportUrl(String studentId, String classId, String examId, boolean share, String sourceType) {
        String baseUrl = AppRequestConst.WEB_ADDRESS + AppRequestConst.DAY_REPORT_DETAIL;
        if( isUseNewReportTemplate( sourceType ) ){
            baseUrl = AppRequestConst.WEB_ADDRESS + AppRequestConst.DAY_REPORT_DETAIL_NEW;
        }

        Map<String, String> restfulParams = new HashMap<>();
        restfulParams.put("studentId", studentId);

        HashMap<String, String> suffixParams = new LinkedHashMap<>();
        suffixParams.put("classId", classId);
        suffixParams.put("examId", examId);
        if (share) {
            suffixParams.put("status", "share");
        }
        putTokenVersion(suffixParams);
        return getRestUrl(baseUrl, restfulParams) + StringUtil.getUrl(suffixParams);
    }


    public static String getWeekReportUrl(String studentId,String recordId, boolean share) {
        String baseUrl = AppRequestConst.WEB_ADDRESS + AppRequestConst.WEEK_REPORT_DETAIL;
        Map<String, String> restfulParams = new HashMap<>();
        restfulParams.put("recordId", recordId);
        HashMap<String, String> suffixParams = new LinkedHashMap<>();
        if (share) {
            suffixParams.put("status", "share");
        }
        suffixParams.put("studentId", studentId);
        putTokenVersion(suffixParams);
        String url = getRestUrl(baseUrl, restfulParams) + StringUtil.getUrl(suffixParams);
        return url;
    }

    public static String getDayReportName() {
        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        if (detailinfo != null) {
            return detailinfo.getReallyName() + "的日报告";
        }
        return "日报告";
    }

    public static String getDayReportShareName(DayReportItemBean bean) {
        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        if (detailinfo != null) {
            return detailinfo.getReallyName() + "的" + bean.getExamName() + "报告";
        }
        return bean.getExamName() + "报告";
    }


    public static String getWeekReportName() {
        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        if (detailinfo != null) {
            return detailinfo.getReallyName() + "的周报告";
        }
        return "周报告";
    }

    public static String getWeekReportShareName(WeekReportItemBean bean) {
        UserDetailinfo detailinfo = AccountUtils.getParentUserDetailinfo();
        if (detailinfo != null) {
            return detailinfo.getReallyName() + "的" + bean.getTitle();
        }
        return bean.getTitle();
    }

    public static String getDayReportShareText(DayReportItemBean bean) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm 更新");
        return dateFormat.format(bean.getCreateTime());
    }

    public static String getDayReportShareText(long createtime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm 更新");
        return dateFormat.format(createtime);
    }

    public static String getWeekReportShareText(WeekReportItemBean bean) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm 生成");
        return dateFormat.format(bean.getCreateTime());
    }

    public static String getWeekReportShareText(long createtime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm 生成");
        return dateFormat.format(createtime);
    }

    //-------------------------------------------------------------------------
    private static boolean isUseNewReportTemplate( String sourcetype ){
        boolean use = false;

        if(AppConst.WORK_TYPE_EBOOKWEEKTRAIN.equals( sourcetype )                 //错题精练 三种
                || AppConst.WORK_TYPE_MONTH_REFINE.equals( sourcetype )
                || AppConst.WORK_TYPE_CUSTOM_REFINE.equals( sourcetype )

                || AppConst.WORK_TYPE_WEEK_VARIANT.equals( sourcetype )           //变式训练
                || AppConst.WORK_TYPE_MONTH_VARIANT.equals( sourcetype )
                || AppConst.WORK_TYPE_CUSTOM_VARIANT.equals( sourcetype )

                || AppConst.WORK_TYPE_DDCHECK.equals( sourcetype )                //自我诊断
                || AppConst.WORK_TYPE_CLASSIC.equals( sourcetype )           //精品套题
                ){
            use = true;
        }

        return use;
    }
}
