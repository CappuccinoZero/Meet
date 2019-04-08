package com.lin.meet.main.fragment.Home;

public class VideoBean {
    private String url;
    private String authorName;
    private String headerImage;
    private int authorId;

    public VideoBean(){}
    public VideoBean(String url){setUrl(url);}
    public int getAuthorId() {
        return authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public String getUrl() {
        return url;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
