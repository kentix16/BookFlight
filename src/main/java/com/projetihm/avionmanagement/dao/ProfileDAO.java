package com.projetihm.avionmanagement.dao;

import com.projetihm.avionmanagement.model.Client;
import java.sql.*;

public class ProfileDAO {

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

    // Récupérer un client par ID
    public Client getClientById(int idClient) {
        String sql = "SELECT c.id_client, c.nom, c.prenoms, c.contact, c.mail, c.date_creation, " +
                "cel.login, cel.est_admin " +
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
                client.setDateCreation(rs.getTimestamp("date_creation"));
                client.setLogin(rs.getString("login"));
                client.setAdmin(rs.getBoolean("est_admin"));
                client.setEstClientEnLigne(rs.getString("login") != null);
                return client;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Mettre à jour les informations du client
    public boolean updateClient(Client client) {
        String sql = "UPDATE CLIENT SET nom = ?, prenoms = ?, contact = ?, mail = ? WHERE id_client = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, client.getNom());
            pstmt.setString(2, client.getPrenoms());
            pstmt.setString(3, client.getContact());
            pstmt.setString(4, client.getEmail());
            pstmt.setInt(5, client.getIdClient());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Mettre à jour le mot de passe
    public boolean updatePassword(int idClient, String ancienMotDePasse, String nouveauMotDePasse) {
        // Vérifier l'ancien mot de passe
        String checkSql = "SELECT id_client FROM CLIENT_EN_LIGNE WHERE id_client = ? AND mot_de_passe = crypt(?, mot_de_passe)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {

            pstmt.setInt(1, idClient);
            pstmt.setString(2, ancienMotDePasse);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                System.out.println("Ancien mot de passe incorrect");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        // Mettre à jour le mot de passe
        String updateSql = "UPDATE CLIENT_EN_LIGNE SET mot_de_passe = crypt(?, gen_salt('bf')) WHERE id_client = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSql)) {

            pstmt.setString(1, nouveauMotDePasse);
            pstmt.setInt(2, idClient);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}