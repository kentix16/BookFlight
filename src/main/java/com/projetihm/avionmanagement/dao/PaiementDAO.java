package com.projetihm.avionmanagement.dao;

import com.projetihm.avionmanagement.model.Paiement;
import java.sql.*;
import java.util.UUID;

public class PaiementDAO {

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/ihm_avion";
        String user = "postgres";
        String password = "";  // ← METS TON MOT DE PASSE

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return DriverManager.getConnection(url, user, password);
    }

    // Créer un paiement
    public boolean createPaiement(int idReservation, double montant, String modePaiement) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();

            // Générer une référence unique
            String reference = "PAY_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            String sql = "INSERT INTO PAIEMENT (id_reservation, montant, mode_paiement, reference, statut_paiement) " +
                    "VALUES (?, ?, ?, ?, 'effectué')";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, idReservation);
            pstmt.setDouble(2, montant);
            pstmt.setString(3, modePaiement);
            pstmt.setString(4, reference);

            System.out.println("SQL Paiement: " + pstmt.toString());

            int affected = pstmt.executeUpdate();
            System.out.println("Paiement inséré: " + affected + " ligne(s)");

            if (affected > 0) {
                // Mettre à jour le statut de la réservation
                updateReservationStatus(idReservation, "paid");
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("❌ Erreur createPaiement: " + e.getMessage());
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Mettre à jour le statut de la réservation
    private void updateReservationStatus(int idReservation, String statut) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            String sql = "UPDATE RESERVATION SET statut = ? WHERE id_reservation = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, statut);
            pstmt.setInt(2, idReservation);

            int affected = pstmt.executeUpdate();
            System.out.println("Statut réservation mis à jour: " + affected + " ligne(s)");

        } catch (SQLException e) {
            System.err.println("❌ Erreur updateReservationStatus: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Vérifier si une réservation est payée
    public boolean isPaid(int idReservation) {
        String sql = "SELECT COUNT(*) FROM PAIEMENT WHERE id_reservation = ? AND statut_paiement = 'effectué'";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idReservation);
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