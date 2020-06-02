package com.tsinghuabigdata.edu.ddmath.module.message;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.CommonWvNoActionbarActivity;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.activity.RoboActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.MyTutorClassInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.constant.AppRequestConst;
import com.tsinghuabigdata.edu.ddmath.constant.ErrTag;
import com.tsinghuabigdata.edu.ddmath.event.JumpEvent;
import com.tsinghuabigdata.edu.ddmath.event.ReadWeekReportEvent;
import com.tsinghuabigdata.edu.ddmath.fragment.ErrorBookFragment;
import com.tsinghuabigdata.edu.ddmath.fragment.MyReportFragment;
import com.tsinghuabigdata.edu.ddmath.module.ddwork.DDUploadActivity;
import com.tsinghuabigdata.edu.ddmath.module.entrance.EntranceEvaluationActivity;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.module.mycenter.fragment.UserCenterFragment;
import com.tsinghuabigdata.edu.ddmath.module.mylearn.MyLearnListActivity;
import com.tsinghuabigdata.edu.ddmath.module.myscore.ScoreEventID;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ReportDetailActivity;
import com.tsinghuabigdata.edu.ddmath.parent.bean.ParentInfo;
import com.tsinghuabigdata.edu.ddmath.parent.util.ReportDetailUtils;
import com.tsinghuabigdata.edu.ddmath.receive.JPushClientReceiver;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AlertManager;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.DataUtils;
import com.tsinghuabigdata.edu.ddmath.util.DateUtils;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;
import com.tsinghuabigdata.edu.ddmath.util.StringUtil;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 *
 */
public class MessageUtils {
    /**
     * broadcast action
     */
    public static final  String NEW_MSG_BRD_ACTION = "new message coming";
    //    public static final String IS_HAVE_NEW_MSG_TYPE = "is_have_new_msg_type";
    //    public static final int MSG_RECORDER = 109;
    //    public static final int HOME_MSG_CACHE = 1003;
    //
    //    public static final String MSG_TYPE = "msgtype";
    //    public static final String MSG_TYPE_MYMSG = "wdxx";
    //    public static final String MSG_TYPE_REC = "zxtj";
    //    public static final String CLASS_RANK = "CLASS_RANK";
    //    public static final String ZX_RANK = "ZX_RANK";
    /**
     * 新消息更新广播字段
     */
    private static final String IS_HAVE_NEW_MSG    = "is_have_new_msg";
    private Activity bindingActivity;
    private boolean bRegisterListener = false;
    private NewMsgListener       newMsgListener;
    private NewMsgRefreshReceive mNewMsgReciver;
    private Context              mContext;

    private MessageUtils() {
    }

    public static MessageUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 初始化极光推送
     */
    public static void initJPushSdk(final Context context) {

        String userId = "";
        UserDetailinfo user = AccountUtils.getUserdetailInfo();
        if (user != null) {
            userId = user.getStudentId();
        } else {
            ParentInfo parentInfo = AccountUtils.getParentInfo();
            if (parentInfo != null)
                userId = parentInfo.getParentId();
        }
        //没有用户登录
        if (TextUtils.isEmpty(userId))
            return;

        JPushInterface.clearAllNotifications(context);

        JPushInterface.setDebugMode(true);
        JPushInterface.init(context);

        JPushInterface.resumePush(context);
        Set<String> tags = new HashSet<>();

        //使用班级ID做为tag
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if (detailinfo != null && detailinfo.getQueryTutorClassInfo() != null && detailinfo.getQueryTutorClassInfo().getClassInfos() != null
                && detailinfo.getQueryTutorClassInfo().getClassInfos().size() > 0) {

            List<MyTutorClassInfo> classInfos = detailinfo.getQueryTutorClassInfo().getClassInfos();
            for (int i = 0; i < classInfos.size(); i++) {
                MyTutorClassInfo classInfo = classInfos.get(i);
                if (MyTutorClassInfo.TYPE_JOINED.equals(classInfo.getType())) {
                    tags.add(classInfo.getClassId());
                }
            }
        }

        // Params(Context, Alias, Tags, Callback)
        JPushInterface.setAliasAndTags(context, userId, tags, new
                TagAliasCallback() {
                    @Override
                    public void gotResult(int code, String alias, Set<String> tags) {
                        switch (code) {
                            case 0:
                                String tagStr = null;
                                if (tags != null) {
                                    tagStr = Arrays.toString(tags.toArray());
                                }
                                AppLog.i("Set tag and alias success, alias=" + alias + ", tags=" + tagStr);
                                break;
                            case 6002:
                                AppLog.i("jpush", "Failed to set alias and tags due to timeout. Try again " +
                                        "after " +
                                        "60s. alias=" + alias);
                                if (AppUtils.isNetworkConnected(context)) {
                                    //AppLog.i("重试，设置Again");
                                    new Handler() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            // 重试
                                            initJPushSdk(context);
                                        }
                                    }.sendEmptyMessageDelayed(0, 1000 * 60);
                                } else {
                                    AppLog.i("jpush", "No network");
                                }
                                break;
                            default:
                                AppLog.i("jpush", "Failed with errorCode = " + code);
                        }
                    }
                });
    }

    public static void unIinitJPushSdk(Context context) {

        JPushClientReceiver.clearLocalNotifications();
        //停止推送
        JPushInterface.stopPush(context);
    }

    /**
     * 消息跳转处理
     *
     * @param context cc
     * @param data    remark,跳转参数
     */
    public static void startActivity(Context context, String data) {
        if (context == null || TextUtils.isEmpty(data) || "{}".equals(data)) {
            return;
        }
        data = data.replace("+", ",");

        JSONObject json = JSON.parseObject(data);
        String type = json.getString("type");
        String model = json.getString("model");
        //
        String extent = json.getString("extent");
        JSONObject extentObj = JSON.parseObject(extent);

        String examId = "";
        String studentId = "";
        String classId = "";
        String lastExamId = "";

        switch (type) {
            case MessageRemarkType.ACTIVITY_DDWORK_UPDETAIL: {
                String workId = json.getString("workId");
                String workName = json.getString("workName");
                Intent intent = new Intent(context, DDUploadActivity.class);
                intent.putExtra(DDUploadActivity.PARAM_DDWORKID, workId);
                intent.putExtra(DDUploadActivity.PARAM_TITLE, workName);
                context.startActivity(intent);
                break;
            }
            case MessageRemarkType.ACTIVITY_DDWORK_DETAIL:
            case MessageRemarkType.APPLY_RECORRECT_QUESTION:
            case MessageRemarkType.EBOOK_WEEKTRAIN_CORRECTED:
            case MessageRemarkType.EXCESIVE_PRACTICE_CORRECTED:
                // case MessageRemarkType.REVISE_QUESTION_CORRECTFINISH:
                // case MessageRemarkType.REVISE_QUESTION_RECORRECTFINISH:
                // case MessageRemarkType.REVISE_QUESTION_APPLYCORRECTFINISH:
            {
                String workId = json.getString("workId");
                String workName = json.getString("workName");
                Intent intent = new Intent(context, DDUploadActivity.class);
                intent.putExtra(DDUploadActivity.PARAM_DDWORKID, workId);
                intent.putExtra(DDUploadActivity.PARAM_TITLE, workName);
                context.startActivity(intent);
                break;
            }
            case MessageRemarkType.ACTIVITY_SCWORK_DETAIL: {
                long time = json.getLong("workdate");
                Intent intent = new Intent(context, MyLearnListActivity.class);
                intent.putExtra("date", DateUtils.format(time));
                context.startActivity(intent);
                break;
            }
            case MessageRemarkType.EXAM_REPORT_CREATE:
                if (extentObj != null) {
                    examId = extentObj.getString("examId");
                    studentId = extentObj.getString("studentId");
                    classId = extentObj.getString("classId");
                }
                if ("virtualclass00001".equals(classId)) {
                    gotoWeekWorkReport(context, examId, studentId, classId, "评测", null);
                } else {
                    gotoExamReport(context, examId, studentId, classId, "周练", null);
                }
                break;
            case MessageRemarkType.HOMEWORK_REPORT_CREATE:
                if (extentObj != null) {
                    examId = extentObj.getString("examId");
                    studentId = extentObj.getString("studentId");
                    classId = extentObj.getString("classId");
                }
                gotoHomeworkReport(context, examId, studentId, classId, "作业", null);
                break;
            case MessageRemarkType.EBOOK_WEEKTRAIN_REPORT:
            case MessageRemarkType.EXCESIVE_PRACTICE_REPORT: {
                if (extentObj != null) {
                    examId = extentObj.getString("examId");
                    studentId = extentObj.getString("studentId");
                    classId = extentObj.getString("classId");
                }
                gotoWeekExtractReport(context, examId, studentId, classId, "周题练订正", null);
                break;
            }
            case MessageRemarkType.STUDY_ANALYSISREPORT_CREATE:{
                if (extentObj != null) {
                    String reportId = extentObj.getString("reportId");
                    String createTime = extentObj.getString("createTime");
                    UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
                    if( detailinfo!=null ){
                        gotoWeekAnalysisReport( context, detailinfo.getStudentId(), reportId, createTime );
                    }
                }
                break;
            }
            case MessageRemarkType.WEEKEXAM_REPORT_CREATE:
                if (extentObj != null) {
                    examId = extentObj.getString("examId");
                    studentId = extentObj.getString("studentId");
                    classId = extentObj.getString("classId");
                }
                gotoWeekWorkReport(context, examId, studentId, classId, "作业", null);
                break;
            case MessageRemarkType.KNOWLEDGE_REPORT_CREATE:
                if (extentObj != null) {
                    examId = extentObj.getString("examId");
                    studentId = extentObj.getString("studentId");
                    lastExamId = extentObj.getString("lastExamId");
                }
                gotoKnowledgeReport(context, examId, studentId, lastExamId, "知识图谱", null);
                break;
            case MessageRemarkType.ENROLEXAM_REPORT_CREATE://入学评测报告生成提醒 url
            case MessageRemarkType.ALLROUND_REPORT_CREATE://综合能力报告生成提醒  url
                //remak中的extent {private String reportId;String title;studentId;classId;}
                String reportId = "";
                if (extentObj != null) {
                    reportId = extentObj.getString("reportId");
                }
                gotoAllroundReport(context, reportId, "综合能力", null);
                break;

            case MessageRemarkType.ENROLEXAM_REPORT_RETRIEVE: {  //入学评测报告被退回提醒

                MyTutorClassInfo classInfo = AccountUtils.getFirstClassInfo(MyTutorClassInfo.TYPE_UNJOIN);
                if (classInfo == null) {
                    ToastUtils.showShort(context, "信息已过期");
                    break;
                }
                //跳本地界面
                Intent intent = new Intent(context, EntranceEvaluationActivity.class);
                intent.putExtra(EntranceEvaluationActivity.CLASSID, classInfo.getClassId());
                context.startActivity(intent);
                break;
            }
            case MessageRemarkType.LIVE_END:  //直播结束提醒
            case MessageRemarkType.ENROLEXAM_REPORT_AGREED: //入学评测报告同意提醒
                break;

            case MessageRemarkType.REVISE_ERRORQUESTION_REPORT: {        //查看错题订正报告
                if (extentObj != null) {
                    studentId = extentObj.getString("studentId");
                    //classId = extentObj.getString("classId");
                    gotoErrorCorrectReport(context, studentId, "最近一周错题订正", null);
                }
                break;
            }
            //向学情反馈界面跳转
            case MessageRemarkType.STUDYCONDITION_REPORT: {
                if (model != null) {
                    if (MessageRemarkType.STUDYCONDITION_MODEL_KNOWGRAP.equals(model)) {
                        EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_STUDY_CONDITION, MyReportFragment.KNOWLEDGE_I));
                        //关闭信息界面
                        context.sendBroadcast(new Intent(RoboActivity.ACTION));
                    }
                }
                break;
            }
            //向错题本界面跳转
            case MessageRemarkType.ERRBOOK_FRAGMENT: {
                if (model != null) {
                    int mode = 0;
                    if (MessageRemarkType.ERRBOOKFRAGMENT_MODEL_WEEKTRAIN.equals(model)) {
                        mode = ErrorBookFragment.MODEL_WEEKTRAIN;
                    } else if( MessageRemarkType.ERRBOOKFRAGMENT_MODEL_VARIANTTRAIN.equals(model) ){
                       mode = ErrorBookFragment.MODEL_VARTRAIN;
                    } else if (MessageRemarkType.ERRBOOKFRAGMENT_MODEL_STAGEREVIEW.equals(model)) {
                        mode = ErrorBookFragment.MODEL_ERRBOOK;
                    }
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_QUESTION_BOOK, mode));
                    // 关闭信息界面
                    context.sendBroadcast(new Intent(RoboActivity.ACTION));
                }
                break;
            }
            //向1对1习题跳转
            case MessageRemarkType.ENCHANCE_FRAGMENT: {
                if (model != null) {
                    EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_QUESTION_BOOK, ErrorBookFragment.MODEL_VARTRAIN));
                    // 关闭信息界面
                    context.sendBroadcast(new Intent(RoboActivity.ACTION));
                }
                break;
            }
            case MessageRemarkType.MYCENTER_LEARNDOU:
                //if (extentObj != null) {
                //刷新学豆
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_MY_CENTER, UserCenterFragment.MODEL_MYSTUDYBEAN));
                // 关闭信息界面
                context.sendBroadcast(new Intent(RoboActivity.ACTION));
                //}
                break;
            case MessageRemarkType.MYCENTER_BUYSUIT:{
                //购买套餐
                EventBus.getDefault().post(new JumpEvent(MainActivity.FRAGMENT_MY_CENTER, UserCenterFragment.MODEL_MYSTUDYBEAN));
                // 关闭信息界面
                context.sendBroadcast(new Intent(RoboActivity.ACTION));
                break;
            }
            case MessageRemarkType.PARENT_EXPORT_WORK: {
                if (extentObj.containsKey("studentId") && extentObj.containsKey("examId") && extentObj.containsKey("classId") && extentObj.containsKey("createTime") && extentObj.containsKey("sourceType")) {

                    String title = ReportDetailUtils.getDayReportName();
                    String sourceType = extentObj.getString("sourceType");
                    String url = ReportDetailUtils.getDayReportUrl(extentObj.getString("studentId"), extentObj.getString("classId"), extentObj.getString("examId"), false, sourceType);
                    String shareTitle = extentObj.getString("reportName");
                    String shareText = ReportDetailUtils.getDayReportShareText(extentObj.getLong("createTime"));
                    String shareUrl = ReportDetailUtils.getDayReportUrl(extentObj.getString("studentId"), extentObj.getString("classId"), extentObj.getString("examId"), true, sourceType);
                    ReportDetailActivity.openReportDetailActivity(context, title, url, shareTitle, shareText, shareUrl,"day");
                }
                break;
            }
            case MessageRemarkType.PARENT_EXPORT_WEEK: {
                if (extentObj.containsKey("studentId") && extentObj.containsKey("reportId") && extentObj.containsKey("createTime")) {
                    String title = ReportDetailUtils.getWeekReportName();
                    String url = ReportDetailUtils.getWeekReportUrl(extentObj.getString("studentId"), extentObj.getString("reportId"), false);
                    String shareTitle = extentObj.getString("reportName");
                    String shareText = ReportDetailUtils.getWeekReportShareText(extentObj.getLong("createTime"));
                    String shareUrl = ReportDetailUtils.getWeekReportUrl(extentObj.getString("studentId"), extentObj.getString("reportId"), true);
                    ReportDetailActivity.openReportDetailActivity(context, title, url, shareTitle, shareText, shareUrl,"week");
                    EventBus.getDefault().post(new ReadWeekReportEvent(extentObj.getString("reportId")));
                }
                break;
            }
            default:
                break;
        }
    }

    /**
     * 我的世界重要通知消息跳转处理
     *
     * @param context cc
     * @param data    remark,跳转参数
     */
    public static void startWorkActivity(Context context, String data) {
        if (context == null || TextUtils.isEmpty(data) || "{}".equals(data)) {
            return;
        }
        JSONObject json = JSON.parseObject(data);
        String type = json.getString("type");
        switch (type) {
            case MessageRemarkType.ACTIVITY_DDWORK_UPDETAIL: {
                String workId = json.getString("workId");
                String workName = json.getString("workName");
                Intent intent = new Intent(context, DDUploadActivity.class);
                intent.putExtra(DDUploadActivity.PARAM_DDWORKID, workId);
                intent.putExtra(DDUploadActivity.PARAM_TITLE, workName);
                context.startActivity(intent);
                break;
            }
            case MessageRemarkType.ACTIVITY_DDWORK_DETAIL: {
                String workId = json.getString("workId");
                String workName = json.getString("workName");
                Intent intent = new Intent(context, DDUploadActivity.class);
                intent.putExtra(DDUploadActivity.PARAM_DDWORKID, workId);
                intent.putExtra(DDUploadActivity.PARAM_TITLE, workName);
                context.startActivity(intent);
                break;
            }
            default:
                break;
        }
    }


    public static void gotoAllroundReport(Context context, String reportid, String reportName, String time) {
        if (TextUtils.isEmpty(reportid)) {
            return;
        }
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.WEB_ALLROUND_REPORT;

        Map<String, String> restfulParams = new HashMap<>();
        restfulParams.put("reportedId", reportid);
        url = getRestUrl(url, restfulParams);

        gotoNotitleWeburl(context, url, reportName, time);
    }

    /**
     * 作业报告
     */
    public static void gotoHomeworkReport(Context context, String examId, String studentId, String classId, String reportName, String time) {
        if (TextUtils.isEmpty(studentId) || TextUtils.isEmpty(examId) || TextUtils.isEmpty(classId)) {
            return;
        }
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.WEB_HOMEWORK_REPORT;

        Map<String, String> restfulParams = new HashMap<>();
        restfulParams.put("studentId", studentId);
        restfulParams.put("examId", examId);
        restfulParams.put("classId", classId);
        url = getRestUrl(url, restfulParams);

        gotoNotitleWeburl(context, url, reportName, time, false, ScoreEventID.EVENT_SHARE_DIAGNOSE_REPORT, examId);
    }

    /**
     * 周练作业报告
     */
    public static void gotoWeekWorkReport(Context context, String examId, String studentId, String classId, String reportName, String time) {
        if (TextUtils.isEmpty(studentId) || TextUtils.isEmpty(examId) || TextUtils.isEmpty(classId)) {
            return;
        }
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.WEB_WEEKWORK_REPORT;

        Map<String, String> restfulParams = new HashMap<>();
        restfulParams.put("studentId", studentId);
        restfulParams.put("examId", examId);
        restfulParams.put("classId", classId);
        url = getRestUrl(url, restfulParams);

        gotoNotitleWeburl(context, url, reportName, time, false, ScoreEventID.EVENT_SHARE_DIAGNOSE_REPORT,examId);
    }

    /**
     * 错题周题练报告
     */
    public static void gotoWeekExtractReport(Context context, String examId, String studentId, String classId, String reportName, String time) {
        if (TextUtils.isEmpty(studentId) || TextUtils.isEmpty(examId) || TextUtils.isEmpty(classId)) {
            return;
        }
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.QUESTION_WEEKPRACTICE_REPORT;

        Map<String, String> restfulParams = new HashMap<>();
        restfulParams.put("studentId", studentId);
        restfulParams.put("examId", examId);
        restfulParams.put("classId", classId);
        url = getRestUrl(url, restfulParams);

        gotoNotitleWeburl(context, url, reportName, time, false, ScoreEventID.EVENT_SHARE_DIAGNOSE_REPORT,examId);
    }

    /**
     * 周学习分析报告
     */

    private static void gotoWeekAnalysisReport(Context context, String studentId, String reportId,String time) {
        if (TextUtils.isEmpty(studentId) || TextUtils.isEmpty(reportId) ) {
            return;
        }
        String baseUrl = MessageUtils.getWeekReportUrl( studentId, reportId );
        String mUrl = DataUtils.getUrl(context, baseUrl);
        //mUrl += "startTime=" + currBean.getStartTime() + "&endTime=" + currBean.getStopTime();
        LogUtils.i("errorCorrectReport url=" + mUrl);

        gotoNotitleWeburl(context, mUrl, "周学习分析报告", time, false, ScoreEventID.EVENT_WEEK_ANALYSIS,reportId);
    }



    //--------------------------------------------------------------------

    /**
     * 考试报告
     */
    private static void gotoExamReport(Context context, String examId, String studentId, String classId, String reportName, String time) {
        if (TextUtils.isEmpty(studentId) || TextUtils.isEmpty(examId) || TextUtils.isEmpty(classId)) {
            return;
        }
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.WEB_EXAM_REPORT;

        Map<String, String> restfulParams = new HashMap<>();
        restfulParams.put("studentId", studentId);
        restfulParams.put("examId", examId);
        restfulParams.put("classId", classId);
        url = getRestUrl(url, restfulParams);

        gotoNotitleWeburl(context, url, reportName, time, false, ScoreEventID.EVENT_SHARE_DIAGNOSE_REPORT,examId);
    }

    //--------------------------------------------------------------------

    /**
     * 知识图谱报告——列表模式（以前的知识分析报告）
     */
    public static void gotoKnowledgeReport(Context context, String examId, String studentId, String lastExamId, String reportName, String time) {
        if (TextUtils.isEmpty(studentId) || TextUtils.isEmpty(examId)) {
            return;
        }
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.WEB_KNOWLEDGE_REPORT;

        Map<String, String> restfulParams = new HashMap<>();
        restfulParams.put("studentId", studentId);
        HashMap<String, String> suffixParams = new LinkedHashMap<>();
        suffixParams.put("examId", examId);
        if (!TextUtils.isEmpty(lastExamId)) {
            suffixParams.put("lastExamId", lastExamId);
        }
        if (AccountUtils.getUserdetailInfo() != null) {
            suffixParams.put("name", AccountUtils.getUserdetailInfo().getReallyName());
        }
        if (AccountUtils.getLoginUser() != null) {
            String token = AccountUtils.getLoginUser().getAccessToken();
            try {
                suffixParams.put("access_token", URLEncoder.encode(token, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                AppLog.w(ErrTag.TAG_ENCODE, "err", e);
            }
        }
        url = getRestUrl(url, restfulParams) + StringUtil.getUrl(suffixParams);

        gotoNotitleWeburl(context, url, reportName, time, true, ScoreEventID.EVENT_SHARE_KNOWLEDGE_MAP, examId);
    }

    /**
     * 知识图谱报告——图谱模式
     */
    public static void gotoKnowledgeAtlasModeReport(Context context, String examId, String studentId, String lastExamId, String reportName, String time) {
        if (TextUtils.isEmpty(studentId) || TextUtils.isEmpty(examId)) {
            return;
        }
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.WEB_KNOWLEDGE_REPORT_ATLAS_MODE;

        Map<String, String> restfulParams = new HashMap<>();
        restfulParams.put("studentId", studentId);
        HashMap<String, String> suffixParams = new LinkedHashMap<>();
        suffixParams.put("examId", examId);
        if (!TextUtils.isEmpty(lastExamId)) {
            suffixParams.put("lastExamId", lastExamId);
        }
        UserDetailinfo detailinfo = AccountUtils.getUserdetailInfo();
        if ( detailinfo!= null) {
            suffixParams.put("name", detailinfo.getReallyName());
        }
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if ( loginInfo != null) {
            String token = loginInfo.getAccessToken();
            try {
                suffixParams.put("access_token", URLEncoder.encode(token, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                AppLog.w(ErrTag.TAG_ENCODE, "err", e);
            }
        }
        url = getRestUrl(url, restfulParams) + StringUtil.getUrl(suffixParams);

        gotoNotitleWeburl(context, url, reportName, time, true, ScoreEventID.EVENT_SHARE_KNOWLEDGE_MAP,examId);
    }

    //    private static void gotoWeburl(Context context, String data) {
    //        /**
    //         * {"type":"ddmath.report.create.knowledge","subType":"","url":"相对路径","extent":"{"title":"ssss"}"}
    //         */
    //        JSONObject json = JSON.parseObject(data);
    //        String url = json.getString("url");
    //        String extent = json.getString("extent");
    //        JSONObject extentObj = JSON.parseObject(extent);
    //        String title = "";
    //        if (extentObj != null) {
    //            title = extentObj.getString("title");
    //        }
    //        if (TextUtils.isEmpty(url)) {
    //            AlertManager.toast(context, "网页地址不能为空");
    //            return;
    //        }
    //
    //        Intent intent = new Intent(context, ShowMsgWebviewActivity.class);
    //        intent.putExtra(ShowMsgWebviewActivity.MSG_TITLE, title);
    //        intent.putExtra(ShowMsgWebviewActivity.MSG_URL, url);
    //        context.startActivity(intent);
    //    }

    /**
     * 错题订正报告
     */
    private static void gotoErrorCorrectReport(Context context, String studentId, String reportName, String time) {
        String url = getCorrectReportUrl(studentId);
        gotoNotitleWeburl(context, url, reportName, time);
    }

    /**
     * 获取错题订正报告Url
     */
    public static String getCorrectReportUrl(String studentId) {
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.ERROR_CORRECT_REPORT;
        Map<String, String> restfulParams = new HashMap<>();
        if (!TextUtils.isEmpty(studentId)) {
            restfulParams.put("studentId", studentId);
        }
        MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
        if (currentClassInfo != null && !TextUtils.isEmpty(currentClassInfo.getClassId())) {
            restfulParams.put("classId", currentClassInfo.getClassId());
        }
        url = getRestUrl(url, restfulParams);
        return url;
    }

    /**
     * 获取错题订正报告Url
     */
    public static String getWeekReportUrl(String studentId,String reportId) {
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.WEB_WEEKANALYSIS_REPORT;
        Map<String, String> restfulParams = new HashMap<>();
//        if (!TextUtils.isEmpty(studentId)) {
//            restfulParams.put("studentId", studentId);
//        }
        if (!TextUtils.isEmpty(reportId)) {
            restfulParams.put("reportId", reportId);
        }
//        MyTutorClassInfo currentClassInfo = AccountUtils.getCurrentClassInfo();
//        if (currentClassInfo != null && !TextUtils.isEmpty(currentClassInfo.getClassId())) {
//            restfulParams.put("classId", currentClassInfo.getClassId());
//        }
        url = getRestUrl(url, restfulParams);
        return url;
    }

    /**
     * 获取充值记录URL
     */
    public static String getRechargeRecordUrl(String studentId) {
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.RECHARGE_RECORD;
        Map<String, String> restfulParams = new HashMap<>();
        if (!TextUtils.isEmpty(studentId)) {
            restfulParams.put("studentId", studentId);
        }

        url = getRestUrl(url, restfulParams);
        return DataUtils.getUrl(url);
    }

    /**
     * 获取学豆兑换记录URL
     */
    public static String getExchangeRecordUrl(String studentId) {
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.EXCHANGE_RECORD;
        Map<String, String> restfulParams = new HashMap<>();
        if (!TextUtils.isEmpty(studentId)) {
            restfulParams.put("studentId", studentId);
        }

        url = getRestUrl(url, restfulParams);
        return url;
    }

    /**
     * 获取学豆使用详情URL
     */
    public static String getUseBeanDetailUrl(String studentId) {
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.USE_BEAN_DETAIL;
        Map<String, String> restfulParams = new HashMap<>();
        if (!TextUtils.isEmpty(studentId)) {
            restfulParams.put("studentId", studentId);
        }

        url = getRestUrl(url, restfulParams);
        return DataUtils.getUrl(url);
    }

    /**
     * 获取VIP卡兑换记录URL
     */
    public static String getCardExchangelUrl(String studentId) {
        String url = AppRequestConst.WEB_ADDRESS + AppRequestConst.CARD_EXCHANGE_DETAIL;
        Map<String, String> restfulParams = new HashMap<>();
        if (!TextUtils.isEmpty(studentId)) {
            restfulParams.put("studentId", studentId);
        }
        url = getRestUrl(url, restfulParams);
        String token = AccountUtils.getLoginUser().getAccessToken();
        try {
            url += "&access_token=" + URLEncoder.encode(token, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            AppLog.w(ErrTag.TAG_ENCODE, "err", e);
            url = null;
        }
        return url;
    }

    private static void gotoNotitleWeburl(Context context, String url, String reportName, String time) {
        gotoNotitleWeburl(context, url, reportName, time, false, null, null);
    }

    public static void gotoNotitleWeburl(Context context, String url, String reportName, String time, boolean fromKnowledgemap, String eventId, String contentId ) {
        if (TextUtils.isEmpty(url)) {
            AlertManager.toast(context, "网页地址不能为空");
            return;
        }

        Intent intent = new Intent(context, CommonWvNoActionbarActivity.class);
        intent.putExtra(CommonWvNoActionbarActivity.MSG_URL, url);
        intent.putExtra(CommonWvNoActionbarActivity.MSG_FROM_KnowledgeMap, fromKnowledgemap);
        intent.putExtra(CommonWvNoActionbarActivity.MSG_TITLE, reportName);
        intent.putExtra(CommonWvNoActionbarActivity.MSG_SHARE_TEXT, time);
        intent.putExtra( CommonWvNoActionbarActivity.MSG_SCORE_EVENT, eventId );
        intent.putExtra( CommonWvNoActionbarActivity.MSG_CONTENT_ID, contentId );
        context.startActivity(intent);
    }

    public static String getRestUrl(String url, Map<String, String> restfulParams) {
        // 编译为Restful Url
        if (restfulParams.size() > 0) {
            Set<String> keys = restfulParams.keySet();
            for (String key : keys) {
                url = url.replace(":" + key, restfulParams.get(key));
            }
        }
        return url;
    }

    //处理家长端用户购买套餐的显示
    public static boolean setBuySuiteText(String data, TextView textView) {
        if (TextUtils.isEmpty(data) || "{}".equals(data)) {
            return false;
        }
        data = data.replace("+", ",");
        JSONObject json = JSON.parseObject(data);
        String type = json.getString("type");
        if( MessageRemarkType.PARENT_REPORT_BUYSUIT.equals(type) ){
            int count = json.getInteger("buyCount");
            if( count > 0 ){
                textView.setText( String.format(Locale.getDefault(),"你孩子的同班同学已在学生端购买了%d人次提分服务", count));
            }
            return true;
        }
        return false;
    }

    //处理作业报告有错题的情况
    public static boolean setGoReviseText(Context context, MessageInfo messageInfo, TextView textView) {
        String data = messageInfo.getRemark();
        if (TextUtils.isEmpty(data) || "{}".equals(data)) {
            return false;
        }
        data = data.replace("+", ",");
        JSONObject json = JSON.parseObject(data);
        String type = json.getString("type");
        if( type.startsWith("ddmath.report.create.") && json.containsKey("extent") ){
            JSONObject jsonObject = json.getJSONObject("extent");
            if( jsonObject.containsKey("errorcount") ){
                int count = Integer.valueOf(jsonObject.getString("errorcount"));
                if( count > 0 ){
                    Date date = new Date();
                    date.setTime( messageInfo.getSendTime() );
                    String errstr = String.format(Locale.getDefault(),"你有%d道错题已进入", count);
                    String title = "《" + DateUtils.format(date) + DateUtils.getWeekOfDate(date)+"错题订正》";
                    Spannable span = new SpannableString(  String.format(Locale.getDefault(),"%s%s中，请及时订正～", errstr,title) );
                    int start = errstr.length(), end = start+title.length();
                    //span.setSpan( new AbsoluteSizeSpan(DensityUtils.sp2px( getContext(), GlobalData.isPad()?24:16 )), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    span.setSpan( new ForegroundColorSpan( context.getResources().getColor(R.color.bar_tint)), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                    textView.setText( span );
                }
                return true;
            }

        }
        return false;
    }

    //处理消息点击事件
    public static boolean setClickableText(String data, TextView beforeView, TextView btnView, TextView afterView, TextView customPlanView) {
        boolean isClickable = true;
        if (TextUtils.isEmpty(data) || "{}".equals(data)) {
            return false;
        }
        data = data.replace("+", ",");
        JSONObject json = JSON.parseObject(data);
        String type = json.getString("type");
        switch (type) {
            case MessageRemarkType.ACTIVITY_DDWORK_UPDETAIL:
                btnView.setText("查看详情");
                break;
            case MessageRemarkType.ACTIVITY_DDWORK_DETAIL:
            case MessageRemarkType.ACTIVITY_SCWORK_DETAIL:
            case MessageRemarkType.EBOOK_WEEKTRAIN_CORRECTED:
            case MessageRemarkType.EXCESIVE_PRACTICE_CORRECTED:
                btnView.setText("查看批阅结果");
                break;
            case MessageRemarkType.EXAM_REPORT_CREATE:
            case MessageRemarkType.HOMEWORK_REPORT_CREATE:
            case MessageRemarkType.WEEKEXAM_REPORT_CREATE:
            case MessageRemarkType.KNOWLEDGE_REPORT_CREATE:
            case MessageRemarkType.ALLROUND_REPORT_CREATE:
            case MessageRemarkType.ENROLEXAM_REPORT_CREATE:
            case MessageRemarkType.EBOOK_WEEKTRAIN_REPORT:
            case MessageRemarkType.EXCESIVE_PRACTICE_REPORT:
                btnView.setText("查看结果");
                break;
            case MessageRemarkType.ENROLEXAM_REPORT_RETRIEVE:
                btnView.setText("去编辑");
                break;
            case MessageRemarkType.APPLY_RECORRECT_QUESTION:
                // case MessageRemarkType.REVISE_QUESTION_CORRECTFINISH:
                // case MessageRemarkType.REVISE_QUESTION_RECORRECTFINISH:
                // case MessageRemarkType.REVISE_QUESTION_APPLYCORRECTFINISH:
                btnView.setText("查看批阅结果");
                break;
            case MessageRemarkType.REVISE_ERRORQUESTION_REPORT:
            case MessageRemarkType.STUDY_ANALYSISREPORT_CREATE:
                btnView.setText("查看报告");
                customPlanView.setVisibility(View.VISIBLE);
                break;
            case MessageRemarkType.MYCENTER_LEARNDOU:
                btnView.setText("查看学豆");
                break;
            case MessageRemarkType.MYCENTER_BUYSUIT:
                btnView.setText("获取学习特权");
                break;
            case MessageRemarkType.PARENT_EXPORT_WORK:
            case MessageRemarkType.PARENT_EXPORT_WEEK:
                btnView.setText("查看报告详情");
                break;
            case MessageRemarkType.ERRBOOK_FRAGMENT:{   //提升 错题本
                if(json.containsKey("model")){
                    isClickable = true;
                    String model = json.getString("model");
                    if( model.equals( MessageRemarkType.ERRBOOKFRAGMENT_MODEL_WEEKTRAIN ) ){
                        btnView.setText("查看错题再练本");
                        customPlanView.setVisibility(View.VISIBLE);
                    }else if( model.equals(MessageRemarkType.ERRBOOKFRAGMENT_MODEL_STAGEREVIEW) ){
                        btnView.setText("查看错题浏览本");
                        customPlanView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            }
            case MessageRemarkType.ENCHANCE_FRAGMENT:{      //变式训练
                if(json.containsKey("model")){
                    isClickable = true;
                    String model = json.getString("model");
                    if( model.equals("vartrain") ){
                        btnView.setText("查看变式训练本");
                        customPlanView.setVisibility(View.VISIBLE);
                    }
                }
                break;
            }
            default:
                isClickable = false; //隐藏可点击的字段
                break;
        }
        beforeView.setText("点击");
        afterView.setText("吧~");

        //向学情反馈界面跳转
        if (MessageRemarkType.STUDYCONDITION_REPORT.equals(type) ){      //学情反馈
            isClickable = true;
            beforeView.setText("");
            btnView.setText("快去看看吧");
            afterView.setText("");
        }

        return isClickable;
    }

    public void init(Activity activity) {
        bindActivity(activity);
    }

    private void bindActivity(Activity activity) {
        bindingActivity = activity;
        mContext = activity;
    }

    public void registerNewmsgBrd() {
        mNewMsgReciver = new NewMsgRefreshReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(NEW_MSG_BRD_ACTION);
        if (bindingActivity != null) {
            bindingActivity.registerReceiver(mNewMsgReciver, intentFilter);
            bRegisterListener = true;
        }
    }

    public void unregisterNewmsgBrd() {
        if (bindingActivity != null && bRegisterListener) {
            bindingActivity.unregisterReceiver(mNewMsgReciver);
            bRegisterListener = false;
        }
    }

    public void setNewMsgListener(NewMsgListener newMsgListener) {
        this.newMsgListener = newMsgListener;
    }

    private static class SingletonHolder {
        private static final MessageUtils INSTANCE = new MessageUtils();
    }

    class NewMsgRefreshReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isHaveNew = intent.getBooleanExtra(IS_HAVE_NEW_MSG, false);
            boolean mMsgCenterUpdateEnable = PreferencesUtils.getBoolean(mContext, AppConst.MSG_CENTER_UPDATE_ENABLE, true);
            if (isHaveNew) {
                if (mMsgCenterUpdateEnable) {
                    if (newMsgListener != null) {
                        newMsgListener.msgComeCallback(true);
                    }
                }
            } else {
                if (newMsgListener != null) {
                    newMsgListener.msgComeCallback(false);
                }
            }
        }

    }


}
