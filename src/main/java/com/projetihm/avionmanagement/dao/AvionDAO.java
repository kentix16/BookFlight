package com.projetihm.avionmanagement.dao;

import com.projetihm.avionmanagement.model.Avion;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvionDAO {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/ihm_avion";
        String user = "postgres";
        String password = "";

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(url, user, password);
    }

    // Récupérer tous les avions
    public List<Avion> getAllAvions() {
        List<Avion> avions = new ArrayList<>();
        String sql = "SELECT * FROM AVION ORDER BY id_avion";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Avion avion = new Avion();
                avion.setIdAvion(rs.getInt("id_avion"));
                avion.setModele(rs.getString("modele"));
                avion.setClasse(rs.getString("classe"));
                avion.setNombrePlaces(rs.getInt("nombre_places"));
                avion.setFrais(rs.getDouble("frais"));
                avions.add(avion);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return avions;
    }

    // Récupérer un avion par ID
    public Avion getAvionById(int idAvion) {
        String sql = "SELECT * FROM AVION WHERE id_avion = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAvion);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Avion avion = new Avion();
                avion.setIdAvion(rs.getInt("id_avion"));
                avion.setModele(rs.getString("modele"));
                avion.setClasse(rs.getString("classe"));
                avion.setNombrePlaces(rs.getInt("nombre_places"));
                avion.setFrais(rs.getDouble("frais"));
                return avion;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    // Ajouter un avion
    public boolean addAvion(Avion avion) {
        String sql = "INSERT INTO AVION (modele, classe, nombre_places, frais) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, avion.getModele());
            pstmt.setString(2, avion.getClasse());
            pstmt.setInt(3, avion.getNombrePlaces());
            pstmt.setDouble(4, avion.getFrais());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Modifier un avion
    public boolean updateAvion(Avion avion) {
        String sql = "UPDATE AVION SET modele = ?, classe = ?, nombre_places = ?, frais = ? " +
                "WHERE id_avion = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, avion.getModele());
            pstmt.setString(2, avion.getClasse());
            pstmt.setInt(3, avion.getNombrePlaces());
            pstmt.setDouble(4, avion.getFrais());
            pstmt.setInt(5, avion.getIdAvion());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer un avion
    public boolean deleteAvion(int idAvion) {
        String sql = "DELETE FROM AVION WHERE id_avion = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAvion);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Vérifier si l'avion a des vols associés
    public boolean hasFlights(int idAvion) {
        String sql = "SELECT COUNT(*) FROM VOL WHERE id_avion = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idAvion);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}