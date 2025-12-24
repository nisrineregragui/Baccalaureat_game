package com.example.java_project.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private Connection connection;
    private final String url = "jdbc:mysql://localhost:3306/GAME";
    private final String user = "root";
    private final String video = "Moghiniadamo2025";

    private DatabaseConnection() {
        try {
            connection = DriverManager.getConnection(url, user, video);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else {
            try {
                if (instance.getConnection().isClosed()) {
                    instance = new DatabaseConnection();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    private void createTables() {
        String createCategoryTable = "CREATE TABLE IF NOT EXISTS categories (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL UNIQUE" +
                ");";

        String createWordTable = "CREATE TABLE IF NOT EXISTS words (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "value VARCHAR(255) NOT NULL," +
                "letter CHAR(1) NOT NULL," +
                "category_id INT," +
                "is_valid BOOLEAN DEFAULT FALSE," +
                "FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE" +
                ");";

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createCategoryTable);
            stmt.execute(createWordTable);

            String createPlayerTable = "CREATE TABLE IF NOT EXISTS players (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(255) NOT NULL UNIQUE," +
                    "score INT DEFAULT 0," +
                    "is_host BOOLEAN DEFAULT FALSE," +
                    "is_ready BOOLEAN DEFAULT FALSE" +
                    ");";
            stmt.execute(createPlayerTable);

            String createRoundTable = "CREATE TABLE IF NOT EXISTS rounds (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "letter CHAR(1)," +
                    "game_mode VARCHAR(20)," +
                    "duration INT," +
                    "is_ended BOOLEAN DEFAULT FALSE" +
                    ");";
            stmt.execute(createRoundTable);

            String createRoundPlayersTable = "CREATE TABLE IF NOT EXISTS round_players (" +
                    "round_id INT," +
                    "player_id INT," +
                    "PRIMARY KEY (round_id, player_id)," +
                    "FOREIGN KEY (round_id) REFERENCES rounds(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(createRoundPlayersTable);

            String createRoundCategoriesTable = "CREATE TABLE IF NOT EXISTS round_categories (" +
                    "round_id INT," +
                    "category_id INT," +
                    "PRIMARY KEY (round_id, category_id)," +
                    "FOREIGN KEY (round_id) REFERENCES rounds(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(createRoundCategoriesTable);

            String createGuessesTable = "CREATE TABLE IF NOT EXISTS guesses (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "player_id INT," +
                    "category_id INT," +
                    "word VARCHAR(255)," +
                    "FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(createGuessesTable);
            System.out.println("Tables checked/created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
