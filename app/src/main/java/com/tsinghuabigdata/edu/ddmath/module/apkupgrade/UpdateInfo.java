package com.tsinghuabigdata.edu.ddmath.module.apkupgrade;


import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by 28205 on 2016/6/6.
 */
public class UpdateInfo implements Serializable, Comparable<UpdateInfo> {
    private static final long serialVersionUID = 1425716862853095439L;
    /**
     * appName	String	APP名称
     * version	String	当前版本号，如：2.5.4
     * versionCode	int	当前版本号唯一编号，用户端根据此值来比较版本号，如：266
     * requiredVersion	String	必须升级的最大版本号（根据requiredVersionCode的值），如：
     * 此值为2.5.2，则2.5.2即以下版本必须升级，2.5.3可选升级
     * requiredVersionCode	int	同versionCode，requiredVersion对应的唯一编号
     * updateDesc	String	更新内容说明，多条以#%#分隔
     * fileSize	int	当前版本文件大小，以字节为单位
     * MD5	String	当前版本md5
     * filePath	String	更新文件下载地址（绝对路径）：
     * http://upgrade.iclassedu.com/doudou/upgrade/student/ddapp_v2.5.4_266.apk
     */
    private String appName;
    private String version;
    private int versionCode;
    private String requiredVersion;
    private int requiredVersionCode;
    private String updateDesc;
    private int fileSize;
    private String MD5;
    private String filePath;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getRequiredVersion() {
        return requiredVersion;
    }

    public void setRequiredVersion(String requiredVersion) {
        this.requiredVersion = requiredVersion;
    }

    public int getRequiredVersionCode() {
        return requiredVersionCode;
    }

    public void setRequiredVersionCode(int requiredVersionCode) {
        this.requiredVersionCode = requiredVersionCode;
    }

    public String getUpdateDesc() {
        return updateDesc;
    }

    public void setUpdateDesc(String updateDesc) {
        this.updateDesc = updateDesc;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public String getMD5() {
        return MD5;
    }

    public void setMD5(String MD5) {
        this.MD5 = MD5;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public int compareTo(@NonNull UpdateInfo another) {
        //UpdateInfo u1 = (UpdateInfo) another;
        if (another.versionCode > this.versionCode) {
            return -1;
        }
        return 0;
    }

}