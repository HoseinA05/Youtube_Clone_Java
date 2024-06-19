package youtube.server.migration;

public class M04_comments implements MigrationInterface {
    @Override
    public String getCreateQuery() {
        return """
            CREATE TABLE IF NOT EXISTS comments (
                id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                text VARCHAR(1024) NOT NULL,
                user_id BIGINT NOT NULL,
                video_id BIGINT NOT NULL,
                comment_id BIGINT NULL, -- if be null shows its not a reply
                created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                updated_at TIMESTAMP NULL,
                FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
                FOREIGN KEY (video_id) REFERENCES videos (id) ON DELETE CASCADE,
                FOREIGN KEY (comment_id) REFERENCES comments (id) ON DELETE CASCADE
            );
        """;
    }

    @Override
    public String getDropQuery() {
        return "DROP TABLE IF NOT EXISTS comments;";
    }
}