package com.lin.meet.main.fragment.Home;

public class PicturesBean {
    private float scale;
    private String path;
    private int label;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(int label) {
        this.label = label;
    }

    public PicturesBean(String path, int label){
        this.path = path;
        this.label = label;
        scale = 0;
    }
}
