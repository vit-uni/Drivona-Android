package com.xuexiang.xupdate.entity;

/**
 * User:lenovo
 * Date:2023/2/28
 * Time:16:45
 * author:lzy
 */
public class UpAppDataBean {

    private int code;
    private String msg;
    private int updateStatus;
    private int versionCode;
    private String versionName;
    private String modifyContent;
    private String downloadUrl;
    private int apkSize;
    private String apkMd5;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getUpdateStatus() {
        return updateStatus;
    }

    public void setUpdateStatus(int UpdateStatus) {
        this.updateStatus = UpdateStatus;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int VersionCode) {
        this.versionCode = VersionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String VersionName) {
        this.versionName = VersionName;
    }

    public String getModifyContent() {
        return modifyContent;
    }

    public void setModifyContent(String ModifyContent) {
        this.modifyContent = ModifyContent;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String DownloadUrl) {
        this.downloadUrl = DownloadUrl;
    }

    public int getApkSize() {
        return apkSize;
    }

    public void setApkSize(int ApkSize) {
        this.apkSize = ApkSize;
    }

    public String getApkMd5() {
        return apkMd5;
    }

    public void setApkMd5(String ApkMd5) {
        this.apkMd5 = ApkMd5;
    }
}
