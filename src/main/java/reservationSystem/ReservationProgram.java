package reservationSystem;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@AllArgsConstructor
public class ReservationProgram {

  public String userName;
  public DataSource dataSource;

  public void assignSeat() {
    try(Connection connection = dataSource.getConnection()) {
      // Get available empty seat
      PreparedStatement preparedStatement = connection.prepareStatement("SELECT seat_id FROM " +
        "seats WHERE user_name IS NULL LIMIT 1");
      ResultSet rs = preparedStatement.executeQuery();
      String seatId;
      if(rs.next()) {
        seatId = rs.getString("seat_id");
      } else {
        System.out.println("No seat available");
        return;
      }

      // Assign seat to user;
      preparedStatement = connection.prepareStatement("UPDATE seats SET user_name = ? WHERE seat_id = ?");
      preparedStatement.setString(1, userName);
      preparedStatement.setString(2, seatId);

      preparedStatement.executeUpdate();
      System.out.println(String.format("Seat %s has been allotted to user %s", seatId, userName));
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
