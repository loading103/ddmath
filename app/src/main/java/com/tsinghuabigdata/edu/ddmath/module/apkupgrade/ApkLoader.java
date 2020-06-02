package com.tsinghuabigdata.edu.ddmath.module.apkupgrade;


import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.tsinghuabigdata.edu.ddmath.module.neteaseim.base.util.string.MD5;
import com.tsinghuabigdata.edu.ddmath.util.AppUtils;
import com.tsinghuabigdata.edu.ddmath.util.DirUtils;
import com.tsinghuabigdata.edu.ddmath.util.FileUtil;
import com.tsinghuabigdata.edu.ddmath.util.PreferencesUtils;

import java.io.File;

/**
 * <h1>apk更新管理类</h1>
 * <p<使用API 9添加的DownloadManager类下载,下载完成后在任务栏点击可以进行安装</p>
 *
 * @author YS
 * @version 1.0
 */
public class ApkLoader {
    /**
     * 下载ID
     */
    public static final String APK_DOWNLOAD_ID = "apkDownloadId";

    /**
     * 上下文
     */
    private Context context;

    /**
     * 下载URL
     */
    private String url;

    /**
     * 通知栏标题
     */
    private String notifyTitle;

    /**
     * 通知栏描述
     */
    private String notifyDesc;

    /**
     * 下载ID保存字段
     */
    private String downloadIdField;

    /**
     * 下载目标目录
     */
    private String destPath;

    /**
     * 下载保存Apk文件名
     */
    private String apkName = "";

    /**
     * 下载管理对象
     */
    private DownloadManager downloadManager;

    private Request request;

    private UpdateInfo mUpdateInfo;

    /**
     * 构造函数，形参不能为空
     *
     * @param context
     * @param url
     */
    public ApkLoader(Context context, String url, String apkSaveDir, UpdateInfo updateInfo ) {
        this.context = context;
        this.url = url;
        this.mUpdateInfo = updateInfo;

        // 保存本地信息时，使用当前版本号分割
        String currentVersion = AppUtils.getPackageInfo(context).versionName;
        this.downloadIdField = APK_DOWNLOAD_ID + "_" + currentVersion;
        this.downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
        // 保存文件目录以目标版本分割
        this.destPath = apkSaveDir;
    }

    public void setNotifyTitle(String notificationTitle) {
        this.notifyTitle = notificationTitle;
    }

    public void setNotifyDesc(String notificationDescription) {
        this.notifyDesc = notificationDescription;
    }

    public void setApkName(String apkName) {
        this.apkName = apkName;
    }

    /**
     * 执行下载
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void execute() {
        //清除已下载的内容重新下载
        long downloadId = PreferencesUtils.getLong(context, downloadIdField);
        if (downloadId != -1) {
            downloadManager.remove(downloadId);
            PreferencesUtils.removeSharedPreferenceByKey(context, downloadIdField);
        }

        request = new Request(Uri.parse(url));
        //设置Notification中显示的文字
        request.setTitle(notifyTitle);
        request.setDescription(notifyDesc);
        //设置可用的网络类型
        request.setAllowedNetworkTypes(Request.NETWORK_MOBILE | Request.NETWORK_WIFI);
        //设置状态栏中显示Notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
        }
        //不显示下载界面
        request.setVisibleInDownloadsUi(true);
        request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //设置下载后文件存放的位置
        request.setDestinationInExternalFilesDir(context, destPath, apkName);
//        request.setDestinationInExternalPublicDir(destPath, apkName);
        //设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        if(!TextUtils.isEmpty(mimeString))request.setMimeType(mimeString);
        //保存返回唯一的downloadId
        PreferencesUtils.putLong(context, downloadIdField, downloadManager.enqueue(request));
    }

    /**
     * 是否正在下载
     *
     * @return
     */
    public boolean isDownloading() {
        long downloadId = PreferencesUtils.getLong(context, downloadIdField);
        if (downloadId != -1) {
            return queryDownloadStatus(downloadManager, downloadId) == DownloadManager.STATUS_RUNNING;
        }
        return false;
    }

    /**
     * 是否下载完成
     *
     * @return
     */
    public boolean isDownloadComplete() {
        long downloadId = PreferencesUtils.getLong(context, downloadIdField);
        if (downloadId != -1) {
            return queryDownloadStatus(downloadManager, downloadId) == DownloadManager.STATUS_SUCCESSFUL && exisits(getApkpath());
        }
        return false;
    }

    /**
     * 文件是否无损
     */
    public boolean isFullApk(){
        return mUpdateInfo!=null&&mUpdateInfo.getMD5().equalsIgnoreCase(MD5.getFileMD5( getApkpath() ));
    }

    /**
     * 文件是否存在
     *
     * @param destPath
     * @return
     */
    private boolean exisits(String destPath) {
        return new File(destPath).exists();
    }

    /**
     * 查询下载状态
     */
    private int queryDownloadStatus(DownloadManager downloadManager, long downloadId) {
        int result = -1;
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                result = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    /**
     * 查询下载进度
     *
     * @return
     */
    public int[] queryDownloadProgress() {
        long downloadId = PreferencesUtils.getLong(context, downloadIdField);
        if (downloadId == -1) {
            return null;
        }
        int[] result = new int[2];
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor c = null;
        try {
            c = downloadManager.query(query);
            if (c != null && c.moveToFirst()) {
                result[0] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                result[1] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
            }
        } finally {
            if (c != null) {
                c.close();
            }
        }
        return result;
    }

    /**
     * install app
     * @return whether apk exist
     */
    private boolean install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        File file = new File(filePath);
        if ( file.length() > 0 && file.exists() && file.isFile()) {
            Uri uri = FileUtil.uriFromFile( context, file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // 给目标应用一个临时授权
                i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            i.setDataAndType( uri, "application/vnd.android.package-archive");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            return true;
        }
        return false;
    }

    /**
     * 安装
     *
     * @return
     */
    public boolean install() {
        return install(context, getApkpath());
    }

    /**
     * 获取Apk保存路径
     *
     * @return
     */
    private String getApkpath() {
        String apkFilePath = new StringBuilder(DirUtils.getExternalPrivateFileDir(context,"")).append(destPath).append(File.separator)
                .append(apkName).toString();
        return apkFilePath;
    }
}
