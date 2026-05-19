package com.projetihm.avionmanagement.dao;

import com.projetihm.avionmanagement.model.Reservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationListDAO {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/ihm_avion";
        String user = "postgres";
        String password = "votre_mot_de_passe";  // ← METS TON MOT DE PASSE

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(url, user, password);
    }

    // Toutes les réservations (pour admin)
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT r.id_reservation, r.id_client, r.id_vol, r.date_reservation, " +
                "r.nombre_places, r.statut, r.montant_total, " +
                "c.nom as client_nom, c.prenoms as client_prenoms, " +
                "v.lieu_depart, v.lieu_arrivee, v.date_depart as vol_date_depart " +
                "FROM RESERVATION r " +
                "JOIN CLIENT c ON r.id_client = c.id_client " +
                "JOIN VOL v ON r.id_vol = v.id_vol " +
                "ORDER BY r.date_reservation DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Reservation res = extractReservation(rs);
                reservations.add(res);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur getAllReservations: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    // Réservations par client
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
                Reservation res = extractReservation(rs);
                reservations.add(res);
            }

        } catch (SQLException e) {
            System.err.println("❌ Erreur getReservationsByClient: " + e.getMessage());
            e.printStackTrace();
        }

        return reservations;
    }

    private Reservation extractReservation(ResultSet rs) throws SQLException {
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

        // Info vol
        String volInfo = rs.getString("lieu_depart") + " → " + rs.getString("lieu_arrivee");
        if (rs.getTimestamp("vol_date_depart") != null) {
            volInfo += " (" + rs.getTimestamp("vol_date_depart").toString().substring(0, 16) + ")";
        }
        res.setVolInfo(volInfo);

        return res;
    }

    // Annuler une réservation
    public boolean cancelReservation(int idReservation) {
        String sql = "UPDATE RESERVATION SET statut = 'cancelled' WHERE id_reservation = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReservation);
            int affected = pstmt.executeUpdate();
            System.out.println("Lignes affectées par l'annulation: " + affected);
            return affected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Erreur cancelReservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}