package com.tsinghuabigdata.edu.ddmath.module.upgrade;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.tsinghuabigdata.edu.ddmath.MVPModel.RequestListener;
import com.tsinghuabigdata.edu.ddmath.commons.http.HttpResponse;
import com.tsinghuabigdata.edu.ddmath.dialog.UpgradeProgressDialog;
import com.tsinghuabigdata.edu.ddmath.module.apkupgrade.ApkLoader;
import com.tsinghuabigdata.edu.ddmath.module.apkupgrade.ApkUpgradeManager;
import com.tsinghuabigdata.edu.ddmath.module.apkupgrade.DownProgressObserver;
import com.tsinghuabigdata.edu.ddmath.module.apkupgrade.UpdateInfo;
import com.tsinghuabigdata.edu.ddmath.module.login.model.LoginModel;
import com.tsinghuabigdata.edu.ddmath.util.AppLog;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.LogUtils;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;


public class UpgradeManager {

    private Context mContext;
    private UpgradeProgressDialog mUpgradeProgressDialog;
    //private DownProgressObserver downloadObserve;

    private String versionName = "";
    private int versionCode = 0;
    public UpgradeManager( Context context){
        mContext = context;
        PackageInfo packageInfo = AppUtils.getPackageInfo(mContext);
        if( packageInfo!=null ){
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        }
    }

    public void checkApkUpdate() {
        new LoginModel().checkApkupdate(new RequestListener<UpdateInfo>() {
            @Override
            public void onSuccess(UpdateInfo res) {
                LogUtils.i("checkApkUpdate onSuccess");
                startLoadApk(res);
            }

            @Override
            public void onFail(HttpResponse response, Exception ex) {
                LogUtils.i("checkApkUpdate onFail ex:" + ex.getMessage());
            }
        });
    }

    private void startLoadApk(UpdateInfo latesApkUpdateinfo) {
        if (latesApkUpdateinfo == null) {
            return;
        }

        UpdateInfo currentApkVersionInfo;
        currentApkVersionInfo = new UpdateInfo();
        currentApkVersionInfo.setVersion(versionName);
        currentApkVersionInfo.setVersionCode(versionCode);

        ApkUpgradeManager upgradeManager = ApkUpgradeManager.getInstance();
        if (!upgradeManager.isNeedUpdage(currentApkVersionInfo, latesApkUpdateinfo)) {
            return;
        }
        LogUtils.i("start load apk");
        //取APK URL
        String mApkUpdateUrl = latesApkUpdateinfo.getFilePath();

        //first 构建dialog提示信息
        String dstVer = latesApkUpdateinfo.getVersion();
        String destVerCode = latesApkUpdateinfo.getVersionCode() + "";
        String upgradeDesc = latesApkUpdateinfo.getUpdateDesc();
        String updateMessage = "有新版本了！版本号" + dstVer + "   \n更新内容:" + upgradeDesc;

        upgradeManager.setUpdateMessage(updateMessage);

        //second
        final ApkLoader apkLoader = new ApkLoader(mContext, mApkUpdateUrl, "apk", latesApkUpdateinfo);
        apkLoader.setNotifyTitle(AppUtils.getAppName());
        apkLoader.setNotifyDesc("正在下载更新包,请耐心等待下载完成.");
        apkLoader.setApkName("doudoumath" + "_" + dstVer + "_" + destVerCode + ".apk");
        upgradeManager.setApkLoader(apkLoader);
        final boolean force = versionCode < latesApkUpdateinfo.getRequiredVersionCode();
        //LogUtils.i("versionCode=" + AppUtils.getPackageInfo(mContext).versionCode + " getRequiredVersionCode=" + latesApkUpdateinfo.getRequiredVersionCode());
        upgradeManager.setCheckUpgradeListener(new ApkUpgradeManager.CheckUpgradeListener() {
            @Override
            public boolean isForceInstall() {
                //返回true则dialog取消变回退出， 只改变对话框取消按钮的文字
                AppLog.i("是否强制安装方法被调用");
                return force;
            }
        });
        upgradeManager.setDiaBtListener(new ApkUpgradeManager.DialogButtonListener() {
            @Override
            public void cancelAction(boolean isForce) {
                LogUtils.i("cancelAction");
                if (mUpgradeProgressDialog != null && mUpgradeProgressDialog.isShowing()) {
                    mUpgradeProgressDialog.dismiss();
                }
            }

            @Override
            public void installAfter() {
                LogUtils.i("installAfter 下载完成后被调用");
                if (mUpgradeProgressDialog != null) {
                    mUpgradeProgressDialog.setAfterDownload();
                }
            }
        });

        //初始化进度条
        // third
        mUpgradeProgressDialog = upgradeManager.initProgressDialog( mContext );
        if( mUpgradeProgressDialog == null )
            return;
        mUpgradeProgressDialog.setForce(force);
        mUpgradeProgressDialog.setUpgradeProgressListener(new UpgradeProgressDialog.UpgradeProgressListener() {
            @Override
            public void install() {
                if (apkLoader.isFullApk()) {
                    apkLoader.install();
                } else { //Apk文件损坏，重新下载
                    apkLoader.execute();
                    ToastUtils.show(mContext, "安装包异常，将重新下载。");
                }
            }
        });
        DownProgressObserver downloadObserve = new DownProgressObserver(null, mContext, mUpgradeProgressDialog, apkLoader);
        downloadObserve.registerObserver();

        //start load and instal
        upgradeManager.checkUpgradeInfo(mContext, currentApkVersionInfo, latesApkUpdateinfo);
    }

}
