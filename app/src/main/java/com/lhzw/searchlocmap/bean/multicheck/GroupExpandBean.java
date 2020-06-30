package com.lhzw.searchlocmap.bean.multicheck;

public class GroupExpandBean {
    private boolean isExpand;
    private String orgName;
    private String mac;
    private int icon;
    private boolean isVisible;

    public GroupExpandBean() {
        super();
    }

    public GroupExpandBean(boolean isExpand, String orgName, String mac, int icon, boolean isVisible) {
        this.isExpand = isExpand;
        this.orgName = orgName;
        this.mac = mac;
        this.icon = icon;
        this.isVisible = isVisible;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
