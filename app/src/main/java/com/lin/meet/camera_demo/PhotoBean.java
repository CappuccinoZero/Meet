package com.lin.meet.camera_demo;


import org.litepal.crud.DataSupport;

public class PhotoBean extends DataSupport {
    private String path;
    private String maybe;
    private long time;
    private String location;

    public String getMaybe() {
        return maybe;
    }

    public String getLocation() {
        return location;
    }

    public long getTime() {
        return time;
    }

    public String getPath() {
        return path;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setMaybe(String maybe) {
        this.maybe = maybe;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
