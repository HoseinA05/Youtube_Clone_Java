package youtube.server.migration;

public interface MigrationInterface {
    String getCreateQuery();
    String getDropQuery();
}
