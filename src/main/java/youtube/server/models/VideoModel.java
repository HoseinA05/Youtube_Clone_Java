package youtube.server.models;

import org.postgresql.util.PSQLException;
import youtube.server.database.DbManager;

import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

public class VideoModel {

    public static String[] getPathsById(int id) throws Exception {
        String query = "SELECT thumbnail_path,video_path FROM videos WHERE id=?";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, id);
        var res = s.executeQuery();
        if (!res.next()) {
            return null;
        }
        return new String[]{
                res.getString("thumbnail_path"),
                res.getString("video_path")
        };
    }

    // when user is signed in
    public static Video authGetVideoById(int id, int userId) throws Exception {
        String query = """
                    SELECT
                        V.id,
                        V.is_private,
                        V.title,
                        COALESCE(V.description,'') AS description,
                        V.video_path,
                        V.user_id,
                        (SELECT COUNT(*) FROM followings AS F WHERE F.following_id = V.user_id) AS subscribers,
                        (SELECT COUNT(*) FROM comments WHERE comments.video_id=V.id) AS comments_count,
                        (SELECT COUNT(*) FROM likes WHERE likes.video_id=V.id AND likes.is_like=true) AS likes_count,
                        (SELECT COUNT(*) FROM likes WHERE likes.video_id=V.id AND likes.is_like=false) AS dislikes_count,
                        view_count,
                        COALESCE((SELECT string_agg(tags.name,',') FROM video_tags JOIN tags ON tags.id=video_tags.tag_id WHERE video_id=V.id),'') AS tags,
                        U.username,
                        V.created_at,
                        V.updated_at,
                        COALESCE(V.thumbnail_path,'') AS thumbnail_path,
                        COALESCE(U.profile_photo_path,'') AS profile_photo_path,
                        (SELECT EXISTS(SELECT FROM followings WHERE follower_id=? AND following_id=U.id)) AS is_subbed,
                        COALESCE(((SELECT is_like FROM likes WHERE likes.video_id=V.id AND likes.user_id=?)::INT),2) AS is_liked
                    FROM
                        videos AS V
                    JOIN users AS U
                        ON U.id = V.user_id
                    WHERE
                        V.id=?;
                """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, userId);
        s.setInt(2, userId);
        s.setInt(3, id);
        var res = s.executeQuery();
        if (!res.next()) {
            return null;
        }
        Video v = new Video();
        v.setId(res.getInt("id"));
        v.setPrivate(res.getBoolean("is_private"));
        v.setTitle(res.getString("title"));
        v.setDescription(res.getString("description"));
        v.setVideoPath(res.getString("video_path"));
        v.setUserId(res.getInt("user_id"));
        v.setUserSubscribesCount(res.getInt("subscribers"));
        v.setCommentsCount(res.getInt("comments_count"));
        v.setLikesCount(res.getInt("likes_count"));
        v.setDislikesCount(res.getInt("dislikes_count"));
        v.setViewsCount(res.getInt("view_count"));
        v.setTags(res.getString("tags"));
        v.setUserName(res.getString("username"));
        v.setCreatedAt(res.getTimestamp("created_at"));
        v.setUpdatedAt(res.getTimestamp("updated_at"));
        v.setThumbnailPath(res.getString("thumbnail_path"));
        v.setUserProfilePhotoPath(res.getString("profile_photo_path"));
        v.setCurrentUserSubscribed(res.getBoolean("is_subbed"));
        v.setCurrentUserLike(LikeState.values()[res.getInt("is_liked")]);
        HistoryModel.createHistory(id, userId);
        increaseVideoView(id);
        return v;
    }

    public static Video getVideoById(int id) throws Exception {
        String query = """
                    SELECT
                        V.id,
                        V.is_private,
                        V.title,
                        COALESCE(V.description,'') AS description,
                        V.video_path,
                        V.user_id,
                        (SELECT COUNT(*) FROM followings AS F WHERE F.following_id = V.user_id) AS subscribers,
                        (SELECT COUNT(*) FROM comments WHERE comments.video_id=V.id) AS comments_count,
                        (SELECT COUNT(*) FROM likes WHERE likes.video_id=V.id AND likes.is_like=true) AS likes_count,
                        (SELECT COUNT(*) FROM likes WHERE likes.video_id=V.id AND likes.is_like=false) AS dislikes_count,
                        V.view_count,
                        COALESCE((SELECT string_agg(tags.name,',') FROM video_tags JOIN tags ON tags.id=video_tags.tag_id WHERE video_id=V.id),'') AS tags,
                        U.username,
                        V.created_at,
                        V.updated_at,
                        COALESCE(U.profile_photo_path,'') AS profile_photo_path,
                        COALESCE(V.thumbnail_path,'') AS thumbnail_path
                    FROM
                        videos AS V
                    JOIN users AS U
                        ON U.id = user_id
                    WHERE
                        V.id=? AND V.is_private=FALSE;
                """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, id);
        var res = s.executeQuery();
        if (!res.next()) {
            return null;
        }
        Video v = new Video();
        v.setId(res.getInt("id"));
        v.setPrivate(res.getBoolean("is_private"));
        v.setTitle(res.getString("title"));
        v.setDescription(res.getString("description"));
        v.setVideoPath(res.getString("video_path"));
        v.setUserId(res.getInt("user_id"));
        v.setUserSubscribesCount(res.getInt("subscribers"));
        v.setCommentsCount(res.getInt("comments_count"));
        v.setLikesCount(res.getInt("likes_count"));
        v.setDislikesCount(res.getInt("dislikes_count"));
        v.setViewsCount(res.getInt("view_count"));
        v.setTags(res.getString("tags"));
        v.setUserName(res.getString("username"));
        v.setCreatedAt(res.getTimestamp("created_at"));
        v.setUpdatedAt(res.getTimestamp("updated_at"));
        v.setThumbnailPath(res.getString("thumbnail_path"));
        v.setUserProfilePhotoPath(res.getString("profile_photo_path"));
        increaseVideoView(id);
        return v;
    }

    public static int createVideo(String title, String description, String videoPath, boolean isPrivate, int userId) throws Exception {
        String query = "INSERT INTO videos (title,description,video_path,user_id,is_private,created_at) VALUES (?,?,?,?,?,?)";
        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        s.setString(1, title);
        s.setString(2, description);
        s.setString(3, videoPath);
        s.setInt(4, userId);
        s.setBoolean(5, isPrivate);
        s.setTimestamp(6, new Timestamp(new java.util.Date().getTime()));

        try {
            s.executeUpdate();
        } catch (PSQLException ee) {
            System.out.println(ee.getMessage());
            if (ee.getSQLState().equals("23503")) { // foreign key violation
                // throw new ModelError(ee.getMessage());
                throw new ModelError("user not found");
            }
            throw ee;
        }
        var res = s.getGeneratedKeys();
        if (!res.next()) {
            throw new Exception("ooops");
        }
        int id = res.getInt(1);
        NotificationModel.Notify.newVideoNotificationForUserFollowers(userId, id, title);
        return id;
    }


    private static void increaseVideoView(int videoId) throws Exception {
        String query = "UPDATE videos SET view_count=(view_count+1),updated_at=? WHERE id=?;";

        var s = DbManager.db().prepareStatement(query);

        s.setTimestamp(1, new Timestamp(new java.util.Date().getTime()));
        s.setInt(2, videoId);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("video not found or not belong to user");
        }
    }

    public static void setVideoPrivacy(boolean isPrivate, int videoId, int userId) throws Exception {
        String query = "UPDATE videos SET is_private=?,updated_at=? WHERE id=? AND user_id=?;";

        var s = DbManager.db().prepareStatement(query);

        s.setBoolean(1, isPrivate);
        s.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
        s.setInt(3, videoId);
        s.setInt(4, userId);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("video not found or not belong to user");
        }
    }

    public static void setVideoThumbnail(String thumbnailPath, int videoId, int userId) throws Exception {
        String query = "UPDATE videos SET thumbnail_path=?,updated_at=? WHERE id=? AND user_id=?;";

        var s = DbManager.db().prepareStatement(query);

        s.setString(1, thumbnailPath);
        s.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
        s.setInt(3, videoId);
        s.setInt(4, userId);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("video not found or not belong to user");
        }
    }


    public static void editVideo(String newTitle, String newDescription, boolean isPrivate, int videoId, int userId) throws Exception {
        String query = "UPDATE videos SET title=?,description=?,is_private=?,updated_at=? WHERE id=? AND user_id=?;";

        var s = DbManager.db().prepareStatement(query);

        s.setString(1, newTitle);
        s.setString(2, newDescription);
        s.setBoolean(3, isPrivate);
        s.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
        s.setInt(5, videoId);
        s.setInt(6, userId);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("video not found or not belong to user");
        }
    }

    public static void deleteVideo(int id, int userId) throws Exception {
        String query = "DELETE FROM videos WHERE id=? AND user_id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, id);
        s.setInt(2, userId);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("video not found or not belong to user");
        }
    }

    public static ArrayList<Video> getUserVideos(int userId, int sortType, int page, int perpage) throws Exception {
        String query = """
                SELECT
                    V.id AS video_id,
                    V.title,
                    U.username,
                    U.id AS user_id,
                    V.view_count,
                    COALESCE(V.thumbnail_path,'') AS thumbnail_path,
                        COALESCE(U.profile_photo_path,'') AS profile_photo_path,
                    V.created_at
                FROM
                    videos AS V
                JOIN users AS U ON U.id = user_id
                WHERE V.user_id=? AND V.is_private=FALSE
                ---ORDER BY created_at
                ORDER BY 
                """ + Helper.getVideoOrderFromInt(sortType) + """
                LIMIT ? OFFSET ?;
                """;
        ArrayList<Video> videos = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, userId);
        s.setInt(2, perpage);
        s.setInt(3, (perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()) {
            Video v = new Video();
            v.setId(res.getInt("video_id"));
            v.setTitle(res.getString("title"));
            v.setUserName(res.getString("username"));
            v.setUserId(res.getInt("user_id"));
            v.setViewsCount(res.getInt("view_count"));
            v.setThumbnailPath(res.getString("thumbnail_path"));
            v.setUserProfilePhotoPath(res.getString("profile_photo_path"));
            v.setCreatedAt(res.getTimestamp("created_at"));
            videos.add(v);
        }
        if (videos.isEmpty()) {
            return null;
        }
        return videos;
    }

    public static ArrayList<Video> getNewVideos(int page, int perpage) throws Exception {
        String query = """
                    SELECT
                        V.id AS video_id,
                        V.title,
                        U.username,
                        U.id AS user_id,
                        V.view_count,
                        COALESCE(V.thumbnail_path,'') AS thumbnail_path,
                        COALESCE(U.profile_photo_path,'') AS profile_photo_path,
                        V.created_at
                    FROM
                        videos AS V
                    JOIN users AS U ON U.id = user_id
                    WHERE V.is_private=FALSE
                    ORDER BY created_at DESC
                    LIMIT ? OFFSET ?;
                """;
        ArrayList<Video> videos = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, perpage);
        s.setInt(2, (perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()) {
            Video v = new Video();
            v.setId(res.getInt("video_id"));
            v.setTitle(res.getString("title"));
            v.setUserName(res.getString("username"));
            v.setUserId(res.getInt("user_id"));
            v.setViewsCount(res.getInt("view_count"));
            v.setThumbnailPath(res.getString("thumbnail_path"));
            v.setUserProfilePhotoPath(res.getString("profile_photo_path"));
            v.setCreatedAt(res.getTimestamp("created_at"));
            videos.add(v);
        }
        if (videos.isEmpty()) {
            return null;
        }
        return videos;
    }

    // trending videos
    public static ArrayList<Video> getPopularVideosIn24H(int page, int perpage) throws Exception {
        String query = """
                    SELECT
                        V.id AS video_id,
                        V.title,
                        U.username,
                        U.id AS user_id,
                        V.view_count,
                        COALESCE(V.thumbnail_path,'') AS thumbnail_path,
                        COALESCE(U.profile_photo_path,'') AS profile_photo_path,
                        V.created_at
                    FROM
                        videos AS V
                    JOIN users AS U ON U.id = V.user_id
                    WHERE V.is_private=FALSE AND V.created_at>?
                    ORDER BY V.view_count DESC,V.created_at
                    LIMIT ? OFFSET ?;
                """;
        ArrayList<Video> videos = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setTimestamp(1, new Timestamp(new java.util.Date().getTime() - Duration.ofHours(24).toMillis()));
        s.setInt(2, perpage);
        s.setInt(3, (perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()) {
            Video v = new Video();
            v.setId(res.getInt("video_id"));
            v.setTitle(res.getString("title"));
            v.setUserName(res.getString("username"));
            v.setUserId(res.getInt("user_id"));
            v.setViewsCount(res.getInt("view_count"));
            v.setThumbnailPath(res.getString("thumbnail_path"));
            v.setUserProfilePhotoPath(res.getString("profile_photo_path"));
            v.setCreatedAt(res.getTimestamp("created_at"));
            videos.add(v);
        }
        if (videos.isEmpty()) {
            return null;
        }
        return videos;
    }


    public static ArrayList<Video> getPopularVideosOfSubscriptionsInWeek(int followerId, int page, int perpage) throws Exception {
        String query = """
                    SELECT
                        V.id AS video_id,
                        V.title,
                        U.username,
                        U.id AS user_id,
                        V.view_count,
                        COALESCE(V.thumbnail_path,'') AS thumbnail_path,
                        COALESCE(U.profile_photo_path,'') AS profile_photo_path,
                        V.created_at
                    FROM
                        videos AS V
                    JOIN users AS U ON U.id = V.user_id
                    WHERE
                            V.is_private=FALSE
                        AND
                            U.id IN
                            (
                                SELECT
                                    F.following_id
                                FROM followings F
                                WHERE F.follower_id=? LIMIT 10
                            )
                        AND
                            V.created_at>?
                    ORDER BY V.view_count DESC,V.created_at
                    LIMIT ? OFFSET ?;
                """;
        ArrayList<Video> videos = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, followerId);
        s.setTimestamp(2, new Timestamp(new java.util.Date().getTime() - Duration.ofDays(7).toMillis()));
        s.setInt(3, perpage);
        s.setInt(4, (perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()) {
            Video v = new Video();
            v.setId(res.getInt("video_id"));
            v.setTitle(res.getString("title"));
            v.setUserName(res.getString("username"));
            v.setUserId(res.getInt("user_id"));
            v.setViewsCount(res.getInt("view_count"));
            v.setThumbnailPath(res.getString("thumbnail_path"));
            v.setUserProfilePhotoPath(res.getString("profile_photo_path"));
            v.setCreatedAt(res.getTimestamp("created_at"));
            videos.add(v);
        }
        if (videos.isEmpty()) {
            return null;
        }
        return videos;
    }

    /// TODO
    public static ArrayList<Video> getVideosOfTag(int tagId, int page, int perpage) throws Exception {
        String query = """
                    --- TODO
                """;
        ArrayList<Video> videos = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, perpage);
        s.setInt(2, (perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()) {
            Video v = new Video();
            v.setId(res.getInt("video_id"));
            v.setTitle(res.getString("title"));
            v.setUserName(res.getString("username"));
            v.setUserId(res.getInt("user_id"));
            v.setViewsCount(res.getInt("views_count"));
            v.setCreatedAt(res.getTimestamp("created_at"));
            videos.add(v);
        }
        if (videos.isEmpty()) {
            return null;
        }
        return videos;
    }

    /// video likes
    public static void setVideoLike(boolean isLike, int videoId, int userId) throws Exception {
        String query = "INSERT INTO likes (user_id,video_id,is_like,created_at) VALUES (?,?,?,?);";
        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        s.setInt(1, userId);
        s.setInt(2, videoId);
        s.setBoolean(3, isLike);
        s.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));

        try {
            s.executeUpdate();
        } catch (PSQLException ee) {
            System.out.println(ee.getMessage());
            if (ee.getSQLState().equals("23503")) { // foreign key violation
                throw new ModelError(ee.getMessage());
                // throw new ModelError("user not found");
            }
            if (ee.getSQLState().equals("23505")) {
                System.out.println("duplicate key !!!");
                // throw new ModelError(ee.getMessage());
                throw new ModelError("video already liked/disliked");
            }
            throw ee;
        }
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("failed to set like/dislike");
        }
        if (isLike){
            try {
                PlaylistModel.addVideoToLikeList(videoId,userId);
            } catch (Exception e){
                System.out.println(e);
            }
        }
        NotificationModel.Notify.newVideoLike(videoId, userId, isLike);
    }

    public static void removeVideoLike(int videoId, int userId) throws Exception {
        String query = "DELETE FROM likes WHERE user_id=? AND video_id=?;";
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, userId);
        s.setInt(2, videoId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("failed to remove like/dislike"); // user or video not found or user not liked/disliked
            // throw new ModelError("video not found or user not made the video or video not liked/disliked");
        }
    }

    /// video tags
    public static void addTagToVideo(int videoId, String tagName, int userId) throws Exception {
        createTag(tagName); // assuring tag already exist before adding tag to video
        String query = """
                    INSERT INTO video_tags
                        (video_id,tag_id,created_at)
                    SELECT
                        ?,(SELECT id FROM tags WHERE name=?),?
                    WHERE EXISTS(SELECT id FROM videos WHERE videos.id=? AND videos.user_id=?);
                """;

        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, videoId);
        s.setString(2, tagName);
        s.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
        s.setInt(4, videoId);
        s.setInt(5, userId);

        try {
            s.executeUpdate();
        } catch (PSQLException ee) {
            if (ee.getSQLState().equals("23505")) {
                System.out.println("tag already added to video");
                System.out.println("duplicate key !!!");
                System.out.println("ee:" + ee);
                throw new ModelError("tag already added to video");
                // return false;
            }
            throw ee;
        }

        var n = s.getUpdateCount();
        if (n == 0) {
            // return  false;
            // throw new ModelError("failed to add tag"); // video not exist or user not made the video
            throw new ModelError("video not exist or user not made the video");
        }
    }

    private static void createTag(String tagName) {
        String query = "INSERT INTO tags (name,created_at) VALUES (?,?)";
        try {
            var s = DbManager.db().prepareStatement(query);

            s.setString(1, tagName);
            s.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));

            s.executeUpdate();
        } catch (Exception e) {
            System.out.println("tag already created");
        }
    }

    public static void removeTagFromVideo(String tagName, int videoId, int userId) throws Exception {
        String query = """
                DELETE FROM
                    video_tags
                WHERE
                    tag_id=(SELECT id FROM tags WHERE name=?)
                    AND
                    video_id=?
                    AND
                    EXISTS(SELECT id FROM videos WHERE videos.id=? AND videos.user_id=?);
                """;
        var s = DbManager.db().prepareStatement(query);

        s.setString(1, tagName);
        s.setInt(2, videoId);
        s.setInt(3, videoId);
        s.setInt(4, userId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            // throw new ModelError("failed to remove tag"); // video not exist or tag not exist or video doesnt have ther tag or user hasnt made the video
            throw new ModelError("video or tag not exist or user not made the video");
        }
    }

    /// playlist videos

    public static void addVideoToPlaylist(int playlistId, int videoId, int order, int adminUserId) throws Exception {
        String query = """
                INSERT INTO playlist_videos
                        (playlist_id,video_id,custom_order,admin_id)
                VALUES
                    (
                        ?,
                        ?,
                        ?,
                        (SELECT PA.id FROM playlist_admins PA WHERE PA.playlist_id=? AND PA.user_id=?)
                    );
                """;
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, playlistId);
        s.setInt(2, videoId);
        s.setInt(3, order);
        s.setInt(4, playlistId);
        s.setInt(5, adminUserId);

        try {
            s.executeUpdate();
        } catch (PSQLException ee) {
            System.out.println(ee.getMessage());
            System.out.println(ee.getSQLState());
            if (ee.getSQLState().equals("23502")) { // not null violaiton
                throw new ModelError("collaborator or playlist not found");
                // throw new ModelError("collaborator not found");
            }
            if (ee.getSQLState().equals("23503")) { // foreign key violation
                // throw new ModelError(ee.getMessage());
                throw new ModelError("video not found");
            }
            if (ee.getSQLState().equals("23505")) { // unique key violation
                // throw new ModelError(ee.getMessage());
                throw new ModelError("video already added to playlist");
            }
            throw ee;
        }

        var n = s.getUpdateCount();
        if (n <= 0) {
            // throw new ModelError("failed to add video to playlist");
            throw new ModelError("ooops");
        }
        NotificationModel.Notify.videoAddedToPlaylist(adminUserId, videoId, playlistId);
    }

    public static void editVideoFromPlaylist(int playlistId, int videoId, int newOrder, int adminUserId) throws Exception {
        String query = """
                    UPDATE playlist_videos
                        SET custom_order=?
                    WHERE playlist_id=? AND video_id=? AND admin_id=(SELECT PA.id FROM playlist_admins PA WHERE PA.playlist_id=? AND PA.user_id=?);
                """;
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, newOrder);
        s.setInt(2, playlistId);
        s.setInt(3, videoId);
        s.setInt(4, playlistId);
        s.setInt(5, adminUserId);
        var res = s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("failed to edit video of playlist");
        }
    }

    public static void removeVideoFromPlaylist(int playlistId, int videoId, int adminUserId) throws Exception {
        String query = """
                DELETE FROM
                    playlist_videos
                WHERE
                    playlist_id=?
                    AND
                    video_id=?
                    AND
                    admin_id=(SELECT PA.id FROM playlist_admins PA WHERE PA.playlist_id=? AND PA.user_id=?);
                """;
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, playlistId);
        s.setInt(2, videoId);
        s.setInt(3, playlistId);
        s.setInt(4, adminUserId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("failed to remove video from playlist");
            // return false;
        }
        NotificationModel.Notify.videoRemovedFromPlaylist(adminUserId, videoId, playlistId);
    }

    public static ArrayList<PlaylistVideo> getVideosOfPlaylist(int playlistId, int page, int perpage) throws Exception {
        String query = """
                SELECT
                    PV.playlist_id,
                    PV.custom_order,
                    PV.video_id,
                    PV.created_at AS added_at,
                    U1.username AS video_creator,
                    U1.id AS video_creator_id,
                    COALESCE(V.thumbnail_path,'') AS thumbnail_path,
                    V.title,
                    V.created_at,
                    U2.username AS adder_username,
                    U2.id AS adder_userId,
                    V.view_count
                FROM
                    playlist_videos PV
                JOIN videos V ON
                    V.id = PV.video_id
                JOIN users U1 ON
                    U1.id=V.user_id
                JOIN playlist_admins PA ON
                    PA.id=PV.admin_id
                JOIN users U2 ON
                    U2.id=PA.user_id
                WHERE
                    PV.playlist_id = ? AND V.is_private=FALSE
                ORDER BY PV.custom_order,PV.created_at
                LIMIT ? OFFSET ?;
                """;
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, playlistId);
        s.setInt(2, perpage);
        s.setInt(3, (perpage * page) - perpage);
        var res = s.executeQuery();
        ArrayList<PlaylistVideo> ps = new ArrayList<>();
        while (res.next()) {
            PlaylistVideo p = new PlaylistVideo();
            p.setPlaylistId(res.getInt("playlist_id"));
            p.setOrder(res.getInt("custom_order"));
            p.setVideoId(res.getInt("video_id"));
            p.setAddedAt(res.getTimestamp("added_at"));
            p.setVideoCreator(res.getString("video_creator"));
            p.setVideoCreatorUserId(res.getInt("video_creator_id"));
            p.setVideoThumbnail(res.getString("thumbnail_path"));
            p.setVideoTitle(res.getString("title"));
            p.setVideoCreatedAt(res.getTimestamp("created_at"));
            p.setAdderUserID(res.getInt("adder_userId"));
            p.setAdderUsername(res.getString("adder_username"));
            p.setVideoViewCount(res.getInt("view_count"));
            ps.add(p);
        }
        if (ps.isEmpty()) {
            return null;
        }
        return ps;
    }

}
