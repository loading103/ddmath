package com.tsinghuabigdata.edu.ddmath.receive;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.activity.InitActivity;
import com.tsinghuabigdata.edu.ddmath.activity.MainActivity;
import com.tsinghuabigdata.edu.ddmath.bean.LoginInfo;
import com.tsinghuabigdata.edu.ddmath.bean.UserDetailinfo;
import com.tsinghuabigdata.edu.ddmath.event.AssignNewWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.BuySuiteEvent;
import com.tsinghuabigdata.edu.ddmath.event.CorrectWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.CreateDayReportEvent;
import com.tsinghuabigdata.edu.ddmath.event.CreateWeekReportEvent;
import com.tsinghuabigdata.edu.ddmath.event.CreateWorkReportEvent;
import com.tsinghuabigdata.edu.ddmath.event.RecallWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.RechargeBeanEvent;
import com.tsinghuabigdata.edu.ddmath.event.RecorrectWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.SchoolWorkEvent;
import com.tsinghuabigdata.edu.ddmath.event.UpdateStudybeanEvent;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageRemarkType;
import com.tsinghuabigdata.edu.ddmath.module.message.bean.MessageInfo;
import com.tsinghuabigdata.edu.ddmath.parent.event.ParentCenterEvent;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.LoginUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushLocalNotification;

/**
 *
 */
public class JPushNotifyHelper {

    private static final String TAG                            = "JPushHelper";
    public static final  String CUSTOM_MESSAGE_RECEIVER_ACTION = "com.tsinghuabigdata.edu.ddmath.custom.message.opened";

    public static final int MSG_ID = 555;

    public static void setShowingNotifyNum(Context context, int num) {
        JPushInterface.setLatestNotificationNumber(context, num);
    }

    public static void showNotify4SelfdefMsg2(final Context context, Bundle bundle) {

        JPushLocalNotification ln = new JPushLocalNotification();
        ln.setBuilderId(0);
        ln.setContent(bundle.getString(JPushInterface.EXTRA_MESSAGE));
        ln.setTitle(context.getString(R.string.app_name));
        ln.setNotificationId(MSG_ID);
        ln.setBroadcastTime(System.currentTimeMillis());

        //
        String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        if (!TextUtils.isEmpty(msg)) {
            if (msg.contains("兑换码验证成功")) {
                EventBus.getDefault().post(new UpdateStudybeanEvent());
            } else if (msg.contains("成功购买套餐")) {
                EventBus.getDefault().post(new BuySuiteEvent());
            } else if (msg.contains("学豆充值成功")) {
                EventBus.getDefault().post(new RechargeBeanEvent());
            } else if (msg.contains("撤回")) {
                EventBus.getDefault().post(new RecallWorkEvent());
            }
        }

        //解析消息字段属性
        String res = bundle.getString(JPushInterface.EXTRA_EXTRA);
        LogUtils.i("msg= " + msg + "res= " + res);
        JSONObject jsonObject;
        int msgTypeId = 0;
        String remark = null;
        String source = null;
        String msgType = null;
        String rowKey = null;
        try {
            jsonObject = new JSONObject(res);
            msgTypeId = jsonObject.getInt("msgTypeId");

            if (jsonObject.has("msgType")) {
                msgType = jsonObject.getString("msgType");
            }

            if (jsonObject.has("source")) {
                source = jsonObject.getString("source");
            }
            if (jsonObject.has("rowKey")) {
                rowKey = jsonObject.getString("rowKey");
            }
            if (jsonObject.has("remark")) {
                remark = jsonObject.getString("remark");
            }

            if (!TextUtils.isEmpty(remark)) {
                JSONObject jsonObject1 = new JSONObject(remark);
                //学生信息更新
                String type = jsonObject1.getString("type");
                if (MessageRemarkType.STUDENT_INFO_UPDATE.equals(type)) {
                    String studentId = jsonObject1.getString("studentId");
                    String extstr = jsonObject1.getString("ext");
                    UserDetailinfo userDetailinfo = AccountUtils.getUserdetailInfo();
                    if (userDetailinfo == null || TextUtils.isEmpty(userDetailinfo.getStudentId()) || !userDetailinfo.getStudentId().equals(studentId) || !"class".equals(extstr)) {
                        return;
                    }
                    LoginUtils.queryTutorClass();
                } else if (MessageRemarkType.ACTIVITY_DDWORK_UPDETAIL.equals(type)) {
                    String workId = jsonObject1.getString("workId");
                    String workName = jsonObject1.getString("workName");
                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setRowKey(rowKey);
                    messageInfo.setWorkId(workId);
                    messageInfo.setDescription("你的老师布置了新的作业<" + workName + ">");
                    messageInfo.setWorkType(MessageInfo.ASSGIN_WORK);
                    EventBus.getDefault().post(new AssignNewWorkEvent(messageInfo));

                    //                    WorkInfoBean workBean = new WorkInfoBean();
                    //                    workBean.setWorkId( workId );
                    //                    workBean.setWorkName( workName );
                    //                    BubbleBean bubbleBean = new BubbleBean( BubbleBean.TYPE_DYNAMIC, "all", "你的老师布置了新的作业<" + workName + ">" );
                    //                    bubbleBean.setExtend( workBean );
                    //                    FloatActionController.getInstance().addBubble( bubbleBean );

                    //DDWorkUtil.putWorkId(workId);
                } else if (MessageRemarkType.ACTIVITY_DDWORK_DETAIL.equals(type)) {
                    String workId = jsonObject1.getString("workId");
                    String workName = jsonObject1.getString("workName");
                    MessageInfo messageInfo = new MessageInfo();
                    messageInfo.setRowKey(rowKey);
                    messageInfo.setWorkId(workId);
                    messageInfo.setDescription("你的作业<" + workName + ">已经批阅完成");
                    messageInfo.setWorkType(MessageInfo.COREECT_WORK);
                    EventBus.getDefault().post(new CorrectWorkEvent(messageInfo));

                    //                    WorkInfoBean workBean = new WorkInfoBean();
                    //                    workBean.setWorkId( workId );
                    //                    workBean.setWorkName( workName );
                    //                    BubbleBean bubbleBean = new BubbleBean( BubbleBean.TYPE_DYNAMIC, "all", "你的作业<" + workName + ">已经批阅完成" );
                    //                    bubbleBean.setExtend( workBean );
                    //                    FloatActionController.getInstance().addBubble( bubbleBean );

                } else if (MessageRemarkType.HOMEWORK_REPORT_CREATE.equals(type) || MessageRemarkType.WEEKEXAM_REPORT_CREATE.equals(type) ) {
                    //作业报告生成
                    EventBus.getDefault().post(new CreateWorkReportEvent());
                }else if(MessageRemarkType.APPLY_RECORRECT_QUESTION.equals(type)) {
                    EventBus.getDefault().post(new RecorrectWorkEvent());
                } else if (MessageRemarkType.ACTIVITY_SCWORK_DETAIL.equals(type) || MessageRemarkType.ACTIVITY_DDWORK_DETAIL.equals(type) ) {
                    EventBus.getDefault().post(new SchoolWorkEvent());
                } else if (MessageRemarkType.PARENT_EXPORT_WORK.equals(type)) {
                    EventBus.getDefault().post(new CreateDayReportEvent());
                } else if (MessageRemarkType.PARENT_EXPORT_WEEK.equals(type)) {
                    EventBus.getDefault().post(new CreateWeekReportEvent());
                }
            }

        } catch (Exception e) {
            AppLog.i("", e);
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("msgTypeId", msgTypeId);
        map.put("msgType", msgType);
        map.put("remark", remark);
        map.put("source", source);
        map.put("rowKey", rowKey);
        JSONObject json = new JSONObject(map);
        ln.setExtras(json.toString());
        JPushInterface.addLocalNotification(context, ln);

        //家长端更新消息数量
        AppLog.d("fdjsadfjljkjk -------------");
        EventBus.getDefault().post(new ParentCenterEvent(ParentCenterEvent.TYPE_MSG_ADD));
    }

    /**
     * 发送通知
     */
    //    public static void showNotify4SelfdefMsg(Context context, Bundle bundle) {
    //        //从配置缓存里取出用户设定的模式
    //        //        String notifyMode = AccountUtils.getNotifySettingMode();
    //        int notifyModeCode;
    //        //        if (notifyMode.equals(AppConst.VIBRATION_NOTIFY_MODE)) {
    //        //            notifyModeCode = Notification.DEFAULT_VIBRATE;
    //        //        } else if (notifyMode.equals(AppConst.VOICE_NOTIFY_MODE)) {
    //        //            notifyModeCode = Notification.DEFAULT_SOUND;
    //        //        } else {
    //        notifyModeCode = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
    //        //        }
    //        //新建通知
    //        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    //
    //        //通知点击时，发送广播，注意设置注册广播的action动作
    //        Intent intent = new Intent();
    //        intent.putExtra(JPushInterface.EXTRA_EXTRA, bundle.getString(JPushInterface.EXTRA_EXTRA));
    //        intent.setAction(CUSTOM_MESSAGE_RECEIVER_ACTION);
    //        //使用UUID保证requestCode唯一性，避免多个通知的PendingIntent事件冲突
    //        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, UUID.randomUUID().hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
    //
    //        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
    //                .setContentIntent(pendingIntent)
    //                .setTicker(context.getString(R.string.app_name))
    //                .setContentTitle(context.getString(R.string.app_name))
    //                .setContentText(bundle.getString(JPushInterface.EXTRA_MESSAGE))
    //                .setDefaults(notifyModeCode)
    //                .setAutoCancel(true)
    //                .setSmallIcon(R.drawable.ic_launcher)
    //                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher));
    //
    //        Notification notification = builder.build();
    //
    //        //发送通知，注意通知的ID属性按需设置为不同的值，保证能够实现多条通知的显示，同时，统一类型的通知只显示一条，实时更新即可， 如同一个人的聊天消息
    //        nManager.notify(bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID), notification);
    //    }


    //打开自定义的Activity
    private static void jumpToActivity(Context context, Class cls) {
        String packageName = AppUtils.getPackageInfo(context).packageName;
        ActivityManager.RunningTaskInfo topTask = AppUtils.getTopTask(context);
        if (AppUtils.isTopActivity(topTask, packageName, cls.getName())) {
            AppLog.d("cls : " + cls.getName() + "  在最前端");
        } else {
            Intent i = new Intent(context, cls);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        }
    }

    public static void enterApp(Context context) {
        LoginInfo loginInfo = AccountUtils.getLoginUser();
        if (null == loginInfo) {
            //用户未登录
            Log.i(TAG, "用户未登录");
            jumpToActivity(context, InitActivity.class);
        } else {
            jumpToActivity(context, MainActivity.class);
        }
    }

}