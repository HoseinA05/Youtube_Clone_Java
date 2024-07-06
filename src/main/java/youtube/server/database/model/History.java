package youtube.server.database.model;

import java.sql.Timestamp;

public class History {
    private String username;
    private int userId;
    private int videoId;
    private String videoTitle;
    private String videoThumbnail;
    private Timestamp createdAt;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "History{" +
                "username='" + username + '\'' +
                ", userId=" + userId +
                ", videoId=" + videoId +
                ", videoTitle='" + videoTitle + '\'' +
                ", videoThumbnail='" + videoThumbnail + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
