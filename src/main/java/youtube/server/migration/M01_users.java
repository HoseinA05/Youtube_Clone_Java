package youtube.server.migration;

public class M01_users implements MigrationInterface {
    @Override
    public String getCreateQuery() {
        return """
            CREATE TABLE IF NOT EXISTS users (
                id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                email VARCHAR(128) NOT NULL UNIQUE,
                username VARCHAR(128) NOT NULL,
                about_me VARCHAR(512) NULL,
                hashed_password VARCHAR(128) NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                updated_at TIMESTAMP NULL,
                profile_photo BYTEA NULL,
                channel_photo BYTEA NULL
            );
        """;
    }

    @Override
    public String getDropQuery() {
        return "DROP TABLE IF NOT EXISTS users;";
    }
}