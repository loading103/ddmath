package com.tsinghuabigdata.edu.ddmath.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.Toast;

import com.tsinghuabigdata.edu.commons.http.HttpRequestException;
import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.commons.http.AppRequestException;
import com.tsinghuabigdata.edu.ddmath.commons.http.InformMapping;
import com.tsinghuabigdata.edu.ddmath.dialog.CustomDialog;
import com.tsinghuabigdata.edu.ddmath.dialog.CustomDialogNew;
import com.tsinghuabigdata.edu.ddmath.dialog.DownDialogNew;
import com.tsinghuabigdata.edu.utils.NetworkUtils;

/**
 * <p>
 * Created by yanshen@tsinghuabigdata.com on 2015/11/5.
 * </p>
 *
 * @author yanshen@tsinghuabigdata.com
 * @version V1.0
 * @packageName:
 * @createTime: 2015/11/5 15:15
 */
public final class AlertManager {

    private static Toast currentToast;

    public static void show(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.alert_title)
                .setMessage(message).show();
    }

    public static void show(Context context, String message, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        show(context, context.getString(R.string.alert_title), message, positiveListener,
                negativeListener);
    }

    public static AlertDialog show(Context context, String title, String message, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton(R.string.alert_positive, positiveListener)
                .setNegativeButton(R.string.alert_neutral, negativeListener)
                .setMessage(message).show();
    }

    public static AlertDialog show(Context context, String title, String message, String positiveText, String negativeText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(title)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener)
                .setMessage(message).show();
    }

    public static void show(Context context, String message, String positiveText, String negativeText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.alert_title)
                .setPositiveButton(positiveText, positiveListener)
                .setNegativeButton(negativeText, negativeListener)
                .setMessage(message).show();
    }

    public static void show(Context context, String message, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setCancelable(false)
                .setTitle(R.string.alert_title)
                .setPositiveButton(R.string.alert_positive, onClickListener)
                .setMessage(message).show();
    }

    public static void toast(Context context, String message) {
        toast(context, message, false);
    }

    public static void toast(Context context, String message, boolean showlong) {
        int length;
        if (showlong) {
            length = Toast.LENGTH_LONG;
        } else {
            length = Toast.LENGTH_SHORT;
        }
        // 避免覆盖
        if (currentToast != null) {
            currentToast.cancel();
            currentToast = null;
        }
        currentToast = Toast.makeText(context, message, length);
        currentToast.show();
    }

    public static void toastForForeground(Context context, String message, boolean showlong) {
        if (!AppUtils.isRunningForeground(context)) {
            // 应用程序在后台
            return;
        }
        int length;
        if (showlong) {
            length = Toast.LENGTH_LONG;
        } else {
            length = Toast.LENGTH_SHORT;
        }
        // 避免覆盖
        if (currentToast != null) {
            currentToast.cancel();
            currentToast = null;
        }
        currentToast = Toast.makeText(context, message, length);
        currentToast.show();
    }

    /**
     * 显示短错误信息
     *
     * @param context
     * @param ex
     */
    public static void showErrorInfo(Context context, Exception ex) {
        showErrorInfo(context, ex, false);
    }

    /**
     * 显示长错误信息
     *
     * @param context
     * @param ex
     */
    public static void showLongErrorInfo(Context context, Exception ex) {
        showErrorInfo(context, ex, true);
    }

    /**
     * 显示错误信息
     *
     * @param context
     * @param ex
     */
    public static void showErrorInfo(Context context, Exception ex, boolean showlong) {
        if (context == null) {
            return;
        }
        if (!AppUtils.isNetworkConnected(context) || NetworkUtils.isNoConnection(ex)) {
            AlertManager.toast(context, context.getString(R.string.no_connection), showlong);
        } else if (NetworkUtils.isTimeout(ex) || (ex.getMessage() != null && (ex.getMessage().contains("网络") || ex.getMessage().contains("time out")))) {
            AlertManager.toast(context, "请求超时", showlong);
        } else {
            String message;
            if (InformMapping.getInstance().containsKey(message = getResponseInfo(ex))) {
                AlertManager.toast(context, InformMapping.getInstance().get(message), showlong);
            } else if (InformMapping.getInstance().containsKey(message = ex.getMessage())) {
                AlertManager.toast(context, InformMapping.getInstance().get(message), showlong);
            } else if (ex instanceof AppRequestException) {
                // 服务器接口异常
                String msg = ((AppRequestException) ex).getResponse().getInform();
                if (TextUtils.isEmpty(msg) || msg.charAt(0) < 0x80)
                    AlertManager.toast(context, context.getResources().getString(R.string.server_error), showlong);
                else
                    AlertManager.toast(context, msg, showlong);
            } else {
                AlertManager.toast(context, context.getString(R.string.server_error), showlong);
            }
        }
    }

    /**
     * 显示错误信息
     *
     * @param context
     * @param ex
     */
    public static void showUploadErrorInfo(Context context, Exception ex) {
        if (context == null) {
            return;
        }
        if (!AppUtils.isNetworkConnected(context) || NetworkUtils.isNoConnection(ex)) {
            AlertManager.toast(context, context.getString(R.string.no_connection));
        } else if (NetworkUtils.isTimeout(ex) || (ex.getMessage() != null && (ex.getMessage().contains("网络") || ex.getMessage().contains("time out")))) {
            AlertManager.toast(context, "请求超时");
        } else {
            String message;
            if (InformMapping.getInstance().containsKey(message = getResponseInfo(ex))) {
                AlertManager.toast(context, InformMapping.getInstance().get(message));
            } else if (InformMapping.getInstance().containsKey(message = ex.getMessage())) {
                AlertManager.toast(context, InformMapping.getInstance().get(message));
            } else if (ex instanceof AppRequestException) {
                // 服务器接口异常
                String msg = ((AppRequestException) ex).getResponse().getInform();
                if (TextUtils.isEmpty(msg) || msg.charAt(0) < 0x80)
                    AlertManager.toast(context, context.getResources().getString(R.string.submit_server_exception));
                else
                    AlertManager.toast(context, msg);
            } else if (ex instanceof HttpRequestException) {
                // 网络请求异常
                if (ex.getMessage().contains("AppRequestException")) {
                    AlertManager.toast(context, context.getResources().getString(R.string.submit_server_exception));
                } else {
                    AlertManager.toast(context, context.getResources().getString(R.string.submit_request_exception));
                }
            } else {
                AlertManager.toast(context, context.getString(R.string.server_error));
            }
        }
    }


    /**
     * 获取返回错误码结果中的错误信息
     *
     * @param ex
     * @return
     */
    private static String getResponseInfo(Exception ex) {
        if (ex instanceof AppRequestException) {
            return ((AppRequestException) ex).getResponse().getInform();
        }
        return null;
    }


    public static CustomDialog showCustomDialog(Context context, String message, String positiveText, String negativeText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {

        CustomDialog dialog = new CustomDialog(context, R.style.FullTransparentDialog);
        dialog.setData(message, positiveText, negativeText, positiveListener, negativeListener);
        dialog.show();
        return dialog;
    }

    public static CustomDialog showCustomDialog(Context context, String message, String positiveText, DialogInterface.OnClickListener positiveListener) {

        CustomDialog dialog = new CustomDialog(context, R.style.FullTransparentDialog);
        dialog.setData(message, positiveText, positiveListener);
        dialog.show();
        return dialog;
    }

    public static CustomDialog showCustomDialog1(Context context, String message, String positiveText, String negativeText, DialogInterface.OnClickListener positiveListener, DialogInterface.OnClickListener negativeListener) {
        CustomDialog dialog = new CustomDialog(context, R.style.FullTransparentDialog);
        dialog.setCustomData(message, positiveText, negativeText, positiveListener, negativeListener);
        dialog.show();
        return dialog;
    }

    //--------------------------------------------------------------------------------------------------------------------------------
    //图片dialog

    //一个按钮，取消按钮 走关闭按钮
    public static CustomDialogNew showCustomImageBtnDialog(Context context, String message, String enterstr, DialogInterface.OnClickListener enterListener, DialogInterface.OnClickListener closeListener) {
        CustomDialogNew dialog = new CustomDialogNew(context, R.style.FullTransparentDialog);
        dialog.setData( message, enterstr, enterListener);
        dialog.setCloseBtnListener( closeListener );
        dialog.show();
        return dialog;
    }

    //一个按钮，取消按钮 走关闭按钮
    public static DownDialogNew showCustomImageContentDialog(Context context, String message, String enterstr,String content, DialogInterface.OnClickListener enterListener, DialogInterface.OnClickListener closeListener) {
        DownDialogNew dialog = new DownDialogNew(context, R.style.FullTransparentDialog);
        dialog.setContentData( message, enterstr,content, enterListener);
        dialog.setCloseBtnListener( closeListener );
        dialog.show();
        return dialog;
    }

    //两个按钮，取消按钮 走关闭按钮
    public static CustomDialogNew showCustomImageBtnDialog(Context context, String message, String leftstr, String rightstr, DialogInterface.OnClickListener leftListener, DialogInterface.OnClickListener rightListener, DialogInterface.OnClickListener closeListener) {
        CustomDialogNew dialog = new CustomDialogNew(context, R.style.FullTransparentDialog);
        dialog.setData( message, leftstr, rightstr, leftListener, rightListener);
        dialog.setCloseBtnListener( closeListener );
        dialog.show();
        return dialog;
    }
    public static CustomDialogNew showCustomImageBtnDialog(Context context, String message, String leftstr, String rightstr, String leftTips, DialogInterface.OnClickListener leftListener, DialogInterface.OnClickListener rightListener, DialogInterface.OnClickListener closeListener) {
        CustomDialogNew dialog = new CustomDialogNew(context, R.style.FullTransparentDialog);
        dialog.setData( message, leftstr, rightstr, leftTips, leftListener, rightListener);
        dialog.setCloseBtnListener( closeListener );
        dialog.show();
        return dialog;
    }
    public static CustomDialogNew showCustomImageBtnDialog(Context context, String message, String leftstr, String rightstr, String leftTips, String rightTips,DialogInterface.OnClickListener leftListener, DialogInterface.OnClickListener rightListener, DialogInterface.OnClickListener closeListener) {
        CustomDialogNew dialog = new CustomDialogNew(context, R.style.FullTransparentDialog);
        dialog.setData( message, leftstr, rightstr, leftTips, rightTips, leftListener, rightListener);
        dialog.setCloseBtnListener( closeListener );
        dialog.show();
        return dialog;
    }
}
