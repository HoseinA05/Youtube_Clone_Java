package youtube.server.database.model;

import java.sql.Time;
import java.sql.Timestamp;

public class User {
    private int id;
    private String username;
    private String email;
    private String hashedPassword;
    private String aboutMe;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String profilePhotoPath;
    private String channelPhotoPath;
    /// extra columns
    private int totalViews;
    private int subscribers;
    private int subscribings;
    private int uploadCount;
    private boolean isCurrentUserSubscribed;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", hashedPassword='" + hashedPassword + '\'' +
                ", aboutMe='" + aboutMe + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", profilePhotoPath='" + profilePhotoPath + '\'' +
                ", channelPhotoPath='" + channelPhotoPath + '\'' +
                ", totalViews=" + totalViews +
                ", subscribers=" + subscribers +
                ", subscribings=" + subscribings +
                ", uploadCount=" + uploadCount +
                ", isCurrentUserSubscribed=" + isCurrentUserSubscribed +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
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

    public String getProfilePhotoPath() {
        return profilePhotoPath;
    }

    public void setProfilePhotoPath(String profilePhotoPath) {
        this.profilePhotoPath = profilePhotoPath;
    }

    public String getChannelPhotoPath() {
        return channelPhotoPath;
    }

    public void setChannelPhotoPath(String channelPhotoPath) {
        this.channelPhotoPath = channelPhotoPath;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(int subscribers) {
        this.subscribers = subscribers;
    }

    public int getSubscribings() {
        return subscribings;
    }

    public void setSubscribings(int subscribings) {
        this.subscribings = subscribings;
    }

    public int getUploadCount() {
        return uploadCount;
    }

    public void setUploadCount(int uploadCount) {
        this.uploadCount = uploadCount;
    }

    public boolean isCurrentUserSubscribed() {
        return isCurrentUserSubscribed;
    }

    public void setCurrentUserSubscribed(boolean currentUserSubscribed) {
        isCurrentUserSubscribed = currentUserSubscribed;
    }
}
