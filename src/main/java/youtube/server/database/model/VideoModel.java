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
    public static Video authGetVideoById(int id,int userId) throws Exception {
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
                (SELECT COUNT(*) FROM views WHERE views.video_id=V.id) AS views_count,
                COALESCE((SELECT string_agg(tags.name,',') FROM video_tags JOIN tags ON tags.id=video_tags.tag_id WHERE video_id=V.id),'') AS tags,
                U.username,
                V.created_at,
                V.updated_at,
                COALESCE((SELECT is_like FROM likes WHERE likes.video_id=V.id AND likes.user_id=?),2) AS is_liked
            FROM
                videos AS V
            JOIN users AS U
                ON U.id = user_id
            WHERE
                V.id=?;
        """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1,userId);
        s.setInt(2,id);
        var res = s.executeQuery();
        if (!res.next()){
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
        v.setViewsCount(res.getInt("views_count"));
        v.setTags(res.getString("tags"));
        v.setUserName(res.getString("username"));
        v.setCreatedAt(res.getTimestamp("created_at"));
        v.setUpdatedAt(res.getTimestamp("updated_at"));
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
                (SELECT COUNT(*) FROM views WHERE views.video_id=V.id) AS views_count,
                COALESCE((SELECT string_agg(tags.name,',') FROM video_tags JOIN tags ON tags.id=video_tags.tag_id WHERE video_id=V.id),'') AS tags,
                U.username,
                V.created_at,
                V.updated_at
            FROM
                videos AS V
            JOIN users AS U
                ON U.id = user_id
            WHERE
                V.id=?;
        """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1,id);
        var res = s.executeQuery();
        if (!res.next()){
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
        v.setViewsCount(res.getInt("views_count"));
        v.setTags(res.getString("tags"));
        v.setUserName(res.getString("username"));
        v.setCreatedAt(res.getTimestamp("created_at"));
        v.setUpdatedAt(res.getTimestamp("updated_at"));
        return v;
    }

    public static int createVideo(String title,String description, int userId) throws Exception{
        String query = "INSERT INTO videos (title,description,video_path,user_id,created_at) VALUES (?,?,?,?,?)";

        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        String videoPath = UUID.randomUUID().toString().replace("-",""); // TODO: should be replaced

        s.setString(1,title);
        s.setString(2,description);
        s.setString(3,videoPath);
        s.setInt(4,userId);
        s.setTimestamp(5,new Timestamp(LocalDateTime.now().getNano()));

        s.executeUpdate();
        var res = s.getGeneratedKeys();
        if (!res.next()) {
            throw new Exception("ooops");
        }
        return res.getInt(1);
    }

    public static boolean editVidoe(String newTitle,String newDescription,int videoId) throws Exception{
        String query = "UPDATE videos SET title=?,description=?,updated_at=? WHERE id=?;";

        var s = DbManager.db().prepareStatement(query);


        s.setString(1,newTitle);
        s.setString(2,newDescription);
        s.setInt(3,videoId);
        s.setTimestamp(4, new Timestamp(LocalDateTime.now().getNano()));
        s.setInt(4,videoId);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount()<=0) {
            throw new ModelError("vidoe not found");
        }
        return true;
    }

    public static boolean deleteVideo(int id) throws Exception{
        String query = "DELETE FROM videos WHERE id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1,id);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount()<=0) {
            throw new ModelError("vidoe not found");
        }
        return true;
    }

    public static ArrayList<Video> getUserVideos(int userId,int page,int perpage) throws Exception{
        String query = """
            SELECT
                V.id AS video_id,
                V.title,
                U.username,
                U.id AS user_id,
                (SELECT COUNT(*) FROM views WHERE views.video_id=V.id) AS views_count,
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
        s.setInt(1,userId);
        s.setInt(2,perpage);
        s.setInt(3,(perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()){
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

    public static ArrayList<Video> getNewVideos(int page,int perpage) throws Exception{
        String query = """
            SELECT
                V.id AS video_id,
                V.title,
                U.username,
                U.id AS user_id,
                (SELECT COUNT(*) FROM views WHERE views.video_id=V.id) AS views_count,
                V.created_at
            FROM
                videos AS V
            JOIN users AS U ON U.id = user_id
            LIMIT ? OFFSET ?
            ORDER BY created_at;
        """;
        ArrayList<Video> videos = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1,perpage);
        s.setInt(2,(perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()){
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

    /// TODO
    public static ArrayList<Video> getVideosOfTag(int tagId,int page,int perpage)  throws Exception{
        String query = """
            --- TODO
        """;
        ArrayList<Video> videos = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1,perpage);
        s.setInt(2,(perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()){
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

    public static boolean setVideoLike(boolean isLike,int videoId,int userId) throws Exception {
        String query = "INSERT INTO likes (user_id,video_id,is_like,created_at) VALUES (?,?,?,?);";
        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        s.setInt(1,userId);
        s.setInt(2,videoId);
        s.setBoolean(3,isLike);
        s.setTimestamp(4,new Timestamp(LocalDateTime.now().getNano()));

        try{
            s.executeUpdate();
        }catch (PSQLException ee){
            if (ee.getSQLState().equals("23505")){
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

    public static boolean removeVideoLike(int videoId,int userId) throws Exception {
        String query = "DELETE FROM likes WHERE user_id=$1 AND video_id=?;";
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1,userId);
        s.setInt(2,videoId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n<=0) {
            return false;
        }
        return true;
    }

    public static boolean addTagToVideo(int id,String tagName) throws Exception {
        return false;
    }

    public static boolean removeTagFromVideo(int id,String tagName) throws Exception {
        return false;
    }

}
