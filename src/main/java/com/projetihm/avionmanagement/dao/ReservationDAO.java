package com.projetihm.avionmanagement.dao;

import com.projetihm.avionmanagement.model.Reservation;
import com.projetihm.avionmanagement.model.Vol;
import com.projetihm.avionmanagement.model.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    public Connection getConnection() throws SQLException {
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

    // Rechercher un client
    public List<Client> searchClients(String searchTerm) {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT c.id_client, c.nom, c.prenoms, c.contact, c.mail, cel.login " +
                "FROM CLIENT c " +
                "LEFT JOIN CLIENT_EN_LIGNE cel ON c.id_client = cel.id_client " +
                "WHERE c.mail ILIKE ? OR cel.login ILIKE ? OR c.contact ILIKE ? OR c.nom ILIKE ? " +
                "ORDER BY c.nom, c.prenoms LIMIT 10";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + searchTerm + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            pstmt.setString(3, pattern);
            pstmt.setString(4, pattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Client client = new Client();
                client.setIdClient(rs.getInt("id_client"));
                client.setNom(rs.getString("nom"));
                client.setPrenoms(rs.getString("prenoms"));
                client.setContact(rs.getString("contact"));
                client.setEmail(rs.getString("mail"));
                client.setLogin(rs.getString("login"));
                clients.add(client);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }

    // Récupérer un client par ID
    public Client getClientById(int idClient) {
        String sql = "SELECT c.id_client, c.nom, c.prenoms, c.contact, c.mail, cel.login " +
                "FROM CLIENT c " +
                "LEFT JOIN CLIENT_EN_LIGNE cel ON c.id_client = cel.id_client " +
                "WHERE c.id_client = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idClient);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Client client = new Client();
                client.setIdClient(rs.getInt("id_client"));
                client.setNom(rs.getString("nom"));
                client.setPrenoms(rs.getString("prenoms"));
                client.setContact(rs.getString("contact"));
                client.setEmail(rs.getString("mail"));
                client.setLogin(rs.getString("login"));
                return client;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Créer un nouveau client
    public int createClient(Client client) {
        String sql = "INSERT INTO CLIENT (nom, prenoms, contact, mail, est_client_en_ligne) " +
                "VALUES (?, ?, ?, ?, false) RETURNING id_client";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenoms());
            pstmt.setString(3, client.getContact());
            pstmt.setString(4, client.getEmail());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    // Récupérer tous les vols disponibles
    public List<Vol> getAvailableVols() {
        List<Vol> vols = new ArrayList<>();
        String sql = "SELECT v.id_vol, v.id_avion, v.date_depart, v.date_arrivee, " +
                "v.lieu_depart, v.lieu_arrivee, v.statut_vol, " +
                "a.modele as modele_avion, a.frais " +
                "FROM VOL v " +
                "JOIN AVION a ON v.id_avion = a.id_avion " +
                "WHERE v.date_depart > CURRENT_TIMESTAMP AND v.statut_vol = 'scheduled' " +
                "ORDER BY v.date_depart";

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
                vol.setFrais(rs.getDouble("frais"));
                vols.add(vol);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return vols;
    }

    // Récupérer un vol par ID
    public Vol getVolById(int idVol) {
        String sql = "SELECT v.id_vol, v.id_avion, v.date_depart, v.date_arrivee, " +
                "v.lieu_depart, v.lieu_arrivee, v.statut_vol, " +
                "a.modele as modele_avion, a.frais " +
                "FROM VOL v " +
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
                vol.setFrais(rs.getDouble("frais"));
                return vol;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Vérifier les places disponibles
    // Vérifier les places disponibles pour un vol
    // Vérifier les places disponibles pour un vol (version simple)
    // Vérifier les places disponibles pour un vol (version corrigée)
    public int getAvailableSeats(int idVol) {
        System.out.println("=== getAvailableSeats appelé pour vol ID: " + idVol);

        String sql = "SELECT " +
                "  a.nombre_places as capacite, " +
                "  COALESCE(SUM(r.nombre_places), 0) as reservees " +
                "FROM AVION a " +
                "JOIN VOL v ON a.id_avion = v.id_avion " +
                "LEFT JOIN RESERVATION r ON v.id_vol = r.id_vol AND r.statut != 'cancelled' " +
                "WHERE v.id_vol = ? " +
                "GROUP BY a.nombre_places";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idVol);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int capacite = rs.getInt("capacite");
                int reservees = rs.getInt("reservees");
                int placesDisponibles = capacite - reservees;

                System.out.println("Capacité: " + capacite);
                System.out.println("Réservées: " + reservees);
                System.out.println("Disponibles: " + placesDisponibles);

                return placesDisponibles;
            } else {
                System.out.println("Aucune ligne retournée pour le vol " + idVol);
            }

        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    // Créer une réservation
    // Créer une réservation et retourner l'ID généré
    public int createReservation(Reservation reservation) {
        String sql = "INSERT INTO RESERVATION (id_client, id_vol, nombre_places, statut, montant_total, date_reservation) " +
                "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP) RETURNING id_reservation";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, reservation.getIdClient());
            pstmt.setInt(2, reservation.getIdVol());
            pstmt.setInt(3, reservation.getNombrePlaces());
            pstmt.setString(4, reservation.getStatut());
            pstmt.setDouble(5, reservation.getMontantTotal());

            System.out.println("SQL Reservation: " + pstmt.toString());

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                reservation.setIdReservation(id);
                System.out.println("✅ Réservation créée avec ID: " + id);
                return id;
            }

            return -1;

        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL createReservation: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    // Méthode pour mettre à jour les statuts des vols
    public void updateVolStatuses() {
        String sql = "UPDATE VOL SET statut_vol = 'completed' " +
                "WHERE date_depart < CURRENT_TIMESTAMP AND statut_vol = 'scheduled'";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            int updated = stmt.executeUpdate(sql);
            if (updated > 0) {
                System.out.println(updated + " vols mis à jour en 'completed'");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Récupérer le nombre total de places de l'avion pour un vol donné
    public int getTotalSeatsByVolId(int idVol) {
        String sql = "SELECT a.nombre_places FROM AVION a " +
                "JOIN VOL v ON a.id_avion = v.id_avion " +
                "WHERE v.id_vol = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idVol);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("nombre_places");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // Récupérer le nombre de places déjà réservées pour un vol (hors annulées)
    public int getAlreadyBookedSeats(int idVol) {
        String sql = "SELECT COALESCE(SUM(r.nombre_places), 0) as total_reservees " +
                "FROM RESERVATION r " +
                "WHERE r.id_vol = ? AND r.statut != 'cancelled'";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idVol);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_reservees");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
    public void testConnection(int idVol) {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT a.nombre_places FROM AVION a JOIN VOL v ON a.id_avion = v.id_avion WHERE v.id_vol = " + idVol;
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                System.out.println("TEST - Capacité pour vol " + idVol + ": " + rs.getInt("nombre_places"));
            } else {
                System.out.println("TEST - Aucun résultat pour vol " + idVol);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}