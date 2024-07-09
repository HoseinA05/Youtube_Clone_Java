package youtube.server.models;

import java.sql.Timestamp;

public class Notification {
    public enum NotificationTypes {
        NEW_UNSUBSCRIBED,
        NEW_SUBSCRIBER,
        NEW_VIDEO_LIKE, // when user video gets liked/disliked
        NEW_COMMENT_LIKE, // when user comment/reply gets liked/disliked
        NEW_COMMENT, // when user video gets new comment/reply
        NEW_REPLY_TO_COMMENT, // when user comment gets replied
        NEW_VIDEO_FROM_SUBSCRIBING,
        USER_ADDED_AS_COLLABORATOR,
        USER_REMOVED_AS_COLLABORATOR,
        USER_VIDEO_ADDED_TO_UNOWNED_PLAYLIST,
        ANOTHER_COLLABORATOR_ADDED_VIDEO_TO_USER_PLAYLIST,
        // USER_VIDEO_EDITED_FROM_UNOWNED_PLAYLIST,
        // ANOTHER_COLLABORATOR_EDITED_VIDEO_FROM_USER_PLAYLIST,
        USER_VIDEO_REMOVED_FROM_UNOWNED_PLAYLIST,
        ANOTHER_COLLABORATOR_REMOVED_VIDEO_FROM_USER_PLAYLIST,
    }

    private static NotificationTypes[] nTypes;

    static {
        nTypes = NotificationTypes.values();
    }

    public static NotificationTypes getIntToType(int i) {
        return nTypes[i];
    }


    private int ownerId;
    private int ownerUserId;
    private NotificationTypes type;
    private String message;
    private int userId;
    private int videoId;
    private int commentId;
    private int playlistId;
    private boolean isSeen;
    private Timestamp createdAt;
    // extra
    // private String userUsername;
    // private String videoUsername;
    // private String videoTitle;
    // private String commentUsername;

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public NotificationTypes getType() {
        return type;
    }

    public void setType(NotificationTypes type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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


    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

}
