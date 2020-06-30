package com.lhzw.searchlocmap.bean.multicheck;

public class ChildExpandBean {
    private boolean isBinding;
    private String name;
    private String register;
    private int icon;
    private boolean isChecked;
    private boolean isVisible;
    private int offset;
    public ChildExpandBean() {
        super();
    }

    public ChildExpandBean(boolean isBinding, String name, String register, int icon, boolean isChecked, boolean isVisible, int offset) {
        this.isBinding = isBinding;
        this.name = name;
        this.register = register;
        this.icon = icon;
        this.isChecked = isChecked;
        this.isVisible = isVisible;
        this.offset = offset;
    }

    public boolean isBinding() {
        return isBinding;
    }

    public void setBinding(boolean binding) {
        isBinding = binding;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }
}
