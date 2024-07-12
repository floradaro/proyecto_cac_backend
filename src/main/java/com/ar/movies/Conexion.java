package com.ar.movies;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private Connection connection;  

    public Conexion() {

        try {
         
            Class.forName("com.mysql.cj.jdbc.Driver");

            this.connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/peliculas_cac_java", 
                "root",  
                "" 
            );
        } catch (ClassNotFoundException e) {
            e.printStackTrace();  
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }

    public Connection getConnection() {
        return connection; 
    }


    public void close() {
        try {
           
            if (connection != null && !connection.isClosed()) {
                connection.close(); 
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
    }
}
