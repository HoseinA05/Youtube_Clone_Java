package youtube.server.database.model;

import youtube.server.database.DbManager;

import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PlaylistModel {

    public static Playlist getPlaylistById(int playlistId) throws Exception {
        String query = """     
                    SELECT
                        P.id,
                        P.name,
                        P.text,
                        P.user_id,
                        P.created_at,
                        P.updated_at,
                        U.username,
                        (SELECT COUNT(*) FROM playlist_videos PV WHERE PV.playlist_id=P.id) AS video_count
                    FROM playlists P
                    JOIN users U
                    ON P.user_id = U.id
                    WHERE P.id=?;
                """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, playlistId);
        var res = s.executeQuery();
        if (!res.next()) {
            return null;
        }
        Playlist p = new Playlist();
        p.setId(res.getInt("id"));
        p.setName(res.getString("name"));
        p.setText(res.getString("text"));
        p.setUserId(res.getInt("user_id"));
        p.setCreatedAt(res.getTimestamp("created_at"));
        p.setUpdatedAt(res.getTimestamp("updated_at"));
        p.setCreatorUsername(res.getString("username"));
        p.setVideoCount(res.getInt("video_count"));
        return p;
    }

    public static ArrayList<Playlist> getUserPlaylist(int userId) throws Exception {
        String query = """
                    SELECT
                        P.id,
                        P.name,
                        P.text,
                        P.user_id,
                        P.created_at,
                        P.updated_at,
                        U.username,
                        (SELECT COUNT(*) FROM playlist_videos PV WHERE PV.playlist_id=P.id) AS video_count
                    FROM
                    playlists P
                    JOIN users U
                    ON U.id=P.user_id
                    WHERE
                        P.user_id=?;
                """;
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, userId);
        var res = s.executeQuery();
        ArrayList<Playlist> ps = new ArrayList<>();
        while (res.next()) {
            Playlist p = new Playlist();
            p.setId(res.getInt("id"));
            p.setName(res.getString("name"));
            p.setText(res.getString("text"));
            p.setUserId(res.getInt("user_id"));
            p.setCreatedAt(res.getTimestamp("created_at"));
            p.setUpdatedAt(res.getTimestamp("updated_at"));
            p.setCreatorUsername(res.getString("username"));
            p.setVideoCount(res.getInt("video_count"));
            ps.add(p);
        }
        if (ps.isEmpty()) {
            return null;
        }
        return ps;
    }

    public static int createPlaylist(String name, String text, int userId) throws Exception {
        String query = "INSERT INTO playlists (name,text,user_id,created_at) VALUES (?,?,?,?)";

        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        s.setString(1, name);
        s.setString(2, text);
        s.setInt(3, userId);
        s.setTimestamp(4, new Timestamp(LocalDateTime.now().getNano()));

        s.executeUpdate();
        var res = s.getGeneratedKeys();
        if (!res.next()) {
            throw new Exception("ooops");
        }
        return res.getInt(1);
    }

    public static boolean editPlaylist(String name, String text, int playlistId, int userId) throws Exception {
        String query = "UPDATE playlists SET name=?,text=?,updated_at=? WHERE id=? AND user_id=?;";

        var s = DbManager.db().prepareStatement(query);

        s.setString(1, name);
        s.setString(2, text);
        s.setTimestamp(3, new Timestamp(LocalDateTime.now().getNano()));
        s.setInt(4, playlistId);
        s.setInt(5, userId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new Exception("ooops");
        }
        return true;
    }

    public static boolean deletePlaylist(int playlistId, int userId) throws Exception {
        String query = "DELETE FROM playlists WHERE id=? AND user_id=?;";

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, playlistId);
        s.setInt(2, userId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new Exception("ooops");
        }
        return true;
    }
}
