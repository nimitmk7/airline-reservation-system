package reservationSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseInitializer {

  private String dbUrl;
  private String createTableQuery;
  private String insertRecordsQuery;
  private DataSource dataSource;

  public DatabaseInitializer(String dbUrl, String createTableQuery, String insertRecordsQuery) {
    this.dbUrl = dbUrl;
    this.dataSource = new DataSource(dbUrl);
    this.createTableQuery = createTableQuery;
    this.insertRecordsQuery = insertRecordsQuery;
  }

  protected void setupDatabase() {
    try(Connection connection = dataSource.getConnection()) {
      // Create seat allocation table
      PreparedStatement ps = connection.prepareStatement(createTableQuery);
      ps.executeUpdate();

      // Insert records;
      ps = connection.prepareStatement(insertRecordsQuery);
      ps.executeUpdate();


    } catch (SQLException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
