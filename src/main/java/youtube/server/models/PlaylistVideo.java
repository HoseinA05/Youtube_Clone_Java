package youtube.server.models;

import java.sql.Timestamp;

public class PlaylistVideo {
    private int PlaylistId;
    private int videoId;
    private int Order;
    private int adderUserID;
    private String adderUsername;
    private Timestamp addedAt;
    // extra columns from other tables
    private int videoViewCount;
    private String videoThumbnail;
    private String videoTitle;
    private Timestamp videoCreatedAt;
    private String videoCreator;
    private int videoCreatorUserId;

    public int getPlaylistId() {
        return PlaylistId;
    }

    public void setPlaylistId(int playlistId) {
        PlaylistId = playlistId;
    }

    public int getVideoId() {
        return videoId;
    }

    public void setVideoId(int videoId) {
        this.videoId = videoId;
    }

    public int getOrder() {
        return Order;
    }

    public void setOrder(int order) {
        Order = order;
    }

    public int getVideoViewCount() {
        return videoViewCount;
    }

    public void setVideoViewCount(int videoViewCount) {
        this.videoViewCount = videoViewCount;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public Timestamp getVideoCreatedAt() {
        return videoCreatedAt;
    }

    public void setVideoCreatedAt(Timestamp videoCreatedAt) {
        this.videoCreatedAt = videoCreatedAt;
    }

    public String getVideoCreator() {
        return videoCreator;
    }

    public void setVideoCreator(String videoCreator) {
        this.videoCreator = videoCreator;
    }

    public int getAdderUserID() {
        return adderUserID;
    }

    public void setAdderUserID(int adderUserID) {
        this.adderUserID = adderUserID;
    }

    public String getAdderUsername() {
        return adderUsername;
    }

    public void setAdderUsername(String adderUsername) {
        this.adderUsername = adderUsername;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }

    @Override
    public String toString() {
        return "PlaylistVideo{" +
                "PlaylistId=" + PlaylistId +
                ", videoId=" + videoId +
                ", Order=" + Order +
                ", adderUserID=" + adderUserID +
                ", adderUsername='" + adderUsername + '\'' +
                ", addedAt=" + addedAt +
                ", videoViewCount=" + videoViewCount +
                ", videoThumbnail='" + videoThumbnail + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", videoCreatedAt=" + videoCreatedAt +
                ", videoCreator='" + videoCreator + '\'' +
                '}';
    }

    public int getVideoCreatorUserId() {
        return videoCreatorUserId;
    }

    public void setVideoCreatorUserId(int videoCreatorUserId) {
        this.videoCreatorUserId = videoCreatorUserId;
    }
}