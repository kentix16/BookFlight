package com.projetihm.avionmanagement.servlet;

import com.projetihm.avionmanagement.dao.VolDAO;
import com.projetihm.avionmanagement.model.Vol;
import com.projetihm.avionmanagement.model.Client;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/vols")
public class AdminVolServlet extends HttpServlet {

    private VolDAO volDAO;

    @Override
    public void init() {
        volDAO = new VolDAO();
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

        // Récupérer tous les vols
        List<Vol> vols = volDAO.getAllVols();
        request.setAttribute("vols", vols);

        // Afficher la page
        request.getRequestDispatcher("/admin/vols.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            int idVol = Integer.parseInt(request.getParameter("id"));

            // Vérifier si le vol a des réservations
            if (volDAO.hasReservations(idVol)) {
                request.setAttribute("error", "Impossible de supprimer ce vol car il a des réservations associées.");
                List<Vol> vols = volDAO.getAllVols();
                request.setAttribute("vols", vols);
                request.getRequestDispatcher("/admin/vols.jsp").forward(request, response);
                return;
            }

            boolean deleted = volDAO.deleteVol(idVol);

            if (deleted) {
                response.sendRedirect(request.getContextPath() + "/admin/vols?success=deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/vols?error=delete_failed");
            }
        }
    }
}