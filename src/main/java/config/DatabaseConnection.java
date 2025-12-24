package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/GAME";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection = null;


    // test connexion DbConnection
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connexion réussie à MySQL !");
            } catch (ClassNotFoundException e) {
                System.err.println("Driver non trouvé !");
            }
        }
        return connection;
    }
}