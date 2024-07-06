package youtube.server.database.migration;

public class M09_playlist implements MigrationInterface {
    @Override
    public String getCreateQuery() {
        return """
            CREATE TABLE IF NOT EXISTS playlists (
                id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                name VARCHAR(128) NOT NULL,
                text VARCHAR(1024) NOT NULL,
                user_id BIGINT NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                updated_at TIMESTAMP NULL,
                FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
            );
        """;
    }

    @Override
    public String getDropQuery() {
        return "DROP TABLE IF EXISTS playlists;";
    }
}