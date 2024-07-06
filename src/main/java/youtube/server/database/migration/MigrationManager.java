package youtube.server.database.migration;

import youtube.server.database.DbManager;

import java.sql.SQLException;

public class MigrationManager {
    private static String[] names = {
        "users",
        "videos",
        "likes",
        "comments",
        "commentsLikes",
        "tags",
        "videoTags",
        "followings",
        "playlist",
        "playlistVideos",
        "m11_histories",
    };
    private static MigrationInterface[] ms = {
        new M01_users(),
        new M02_videos(),
        new M03_likes(),
        new M04_comments(),
        new M05_commentsLikes(),
        new M06_tags(),
        new M07_videoTags(),
        new M08_followings(),
        new M09_playlist(),
        new M10_playlistVideos(),
        new M11_histories(),
    };

    public static void migrateAllTables(){
        for (int i = 0; i < ms.length; i++) {
            String query =  ms[i].getCreateQuery();

            System.out.println("migrating " + names[i]);
            try {
                DbManager.db().createStatement().execute(query);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void dropAllTables(){
        for (int i = ms.length-1; i >= 0; i--) {
            String query = ms[i].getDropQuery();

            System.out.println("droping " + names[i]);
            try {
                DbManager.db().createStatement().execute(query);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
//        MigrationManager.dropAllTables();
        MigrationManager.migrateAllTables();
    }
}
