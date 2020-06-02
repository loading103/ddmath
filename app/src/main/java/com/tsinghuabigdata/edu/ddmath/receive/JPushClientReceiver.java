/*
 * Copyright (c) 2015. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.tsinghuabigdata.edu.ddmath.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.tsinghuabigdata.edu.ddmath.constant.AppConst;
import com.tsinghuabigdata.edu.ddmath.event.NewReporEvent;
import com.tsinghuabigdata.edu.ddmath.module.badge.BadgeManager;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageActivity;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageRemarkType;
import com.tsinghuabigdata.edu.ddmath.module.message.MessageUtils;
import com.tsinghuabigdata.edu.ddmath.parent.activity.ParentMsgDetailActivity;
import com.tsinghuabigdata.edu.ddmath.util.AccountUtils;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushClientReceiver extends BroadcastReceiver {

    private static Context mContext;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        AppLog.d("[JPushClientReceiver] onReceive = " + intent.getAction() + ", extras: " + printBundle(bundle));
        setContext(context);

        //
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            AppLog.d("[JPushClientReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            AppLog.d("[JPushClientReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            //消息中心小红点
            if (!handleMsgCenterMsgState(context, bundle)) {
                return;
            }
            //我的报告小红点
            handMyReportNewMsg(context, bundle);

            //判断是否用户愿意接收推送消息
            boolean mPushMsgEnable = PreferencesUtils.getBoolean(context, AppConst.RECV_PUSH_MSG_ENABLE, true);
            if (!mPushMsgEnable) {
                return;
            }
            JPushNotifyHelper.showNotify4SelfdefMsg2(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            AppLog.d("[JPushClientReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            // 消息数量
            long count = PreferencesUtils.getLong(context, AppConst.MESSAGE_COUNT, 0L);
            count++;
            PreferencesUtils.putLong(context, AppConst.MESSAGE_COUNT, count);
            //AppMainChangeStateHandler.notificationMessage(context);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

            AppLog.d("[JPushClientReceiver] 用户点击打开了通知");
            //应用启动时，应该清除应用所有通知
            //((NotificationManager) (context.getSystemService(Service.NOTIFICATION_SERVICE))).cancelAll();
            jumpToHandler(context, bundle);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            AppLog.d("[JPushClientReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            AppLog.d("[JPushClientReceiver]" + intent.getAction() + " connected state change to " + connected);
        }
        //自定义消息发送的通知点击处理：用户点击通知栏的推送通知
        else if (JPushNotifyHelper.CUSTOM_MESSAGE_RECEIVER_ACTION.equals(intent.getAction())) {

            AppLog.d("用户点击平台推送的自定义消息类型！");
            jumpToHandler(context, bundle);

        } else {
            AppLog.d("[JPushClientReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    public static void clearLocalNotifications() {

        if (mContext != null) {
            JPushInterface.clearLocalNotifications(mContext);
            JPushInterface.clearAllNotifications(mContext);
            JPushInterface.clearNotificationById(mContext, JPushNotifyHelper.MSG_ID);
        }
    }


    //-----------------------------------------------------------------------
    private synchronized void setContext(Context context) {
        mContext = context;
    }

    private void jumpToHandler(Context context, Bundle bundle) {
        /**
         * 通知消息跳转的三种规则：
         * 1、remark与rowkey都不为空，跳转到应用对应的页面；
         * 2、remark为空，rowkey不为空，跳到详情页面
         * 3、remark与rowkey都为空，进入到应用主页面
         */

        String res = bundle.getString(JPushInterface.EXTRA_EXTRA);
        JSONObject jsonObject = null;
//        int msgTypeId = 0;
        String rowKey = null;
//        String remark = null;
//        String source = null;
//        String msgType = null;
        try {
            jsonObject = new JSONObject(res);
//            msgTypeId = jsonObject.getInt("msgTypeId");
//            msgType = jsonObject.getString("msgType");
//            remark = jsonObject.getString("remark");
//            source = jsonObject.getString("source");
            rowKey = jsonObject.getString("rowKey");
        } catch (JSONException e) {
            AppLog.i("", e);
        }

//        //1、remark与rowkey都不为空，跳转到应用对应的页面；
//        if (!TextUtils.isEmpty(remark) && !TextUtils.isEmpty(rowKey)) {
//            Intent intent = new Intent(context, CompMessageDetailActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra(AppMaskActivity.HOME_MSG_CONTENT, rowKey);
//            context.startActivity(intent);
//            return;
//        }
//        //2、remark为空，rowkey不为空，跳到详情页面
//        if (TextUtils.isEmpty(remark) && !TextUtils.isEmpty(rowKey)){
//            Intent intent = new Intent(context, CompMessageDetailActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.putExtra(AppMaskActivity.HOME_MSG_CONTENT, rowKey);
//            context.startActivity(intent);
//            return;
//        }
//        //3、remark与rowkey都为空，进入到应用主页面
//        if (TextUtils.isEmpty(remark) && TextUtils.isEmpty(rowKey)){
//            JPushNotifyHelper.enterApp(context);
//        }

        //
        if (!TextUtils.isEmpty(rowKey)) {
            if(AccountUtils.getLoginParent()!=null){
                Intent intent = new Intent(context, ParentMsgDetailActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(AppConst.MSG_ROWKEY, rowKey);
                intent.putExtra("isread", false);
                context.startActivity(intent);
            }else if( AccountUtils.getLoginUser()!=null ){
                Intent intent = new Intent(context, MessageActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(AppConst.MSG_ROWKEY, rowKey);
                context.startActivity(intent);
            }else {
                ToastUtils.show( context, "请先登录");
            }

        } else {
            JPushNotifyHelper.enterApp(context);
        }

    }

    private void handMyReportNewMsg(Context context, Bundle bundle) {
        /*
         * 通知消息跳转的三种规则：
         * 1、remark与rowkey都不为空，跳转到应用对应的页面；
         * 2、remark为空，rowkey不为空，跳到详情页面
         * 3、remark与rowkey都为空，进入到应用主页面
         */

        String res = bundle.getString(JPushInterface.EXTRA_EXTRA);
        JSONObject jsonObject;
//        int msgTypeId = 0;
//        String rowKey = null;
        String remark = null;
//        String source = null;
//        String msgType = null;

        try {
            jsonObject = new JSONObject(res);
            if(jsonObject.has("remark") ){
                remark = jsonObject.getString("remark");
            }
//            msgTypeId = jsonObject.getInt("msgTypeId");
//            msgType = jsonObject.getString("msgType");
//            source = jsonObject.getString("source");
//            rowKey = jsonObject.getString("rowKey");
        } catch (JSONException e) {
            AppLog.d("", e);
        }

        //
        if (!TextUtils.isEmpty(remark)) {
            try {
                JSONObject remarkobj = new JSONObject(remark);
                String type = remarkobj.getString("type");
                switch (type) {
                    case MessageRemarkType.EXAM_REPORT_CREATE:
                    case MessageRemarkType.HOMEWORK_REPORT_CREATE:
                    case MessageRemarkType.WEEKEXAM_REPORT_CREATE:
                    case MessageRemarkType.KNOWLEDGE_REPORT_CREATE:
                    case MessageRemarkType.ALLROUND_REPORT_CREATE:
                    case MessageRemarkType.ENROLEXAM_REPORT_CREATE:
                        //sendNewReportBrd(context, true);
                        EventBus.getDefault().post(new NewReporEvent());
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private boolean handleMsgCenterMsgState(Context context, Bundle bundle) {
        //缓存对应类型的消息是否有新消息
        /**
         *     msgType        msgTypeId
         *     我的消息(系统)       1
         *     我的消息(人工)       2
         *     推荐消息            3
         *
         *     字段           来源
         *     source        “SITELETTER”:有新的站内信时  有消息内容
         *                   “APPC”:来自web普通通知消息   无消息内容
         *                   “SYSTEM”：来自系统的通知消息  无消息内容 ： 众包
         */
        String res = bundle.getString(JPushInterface.EXTRA_EXTRA);
        JSONObject jsonObject = null;
        int msgTypeId = 0;
        String source = "";
        try {
            jsonObject = new JSONObject(res);
            msgTypeId = jsonObject.getInt("msgTypeId");
            source = jsonObject.getString("source");
        } catch (JSONException e) {
            AppLog.i("", e);
        }

        if (source.equals("SITELETTER")) {
            switch (msgTypeId) {
                case 1:
                case 2:
                    PreferencesUtils.putBoolean(context, AppConst.MSG_TYPE_MYMSG, true);
                    //发送广播通知新消息更新
                    sendNewMsgBrd(context, true);
                    //更新桌面消息数量
                    new BadgeManager(context).addBadge( 1 );
                    break;
                case 3:
                    PreferencesUtils.putBoolean(context, AppConst.MSG_TYPE_REC, true);
                    //发送广播通知新消息更新
                    sendNewMsgBrd(context, true);
                    //更新桌面消息数量
                    new BadgeManager(context).addBadge( 1 );
                    break;
                default:
                    break;
            }
            //实时更改消息中心小红点状态
            sendMsgCenterNewMsgBrd(context);
        }
        return true;
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    AppLog.i("This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    AppLog.i("Get message extra JSON error!", e);
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    public static void sendNewMsgBrd(Context context, boolean isHaveNew) {

        Intent intent = new Intent();
        intent.setAction(MessageUtils.NEW_MSG_BRD_ACTION);
        intent.putExtra(AppConst.IS_HAVE_NEW_MSG, isHaveNew);
        context.sendBroadcast(intent);
    }

    public static void sendMsgCenterNewMsgBrd(Context context) {

//        Intent intent = new Intent();
//        intent.setAction(MessageUtils.MSGCENTER_NEW_MSG_BRD_ACTION);
//        context.sendBroadcast(intent);
    }
}
