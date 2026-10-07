package main;

import dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class PruebaConexion {

    public static void main(String[] args) {

        try {
            Connection conexion =
                    DatabaseConnection.getInstance().getConnection();

            if (conexion != null && !conexion.isClosed()) {
                System.out.println("Conexión exitosa con la base de datos biblioteca.");
            }

        } catch (SQLException e) {
            System.err.println("Error al comprobar la conexión: "
                    + e.getMessage());
        }
    }
}