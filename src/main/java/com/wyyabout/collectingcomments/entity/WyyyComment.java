package com.wyyabout.collectingcomments.entity;


import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table(name = "wyyy_comment")
public class WyyyComment {
    @Id
    private String id;
    private String masterId;
    private String songListId;
    private String userId;
    private String songId;
    private int pageIndex;
    @Transient
    private WyyyUser user;
    private String userName;
    private String beReplied;
    private String pendantData;
    private String likedCount;
    private String expressionUrl;
    private String commentId;
    private String liked;
    private String time;
    private String content;
    private String isRemoveHotComment;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMasterId() {
        return masterId;
    }

    public void setMasterId(String masterId) {
        this.masterId = masterId;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSongListId() {
        return songListId;
    }

    public void setSongListId(String songListId) {
        this.songListId = songListId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public WyyyUser getUser() {
        return user;
    }

    public void setUser(WyyyUser user) {
        this.user = user;
    }

    public String getBeReplied() {
        return beReplied;
    }

    public void setBeReplied(String beReplied) {
        this.beReplied = beReplied;
    }

    public String getPendantData() {
        return pendantData;
    }

    public void setPendantData(String pendantData) {
        this.pendantData = pendantData;
    }

    public String getLikedCount() {
        return likedCount;
    }

    public void setLikedCount(String likedCount) {
        this.likedCount = likedCount;
    }

    public String getExpressionUrl() {
        return expressionUrl;
    }

    public void setExpressionUrl(String expressionUrl) {
        this.expressionUrl = expressionUrl;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getLiked() {
        return liked;
    }

    public void setLiked(String liked) {
        this.liked = liked;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsRemoveHotComment() {
        return isRemoveHotComment;
    }

    public void setIsRemoveHotComment(String isRemoveHotComment) {
        this.isRemoveHotComment = isRemoveHotComment;
    }
}
