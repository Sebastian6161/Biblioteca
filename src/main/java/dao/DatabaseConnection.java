package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;

    private static final String URL =
            "jdbc:mysql://localhost:3306/biblioteca?useSSL=false&serverTimezone=America/Santiago";

    private static final String USER = "root";
    private static final String PASSWORD = "Admin1234!";

    private Connection connection;

    private DatabaseConnection() {
        conectar();
    }

    private void conectar() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: "
                    + e.getMessage());
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }

        return instance;
    }

    public Connection getConnection() throws SQLException {

        if (connection == null || connection.isClosed()) {
            conectar();
        }

        return connection;
    }
}