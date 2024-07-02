package youtube.server.database.model;

import java.sql.Timestamp;

public class Video {
    private int   id;
    private String title;
    private String description;
    private String videoPath;
    private int   userId;
    private Timestamp createdAt;
    private Timestamp  updatedAt;
    private int  viewsCount;
    /// extra columns
    private String userName;
    private String tags;  // comma seperated
    private int  userSubscribesCount;
    private int  likesCount;
    private int  dislikesCount;
    private int  commentsCount;
    private LikeState currentUserLike;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getUserSubscribesCount() {
        return userSubscribesCount;
    }

    public void setUserSubscribesCount(int userSubscribesCount) {
        this.userSubscribesCount = userSubscribesCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public LikeState getCurrentUserLike() {
        return currentUserLike;
    }

    public void setCurrentUserLike(LikeState currentUserLike) {
        this.currentUserLike = currentUserLike;
    }
}
