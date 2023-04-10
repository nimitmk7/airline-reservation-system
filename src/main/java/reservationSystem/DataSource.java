package reservationSystem;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource {
  private static HikariConfig config;
  private static HikariDataSource ds;

  public DataSource(String jdbcUrl) {
    config = new HikariConfig();
    config.setJdbcUrl(jdbcUrl);
    config.setMaximumPoolSize(30);
    config.setMinimumIdle(5);
    config.setDriverClassName("com.mysql.cj.jdbc.Driver");
    ds = new HikariDataSource(config);
  }

  public Connection getConnection() throws SQLException {
    return ds.getConnection();
  }
}
