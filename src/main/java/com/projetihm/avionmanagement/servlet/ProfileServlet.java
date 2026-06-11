package com.projetihm.avionmanagement.servlet;

import com.projetihm.avionmanagement.dao.ProfileDAO;
import com.projetihm.avionmanagement.model.Client;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {

    private ProfileDAO profileDAO;

    @Override
    public void init() {
        profileDAO = new ProfileDAO();
        System.out.println("✅ ProfileServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");

        if (client == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Récupérer les informations complètes du client depuis la base
        Client fullClient = profileDAO.getClientById(client.getIdClient());
        request.setAttribute("client", fullClient);

        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        Client sessionClient = (Client) session.getAttribute("client");

        if (sessionClient == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");

        if ("update".equals(action)) {
            try {
                String nom = request.getParameter("nom");
                String prenoms = request.getParameter("prenoms");
                String email = request.getParameter("email");
                String contact = request.getParameter("contact");

                // Validation
                if (nom == null || nom.trim().isEmpty()) {
                    throw new Exception("Le nom ne peut pas être vide");
                }
                if (prenoms == null || prenoms.trim().isEmpty()) {
                    throw new Exception("Les prénoms ne peuvent pas être vides");
                }
                if (email == null || email.trim().isEmpty() || !email.contains("@")) {
                    throw new Exception("Email invalide");
                }
                if (contact == null || contact.trim().isEmpty()) {
                    throw new Exception("Le téléphone ne peut pas être vide");
                }

                Client updatedClient = new Client();
                updatedClient.setIdClient(sessionClient.getIdClient());
                updatedClient.setNom(nom);
                updatedClient.setPrenoms(prenoms);
                updatedClient.setEmail(email);
                updatedClient.setContact(contact);

                boolean success = profileDAO.updateClient(updatedClient);

                if (success) {
                    // Mettre à jour la session
                    sessionClient.setNom(nom);
                    sessionClient.setPrenoms(prenoms);
                    sessionClient.setEmail(email);
                    sessionClient.setContact(contact);
                    session.setAttribute("client", sessionClient);

                    session.setAttribute("toastMessage", "Profil mis à jour avec succès !");
                    session.setAttribute("toastType", "success");
                } else {
                    throw new Exception("Erreur lors de la mise à jour");
                }

            } catch (Exception e) {
                session.setAttribute("toastMessage", "Erreur : " + e.getMessage());
                session.setAttribute("toastType", "error");
            }

            response.sendRedirect(request.getContextPath() + "/profile");

        } else if ("changePassword".equals(action)) {
            try {
                String ancienMotDePasse = request.getParameter("ancien_mot_de_passe");
                String nouveauMotDePasse = request.getParameter("nouveau_mot_de_passe");
                String confirmationMotDePasse = request.getParameter("confirmation_mot_de_passe");

                // Validation
                if (ancienMotDePasse == null || ancienMotDePasse.isEmpty()) {
                    throw new Exception("Veuillez entrer votre mot de passe actuel");
                }
                if (nouveauMotDePasse == null || nouveauMotDePasse.length() < 4) {
                    throw new Exception("Le nouveau mot de passe doit contenir au moins 4 caractères");
                }
                if (!nouveauMotDePasse.equals(confirmationMotDePasse)) {
                    throw new Exception("Les nouveaux mots de passe ne correspondent pas");
                }

                boolean success = profileDAO.updatePassword(sessionClient.getIdClient(), ancienMotDePasse, nouveauMotDePasse);

                if (success) {
                    session.setAttribute("toastMessage", "Mot de passe modifié avec succès !");
                    session.setAttribute("toastType", "success");
                } else {
                    throw new Exception("Mot de passe actuel incorrect");
                }

            } catch (Exception e) {
                session.setAttribute("toastMessage", "Erreur : " + e.getMessage());
                session.setAttribute("toastType", "error");
            }

            response.sendRedirect(request.getContextPath() + "/profile");
        }
    }
}