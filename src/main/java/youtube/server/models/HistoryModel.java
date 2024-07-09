package youtube.server.models;

import youtube.server.database.DbManager;

import java.sql.Statement;
import java.util.ArrayList;

public class HistoryModel {

    public static void createHistory(int videoId, int userId) throws Exception {
        String query = "INSERT INTO histories (video_id,user_id) VALUES (?,?)";

        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        s.setInt(1, videoId);
        s.setInt(2, userId);
        s.executeUpdate();
        var res = s.getGeneratedKeys();
        if (!res.next()) {
            throw new Exception("ooops");
        }
    }

    public static void removeHistory(int historyId,int userId) throws Exception {
        String query = "DELETE FROM histories WHERE id=? AND user_id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, historyId);
        s.setInt(2, userId);
        s.executeUpdate();
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("no history found");
        }
    }

    public static void removeAllHistory(int userId) throws Exception {
        String query = "DELETE FROM histories WHERE  user_id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, userId);
        s.executeUpdate();
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("no history found");
        }
    }

    public static ArrayList<History> getHistory(int userId, int perpage, int page) throws Exception {
        String query = """
                    SELECT
                        H.id,
                        H.video_id,
                        H.user_id,
                        U.username,
                        V.title,
                        V.thumbnail_path,
                        V.created_at,
                        H.created_at AS watched_at
                    FROM
                        histories AS H
                    JOIN users AS U ON U.id = user_id
                    JOIN videos AS V ON V.id=H.video_id
                    WHERE H.user_id=?
                    ORDER BY H.created_at
                    LIMIT ? OFFSET ?;
                """;
        ArrayList<History> hs = new ArrayList<>();
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, userId);
        s.setInt(2, perpage);
        s.setInt(3, (perpage * page) - perpage);
        var res = s.executeQuery();
        while (res.next()) {
            History h = new History();
            h.setId(res.getInt("id"));
            h.setVideoId(res.getInt("video_id"));
            h.setUserId(res.getInt("user_id"));
            h.setUsername(res.getString("username"));
            h.setVideoTitle(res.getString("title"));
            h.setVideoThumbnail(res.getString("thumbnail_path"));
            h.setVideoCreatedAt(res.getTimestamp("created_at"));
            h.setWatchedAt(res.getTimestamp("watched_at"));
            hs.add(h);
        }
        if (hs.isEmpty()) {
            return null;
        }
        return hs;
    }
}
