package cookbook;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A class that handles Database connection.
 * 
 * @author Al Hussein Al Ahmed
 */
public class Database {
  public Connection connection;
  public Statement statement;
  public static Database instance;

  public static synchronized Database getInstance() throws SQLException, ClassNotFoundException {
    if (instance == null) {
      instance = new Database();
    }
    return instance;
  }

  private Database() throws SQLException, ClassNotFoundException {
    this.connect("jdbc:mysql://localhost:3306/cookbook_schema", "root", "Castle123");

  }

  public void connect(String url, String username, String password) throws SQLException,
      ClassNotFoundException {
    Class.forName("com.mysql.cj.jdbc.Driver");
    connection = DriverManager.getConnection(url, username, password);
    statement = connection.createStatement();
  }

  public void disconnect() throws SQLException {
    // Close the connection
    if (statement != null) {
      statement.close();
    }
    if (connection != null) {
      connection.close();
    }
  }
}
