package com.example.ecg_vr.myTools;

public class ListBean {
    private int resource;
    private String title;

    /**
     * 设置图标与菜单名称
     * @param resource 图标资源ID如： R.mipmap.menu1
     * @param title 菜单名称 String
     */
    public ListBean(int resource, String title) {
        this.resource = resource;
        this.title = title;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
