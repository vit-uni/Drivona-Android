package com.lalifa.bean;


/**
 * Create by peng on 2020/11/13
 */
public class FormatRangBean extends RangBean {
    private String uploadFormatText;
    private String nickname;
    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public FormatRangBean(int from, int to) {
        super(from, to);
    }

    public String getUploadFormatText() {
        return uploadFormatText;
    }

    public void setUploadFormatText(String uploadFormatText) {
        this.uploadFormatText = uploadFormatText;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

}
