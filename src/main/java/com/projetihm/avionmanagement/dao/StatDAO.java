package com.projetihm.avionmanagement.dao;

import java.sql.*;

public class StatDAO {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/ihm_avion";
        String user = "postgres";
        String password = "votre_mot_de_passe";

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(url, user, password);
    }

    // Nombre de réservations d'un client
    public int getReservationsCount(int clientId, boolean isAdmin) {
        String sql;
        if (isAdmin) {
            sql = "SELECT COUNT(*) FROM RESERVATION";
        } else {
            sql = "SELECT COUNT(*) FROM RESERVATION WHERE id_client = ?";
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (!isAdmin) {
                pstmt.setInt(1, clientId);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Nombre de vols disponibles
    public int getAvailableFlightsCount() {
        String sql = "SELECT COUNT(*) FROM VOL WHERE date_depart > CURRENT_TIMESTAMP AND statut_vol = 'scheduled'";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Total dépensé par un client
    public double getTotalDepense(int clientId, boolean isAdmin) {
        String sql;
        if (isAdmin) {
            sql = "SELECT COALESCE(SUM(montant_total), 0) FROM RESERVATION WHERE statut = 'paid'";
        } else {
            sql = "SELECT COALESCE(SUM(montant_total), 0) FROM RESERVATION WHERE id_client = ? AND statut = 'paid'";
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (!isAdmin) {
                pstmt.setInt(1, clientId);
            }

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}