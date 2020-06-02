package com.tsinghuabigdata.edu.ddmath.util;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tsinghuabigdata.edu.ddmath.bean.ExclusivePapersBean;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MonthSumbitedItem;
import com.tsinghuabigdata.edu.ddmath.bean.MyCourse;
import com.tsinghuabigdata.edu.ddmath.bean.Records;
import com.tsinghuabigdata.edu.ddmath.bean.SubmitQuestion;
import com.tsinghuabigdata.edu.ddmath.bean.WeekErrorStatus;
import com.tsinghuabigdata.edu.ddmath.bean.WeekExercisesBean;
import com.tsinghuabigdata.edu.ddmath.commons.newbieguide.ScreenUtils;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;
import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.sys.InstallUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/18.
 */


public class DataUtils {

    private static final String TAG = "Utils";

    private static float liveRatio = (float) (3.0 / 4);

    public static List<Records> select(List<MyCourse> resList) {
        List<Records> recordsList = new ArrayList<Records>();
        List<MyCourse> list = new ArrayList<MyCourse>();
        String lastDate = resList.get(0).getDate().substring(0, 7);
        for (int i = 0; i < resList.size(); i++) {
            MyCourse myCourse = resList.get(i);
            String date = myCourse.getDate().substring(0, 7);
            if (!date.equals(lastDate)) {
                Log.i(TAG, "lastDate=" + lastDate + "date=" + date);
                String month = lastDate.substring(0, 4) + "年" + lastDate.substring(5, 7) + "月";
                ArrayList<MyCourse> teamList = new ArrayList<MyCourse>();
                teamList.addAll(list);
                Log.i(TAG, "teamList.size() =" + teamList.size());
                Records records = new Records(month, teamList);
                recordsList.add(records);
                list.clear();
                lastDate = date;
            }
            list.add(myCourse);
            if (i == resList.size() - 1) {
                String month = lastDate.substring(0, 4) + "年" + lastDate.substring(5, 7) + "月";
                ArrayList<MyCourse> teamList = new ArrayList<MyCourse>();
                teamList.addAll(list);
                Records records = new Records(month, teamList);
                recordsList.add(records);
            }
        }
        Log.i(TAG, "recordsList.size()=" + recordsList.size());
        return recordsList;
    }

    public static String getFormatName(String reallyName) {
        if (TextUtils.isEmpty(reallyName)) {
            return null;
        } else if (reallyName.length() > 6) {
            return reallyName.substring(0, 6) + "...";
        } else {
            return reallyName;
        }

    }


    /**
     * 获得今天的格式 2017-02-18
     */
    public static String getFormat(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(time);
    }

    /**
     * 获得今天的格式 2017-02
     */
    private static String getFormatMonth(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月");
        return format.format(time);
    }


    public static List<MonthSumbitedItem> selectDoudouWork(List<SubmitQuestion> resList) {
        List<MonthSumbitedItem> monthList = new ArrayList<MonthSumbitedItem>();
        List<SubmitQuestion> list = new ArrayList<SubmitQuestion>();
        String lastMonth = getFormatMonth(resList.get(0).getStartTime());
        for (int i = 0; i < resList.size(); i++) {
            SubmitQuestion submitQuestion = resList.get(i);
            String month = getFormatMonth(submitQuestion.getStartTime());
            if (!month.equals(lastMonth)) {
                ArrayList<SubmitQuestion> teamList = new ArrayList<SubmitQuestion>();
                teamList.addAll(list);
                MonthSumbitedItem monthSumbitedItem = new MonthSumbitedItem(lastMonth, teamList);
                monthList.add(monthSumbitedItem);
                list.clear();
                lastMonth = month;
            }
            list.add(submitQuestion);
            if (i == resList.size() - 1) {
                ArrayList<SubmitQuestion> teamList = new ArrayList<SubmitQuestion>();
                teamList.addAll(list);
                MonthSumbitedItem monthSumbitedItem = new MonthSumbitedItem(month, teamList);
                monthList.add(monthSumbitedItem);
            }
        }
        return monthList;
    }

    public static List<MonthSumbitedItem> selectDiagnosedWork(List<SubmitQuestion> resList) {
        List<SubmitQuestion> list = new ArrayList<SubmitQuestion>();
        for (int i = 0; i < resList.size(); i++) {
            SubmitQuestion submitQuestion = resList.get(i);
            //if (submitQuestion.getExerStatus() == SubmitQuestion.CORRECTED) {
                list.add(submitQuestion);
            //}
        }
        if (list.size() > 0) {
            return selectDoudouWork(list);
        }
        return new ArrayList<MonthSumbitedItem>();
    }

    public static List<SubmitQuestion> selectDiagnosingdWork(List<SubmitQuestion> resList) {
        List<SubmitQuestion> list = new ArrayList<SubmitQuestion>();
        for (int i = 0; i < resList.size(); i++) {
            SubmitQuestion submitQuestion = resList.get(i);
            if (submitQuestion.getExerStatus() < SubmitQuestion.CORRECTED) {
                list.add(submitQuestion);
            }
        }
        return list;
    }


    /**
     * 获得直播界面视频最大高度
     */
    private static int getLivevideoHeight(Context context) {
        int allHeight = ScreenUtils.getScreenContentHeight(context) - DensityUtils.dp2px(context, 44 + 20 + 2 + 48 + 17 + 149);
        return allHeight / 2;
    }

    /**
     * 获得直播界面视频区块宽度
     */
    public static int getLiveWidth(Context context) {
        int maxHeight = ScreenUtils.getScreenContentHeight(context) - DensityUtils.dp2px(context, 20 + 44 + 20);
        int maxLiveWidth = (int) (DataUtils.getLivevideoHeight(context) / liveRatio + DensityUtils.dp2px(context, 40));
        int minLiveWidth = DensityUtils.dp2px(context, 100 + 40);

        int remainWidth = ScreenUtils.getScreenWidth(context) - maxHeight * 7 / 5;
        int realLiveWidth;
        Log.i(TAG, "remainWidth=" + remainWidth + " maxLiveWidth=" + maxLiveWidth + " minLiveWidth=" + minLiveWidth);
        if (remainWidth > maxLiveWidth) {
            realLiveWidth = maxLiveWidth;
        } else if (remainWidth < minLiveWidth) {
            realLiveWidth = minLiveWidth;
        } else {
            realLiveWidth = remainWidth;
        }
        return realLiveWidth;
    }

    /**
     * 获得直播界面视频实际高度
     */
    public static int getRealVideoHeight(Context context) {
        int videoWidth = getLiveWidth(context) - DensityUtils.dp2px(context, 40);
        return (int) (videoWidth * liveRatio);
    }

    public static int getModuleMargin(Context context) {
        int spaceHeight = ScreenUtils.getScreenContentHeight(context) - DensityUtils.dp2px(context, 64 + 20 + 80 * 4);
        int margin = spaceHeight / 6;
        return margin;
    }

    public static int getPhoneMianDistance(Context context) {
        int spaceHeight = ScreenUtils.getScreenHeight(context) - DensityUtils.dp2px(context, 48 + 25 + 36);
        return spaceHeight;
    }


    public static boolean isLoginSuccess() {
        if (AccountUtils.getLoginUser() != null && AccountUtils.getUserdetailInfo() != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param url 为绝对路径
     * @return
     */
    /*public static String getUrl(Context context, String url) {
        // 加入token
        String token = AccountUtils.getLoginUser().getAccessToken();
        try {
            if (!url.contains("access_token")) {
                url += "?access_token=" + URLEncoder.encode(token, "UTF-8");
                url += "&v=" + InstallUtil.getVersionName(context, context.getPackageName());
            }
        } catch (UnsupportedEncodingException e) {
            AppLog.w(ErrTag.TAG_ENCODE, "err", e);
            url = null;
        }

        AppLog.d("common webview  url = " + url);
        return url;
    }*/

    /**
     * @param url 为绝对路径
     * @return
     */
    public static String getUrl(Context context, String url) {
        // 加入token
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo == null ) loginInfo = AccountUtils.getLoginParent();
        if( loginInfo == null ) return url;

        String token = loginInfo.getAccessToken();
        try {
            if (!url.contains("access_token")) {
                url += url.contains("?")?"&access_token=":"?access_token=";
                url += URLEncoder.encode(token, "UTF-8");
            }
            url += "&v=" + InstallUtil.getVersionName(context, context.getPackageName());
            //url += "&t=" + System.currentTimeMillis();
            url = AppUtils.getUrlTimestamp(url);
        } catch (UnsupportedEncodingException e) {
            AppLog.w(ErrTag.TAG_ENCODE, "err", e);
            url = null;
        }

        AppLog.d("common webview  url = " + url);
        return url;
    }
    public static String getUrl( String url) {
        // 加入token
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if( loginInfo == null ) loginInfo = AccountUtils.getLoginParent();
        if( loginInfo == null ) return url;

        String token = loginInfo.getAccessToken();
        try {
            if (!url.contains("access_token")) {
                url += "?access_token=" + URLEncoder.encode(token, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            AppLog.w(ErrTag.TAG_ENCODE, "err", e);
        }
        return url;
    }

    public static int getNewWeekPracCount(List<WeekExercisesBean> weekExercises) {
        int num = 0;
        for (int i = 0; i < weekExercises.size(); i++) {
            WeekExercisesBean bean = weekExercises.get(i);
            if (bean.getDownloadStatus() == WeekErrorStatus.NOT_SHARE) {
                num++;
            }
        }
        return num;
    }

    public static int getTrainExcellentCount(List<ExclusivePapersBean> exclusivePapers) {
        int num = 0;
        for (int i = 0; i < exclusivePapers.size(); i++) {
            ExclusivePapersBean bean = exclusivePapers.get(i);
            if (bean.getStatus() == WeekErrorStatus.NOT_SHARE) {
                num++;
            }
        }
        return num;
    }

}
