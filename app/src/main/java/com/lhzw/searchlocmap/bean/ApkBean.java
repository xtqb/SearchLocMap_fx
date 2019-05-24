package com.lhzw.searchlocmap.bean;

public class ApkBean {
    private int id;
    private String appCode;
    private String appType;
    private int versionCode;
    private int attachmentId;
    private String versionName;
    private String createTime;

    public ApkBean(int id, String appCode, String appType, int versionCode, int attachmentId, String versionName, String createTime) {
        this.id = id;
        this.appCode = appCode;
        this.appType = appType;
        this.versionCode = versionCode;
        this.attachmentId = attachmentId;
        this.versionName = versionName;
        this.createTime = createTime;
    }

    public ApkBean() { super();
    }

    public int getId() {
        return id;
    }

    public String getAppCode() {
        return appCode;
    }

    public String getAppType() {
        return appType;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public int getAttachmentId() {
        return attachmentId;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public void setAttachmentId(int attachmentId) {
        this.attachmentId = attachmentId;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
