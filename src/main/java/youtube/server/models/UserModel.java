package youtube.server.models;

import org.postgresql.util.PSQLException;
import youtube.server.database.DbManager;

import java.security.MessageDigest;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;

public class UserModel {

    public static String[] getThumbnailsById(int id) throws Exception {
        String query = "SELECT profile_photo_path,channel_photo_path FROM users WHERE id=?";
        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, id);
        var res = s.executeQuery();
        if (!res.next()) {
            return null;
        }
        return new String[]{
                res.getString("profile_photo_path"),
                res.getString("channel_photo_path")
        };
    }

    public static User authGetUserById(int id, int currentUserId) throws Exception {
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
            // throw new ModelError("user not found");
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

    public static User getUserById(int id) throws Exception {
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
            // throw new ModelError("user not found");
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
            throw new ModelError("incorrect username or password");
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
        s.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));

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

    public static void setUserProfilePhoto(String profilePhotoPath, int oldId) throws Exception {
        String query = "UPDATE users profile_photo_path=?,updated_at=? WHERE id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setString(1, profilePhotoPath);
        s.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
        s.setInt(3, oldId);
        s.executeUpdate();
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("user not found");
        }
    }

    public static void setUserChannelPhoto( String channelPhotoPath, int oldId) throws Exception {
        String query = "UPDATE users channel_photo_path=?,updated_at=? WHERE id=?;";
        var s = DbManager.db().prepareStatement(query);
        s.setString(1, channelPhotoPath);
        s.setTimestamp(2, new Timestamp(new java.util.Date().getTime()));
        s.setInt(3, oldId);
        s.executeUpdate();
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("user not found");
        }
    }


    public static void editUser(String newUsername, String newPassword, String newAboutMe, int oldId, String oldPassword) throws Exception {
        String query = "UPDATE users SET username=?,hashed_password=?,about_me=?,updated_at=? WHERE id=? AND hashed_password=?;";

        var s = DbManager.db().prepareStatement(query);
        var md5 = MessageDigest.getInstance("MD5");
        String newHashedPassword = Base64.getEncoder().encodeToString(md5.digest(newPassword.getBytes()));
        String oldHashedPassword = Base64.getEncoder().encodeToString(md5.digest(oldPassword.getBytes()));


        s.setString(1, newUsername);
        s.setString(2, newHashedPassword);
        s.setString(3, newAboutMe);
        s.setTimestamp(4, new Timestamp(new java.util.Date().getTime()));
        s.setInt(5, oldId);
        s.setString(6, oldHashedPassword);

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
    }

    public static void deleteUser(int id, String password) throws Exception {
        String query = "DELETE FROM users WHERE id=? AND hashed_password=?;";

        var s = DbManager.db().prepareStatement(query);
        var md5 = MessageDigest.getInstance("MD5");
        String hashedPassword = Base64.getEncoder().encodeToString(md5.digest(password.getBytes()));

        s.setInt(1, id);
        s.setString(2, hashedPassword);

        s.executeUpdate();
        System.out.println(s.getUpdateCount());
        if (s.getUpdateCount() <= 0) {
            throw new ModelError("incorrect user id or password");
        }
    }

    public static void setFollowing(int followerId, int followingId) throws Exception {
        String query = "INSERT INTO followings (follower_id,following_id,created_at) VALUES (?,?,?);";
        var s = DbManager.db().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

        s.setInt(1, followerId);
        s.setInt(2, followingId);
        s.setTimestamp(3, new Timestamp(new java.util.Date().getTime()));

        try {
            s.executeUpdate();
        } catch (PSQLException ee) {
            System.out.println(ee.getMessage());
            if (ee.getSQLState().equals("23503")) { // foreign key violation
                throw new ModelError(ee.getMessage());
                // throw new ModelError("user not found");
            }
            if (ee.getSQLState().equals("23505")) { // unique key violation
                throw new ModelError("user already followed");
            }
            throw ee;
        }
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("oops");
        }
        NotificationModel.Notify.newSubscriber(followingId, followerId);
    }

    public static void removeFollowing(int followerId, int followingId) throws Exception {
        String query = "DELETE FROM followings WHERE follower_id=? AND following_id=?;";
        var s = DbManager.db().prepareStatement(query);

        s.setInt(1, followerId);
        s.setInt(2, followingId);

        s.executeUpdate();
        var n = s.getUpdateCount();
        if (n <= 0) {
            throw new ModelError("user not exist or not followed");
        }
        NotificationModel.Notify.newUnsubscribed(followingId, followerId);
    }

    public static ArrayList<User> getUserFollowings(int userId) throws Exception {
        String query = """     
                    SELECT
                        U.id,
                        U.username,
                        COALESCE(U.profile_photo_path,'') AS profile_photo_path
                    FROM
                        users U
                    JOIN
                        followings F ON F.following_id=U.id
                    WHERE F.follower_id=?;
                """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, userId);
        ArrayList<User> users = new ArrayList<>();
        var res = s.executeQuery();
        while (res.next()) {
            User u = new User();
            u.setId(res.getInt("id"));
            u.setUsername(res.getString("username"));
            u.setProfilePhotoPath(res.getString("profile_photo_path"));
            u.setChannelPhotoPath(res.getString("channel_photo_path"));
            users.add(u);
        }
        if (users.isEmpty()) {
            return null;
        }
        return users;
    }

    public static ArrayList<User> getUserFollowers(int userId, int page, int perpage) throws Exception {
        String query = """     
                    SELECT
                        U.id,
                        U.username,
                        COALESCE(U.profile_photo_path,'') AS profile_photo_path
                    FROM
                        users U
                    JOIN
                        followings F ON F.follower_id=U.id
                    WHERE F.following_id=?
                    LIMIT ? OFFSET ?;
                """;

        var s = DbManager.db().prepareStatement(query);
        s.setInt(1, userId);
        s.setInt(2, perpage);
        s.setInt(3, (perpage * page) - perpage);

        ArrayList<User> users = new ArrayList<>();
        var res = s.executeQuery();
        while (res.next()) {
            User u = new User();
            u.setId(res.getInt("id"));
            u.setUsername(res.getString("username"));
            u.setProfilePhotoPath(res.getString("profile_photo_path"));
            u.setChannelPhotoPath(res.getString("channel_photo_path"));
            users.add(u);
        }
        if (users.isEmpty()) {
            return null;
        }
        return users;
    }

}
