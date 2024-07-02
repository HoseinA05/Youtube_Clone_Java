package youtube.server.database.model;

import org.postgresql.util.PSQLException;
import youtube.server.database.DbManager;

import javax.swing.plaf.multi.MultiSeparatorUI;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

public class CommentModel {

    public static ArrayList<Comment> getCommentsOfVideo(int videoId,int page,int perpage) throws Exception{
        String query = """
            SELECT
                C.id AS comment_id,
                C.text,
                U.username,
                U.id AS user_id,
                (SELECT COUNT(*) FROM comments WHERE comment_id=c.id) AS replies_count,
                (SELECT COUNT(*) FROM comments_likes WHERE comments_likes.comment_id=C.id AND comments_likes.is_like=true) AS likes_count,
                (SELECT COUNT(*) FROM comments_likes WHERE comments_likes.comment_id=C.id AND comments_likes.is_like=false) AS dislikes_count,
                C.created_at,
                C.updated_at
            FROM
                comments C
            JOIN users U
            ON U.id = C.user_id
            WHERE
                video_id=?
                AND
                C.comment_id IS NULL
            LIMIT ? OFFSET ?;
        """;
        ArrayList<Comment> comments = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1,videoId);
        s.setInt(2,perpage);
        s.setInt(3,(perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()){
            Comment c = new Comment();
            c.setId(res.getInt("comment_id"));
            c.setText(res.getString("text"));
            c.setUserId(res.getInt("user_id"));
            c.setUserName(res.getString("username"));
            c.setRepliesCount(res.getInt("replies_count"));
            c.setLikesCount(res.getInt("likes_count"));
            c.setDislikesCount(res.getInt("dislikes_count"));
            c.setCreatedAt(res.getTimestamp("created_at"));
            c.setUpdatedAt(res.getTimestamp("updated_at"));
            comments.add(c);
        }
        if (comments.isEmpty()) {
            return null;
        }
        return comments;
    }

    public static ArrayList<Comment> authGetCommentsOfVideo(int videoId,int page,int perpage,int userId) throws Exception{
        String query = """
            SELECT
                C.id AS comment_id,
                C.text,
                U.username,
                U.id AS user_id,
                (SELECT COUNT(*) FROM comments WHERE comment_id=c.id) AS replies_count,
                (SELECT COUNT(*) FROM comments_likes WHERE comments_likes.comment_id=C.id AND comments_likes.is_like=true) AS likes_count,
                (SELECT COUNT(*) FROM comments_likes WHERE comments_likes.comment_id=C.id AND comments_likes.is_like=false) AS dislikes_count,
                C.created_at,
                C.updated_at,
                COALESCE((SELECT is_like FROM comments_likes WHERE comments_likes.comment_id=C.id AND comments_likes.user_id=?),2) AS is_liked
            FROM
                comments C
            JOIN users U
            ON U.id = C.user_id
            WHERE
                video_id=?
                AND
                C.comment_id IS NULL
            LIMIT ? OFFSET ?;
        """;
        ArrayList<Comment> comments = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1,userId);
        s.setInt(2,videoId);
        s.setInt(3,perpage);
        s.setInt(4,(perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()){
            Comment c = new Comment();
            c.setId(res.getInt("comment_id"));
            c.setText(res.getString("text"));
            c.setUserId(res.getInt("user_id"));
            c.setUserName(res.getString("username"));
            c.setRepliesCount(res.getInt("replies_count"));
            c.setLikesCount(res.getInt("likes_count"));
            c.setDislikesCount(res.getInt("dislikes_count"));
            c.setCreatedAt(res.getTimestamp("created_at"));
            c.setUpdatedAt(res.getTimestamp("updated_at"));
            c.setCurrentUserLike(LikeState.values()[res.getInt("is_like")]);
            comments.add(c);
        }
        if (comments.isEmpty()) {
            return null;
        }
        return comments;
    }

    public static ArrayList<Comment> getRepliesOfComment(int commentId,int page,int perpage) throws Exception{
        String query = """
            SELECT
                C.id AS comment_id,
                C.text,
                U.username,
                U.id AS user_id,
                (SELECT COUNT(*) FROM comments WHERE comment_id=c.id) AS replies_count,
                (SELECT COUNT(*) FROM comments_likes WHERE comments_likes.comment_id=C.id AND comments_likes.is_like=true) AS likes_count,
                (SELECT COUNT(*) FROM comments_likes WHERE comments_likes.comment_id=C.id AND comments_likes.is_like=false) AS dislikes_count,
                C.created_at,
                C.updated_at
            FROM
                comments C
            JOIN users U
            ON U.id = C.user_id
            WHERE
                C.comment_id = ?
            LIMIT ? OFFSET ?;
        """;
        ArrayList<Comment> comments = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1,commentId);
        s.setInt(2,perpage);
        s.setInt(3,(perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()){
            Comment c = new Comment();
            c.setId(res.getInt("comment_id"));
            c.setText(res.getString("text"));
            c.setUserId(res.getInt("user_id"));
            c.setUserName(res.getString("username"));
            c.setRepliesCount(res.getInt("replies_count"));
            c.setLikesCount(res.getInt("likes_count"));
            c.setDislikesCount(res.getInt("dislikes_count"));
            c.setCreatedAt(res.getTimestamp("created_at"));
            c.setUpdatedAt(res.getTimestamp("updated_at"));
            comments.add(c);
        }
        if (comments.isEmpty()) {
            return null;
        }
        return comments;
    }

    public static ArrayList<Comment> authGetRepliesOfComment(int commentId,int page,int perpage,int userId) throws Exception{
        String query = """
            SELECT
                C.id AS comment_id,
                C.text,
                U.username,
                U.id AS user_id,
                (SELECT COUNT(*) FROM comments WHERE comment_id=c.id) AS replies_count,
                (SELECT COUNT(*) FROM comments_likes WHERE comments_likes.comment_id=C.id AND comments_likes.is_like=true) AS likes_count,
                (SELECT COUNT(*) FROM comments_likes WHERE comments_likes.comment_id=C.id AND comments_likes.is_like=false) AS dislikes_count,
                C.created_at,
                C.updated_at,
                COALESCE((SELECT is_like FROM comments_likes WHERE comments_likes.comment_id=C.id AND comments_likes.user_id=?),2) AS is_liked
            FROM
                comments C
            JOIN users U
            ON U.id = C.user_id
            WHERE
                C.comment_id = ?
            LIMIT ? OFFSET ?;
        """;
        ArrayList<Comment> comments = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1,userId);
        s.setInt(2,commentId);
        s.setInt(3,perpage);
        s.setInt(4,(perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()){
            Comment c = new Comment();
            c.setId(res.getInt("comment_id"));
            c.setText(res.getString("text"));
            c.setUserId(res.getInt("user_id"));
            c.setUserName(res.getString("username"));
            c.setRepliesCount(res.getInt("replies_count"));
            c.setLikesCount(res.getInt("likes_count"));
            c.setDislikesCount(res.getInt("dislikes_count"));
            c.setCreatedAt(res.getTimestamp("created_at"));
            c.setUpdatedAt(res.getTimestamp("updated_at"));
            c.setCurrentUserLike(LikeState.values()[res.getInt("is_like")]);
            comments.add(c);
        }
        if (comments.isEmpty()) {
            return null;
        }
        return comments;
    }

    public static int createComment(String text,int videoId,int userId) throws Exception{
        String query = "INSERT INTO comments (text,video_id,user_id,created_at) VALUES (?,?,?,?)";

        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        s.setString(1,text);
        s.setInt(2,videoId);
        s.setInt(3,userId);
        s.setTimestamp(4,new Timestamp(LocalDateTime.now().getNano()));

        s.executeUpdate();
        var res = s.getGeneratedKeys();
        if (!res.next()) {
            return 0;
        }
        return res.getInt(1);
    }

    public static int createReply(String text,int videoId,int userId,int commentId) throws Exception{
        String query = "INSERT INTO comments (text,video_id,user_id,comment_id,created_at) VALUES (?,?,?,?)";

        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        s.setString(1,text);
        s.setInt(2,videoId);
        s.setInt(3,userId);
        s.setInt(4,commentId);
        s.setTimestamp(5,new Timestamp(LocalDateTime.now().getNano()));

        s.executeUpdate();
        var res = s.getGeneratedKeys();
        if (!res.next()) {
            return 0;
        }
        return res.getInt(1);
    }

    public static boolean editComment(String text,int id,int userId) throws Exception{
        String query = "UPDATE comments SET text=?,updated_at=? WHERE id=? AND user_id=?;";

        var s = DbManager.db().prepareStatement(query);

        s.setString(1,text);
        s.setTimestamp(2, new Timestamp(LocalDateTime.now().getNano()));
        s.setInt(3, id);
        s.setInt(3, userId);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount()<=0) {
            throw new ModelError("comment not found");
        }
        return true;
    }

    public static boolean deleteComment(int id,int userId) throws Exception{
        String query = "DELETE FROM comments SWHERE id=? AND user_id=?;";

        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, id);
        s.setInt(2, userId);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount()<=0) {
            throw new ModelError("comment not found");
        }
        return true;
    }

    public static boolean setCommentLike(boolean isLike,int commentId,int userId) throws Exception {
        String query = "INSERT INTO commments_likes (user_id,comment_id,is_like,created_at) VALUES (?,?,?,?);";
        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        s.setInt(1,userId);
        s.setInt(2,commentId);
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

    public static boolean removeCommentLike(int commentId,int userId) throws Exception {
        String query = "DELETE FROM commments_likes WHERE user_id=$1 AND comment_id=?;";
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1,userId);
        s.setInt(2,commentId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n<=0) {
            return false;
        }
        return true;
    }

}
