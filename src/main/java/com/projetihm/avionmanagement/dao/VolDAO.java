package com.projetihm.avionmanagement.dao;

import com.projetihm.avionmanagement.model.Vol;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VolDAO {

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

    // Récupérer tous les vols
    public List<Vol> getAllVols() {
        List<Vol> vols = new ArrayList<>();
        String sql = "SELECT v.*, a.modele as modele_avion FROM VOL v " +
                "JOIN AVION a ON v.id_avion = a.id_avion " +
                "ORDER BY v.date_depart DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vol vol = new Vol();
                vol.setIdVol(rs.getInt("id_vol"));
                vol.setIdAvion(rs.getInt("id_avion"));
                vol.setModeleAvion(rs.getString("modele_avion"));
                vol.setDateDepart(rs.getTimestamp("date_depart"));
                vol.setDateArrivee(rs.getTimestamp("date_arrivee"));
                vol.setLieuDepart(rs.getString("lieu_depart"));
                vol.setLieuArrivee(rs.getString("lieu_arrivee"));
                vol.setStatutVol(rs.getString("statut_vol"));
                vols.add(vol);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vols;
    }

    // Récupérer un vol par ID
    public Vol getVolById(int idVol) {
        String sql = "SELECT v.*, a.modele as modele_avion FROM VOL v " +
                "JOIN AVION a ON v.id_avion = a.id_avion " +
                "WHERE v.id_vol = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idVol);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Vol vol = new Vol();
                vol.setIdVol(rs.getInt("id_vol"));
                vol.setIdAvion(rs.getInt("id_avion"));
                vol.setModeleAvion(rs.getString("modele_avion"));
                vol.setDateDepart(rs.getTimestamp("date_depart"));
                vol.setDateArrivee(rs.getTimestamp("date_arrivee"));
                vol.setLieuDepart(rs.getString("lieu_depart"));
                vol.setLieuArrivee(rs.getString("lieu_arrivee"));
                vol.setStatutVol(rs.getString("statut_vol"));
                return vol;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Ajouter un vol
    public boolean addVol(Vol vol) {
        String sql = "INSERT INTO VOL (id_avion, date_depart, date_arrivee, lieu_depart, lieu_arrivee, statut_vol) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, vol.getIdAvion());
            pstmt.setTimestamp(2, vol.getDateDepart());
            pstmt.setTimestamp(3, vol.getDateArrivee());
            pstmt.setString(4, vol.getLieuDepart());
            pstmt.setString(5, vol.getLieuArrivee());
            pstmt.setString(6, vol.getStatutVol());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Modifier un vol
    public boolean updateVol(Vol vol) {
        String sql = "UPDATE VOL SET id_avion = ?, date_depart = ?, date_arrivee = ?, " +
                "lieu_depart = ?, lieu_arrivee = ?, statut_vol = ? WHERE id_vol = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, vol.getIdAvion());
            pstmt.setTimestamp(2, vol.getDateDepart());
            pstmt.setTimestamp(3, vol.getDateArrivee());
            pstmt.setString(4, vol.getLieuDepart());
            pstmt.setString(5, vol.getLieuArrivee());
            pstmt.setString(6, vol.getStatutVol());
            pstmt.setInt(7, vol.getIdVol());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer un vol
    public boolean deleteVol(int idVol) {
        String sql = "DELETE FROM VOL WHERE id_vol = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idVol);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Vérifier si le vol a des réservations
    public boolean hasReservations(int idVol) {
        String sql = "SELECT COUNT(*) FROM RESERVATION WHERE id_vol = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idVol);
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