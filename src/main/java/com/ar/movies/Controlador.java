package com.ar.movies;

import com.fasterxml.jackson.databind.ObjectMapper; 
import javax.servlet.ServletException; 
import javax.servlet.annotation.WebServlet; 
import javax.servlet.http.HttpServlet; 
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse;
import java.io.IOException; 
import java.sql.*;
import java.util.ArrayList; 
import java.util.List; 

@WebServlet("/peliculas") 
public class Controlador extends HttpServlet { 

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*"); 
        response.setHeader("Access-Control-Allow-Methods", "*"); 
        response.setHeader("Access-Control-Allow-Headers", "Content-Type"); 
        Conexion conexion = new Conexion(); 
        Connection conn = conexion.getConnection();  

        try {
            ObjectMapper mapper = new ObjectMapper();  
            Pelicula pelicula = mapper.readValue(request.getInputStream(), Pelicula.class);  
        
            String query = "INSERT INTO peliculas (titulo, genero, duracion, imagen) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);  
        

            statement.setString(1, pelicula.getTitulo());
            statement.setString(2, pelicula.getGenero());
            statement.setString(3, pelicula.getDuracion());
            statement.setString(4, pelicula.getImagen());
        
            statement.executeUpdate();  
        
            ResultSet rs = statement.getGeneratedKeys();
            if (rs.next()) {
                Long idPeli = rs.getLong(1);  
                
                response.setContentType("application/json");  
                String json = mapper.writeValueAsString(idPeli); 
                response.getWriter().write(json); 
            }
            
            response.setStatus(HttpServletResponse.SC_CREATED); 
        } catch (SQLException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  
        } catch (IOException e) {
            e.printStackTrace();  
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  // Configurar el código de estado de la respuesta HTTP como 500 (INTERNAL_SERVER_ERROR)
        } finally {
            conexion.close();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*"); 
        response.setHeader("Access-Control-Allow-Methods", "*"); 
        response.setHeader("Access-Control-Allow-Headers", "Content-Type"); 
        Conexion conexion = new Conexion();  
        Connection conn = conexion.getConnection(); 

        try {
        
            String idParam = request.getParameter("id");
           
            String query;

            if (idParam != null) {
              
                query = "SELECT * FROM peliculas WHERE id_pelicula = ?";
            } else {

                query = "SELECT * FROM peliculas";
            }
            PreparedStatement statement = conn.prepareStatement(query);
        
            if (idParam != null) {
                statement.setInt(1, Integer.parseInt(idParam)); 
            }
            ResultSet resultSet = statement.executeQuery();  

            List<Pelicula> peliculas = new ArrayList<>();  

            while (resultSet.next()) {
                
                Pelicula pelicula = new Pelicula(
                    resultSet.getInt("id_pelicula"),
                    resultSet.getString("titulo"),  
                    resultSet.getString("genero"),
                    resultSet.getString("duracion"),
                    resultSet.getString("imagen")
                );
                peliculas.add(pelicula); 
            }

            ObjectMapper mapper = new ObjectMapper();  
            String json = mapper.writeValueAsString(peliculas); 

            response.setContentType("application/json");  
            response.getWriter().write(json); 
        } catch (SQLException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  
        } finally {
            conexion.close(); 
        }
    }
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type"); 
        Conexion conexion = new Conexion();  
        Connection conn = conexion.getConnection(); 

        try {
            ObjectMapper mapper = new ObjectMapper(); 
            Pelicula pelicula = mapper.readValue(request.getInputStream(), Pelicula.class);  

            String query = "UPDATE peliculas SET titulo = ?, genero = ?, duracion = ?, imagen = ? WHERE id_pelicula = ?";
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setString(1, pelicula.getTitulo());
            statement.setString(2, pelicula.getGenero());
            statement.setString(3, pelicula.getDuracion());
            statement.setString(4, pelicula.getImagen());
            statement.setInt(5, pelicula.getIdPelicula());


            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("{\"message\": \"Pelicula actualizada exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); 
                response.getWriter().write("{\"message\": \"Pelicula no encontrada.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
        } catch (IOException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
        } finally {
            conexion.close(); 
        }
    }
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Configurar cabeceras CORS
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*"); 
        response.setHeader("Access-Control-Allow-Headers", "Content-Type"); 
        Conexion conexion = new Conexion();  
        Connection conn = conexion.getConnection();  

        try {
            String idParam = request.getParameter("id");  
            if (idParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
                response.getWriter().write("{\"message\": \"ID de pelicula no proporcionado.\"}");
                return;
            }

            int idPelicula = Integer.parseInt(idParam);

            String query = "DELETE FROM peliculas WHERE id_pelicula = ?";
            PreparedStatement statement = conn.prepareStatement(query);

            statement.setInt(1, idPelicula);

            int rowsDeleted = statement.executeUpdate();

            if (rowsDeleted > 0) {
                response.setStatus(HttpServletResponse.SC_OK); 
                response.getWriter().write("{\"message\": \"Pelicula eliminada exitosamente.\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND); 
                response.getWriter().write("{\"message\": \"Pelicula no encontrada.\"}");
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
        } catch (NumberFormatException e) {
            e.printStackTrace(); 
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            response.getWriter().write("{\"message\": \"ID de pelicula inválido.\"}");
        } finally {
            conexion.close(); 
        }
    }

}

