package com.projetihm.avionmanagement.dao;  // ← MODIFIÉ

import com.projetihm.avionmanagement.model.Client;  // ← MODIFIÉ
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

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

    // Authentification
    public Client authenticate(String username, String password) {
        String sql = "SELECT c.*, cel.login, cel.mot_de_passe, cel.est_admin " +
                "FROM CLIENT c " +
                "LEFT JOIN CLIENT_EN_LIGNE cel ON c.id_client = cel.id_client " +
                "WHERE (c.mail = ? OR cel.login = ?) AND cel.mot_de_passe = crypt(?, cel.mot_de_passe) " +
                "AND cel.id_client IS NOT NULL";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, username);
            pstmt.setString(3, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Client client = new Client();
                client.setIdClient(rs.getInt("id_client"));
                client.setNom(rs.getString("nom"));
                client.setPrenoms(rs.getString("prenoms"));
                client.setContact(rs.getString("contact"));
                client.setEmail(rs.getString("mail"));
                client.setLogin(rs.getString("login"));
                client.setAdmin(rs.getBoolean("est_admin"));
                client.setEstClientEnLigne(true);

                updateLastLogin(client.getIdClient());

                return client;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Inscription
    public boolean register(Client client) {
        Connection conn = null;
        PreparedStatement pstmtClient = null;
        PreparedStatement pstmtEnLigne = null;

        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            String sqlClient = "INSERT INTO CLIENT (nom, prenoms, contact, mail, est_client_en_ligne) " +
                    "VALUES (?, ?, ?, ?, ?) RETURNING id_client";

            pstmtClient = conn.prepareStatement(sqlClient);
            pstmtClient.setString(1, client.getNom());
            pstmtClient.setString(2, client.getPrenoms());
            pstmtClient.setString(3, client.getContact());
            pstmtClient.setString(4, client.getEmail());
            pstmtClient.setBoolean(5, true);

            ResultSet rs = pstmtClient.executeQuery();
            int clientId = 0;
            if (rs.next()) {
                clientId = rs.getInt(1);
            }

            String sqlEnLigne = "INSERT INTO CLIENT_EN_LIGNE (id_client, login, mot_de_passe, est_admin) " +
                    "VALUES (?, ?, crypt(?, gen_salt('bf')), ?)";

            pstmtEnLigne = conn.prepareStatement(sqlEnLigne);
            pstmtEnLigne.setInt(1, clientId);
            pstmtEnLigne.setString(2, client.getLogin());
            pstmtEnLigne.setString(3, client.getMotDePasse());
            pstmtEnLigne.setBoolean(4, client.isAdmin());

            int affectedRows = pstmtEnLigne.executeUpdate();

            conn.commit();
            return affectedRows > 0;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (pstmtClient != null) pstmtClient.close();
                if (pstmtEnLigne != null) pstmtEnLigne.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Vérifier si l'utilisateur existe
    public boolean userExists(String email, String login) {
        String sql = "SELECT 1 FROM CLIENT c " +
                "JOIN CLIENT_EN_LIGNE cel ON c.id_client = cel.id_client " +
                "WHERE c.mail = ? OR cel.login = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, login);

            ResultSet rs = pstmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateLastLogin(int clientId) {
        String sql = "UPDATE CLIENT_EN_LIGNE SET dernier_login = CURRENT_TIMESTAMP " +
                "WHERE id_client = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, clientId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Client> getAllClients() {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT c.*, cel.login, cel.est_admin " +
                "FROM CLIENT c " +
                "LEFT JOIN CLIENT_EN_LIGNE cel ON c.id_client = cel.id_client";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Client client = new Client();
                client.setIdClient(rs.getInt("id_client"));
                client.setNom(rs.getString("nom"));
                client.setPrenoms(rs.getString("prenoms"));
                client.setContact(rs.getString("contact"));
                client.setEmail(rs.getString("mail"));
                client.setLogin(rs.getString("login"));
                client.setAdmin(rs.getBoolean("est_admin"));
                client.setEstClientEnLigne(rs.getString("login") != null);
                clients.add(client);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clients;
    }
}