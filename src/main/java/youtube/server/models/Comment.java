package youtube.server.models;

import java.sql.Timestamp;

public class Comment {
    private int id;
    private String text;
    private int userId;
    private int videoId;
    private int commentId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    /// extra columns
    private int repliesCount;
    private int likesCount;
    private int dislikesCount;
    private String replyUsername;
    private String userName;
    private LikeState CurrentUserLike;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
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

    public int getRepliesCount() {
        return repliesCount;
    }

    public void setRepliesCount(int repliesCount) {
        this.repliesCount = repliesCount;
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

    public String getReplyUsername() {
        return replyUsername;
    }

    public void setReplyUsername(String replyUsername) {
        this.replyUsername = replyUsername;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LikeState getCurrentUserLike() {
        return CurrentUserLike;
    }

    public void setCurrentUserLike(LikeState currentUserLike) {
        CurrentUserLike = currentUserLike;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", userId=" + userId +
                ", videoId=" + videoId +
                ", commentId=" + commentId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", repliesCount=" + repliesCount +
                ", likesCount=" + likesCount +
                ", dislikesCount=" + dislikesCount +
                ", replyUsername='" + replyUsername + '\'' +
                ", userName='" + userName + '\'' +
                ", CurrentUserLike=" + CurrentUserLike +
                '}';
    }
}
