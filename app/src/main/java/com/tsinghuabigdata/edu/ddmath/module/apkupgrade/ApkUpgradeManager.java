package com.tsinghuabigdata.edu.ddmath.module.apkupgrade;

import android.app.Activity;
import android.content.Context;

import com.tsinghuabigdata.edu.ddmath.R;
import com.tsinghuabigdata.edu.ddmath.dialog.UpgradeDialog;
import com.tsinghuabigdata.edu.ddmath.dialog.UpgradeProgressDialog;
import com.tsinghuabigdata.edu.ddmath.util.ToastUtils;


/**
 * Created by 28205 on 2016/6/3.
 */
public class ApkUpgradeManager {

    private static final String CANCEL = "取消";
    private static final String EXIT = "退出";
    private static final String UPGRAGE_NOW = "立即更新";
    private String mPositiveText = UPGRAGE_NOW;
    private String mNegativeText = CANCEL;
    private String mNegativeTextForce = EXIT;

    private boolean mIsForceInstall;
    private ApkLoader mApkLoader;
    private String updateMessage;
    private DialogButtonListener mDiaBtListener;
    private CheckUpgradeListener mCheckUpgradeListener;

    //静态内部私有类
    private static class SingletonHolder {
        static final ApkUpgradeManager instance = new ApkUpgradeManager();
    }

    /**
     * 得到实例对象
     * @return
     */
    public static ApkUpgradeManager getInstance(){
        return SingletonHolder.instance;
    }

    private ApkUpgradeManager(){

    }

    public void setPositiveText(String positiveText) {
        this.mPositiveText = positiveText;
    }

    public void setNegativeText(String negativeText) {
        this.mNegativeText = negativeText;
    }

    public void setNegativeTextForce(String negativeTextForce) {
        this.mNegativeTextForce = negativeTextForce;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public void setDiaBtListener(DialogButtonListener diaBtListener) {
        this.mDiaBtListener = diaBtListener;
    }

    public void setCheckUpgradeListener(CheckUpgradeListener checkUpgradeListener) {
        this.mCheckUpgradeListener = checkUpgradeListener;
    }

    public ApkLoader getApkLoader() {
        return mApkLoader;
    }

    public void setApkLoader(ApkLoader apkLoader) {
        this.mApkLoader = apkLoader;
    }

    public void checkUpgradeInfo(Context context, Comparable src, Comparable dst){
        mIsForceInstall = mCheckUpgradeListener == null? false : mCheckUpgradeListener.isForceInstall();
        if (isNeedUpdage(src, dst)){
            showMyUpgradeDialog(context, mIsForceInstall);
        }
    }

    public  <T extends Comparable,T1 extends Comparable> boolean isNeedUpdage(T src, T1 dst){
        if (src.compareTo(dst) < 0){
            return true;
        }else {
            return false;
        }
    }

//    public void showUpgradeDialog(final Context context, final boolean isForce){
//        String negativeText = isForce ? mNegativeTextForce : mNegativeText;
//        AlertManager.show(context, updateMessage, mPositiveText, negativeText, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (mApkLoader == null){
//                    throw new RuntimeException("APKLoader is null error.");
//                }
//                if (mApkLoader.isDownloadComplete()) {
//                    // 检查到已下载了更新版本
//                    // 安装
//                    mApkLoader.install();
//                    mDiaBtListener.installAfter();
//                } else if (!mApkLoader.isDownloading()) {
//                    // 没有在下载
//                    mApkLoader.execute();
//                }
//            }
//        }, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //取消按钮操作
//                if (mDiaBtListener != null){
//                    mDiaBtListener.cancelAction(isForce);
//                }
//            }
//        });
//    }

    public void showMyUpgradeDialog(final Context context, final boolean isForce){
        UpgradeDialog upgradeDialog = new UpgradeDialog(context, R.style.dialog);
        upgradeDialog.setInforce(isForce);
        upgradeDialog.setInfo(updateMessage);
        upgradeDialog.setUpradeListener(new UpgradeDialog.UpradeListener() {
            @Override
            public void upradeNow() {
                if (mApkLoader == null){
                    throw new RuntimeException("APKLoader is null error.");
                }
                if (mApkLoader.isDownloadComplete()) {
                    // 检查到已下载了更新版本
                    // 安装
                    if( mApkLoader.isFullApk() ){
                        if( mApkLoader.install() ){
                            mDiaBtListener.installAfter();
                        }else{   //安装失败，
                            ToastUtils.show( context, "安装失败，请重新安装。" );
                        }
                    }else{
                        //apk 损坏,重新下载
                        mApkLoader.execute();
                        ToastUtils.show( context, "安装包异常，将重新下载。" );
                    }
                } else if (!mApkLoader.isDownloading()) {
                    // 没有在下载
                    mApkLoader.execute();
                }
            }

            @Override
            public void upradeAfter() {
                if (mDiaBtListener != null){
                    mDiaBtListener.cancelAction(isForce);
                }
            }
        });
        upgradeDialog.show();
    }

    public UpgradeProgressDialog initProgressDialog(Context context){
        /*ProgressDialog dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置水平进度条
        dialog.setCancelable(false);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        dialog.setTitle("正在更新");
        dialog.setMax(100);
        dialog.show();*/
        if( context instanceof Activity){
            Activity activity = (Activity)context;
            if( activity.isFinishing() || activity.isDestroyed() ) return null;
        }
        UpgradeProgressDialog progressDialog = new UpgradeProgressDialog(context, R.style.dialog);
        progressDialog.show();
        return progressDialog;
    }

    public interface DialogButtonListener{
        public void cancelAction(boolean isForce) ;
        public void installAfter();
    }
    public interface CheckUpgradeListener{
        public boolean isForceInstall() ;
    }
}
