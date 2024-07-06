package youtube.server.database.model;

import org.postgresql.util.PSQLException;
import youtube.server.database.DbManager;

import java.security.MessageDigest;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.UUID;

public class VideoModel {

    // when user is signed in
    public static Video authGetVideoById(int id, int userId) throws Exception {
        String query = """
                    SELECT
                        V.id,
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
                        V.thumbnail_path,
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
        v.setCurrentUserSubscribed(res.getBoolean("is_subbed"));
        v.setCurrentUserLike(LikeState.values()[res.getInt("is_liked")]);
        return v;
    }

    public static Video getVideoById(int id) throws Exception {
        String query = """
                    SELECT
                        V.id,
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
                        V.thumbnail_path
                    FROM
                        videos AS V
                    JOIN users AS U
                        ON U.id = user_id
                    WHERE
                        V.id=?;
                """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, id);
        var res = s.executeQuery();
        if (!res.next()) {
            return null;
        }
        Video v = new Video();
        v.setId(res.getInt("id"));
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
        return v;
    }

    public static int createVideo(String title, String description, String videoPath, int userId) throws Exception {
        String query = "INSERT INTO videos (title,description,video_path,user_id,created_at) VALUES (?,?,?,?,?)";

        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        // String videoPath = UUID.randomUUID().toString().replace("-", ""); // TODO: should be replaced

        s.setString(1, title);
        s.setString(2, description);
        s.setString(3, videoPath);
        s.setInt(4, userId);
        s.setTimestamp(5, new Timestamp(LocalDateTime.now().getNano()));

        s.executeUpdate();
        var res = s.getGeneratedKeys();
        if (!res.next()) {
            throw new Exception("ooops");
        }
        return res.getInt(1);
    }

    public static boolean editVideo(String newTitle, String newDescription, int videoId, int userId) throws Exception {
        String query = "UPDATE videos SET title=?,description=?,updated_at=? WHERE id=? AND user_id=?;";

        var s = DbManager.db().prepareStatement(query);


        s.setString(1, newTitle);
        s.setString(2, newDescription);
        s.setTimestamp(3, new Timestamp(LocalDateTime.now().getNano()));
        s.setInt(4, videoId);
        s.setInt(5, userId);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("video not found");
        }
        return true;
    }

    public static boolean deleteVideo(int id, int userId) throws Exception {
        String query = "DELETE FROM videos WHERE id=? AND user_id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, id);
        s.setInt(2, userId);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("video not found");
        }
        return true;
    }

    public static ArrayList<Video> getUserVideos(int userId, int page, int perpage) throws Exception {
        String query = """
                    SELECT
                        V.id AS video_id,
                        V.title,
                        U.username,
                        U.id AS user_id,
                        (SELECT COUNT(*) FROM views WHERE views.video_id=V.id) AS views_count,
                        V.thumbnail_path,
                        V.created_at
                    FROM
                        videos AS V
                    JOIN users AS U ON U.id = user_id
                        WHERE V.user_id=?
                    LIMIT ? OFFSET ?
                    ORDER BY created_at;
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
            v.setViewsCount(res.getInt("views_count"));
            v.setThumbnailPath(res.getString("thumbnail_path"));
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
                        V.thumbnail_path,
                        V.created_at
                    FROM
                        videos AS V
                    JOIN users AS U ON U.id = user_id
                    ORDER BY created_at
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
            v.setCreatedAt(res.getTimestamp("created_at"));
            videos.add(v);
        }
        if (videos.isEmpty()) {
            return null;
        }
        return videos;
    }

    public static ArrayList<Video> getNewPopularVideos(int page, int perpage) throws Exception {
        String query = """
                    SELECT
                        V.id AS video_id,
                        V.title,
                        U.username,
                        U.id AS user_id,
                        V.view_count,
                        V.thumbnail_path,
                        V.created_at
                    FROM
                        videos AS V
                    JOIN users AS U ON U.id = V.user_id
                    ORDER BY V.view_count DESC,V.created_at 
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
    public static boolean setVideoLike(boolean isLike, int videoId, int userId) throws Exception {
        String query = "INSERT INTO likes (user_id,video_id,is_like,created_at) VALUES (?,?,?,?);";
        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        s.setInt(1, userId);
        s.setInt(2, videoId);
        s.setBoolean(3, isLike);
        s.setTimestamp(4, new Timestamp(LocalDateTime.now().getNano()));

        try {
            s.executeUpdate();
        } catch (PSQLException ee) {
            if (ee.getSQLState().equals("23505")) {
                System.out.println("duplicate key !!!");
                throw new ModelError(ee.getMessage());
            }
            throw ee;
        }
        var res = s.getGeneratedKeys();
        if (!res.next()) {
            return false;
        }
        return true;
    }

    public static boolean removeVideoLike(int videoId, int userId) throws Exception {
        String query = "DELETE FROM likes WHERE user_id=? AND video_id=?;";
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, userId);
        s.setInt(2, videoId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            return false;
        }
        return true;
    }


    /// video tags
    public static boolean addTagToVideo(int videoId, String tagName, int userId) throws Exception {
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
        s.setTimestamp(3, new Timestamp(LocalDateTime.now().getNano()));
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
            throw new Exception("failed to add tag"); // video not exist or user not made the video
        }
        return true;
    }

    private static void createTag(String tagName) {
        String query = "INSERT INTO tags (name,created_at) VALUES (?,?)";
        try {
            var s = DbManager.db().prepareStatement(query);

            s.setString(1, tagName);
            s.setTimestamp(2, new Timestamp(LocalDateTime.now().getNano()));

            s.executeUpdate();
        } catch (Exception e) {
            System.out.println("failed to create tag:" + e.getMessage());
        }
    }

    public static boolean removeTagFromVideo(String tagName, int videoId, int userId) throws Exception {
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
            throw new ModelError("failed to remove tag"); // video not exist or tag not exist or video doesnt have ther tag or user hasnt made the video
            // return false;
        }
        return true;
    }

    /// playlist videos

    public static boolean addVideoToPlaylist(int playlistId, int videoId, int order, int userId) throws Exception {
        String query = """
                INSERT INTO playlist_videos
                        (playlist_id,video_id,custom_order)
                SELECT
                    ?,?,?
                WHERE EXISTS(SELECT user_id FROM playlists WHERE playlists.user_id=? AND playlists.id=?);
                """;
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, playlistId);
        s.setInt(2, videoId);
        s.setInt(3, order);
        s.setInt(4, userId);
        s.setInt(5, playlistId);
        var res = s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("failed to add video to playlist");
            // return false;
        }
        return true;
    }

    public static boolean editVideoFromPlaylist(int playlistId, int videoId, int newOrder, int userId) throws Exception {
        String query = """
                    UPDATE playlist_videos
                        SET custom_order=?
                    WHERE playlist_id=? AND video_id=? AND EXISTS(SELECT user_id FROM playlists WHERE playlists.user_id=? AND playlists.id=?);
                """;
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, newOrder);
        s.setInt(2, playlistId);
        s.setInt(3, videoId);
        s.setInt(4, userId);
        s.setInt(5, playlistId);
        var res = s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("failed to edit video of playlist");
            // return false;
        }
        return true;
    }

    public static boolean removeVideoFromPlaylist(int playlistId, int videoId, int userId) throws Exception {
        String query = """
                DELETE FROM
                    playlist_videos
                WHERE
                    playlist_id=?
                    AND
                    video_id=?
                    AND
                    EXISTS(SELECT user_id FROM playlists WHERE playlists.user_id=? AND playlists.id=?);
                """;
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, playlistId);
        s.setInt(2, videoId);
        s.setInt(3, userId);
        s.setInt(4, playlistId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("failed to remove video from playlist");
            // return false;
        }
        return true;
    }

    public static ArrayList<PlaylistVideo> getVideosOfPlaylist(int playlistId, int page, int perpage) throws Exception {
        String query = """
                SELECT
                    PV.playlist_id,PV.custom_order,PV.video_id,U.username,V.thumbnail_path,V.title,V.created_at,V.view_count
                FROM
                    playlist_videos PV
                JOIN videos V ON
                    V.id = PV.video_id
                JOIN users U ON
                    U.id=V.user_id
                WHERE
                    playlist_id = ?
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
            p.setVideoCreator(res.getString("username"));
            p.setVideoThumbnail(res.getString("thumbnail_path"));
            p.setVideoTitle(res.getString("title"));
            p.setVideoCreatedAt(res.getTimestamp("created_at"));
            p.setVideoViewCount(res.getInt("view_count"));
            ps.add(p);
        }
        if (ps.isEmpty()) {
            return null;
        }
        return ps;
    }

}
