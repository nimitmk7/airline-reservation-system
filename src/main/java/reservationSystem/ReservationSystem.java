package reservationSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;

public class ReservationSystem {

  private DataSource dataSource;
  private void runProgram() {
    List<User> users = getAeroplanePassengers();
    ExecutorService executor= Executors.newFixedThreadPool(users.size());
    List<CompletableFuture<Void>> futures = new ArrayList<>();
    for(User user: users) {
      CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        ReservationProgram reservationProgram = new ReservationProgram(user.getName(), dataSource);
        reservationProgram.assignSeat();
      }, executor);
      futures.add(future);
    }

    try {
      CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
    } catch (Exception e) {
      System.out.println("Thread interrupted");
    }
    executor.shutdown();
  }

  private void publishResults() {
    try (Connection connection = dataSource.getConnection();
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) AS filled_seats FROM seats" +
        " WHERE user_name IS NOT NULL");) {
      ResultSet resultSet = preparedStatement.executeQuery();
      if(resultSet.next()) {
        int filledSeats = resultSet.getInt("filled_seats");
        System.out.println(String.format("Filled_seats: %s/24", filledSeats));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private List<User> getAeroplanePassengers() {
    List<User> aeroplanePassengers = new ArrayList<>();
    try(Connection conn = dataSource.getConnection()) {
      PreparedStatement preparedStatement = conn.prepareStatement("SELECT * from users");
      ResultSet resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        User user = new User(id, name);
        aeroplanePassengers.add(user);
      }
    } catch(SQLException e) {
      System.out.println(e.getMessage());
      System.out.println(e.getStackTrace());
      System.exit(1);
    }

    return aeroplanePassengers;
  }

  // Setup method
  private void initialize() {
    String configFilePath = "src/main/java/config.properties";
    try {
      // Get properties from config file
      FileInputStream propsInput = new FileInputStream(configFilePath);
      Properties prop = new Properties();
      prop.load(propsInput);
      this.dataSource = new DataSource(prop.getProperty("DB_URL"));
      /*
      // Initialize database
      DatabaseInitializer databaseInitializer = new DatabaseInitializer(prop.getProperty("DB_URL"),
        prop.getProperty("CREATE_TABLE_QUERY"), prop.getProperty("INSERT_RECORDS_QUERY"));
      databaseInitializer.setupDatabase();
       */


    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void main(String args[]) {
    ReservationSystem reservationSystem = new ReservationSystem();
    reservationSystem.initialize();
    reservationSystem.runProgram();
    reservationSystem.publishResults();
  }
}
