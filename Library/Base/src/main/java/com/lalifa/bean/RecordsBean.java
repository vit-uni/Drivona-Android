package com.lalifa.bean;

import android.graphics.Color;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * @author ： I5
 * time    ： 2021/1/30
 * package_name : com.benben.guohuo.ui.mine.bean
 * usefuless    : guochao
 */
public class RecordsBean  implements Serializable, InsertData {
    /**
     * noticedUserIs : 784412910121259008
     * isfocus : 2
     * sex : 0
     * nickname : 用户006
     * avatar :
     * id : 786269261856378880
     */

    private String noticedUserIs;
    private int isfocus;//1已关注 3已回粉
    private int sex;
    private String nickname;
    private String avatar;
    private String id;
    private String createTime;
    private int isfans;

    /**
     * topicId : 32d1db5dc850a89c254c621b609e45b4
     * topicImg : 802590375733760000.jpg
     * topicName : 潮流生活打卡
     * topicNum : 0
     * unreadNum : 3.3738305668118005E15
     */

    private String topicId;
    private String topicImg;
    private String topicName;
    private int topicNum;
    @JSONField(name = "unreadNum")
    private double unreadNumX;

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getIsfans() {
        return isfans;
    }

    public void setIsfans(int isfans) {
        this.isfans = isfans;
    }

    public String getNoticedUserIs() {
        return noticedUserIs;
    }

    public void setNoticedUserIs(String noticedUserIs) {
        this.noticedUserIs = noticedUserIs;
    }

    public int getIsfocus() {
        return isfocus;
    }

    public void setIsfocus(int isfocus) {
        this.isfocus = isfocus;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String showText() {
        return "@" + nickname + " ";
    }

    @Override
    public String uploadFormatText() {
        final String USER_FORMART = "{[%s, %s]}";
        return String.format(USER_FORMART, "@" + nickname, id);
    }

    @Override
    public int color() {
        return Color.parseColor("#4ACFFf");
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getTopicImg() {
        return topicImg;
    }

    public void setTopicImg(String topicImg) {
        this.topicImg = topicImg;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public int getTopicNum() {
        return topicNum;
    }

    public void setTopicNum(int topicNum) {
        this.topicNum = topicNum;
    }


    /**
     * 喜欢和赞
     * avatar : https://guohuoapp.oss-cn-shanghai.aliyuncs.com/20210118/1610974295291.jpg
     * commentPid : 7ACA
     * content : 喜欢你的动态
     * createTime : 2021-01-26 20:28:35
     * foreignId : 801046897312796672
     * imgUrl : https://guohuoapp.oss-cn-shanghai.aliyuncs.com/20210119/1611026061802.jpg,https://guohuoapp.oss-cn-shanghai.aliyuncs.com/20210119/1611026062160.jpg,https://guohuoapp.oss-cn-shanghai.aliyuncs.com/20210119/1611026062503.jpg
     * likeType : 1
     * replyCommentId : R$IV
     * replyLevel : -3943704293394832
     * title : 小H
     * type : 5
     * unreadNum : 3.3738305668118005E15
     * videoUrl : D82zji
     * zgtype : -3057288031143776
     */

    private String commentPid;
    private String content;
    private String foreignId;
    private String imgUrl;
    /**
     *  1动态2资讯
     */
    private int likeType;
    private String replyCommentId;
    private long replyLevel;
    private String title;
    private int type;
    private double unreadNum;
    private String videoUrl;
    private long zgtype;

    public String getCommentPid() {
        return commentPid;
    }

    public void setCommentPid(String commentPid) {
        this.commentPid = commentPid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getForeignId() {
        return foreignId;
    }

    public void setForeignId(String foreignId) {
        this.foreignId = foreignId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getLikeType() {
        return likeType;
    }

    public void setLikeType(int likeType) {
        this.likeType = likeType;
    }

    public String getReplyCommentId() {
        return replyCommentId;
    }

    public void setReplyCommentId(String replyCommentId) {
        this.replyCommentId = replyCommentId;
    }

    public long getReplyLevel() {
        return replyLevel;
    }

    public void setReplyLevel(long replyLevel) {
        this.replyLevel = replyLevel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getUnreadNum() {
        return unreadNum;
    }

    public void setUnreadNum(double unreadNum) {
        this.unreadNum = unreadNum;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public long getZgtype() {
        return zgtype;
    }

    public void setZgtype(long zgtype) {
        this.zgtype = zgtype;
    }

    public double getUnreadNumX() {
        return unreadNumX;
    }

    public void setUnreadNumX(double unreadNumX) {
        this.unreadNumX = unreadNumX;
    }

}
