package youtube.server.database.migration;

public class M02_videos implements MigrationInterface {
    @Override
    public String getCreateQuery() {
        return """
           CREATE TABLE IF NOT EXISTS videos (
               id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
               title VARCHAR(128) NOT NULL,
               description VARCHAR(1024) NULL,
               video_path VARCHAR(36) NOT NULL UNIQUE,
               user_id BIGINT NOT NULL,
               created_at TIMESTAMP NOT NULL DEFAULT NOW(),
               updated_at TIMESTAMP NULL,
               view_count BIGINT DEFAULT 0,
               thumbnail_path VARCHAR(36) NULL,
               is_private BOOLEAN DEFAULT FALSE,
               FOREIGN KEY ("user_id") REFERENCES "users" ("id")
           );
        """;
    }

    @Override
    public String getDropQuery() {
        return "DROP TABLE IF EXISTS videos;";
    }
}
