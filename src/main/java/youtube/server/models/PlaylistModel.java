package youtube.server.models;

import org.postgresql.util.PSQLException;
import youtube.server.database.DbManager;

import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PlaylistModel {

    public static String getThumbnailById(int id) throws Exception {
        String query = "SELECT thumbnail_path FROM playlists WHERE id=?";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, id);
        var res = s.executeQuery();
        if (!res.next()) {
            return "";
        }
        return res.getString("thumbnail_path");
    }

    public static int getPlaylistIdByName(String playlistName,int userId) throws Exception {
        String query = """
                    SELECT
                        P.id
                    FROM playlists P
                    WHERE P.name=? AND P.user_id=?;
                """;

        var s = DbManager.db().prepareStatement(query);
        s.setString(1, playlistName);
        s.setInt(2, userId);
        var res = s.executeQuery();
        if (!res.next()) {
            throw new ModelError("playlist not found");
        }
        return res.getInt("id");
    }

    // public static Playlist getPlaylistByName(String playlistName) throws Exception {
    //     String query = """
    //                 SELECT
    //                     P.id,
    //                     P.name,
    //                     P.text,
    //                     COALESCE(P.thumbnail_path,'') AS thumbnail_path,
    //                     P.user_id,
    //                     P.created_at,
    //                     P.updated_at,
    //                     U.username,
    //                     (SELECT COUNT(*) FROM playlist_videos PV WHERE PV.playlist_id=P.id) AS video_count
    //                 FROM playlists P
    //                 JOIN users U
    //                 ON P.user_id = U.id
    //                 WHERE P.name=?;
    //             """;
    //
    //     var s = DbManager.db().prepareStatement(query);
    //     s.setString(1, playlistName);
    //     var res = s.executeQuery();
    //     if (!res.next()) {
    //         return null;
    //     }
    //     Playlist p = new Playlist();
    //     p.setId(res.getInt("id"));
    //     p.setName(res.getString("name"));
    //     p.setText(res.getString("text"));
    //     p.setThumbnailPath(res.getString("thumbnail_path"));
    //     p.setUserId(res.getInt("user_id"));
    //     p.setCreatedAt(res.getTimestamp("created_at"));
    //     p.setUpdatedAt(res.getTimestamp("updated_at"));
    //     p.setCreatorUsername(res.getString("username"));
    //     p.setVideoCount(res.getInt("video_count"));
    //     return p;
    // }

    public static Playlist getPlaylistById(int playlistId) throws Exception {
        String query = """     
                    SELECT
                        P.id,
                        P.name,
                        P.text,
                        COALESCE(P.thumbnail_path,'') AS thumbnail_path,
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
        p.setThumbnailPath(res.getString("thumbnail_path"));
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
                        COALESCE(P.thumbnail_path,'') AS thumbnail_path,
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
            p.setThumbnailPath(res.getString("thumbnail_path"));
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

    public static int createPlaylist(String name, String text, int userId,boolean isDeletable) throws Exception {
        String query = "INSERT INTO playlists (name,text,user_id,created_at,is_deletable) VALUES (?,?,?,?,?)";
        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        s.setString(1, name);
        s.setString(2, text);
        s.setInt(3, userId);
        s.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
        s.setBoolean(5, isDeletable);

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
        PlaylistModel.addPlaylistCreatorAsAdmin(id, userId);
        return id;
    }

    public static void setPlaylistThumbnail(String thumbnailPath, int playlistId, int userId) throws Exception {
        String query = "UPDATE playlists SET thumbnail_path=?,updated_at=? WHERE id=? AND user_id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setString(1, thumbnailPath);
        s.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
        s.setInt(3, playlistId);
        s.setInt(4, userId);
        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("playlist or user not found or not belong to user");
        }
    }

    public static void editPlaylist(String name, String text, int playlistId, int userId) throws Exception {
        String query = "UPDATE playlists SET name=?,text=?,updated_at=? WHERE id=? AND user_id=? AND is_deletable=true;";
        var s = DbManager.db().prepareStatement(query);

        s.setString(1, name);
        s.setString(2, text);
        s.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
        s.setInt(4, playlistId);
        s.setInt(5, userId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("playlist or user not found or not belong to user");
        }
    }

    public static void deletePlaylist(int playlistId, int userId) throws Exception {
        String query = "DELETE FROM playlists WHERE id=? AND user_id=? AND is_deletable=true;";

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, playlistId);
        s.setInt(2, userId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("playlist or user not found or not belong to user or is not deletable");
        }
    }

    private static void addPlaylistCreatorAsAdmin(int playlistId, int playlistCreatorUserId) throws Exception {
        String query = """
                INSERT INTO playlist_admins
                    (user_id,playlist_id,created_at)
                SELECT
                    ?,?,?
                WHERE
                    (SELECT user_id FROM playlists WHERE playlists.id=?) = ?;
                """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, playlistCreatorUserId);
        s.setInt(2, playlistId);
        s.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
        s.setInt(4, playlistId);
        s.setInt(5, playlistCreatorUserId);

        try {
            s.executeUpdate();
        } catch (PSQLException ee) {
            System.out.println(ee.getMessage());
            if (ee.getSQLState().equals("23503")) { // foreign key violation
                throw new ModelError("collaborator or playlist not found");
            }
            if (ee.getSQLState().equals("23505")) { // foreign key violation
                throw new ModelError("collaborator already added");
            }
            throw ee;
        }

        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("playlist not found or not belong to user");
        }
    }

    public static void addAdminToPlaylist(int playlistId, int playlistCreatorUserId, int newAdminUserId) throws Exception {
        String query = """
                INSERT INTO playlist_admins
                    (user_id,playlist_id,created_at)
                SELECT
                    ?,?,?
                WHERE
                    (SELECT user_id FROM playlists WHERE playlists.id=? AND is_deletable=true) = ?;
                """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, newAdminUserId);
        s.setInt(2, playlistId);
        s.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));
        s.setInt(4, playlistId);
        s.setInt(5, playlistCreatorUserId);

        try {
            s.executeUpdate();
        } catch (PSQLException ee) {
            System.out.println(ee.getMessage());
            if (ee.getSQLState().equals("23503")) { // foreign key violation
                throw new ModelError("collaborator or playlist not found");
            }
            if (ee.getSQLState().equals("23505")) { // foreign key violation
                throw new ModelError("collaborator already added");
            }
            throw ee;
        }

        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("playlist not found or not belong to user");
        }
        if (newAdminUserId != playlistCreatorUserId) {
            NotificationModel.Notify.userAddedAsCollaborator(newAdminUserId, playlistCreatorUserId, playlistId);
        }
    }

    public static void removeAdminFromPlaylist(int playlistId, int playlistCreatorUserId, int adminUserId) throws Exception {
        if (adminUserId == playlistCreatorUserId) {
            throw new RuntimeException("playlist creator should not be removed from admins!");
        }
        String query = "DELETE FROM playlist_admins WHERE playlist_id=? AND user_id=? AND EXISTS(SELECT user_id FROM playlists WHERE playlists.user_id=? AND playlists.id=?);";

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, playlistId);
        s.setInt(2, adminUserId);
        s.setInt(3, playlistCreatorUserId);
        s.setInt(4, playlistId);

        s.executeUpdate();

        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("failed to remove playlist admin");
        }
        NotificationModel.Notify.userRemovedAsCollaborator(adminUserId, playlistCreatorUserId, playlistId);
    }

    public static void addVideoToLikeList(int videoId, int userId) throws Exception {
        String query = """
                INSERT INTO playlist_videos
                        (playlist_id,video_id,admin_id)
                VALUES
                    (
                        (SELECT playlists.id FROM playlists WHERE playlists.name=? AND playlists.user_id=?),
                        ?,
                        ?
                    );
                """;
        var s = DbManager.db().prepareStatement(query);

        s.setString(1, "Liked videos");
        s.setInt(2, userId);
        s.setInt(3, videoId);
        s.setInt(4, userId);

        try {
            s.executeUpdate();
        } catch (PSQLException ee) {
            System.out.println(ee.getMessage());
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
    }

}
