package com.tsinghuabigdata.edu.ddmath.module.apkupgrade;

import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.tsinghuabigdata.edu.ddmath.dialog.UpgradeProgressDialog;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;

import java.util.Arrays;

/**
 * Created by 28205 on 2016/6/7.
 */
public class DownProgressObserver extends ContentObserver {
    public static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    private UpgradeProgressDialog dialog;
    private Context               mContext;
    private ApkLoader             mApkLoader;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public DownProgressObserver(Handler handler, Context context, UpgradeProgressDialog dialog, ApkLoader apkLoader) {
        super(handler);
        this.dialog = dialog;
        mContext = context;
        this.mApkLoader = apkLoader;
    }

    @Override
    public void onChange(boolean selfChange) {
        int[] res = mApkLoader.queryDownloadProgress();
        if (res != null && res[1] != 0) {
            int progress = (int) (1f * res[0] / res[1] * 100);
            dialog.setProgress(progress);
            dialog.setMessage(progress + "%");
            if (progress == 100) {
                //下载完成后执行
                if( mApkLoader.isFullApk() ){
                    mApkLoader.install();
                    dialog.setAfterDownload();
                }else{ //Apk文件损坏，重新下载
                    mApkLoader.execute();
                    ToastUtils.show( mContext, "安装包异常，将重新下载。" );
                }
                //dialog.dismiss();
            }
            AppLog.i(Arrays.toString(res) + " " + progress);
        } else {
            AppLog.w("apk download", "返回下载进度为null");
        }
    }

    public void registerObserver() {
        mContext.getContentResolver().registerContentObserver(CONTENT_URI, true, this);
    }

    public void unregisterObserver() {
        mContext.getContentResolver().unregisterContentObserver(this);
    }
}