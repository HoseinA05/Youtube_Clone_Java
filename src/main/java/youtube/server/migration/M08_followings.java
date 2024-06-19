package youtube.server.migration;

public class M08_followings implements MigrationInterface {
    @Override
    public String getCreateQuery() {
        return """
            CREATE TABLE IF NOT EXISTS followings (
                id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                follower_id BIGINT NOT NULL,
                following_id BIGINT NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                FOREIGN KEY (follower_id) REFERENCES users (id) ON DELETE CASCADE,
                FOREIGN KEY (following_id) REFERENCES users (id) ON DELETE CASCADE,
                UNIQUE (follower_id,following_id)
            );
        """;
    }

    @Override
    public String getDropQuery() {
        return "DROP TABLE IF NOT EXISTS followings;";
    }
}
