package com.projetihm.avionmanagement.dao;

import com.projetihm.avionmanagement.model.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDashboardDAO {

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

    // Réservations d'un client
    public List<Reservation> getReservationsByClient(int clientId) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.id_reservation, r.id_client, r.id_vol, r.date_reservation, " +
                "r.nombre_places, r.statut, r.montant_total, " +
                "c.nom as client_nom, c.prenoms as client_prenoms, " +
                "v.lieu_depart, v.lieu_arrivee, v.date_depart as vol_date_depart " +
                "FROM RESERVATION r " +
                "JOIN CLIENT c ON r.id_client = c.id_client " +
                "JOIN VOL v ON r.id_vol = v.id_vol " +
                "WHERE r.id_client = ? " +
                "ORDER BY r.date_reservation DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Reservation res = new Reservation();
                res.setIdReservation(rs.getInt("id_reservation"));
                res.setIdClient(rs.getInt("id_client"));
                res.setClientNom(rs.getString("client_nom"));
                res.setClientPrenoms(rs.getString("client_prenoms"));
                res.setIdVol(rs.getInt("id_vol"));
                res.setDateReservation(rs.getTimestamp("date_reservation"));
                res.setNombrePlaces(rs.getInt("nombre_places"));
                res.setStatut(rs.getString("statut"));
                res.setMontantTotal(rs.getDouble("montant_total"));

                String volInfo = rs.getString("lieu_depart") + " → " + rs.getString("lieu_arrivee");
                if (rs.getTimestamp("vol_date_depart") != null) {
                    volInfo += " (" + rs.getTimestamp("vol_date_depart").toString().substring(0, 16) + ")";
                }
                res.setVolInfo(volInfo);
                reservations.add(res);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    // Total dépensé par un client
    public double getTotalDepense(int clientId) {
        String sql = "SELECT COALESCE(SUM(montant_total), 0) FROM RESERVATION WHERE id_client = ? AND statut = 'paid'";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getDouble(1);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}