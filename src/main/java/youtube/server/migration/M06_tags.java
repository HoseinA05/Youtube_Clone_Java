package youtube.server.migration;

public class M06_tags implements MigrationInterface {
    @Override
    public String getCreateQuery() {
        return """
            CREATE TABLE IF NOT EXISTS tags (
                id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
                name VARCHAR(64) NOT NULL UNIQUE,
                created_at TIMESTAMP NOT NULL DEFAULT NOW()
            );
        """;
    }

    @Override
    public String getDropQuery() {
        return "DROP TABLE IF NOT EXISTS tags;";
    }
}
