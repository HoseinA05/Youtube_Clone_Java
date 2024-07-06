package youtube.server.database.model;

import org.postgresql.util.PSQLException;
import youtube.server.database.DbManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Base64;

public class UserModel {

    public static User authGetUserById(int id, int currentUserId) throws SQLException {
        String query = """
                    SELECT
                        id,
                        email,
                        username,
                        COALESCE(about_me,'') AS about_me,
                        COALESCE(profile_photo_path,'') AS profile_photo_path,
                        COALESCE(channel_photo_path,'') AS channel_photo_path,
                        created_at,
                        COALESCE((SELECT SUM(V.view_count) FROM videos V WHERE V.user_id=users.id GROUP BY user_id),0) AS view_count,
                        (SELECT COUNT(*) FROM videos WHERE user_id=users.id) AS upload_count,
                        (SELECT COUNT(*) FROM followings WHERE followings.following_id=users.id) AS subscriber_count,
                        (SELECT COUNT(*) FROM followings WHERE followings.follower_id=users.id) AS subscribing_count,
                        (SELECT EXISTS(SELECT id FROM followings WHERE follower_id=? AND following_id=users.id)) AS is_subbed
                    FROM users WHERE id=?;
                """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, currentUserId);
        s.setInt(2, id);
        var res = s.executeQuery();
        if (!res.next()) {
            return null;
        }
        User u = new User();
        u.setId(res.getInt("id"));
        u.setEmail(res.getString("email"));
        u.setUsername(res.getString("username"));
        u.setAboutMe(res.getString("about_me"));
        u.setProfilePhotoPath(res.getString("profile_photo_path"));
        u.setChannelPhotoPath(res.getString("channel_photo_path"));
        u.setCreatedAt(res.getTimestamp("created_at"));
        u.setTotalViews(res.getInt("view_count"));
        u.setUploadCount(res.getInt("upload_count"));
        u.setSubscribers(res.getInt("subscriber_count"));
        u.setSubscribings(res.getInt("subscribing_count"));
        u.setCurrentUserSubscribed(res.getBoolean("is_subbed"));
        return u;
    }

    public static User getUserById(int id) throws SQLException {
        String query = """     
                    SELECT
                        id,
                        email,
                        username,
                        COALESCE(about_me,'') AS about_me,
                        COALESCE(profile_photo_path,'') AS profile_photo_path,
                        COALESCE(channel_photo_path,'') AS channel_photo_path,
                        created_at,
                        COALESCE((SELECT SUM(V.view_count) FROM videos V WHERE V.user_id=users.id GROUP BY user_id),0) AS view_count,
                        (SELECT COUNT(*) FROM videos WHERE user_id=users.id) AS upload_count,
                        (SELECT COUNT(*) FROM followings WHERE followings.following_id=users.id) AS subscriber_count,
                        (SELECT COUNT(*) FROM followings WHERE followings.follower_id=users.id) AS subscribing_count
                    FROM users WHERE id=?;
                """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, id);
        var res = s.executeQuery();
        if (!res.next()) {
            return null;
        }
        User u = new User();
        u.setId(res.getInt("id"));
        u.setEmail(res.getString("email"));
        u.setUsername(res.getString("username"));
        u.setAboutMe(res.getString("about_me"));
        u.setProfilePhotoPath(res.getString("profile_photo_path"));
        u.setChannelPhotoPath(res.getString("channel_photo_path"));
        u.setCreatedAt(res.getTimestamp("created_at"));
        u.setTotalViews(res.getInt("view_count"));
        u.setUploadCount(res.getInt("upload_count"));
        u.setSubscribers(res.getInt("subscriber_count"));
        u.setSubscribings(res.getInt("subscribing_count"));
        return u;
    }

    public static User getUserByPassword(String username, String password) throws Exception {
        String query = """     
                    SELECT
                        id,
                        username,
                        COALESCE(profile_photo_path,'') AS profile_photo_path,
                        COALESCE(channel_photo_path,'') AS channel_photo_path
                    FROM users WHERE username=? AND hashed_password=?;
                """;

        var s = DbManager.db().prepareStatement(query);
        var md5 = MessageDigest.getInstance("MD5");
        String hashedPassword = Base64.getEncoder().encodeToString(md5.digest(password.getBytes()));

        System.out.println(hashedPassword);
        s.setString(1, username);
        s.setString(2, hashedPassword);

        var res = s.executeQuery();
        if (!res.next()) {
            return null;
        }
        User u = new User();
        u.setId(res.getInt("id"));
        u.setUsername(res.getString("username"));
        u.setProfilePhotoPath(res.getString("profile_photo_path"));
        u.setChannelPhotoPath(res.getString("channel_photo_path"));
        return u;
    }

    public static int createUser(String email, String username, String password) throws Exception {
        String query = "INSERT INTO users (email,username,hashed_password,created_at) VALUES (?,?,?,?)";

        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        var md5 = MessageDigest.getInstance("MD5");
        String hashedPassword = Base64.getEncoder().encodeToString(md5.digest(password.getBytes()));

        s.setString(1, email);
        s.setString(2, username);
        s.setString(3, hashedPassword);
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
            throw new Exception("ooops");
        }
        return res.getInt(1);
    }

    public static boolean editUser(String newUsername, String newPassword, String newAboutMe, String profilePhotoPath, String channelPhotoPath, int oldId, String oldPassword) throws Exception {
        String query = "UPDATE users SET username=?,hashed_password=?,about_me=?,profile_photo_path=?,channel_photo_path=?,updated_at=? WHERE id=? AND hashed_password=?;";

        var s = DbManager.db().prepareStatement(query);
        var md5 = MessageDigest.getInstance("MD5");
        String newHashedPassword = Base64.getEncoder().encodeToString(md5.digest(newPassword.getBytes()));
        String oldHashedPassword = Base64.getEncoder().encodeToString(md5.digest(oldPassword.getBytes()));


        s.setString(1, newUsername);
        s.setString(2, newHashedPassword);
        s.setString(3, newAboutMe);
        s.setString(4, profilePhotoPath);
        s.setString(5, channelPhotoPath);
        s.setTimestamp(6, new Timestamp(LocalDateTime.now().getNano()));
        s.setInt(7, oldId);
        s.setString(8, oldHashedPassword);

        try {
            s.executeUpdate();
        } catch (PSQLException ee) {
            if (ee.getSQLState().equals("23505")) {
                System.out.println("duplicate key !!!");
                throw new ModelError(ee.getMessage());
            }
            throw ee;
        }
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("user not found");
        }
        return true;
    }

    public static boolean deleteUser(int id, String password) throws Exception {
        String query = "DELETE FROM users WHERE id=? AND hashed_password=?;";

        var s = DbManager.db().prepareStatement(query);
        var md5 = MessageDigest.getInstance("MD5");
        String hashedPassword = Base64.getEncoder().encodeToString(md5.digest(password.getBytes()));

        s.setInt(1, id);
        s.setString(2, hashedPassword);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("user not found");
        }
        return true;
    }

    public static boolean setFollowing(int followerId, int followingId) throws Exception {
        String query = "INSERT INTO followings (follower_id,following_id,created_at) VALUES (?,?,?);";
        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        s.setInt(1, followerId);
        s.setInt(2, followingId);
        s.setTimestamp(3, new Timestamp(LocalDateTime.now().getNano()));

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

    public static boolean removeFollowing(int followerId, int followingId) throws Exception {
        String query = "DELETE FROM followings WHERE follower_id=$1 AND following_id=?;";
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, followerId);
        s.setInt(2, followingId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            return false;
        }
        return true;
    }
}
