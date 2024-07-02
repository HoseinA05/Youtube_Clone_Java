package youtube.server.database.migration;

public interface MigrationInterface {
    String getCreateQuery();
    String getDropQuery();
}
