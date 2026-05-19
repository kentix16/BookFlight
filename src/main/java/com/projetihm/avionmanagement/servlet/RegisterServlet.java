package com.projetihm.avionmanagement.servlet;  // ← MODIFIÉ

import com.projetihm.avionmanagement.dao.ClientDAO;  // ← MODIFIÉ
import com.projetihm.avionmanagement.model.Client;    // ← MODIFIÉ
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/RegisterServlet"})
public class RegisterServlet extends HttpServlet {

    private ClientDAO clientDAO;

    @Override
    public void init() {
        clientDAO = new ClientDAO();
        System.out.println("✅ RegisterServlet initialisé");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔵 RegisterServlet appelé");

        String nom = request.getParameter("nom");
        String prenoms = request.getParameter("prenoms");
        String email = request.getParameter("email");
        String contact = request.getParameter("contact");
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        if (nom == null || nom.trim().isEmpty() ||
                prenoms == null || prenoms.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                contact == null || contact.trim().isEmpty() ||
                login == null || login.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {

            response.sendRedirect(request.getContextPath() + "/login.jsp?error=Tous les champs sont requis");
            return;
        }

        try {
            if (clientDAO.userExists(email, login)) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=user_exists");
                return;
            }

            Client client = new Client();
            client.setNom(nom);
            client.setPrenoms(prenoms);
            client.setEmail(email);
            client.setContact(contact);
            client.setLogin(login);
            client.setMotDePasse(password);
            client.setAdmin(false);

            boolean registered = clientDAO.register(client);

            if (registered) {
                response.sendRedirect(request.getContextPath() + "/login.jsp?success=registered");
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=registration_failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=db_error");
        }
    }
}