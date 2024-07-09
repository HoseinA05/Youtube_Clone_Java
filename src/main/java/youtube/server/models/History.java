package youtube.server.models;

import java.sql.Timestamp;

public class History {
    private int id;
    private String username;
    private int userId;
    private int videoId;
    private String videoTitle;
    private String videoThumbnail;
    private Timestamp videoCreatedAt;
    private Timestamp watchedAt;

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

    public Timestamp getVideoCreatedAt() {
        return videoCreatedAt;
    }

    public void setVideoCreatedAt(Timestamp videoCreatedAt) {
        this.videoCreatedAt = videoCreatedAt;
    }

    public Timestamp getWatchedAt() {
        return watchedAt;
    }

    public void setWatchedAt(Timestamp watchedAt) {
        this.watchedAt = watchedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", userId=" + userId +
                ", videoId=" + videoId +
                ", videoTitle='" + videoTitle + '\'' +
                ", videoThumbnail='" + videoThumbnail + '\'' +
                ", videoCreatedAt=" + videoCreatedAt +
                ", watchedAt=" + watchedAt +
                '}';
    }
}
