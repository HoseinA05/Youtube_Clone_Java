package youtube.server.models;

import youtube.server.database.DbManager;

import java.sql.Types;
import java.util.ArrayList;

public class NotificationModel {

    public static class Notify {

        private static String getUsernameById(int id) throws Exception {
            var s = DbManager.db().prepareStatement("SELECT username FROM users WHERE id=?;");
            s.setInt(1, id);
            var res = s.executeQuery();
            if (!res.next()) {
                throw new RuntimeException("videoId not found");
            }
            return res.getString("username");
        }

        // NEW_UNSUBSCRIBED
        public static void newUnsubscribed(int ownerId, int followerId) throws Exception {
            String followerUsername = getUsernameById(followerId);
            var n = new Notification();
            n.setType(Notification.NotificationTypes.NEW_UNSUBSCRIBED);
            n.setOwnerId(ownerId);
            n.setUserId(followerId);
            n.setMessage(String.format("user with id:`%d` and username:`%s` unsubscribed you", followerId, followerUsername));
            NotificationModel.createNotification(n);
        }

        // NEW_SUBSCRIBER
        public static void newSubscriber(int ownerId, int followerId) throws Exception {
            String followerUsername = getUsernameById(followerId);
            var n = new Notification();
            n.setType(Notification.NotificationTypes.NEW_SUBSCRIBER);
            n.setOwnerId(ownerId);
            n.setUserId(followerId);
            n.setMessage(String.format("user:`%s` is now subscribing you", followerUsername));
            NotificationModel.createNotification(n);
        }

        // NEW_VIDEO_LIKE
        public static void newVideoLike(int videoId, int likerUserId, boolean isLike) throws Exception {
            var s = DbManager.db().prepareStatement("SELECT V.user_id,V.title FROM videos V WHERE V.id=?;");
            s.setInt(1, videoId);
            var res = s.executeQuery();
            if (!res.next()) {
                throw new RuntimeException("videoId not found");
            }
            int videoCreatorId = res.getInt("user_id"); // owner
            String videoTitle = res.getString("title");

            String likerUsername = getUsernameById(likerUserId);

            var n = new Notification();
            n.setType(Notification.NotificationTypes.NEW_VIDEO_LIKE);
            n.setOwnerId(videoCreatorId);
            n.setUserId(likerUserId);
            n.setVideoId(videoId);
            if (isLike) {
                n.setMessage(String.format("user:`%s` liked your video:`%s`", likerUsername, videoTitle));
            } else {
                n.setMessage(String.format("user:`%s` disliked your video:`%s`", likerUsername, videoTitle));
            }
            NotificationModel.createNotification(n);
        }

        // NEW_COMMENT_LIKE
        public static void newCommentLike(int commentId, int commentLikerUserId, boolean isLike) throws Exception {
            var s = DbManager.db().prepareStatement("SELECT user_id FROM comments WHERE id=?;");
            s.setInt(1, commentId);
            var res = s.executeQuery();
            if (!res.next()) {
                throw new RuntimeException("commentId not found");
            }
            int commentCreatorId = res.getInt("user_id"); // owner

            String commentLikerUsername = getUsernameById(commentLikerUserId);

            var n = new Notification();
            n.setType(Notification.NotificationTypes.NEW_COMMENT_LIKE);
            n.setOwnerId(commentCreatorId);
            n.setUserId(commentLikerUserId);
            n.setCommentId(commentId);
            if (isLike) {
                n.setMessage(String.format("user:`%s` liked your comment/reply with id:`%d`", commentLikerUsername, commentId));
            } else {
                n.setMessage(String.format("user:`%s` disliked your comment/reply with id:`%d`", commentLikerUsername, commentId));
            }
            NotificationModel.createNotification(n);
        }

        // NEW_COMMENT
        public static void newCommentOnUserVideo(int videoId, int commenterUserId, String text) throws Exception {
            var s = DbManager.db().prepareStatement("SELECT V.user_id,V.title FROM videos V WHERE V.id=?;");
            s.setInt(1, videoId);
            var res = s.executeQuery();
            if (!res.next()) {
                throw new RuntimeException("videoId not found");
            }
            int videoCreatorId = res.getInt("user_id"); // owner
            String videoTitle = res.getString("title");

            String commenterUsername = getUsernameById(commenterUserId);

            var n = new Notification();
            n.setType(Notification.NotificationTypes.NEW_COMMENT);
            n.setOwnerId(videoCreatorId);
            n.setUserId(commenterUserId);
            n.setVideoId(videoId);
            n.setMessage(String.format("user:`%s` commented:`%s` on your video:`%s`", commenterUsername, text, videoTitle));
            NotificationModel.createNotification(n);
        }

        // NEW_REPLY_TO_COMMENT
        public static void newReplyOnUserComment(int commentId, int replierUserId, String text) throws Exception {
            var s = DbManager.db().prepareStatement("SELECT C.user_id,C.video_id,V.title FROM comments C JOIN videos V ON C.video_id=V.id WHERE C.id=?;");
            s.setInt(1, commentId);
            var res = s.executeQuery();
            if (!res.next()) {
                throw new RuntimeException("commentId not found");
            }
            int commenterUserId = res.getInt("user_id"); // owner
            int videoId = res.getInt("video_id");
            String videoTitle = res.getString("title");

            String replierUsername = getUsernameById(replierUserId);

            var n = new Notification();
            n.setType(Notification.NotificationTypes.NEW_REPLY_TO_COMMENT);
            n.setOwnerId(commenterUserId);
            n.setUserId(replierUserId);
            n.setVideoId(videoId);
            n.setCommentId(commentId);
            n.setMessage(String.format("user:`%s` replied:`%s` to your comment with id:`%d` in video:`%s`", replierUsername, text, commentId, videoTitle));
            NotificationModel.createNotification(n);
        }

        private static ArrayList<User> getFollowers(int userId) throws Exception {
            String query = """     
                        SELECT
                            U.id,
                            U.username
                        FROM users U
                        JOIN followings F
                            ON F.follower_id=U.id
                        WHERE F.following_id=?;
                    """;

            var s = DbManager.db().prepareStatement(query);
            s.setInt(1, userId);

            ArrayList<User> users = new ArrayList<>();
            var res = s.executeQuery();
            while (res.next()) {
                User u = new User();
                u.setId(res.getInt("id"));
                u.setUsername(res.getString("username"));
                users.add(u);
            }
            if (users.isEmpty()) {
                return null;
            }
            return users;
        }

        // NEW_SUBSCRIBING_VIDEO
        public static void newVideoNotificationForUserFollowers(int userId, int videoId, String videoTitle) throws Exception {
            var s = DbManager.db().prepareStatement("SELECT U.username FROM videos V JOIN users U ON U.id=V.user_id WHERE V.id=?;");
            s.setInt(1, videoId);
            var res = s.executeQuery();
            if (!res.next()) {
                throw new RuntimeException("videoId not found");
            }
            String videoCreator = res.getString("username"); // owner

            ArrayList<User> users = getFollowers(userId);
            if (users == null) {
                System.out.println("user has no follower to notify them!");
                return;
            }
            for (User follower : users) {
                var n = new Notification();
                n.setType(Notification.NotificationTypes.NEW_VIDEO_FROM_SUBSCRIBING);
                n.setOwnerId(follower.getId());
                n.setUserId(userId);
                n.setVideoId(videoId);
                n.setMessage(String.format("your subscribing channel:`%s` published a new video:`%s`", videoCreator, videoTitle));
                NotificationModel.createNotification(n);
            }
        }

        // USER_ADDED_AS_COLLABORATOR
        public static void userAddedAsCollaborator(int ownerId, int playlistCreatorUserId, int playlistId) throws Exception {
            String playlistCreator = getUsernameById(playlistCreatorUserId);
            var n = new Notification();
            n.setType(Notification.NotificationTypes.USER_ADDED_AS_COLLABORATOR);
            n.setOwnerId(ownerId);
            n.setUserId(playlistCreatorUserId);
            n.setPlaylistId(playlistId);
            n.setMessage(String.format("user:`%s` added you as collaborator on playlist with id:`%d`", playlistCreator, playlistId));
            NotificationModel.createNotification(n);
        }

        // USER_REMOVED_AS_COLLABORATOR
        public static void userRemovedAsCollaborator(int ownerId, int playlistCreatorUserId, int playlistId) throws Exception {
            String playlistCreator = getUsernameById(playlistCreatorUserId);
            var n = new Notification();
            n.setType(Notification.NotificationTypes.USER_REMOVED_AS_COLLABORATOR);
            n.setOwnerId(ownerId);
            n.setUserId(playlistCreatorUserId);
            n.setPlaylistId(playlistId);
            n.setMessage(String.format("user:`%s` removed you as collaborator on playlist with id:`%d`", playlistCreator, playlistId));
            NotificationModel.createNotification(n);
        }

        // USER_VIDEO_ADDED_TO_UNOWNED_PLAYLIST,ANOTHER_COLLABORATOR_ADDED_VIDEO_TO_USER_PLAYLIST
        public static void videoAddedToPlaylist(int adderUserId, int videoId, int playlistId) throws Exception {
            String adderUsername = getUsernameById(adderUserId);

            var s = DbManager.db().prepareStatement("SELECT V.title,U.username,U.id FROM videos V JOIN users U ON U.id=V.user_id WHERE V.id=?;");
            s.setInt(1, videoId);
            var res = s.executeQuery();
            if (!res.next()) {
                throw new RuntimeException("videoId not found");
            }
            String videoTitle = res.getString("title");
            String videoCreatorUsername = res.getString("username");
            int videoCreatorUserId = res.getInt("id");

            s = DbManager.db().prepareStatement("SELECT P.name,U.username,U.id FROM playlists P JOIN users U ON U.id=P.user_id WHERE P.id=?;");
            s.setInt(1, playlistId);
            res = s.executeQuery();
            if (!res.next()) {
                throw new RuntimeException("playlistId not found");
            }
            String playlistName = res.getString("name");
            // String playlistCreatorUsername = res.getString("username");
            int playlistCreatorUserId = res.getInt("id");

            // ANOTHER_COLLABORATOR_ADDED_VIDEO_TO_USER_PLAYLIST
            if (playlistCreatorUserId != adderUserId) {
                var n = new Notification();
                n.setType(Notification.NotificationTypes.ANOTHER_COLLABORATOR_ADDED_VIDEO_TO_USER_PLAYLIST);
                n.setOwnerId(playlistCreatorUserId);
                n.setVideoId(videoId);
                n.setUserId(adderUserId);
                n.setPlaylistId(playlistId);
                n.setMessage(String.format("user:`%s` added video:`%s` to your playlist:`%s`", adderUsername, videoTitle, playlistName));
                NotificationModel.createNotification(n);
            }
            // USER_VIDEO_ADDED_TO_UNOWNED_PLAYLIST
            if (videoCreatorUserId != playlistCreatorUserId) {
                var n = new Notification();
                n.setType(Notification.NotificationTypes.USER_VIDEO_ADDED_TO_UNOWNED_PLAYLIST);
                n.setOwnerId(videoCreatorUserId);
                n.setVideoId(videoId);
                n.setUserId(adderUserId);
                n.setPlaylistId(playlistId);
                n.setMessage(String.format("your video:`%s` added to another playlist:`%s` by user:`%s`", videoTitle, playlistName, adderUsername));
                NotificationModel.createNotification(n);
            }
        }

        // USER_VIDEO_REMOVED_FROM_UNOWNED_PLAYLIST,ANOTHER_COLLABORATOR_REMOVED_VIDEO_FROM_USER_PLAYLIST
        public static void videoRemovedFromPlaylist(int adderUserId, int videoId, int playlistId) throws Exception {
            String adderUsername = getUsernameById(adderUserId);

            var s = DbManager.db().prepareStatement("SELECT V.title,U.username,U.id FROM videos V JOIN users U ON U.id=V.user_id WHERE V.id=?;");
            s.setInt(1, videoId);
            var res = s.executeQuery();
            if (!res.next()) {
                throw new RuntimeException("videoId not found");
            }
            String videoTitle = res.getString("title");
            String videoCreatorUsername = res.getString("username");
            int videoCreatorUserId = res.getInt("id");

            s = DbManager.db().prepareStatement("SELECT P.name,U.username,U.id FROM playlists P JOIN users U ON U.id=P.user_id WHERE P.id=?;");
            s.setInt(1, playlistId);
            res = s.executeQuery();
            if (!res.next()) {
                throw new RuntimeException("playlistId not found");
            }
            String playlistName = res.getString("name");
            // String playlistCreatorUsername = res.getString("username");
            int playlistCreatorUserId = res.getInt("id");

            // ANOTHER_COLLABORATOR_REMOVED_VIDEO_FROM_USER_PLAYLIST
            if (playlistCreatorUserId != adderUserId) {
                var n = new Notification();
                n.setType(Notification.NotificationTypes.ANOTHER_COLLABORATOR_REMOVED_VIDEO_FROM_USER_PLAYLIST);
                n.setOwnerId(playlistCreatorUserId);
                n.setVideoId(videoId);
                n.setUserId(adderUserId);
                n.setPlaylistId(playlistId);
                n.setMessage(String.format("user:`%s` deleted video:`%s` from your playlist:`%s`", adderUsername, videoTitle, playlistName));
                NotificationModel.createNotification(n);
            }
            // USER_VIDEO_REMOVED_FROM_UNOWNED_PLAYLIST
            if (videoCreatorUserId != playlistCreatorUserId) {
                var n = new Notification();
                n.setType(Notification.NotificationTypes.USER_VIDEO_REMOVED_FROM_UNOWNED_PLAYLIST);
                n.setOwnerId(videoCreatorUserId);
                n.setVideoId(videoId);
                n.setUserId(adderUserId);
                n.setPlaylistId(playlistId);
                n.setMessage(String.format("your video:`%s` deleted from another playlist:`%s` by user:`%s`", videoTitle, playlistName, adderUsername));
                NotificationModel.createNotification(n);
            }
        }
    }

    private static void createNotification(Notification n) throws Exception {
        String query = """
                INSERT INTO notifications
                    (notification_type,owner_user_id,message,user_id,video_id,comment_id,playlist_id)
                VALUES
                    (?,?,?,?,?,?,?);
                """;
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, n.getType().ordinal());
        s.setInt(2, n.getOwnerId());
        s.setString(3, n.getMessage());

        if (n.getUserId() != 0) {
            s.setInt(4, n.getUserId());
        } else {
            s.setNull(4, Types.BIGINT);
        }
        if (n.getVideoId() != 0) {
            s.setInt(5, n.getVideoId());
        } else {
            s.setNull(5, Types.BIGINT);
        }
        if (n.getCommentId() != 0) {
            s.setInt(6, n.getCommentId());
        } else {
            s.setNull(6, Types.BIGINT);

        }
        if (n.getPlaylistId() != 0) {
            s.setInt(7, n.getPlaylistId());
        } else {
            s.setNull(7, Types.BIGINT);
        }

        s.executeUpdate();
        if (s.getUpdateCount() <= 0) {
            throw new RuntimeException("failed to create notification");
        }
    }

    public static int getUnseenNotificationsCount(int userId) throws Exception {
        String query = """
                    SELECT
                        COUNT(*) AS cnt
                    FROM
                        notifications AS N
                    WHERE N.owner_user_id=? AND N.seen=FALSE;
                """;
        ArrayList<Notification> ns = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, userId);
        var res = s.executeQuery();
        if (!res.next()) {
            return 0;
        }
        return res.getInt("cnt");
    }

    public static ArrayList<Notification> getUnseenNotifications(int userId, int perpage, int page) throws Exception {
        String query = """
                    SELECT
                        N.id,
                        N.owner_user_id,
                        N.message,
                        N.notification_type,
                        N.user_id,
                        N.video_id,
                        N.comment_id,
                        COALESCE(N.is_like::INT,2) AS is_like,
                        N.seen,
                        N.created_at
                    FROM
                        notifications AS N
                    WHERE N.owner_user_id=? AND N.seen=FALSE
                    ORDER BY N.created_at DESC
                    LIMIT ? OFFSET ?;
                """;
        ArrayList<Notification> ns = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, userId);
        s.setInt(2, perpage);
        s.setInt(3, (perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()) {
            Notification n = new Notification();
            n.setOwnerId(res.getInt("id"));
            n.setMessage(res.getString("message"));
            n.setType(Notification.getIntToType(res.getInt("notification_type")));
            n.setUserId(res.getInt("user_id"));
            n.setVideoId(res.getInt("video_id"));
            n.setCommentId(res.getInt("comment_id"));
            // n.setLike(LikeState.values()[res.getInt("is_like")]);
            n.setSeen(res.getBoolean("seen"));
            n.setCreatedAt(res.getTimestamp("created_at"));
            ns.add(n);
        }
        if (ns.isEmpty()) {
            return null;
        }
        return ns;
    }

    // public static ArrayList<Notification> getNotifications(int userId, int perpage, int page) throws Exception {
    //     String query = """
    //                 SELECT
    //                     N.owner_user_id,
    //                     N.message,
    //                     N.notification_type,
    //                     N.user_id,
    //                     N.video_id,
    //                     N.comment_id,
    //                     N.is_like,
    //                     N.seen,
    //                     N.created_at
    //                 FROM
    //                     notifications AS N
    //                 WHERE N.owner_user_id=?
    //                 ORDER BY H.created_at
    //                 LIMIT ? OFFSET ?;
    //             """;
    //     ArrayList<Notification> ns = new ArrayList<>();
    //     var s = DbManager.db().prepareStatement(query);
    //     s.setInt(1, userId);
    //     s.setInt(2, perpage);
    //     s.setInt(3, (perpage * page) - perpage);
    //     var res = s.executeQuery();
    //     while (res.next()) {
    //         Notification n = new Notification();
    //         n.setId(res.getInt("id"));
    //         n.setMessage(res.getString("message"));
    //         n.setType(Notification.getIntToType(res.getInt("notification_type")));
    //         n.setUserId(res.getInt("user_id"));
    //         n.setVideoId(res.getInt("video_id"));
    //         n.setCommentId(res.getInt("comment_id"));
    //         n.setLike(LikeState.values()[res.getInt("is_like")]);
    //         n.setSeen(res.getBoolean("seen"));
    //         n.setCreatedAt(res.getTimestamp("created_at"));
    //         ns.add(n);
    //     }
    //     if (ns.isEmpty()) {
    //         return null;
    //     }
    //     return ns;
    // }

    public static void setNotificationSeen(int notificationId, int userId) throws Exception {
        String query = "UPDATE notifications SET seen=TRUE WHERE id=? AND owner_user_id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, notificationId);
        s.setInt(2, userId);
        s.executeUpdate();
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("notification or user not found");
        }
    }

    public static void setAllNotificationsSeen(int userId) throws Exception {
        String query = "UPDATE notifications SET seen=TRUE WHERE owner_user_id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, userId);
        s.executeUpdate();
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("no notification not found");
        }
    }

    public static void deleteNotification(int notificationId, int userId) throws Exception {
        String query = "DELETE FROM notifications WHERE id=? AND owner_user_id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, notificationId);
        s.setInt(2, userId);
        s.executeUpdate();
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("notification or user not found");
        }
    }

    public static void deleteAllNotification(int userId) throws Exception {
        String query = "DELETE FROM notifications WHERE owner_user_id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, userId);
        s.executeUpdate();
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("notification or user not found");
        }
    }
}
