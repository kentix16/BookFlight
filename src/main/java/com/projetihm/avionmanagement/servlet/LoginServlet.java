package com.projetihm.avionmanagement.servlet;  // ← MODIFIÉ

import com.projetihm.avionmanagement.dao.ClientDAO;  // ← MODIFIÉ
import com.projetihm.avionmanagement.model.Client;    // ← MODIFIÉ
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    private ClientDAO clientDAO;

    @Override
    public void init() {
        clientDAO = new ClientDAO();
        System.out.println("✅ LoginServlet initialisé");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔵 LoginServlet appelé");

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            Client client = clientDAO.authenticate(username, password);

            if (client != null) {
                HttpSession session = request.getSession();
                session.setAttribute("client", client);
                session.setAttribute("id_client", client.getIdClient());
                session.setAttribute("nom", client.getNom());
                session.setAttribute("prenoms", client.getPrenoms());
                session.setAttribute("isAdmin", client.isAdmin());

                if (client.isAdmin()) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
                } else {
                    response.sendRedirect(request.getContextPath() + "/user/dashboard.jsp");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/login.jsp?error=invalid_credentials");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp?error=db_error");
        }
    }
}