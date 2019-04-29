package com.lin.meet.bean;

import cn.bmob.v3.BmobObject;

public class recommentBean{
    public static class recomment_main extends BmobObject{
        private String uri = "";
        private int comment = 0;//cancel
        private int like = 0;//cancel

        public String getUri() {
            return uri;
        }

        public int getComment() {
            return comment;
        }

        public int getLike() {
            return like;
        }

        public recomment_main setComment(int comment) {
            this.comment = comment;
            return this;
        }

        public recomment_main setLike(int like) {
            this.like = like;
            return this;
        }

        public recomment_main setUri(String uri) {
            this.uri = uri;
            return this;
        }
    }

    public static class recomment_comment extends BmobObject{
        private String uri = "";
        private int floor = 0;
        private int id = 0;
        private String content = "";
        private int comment = 0;
        private String uid = "";
        private int like = 0;
        private String reply_uid1;
        private String reply_uid2;
        private String content1 = "";
        private String content2 = "";
        public recomment_comment setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public recomment_comment setLike(int like) {
            this.like = like;
            return this;
        }

        public recomment_comment setComment(int comment) {
            this.comment = comment;
            return this;
        }

        public int getLike() {
            return like;
        }

        public int getComment() {
            return comment;
        }

        public String getUri() {
            return uri;
        }

        public recomment_comment setUid(String uid) {
            this.uid = uid;
            return this;
        }

        public recomment_comment setContent(String content) {
            this.content = content;
            return this;
        }

        public String getContent() {
            return content;
        }

        public recomment_comment setId(int id) {
            this.id = id;
            return this;
        }

        public int getId() {
            return id;
        }

        public int getFloor() {
            return floor;
        }

        public String getUid() {
            return uid;
        }

        public recomment_comment setFloor(int floor) {
            this.floor = floor;
            return this;
        }

        public String getReply_uid1() {
            return reply_uid1;
        }

        public String getReply_uid2() {
            return reply_uid2;
        }

        public recomment_comment setReply_uid1(String reply_uid1) {
            this.reply_uid1 = reply_uid1;
            return this;
        }

        public recomment_comment setReply_uid2(String reply_uid2) {
            this.reply_uid2 = reply_uid2;
            return this;
        }

        public String getContent1() {
            return content1;
        }

        public String getContent2() {
            return content2;
        }

        public recomment_comment setContent1(String content1) {
            this.content1 = content1;
            return this;
        }

        public recomment_comment setContent2(String content2) {
            this.content2 = content2;
            return this;
        }
    }

    public static class recomment_islike extends BmobObject{
        private String uid = "";
        private String uri = "";
        private boolean islike = false;

        public String getUid() {
            return uid;

        }

        public recomment_islike setUid(String uid) {
            this.uid = uid;
            return this;
        }

        public String getUri() {
            return uri;
        }

        public recomment_islike setUri(String uri) {
            this.uri = uri;
            return this;
        }

        public recomment_islike setIslike(boolean islike) {
            this.islike = islike;
            return this;
        }

        public boolean getIslike() {
            return islike;
        }
    }
}
