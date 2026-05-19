package com.projetihm.avionmanagement.servlet;

import com.projetihm.avionmanagement.dao.AvionDAO;
import com.projetihm.avionmanagement.model.Avion;
import com.projetihm.avionmanagement.model.Client;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/avions")
public class AdminAvionServlet extends HttpServlet {

    private AvionDAO avionDAO;

    @Override
    public void init() {
        avionDAO = new AvionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérifier si l'utilisateur est admin
        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");

        if (client == null || !client.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Récupérer tous les avions
        List<Avion> avions = avionDAO.getAllAvions();
        request.setAttribute("avions", avions);

        // Afficher la page
        request.getRequestDispatcher("/admin/avions.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            int idAvion = Integer.parseInt(request.getParameter("id"));

            // Vérifier si l'avion a des vols
            if (avionDAO.hasFlights(idAvion)) {
                request.setAttribute("error", "Impossible de supprimer cet avion car il a des vols associés.");
                List<Avion> avions = avionDAO.getAllAvions();
                request.setAttribute("avions", avions);
                request.getRequestDispatcher("/admin/avions.jsp").forward(request, response);
                return;
            }

            boolean deleted = avionDAO.deleteAvion(idAvion);

            if (deleted) {
                response.sendRedirect(request.getContextPath() + "/admin/avions?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/avions?error=delete_failed");
            }
        }
    }
}