package com.projetihm.avionmanagement.servlet;

import com.projetihm.avionmanagement.dao.AvionDAO;
import com.projetihm.avionmanagement.model.Avion;
import com.projetihm.avionmanagement.model.Client;
import java.io.IOException;
import java.util.Arrays;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/avion-edit")
public class AdminAvionEditServlet extends HttpServlet {

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

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

        if ("edit".equals(action) && idParam != null) {
            int idAvion = Integer.parseInt(idParam);
            Avion avion = avionDAO.getAvionById(idAvion);
            request.setAttribute("avion", avion);
            request.setAttribute("title", "Modifier un avion");

        } else if ("add".equals(action)) {
            request.setAttribute("title", "Ajouter un avion");

        } else {
            response.sendRedirect(request.getContextPath() + "/admin/avions");
            return;
        }

        request.getRequestDispatcher("/admin/avion-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String avionId = request.getParameter("avion_id");
        String action = request.getParameter("action");

        try {
            String modele = request.getParameter("modele");
            String classe = request.getParameter("classe");
            int nombrePlaces = Integer.parseInt(request.getParameter("nombre_places"));
            double frais = Double.parseDouble(request.getParameter("frais"));

            // Validation
            if (modele == null || modele.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/avions?error=invalid_model");
                return;
            }

            if (!Arrays.asList("simple", "premium", "VIP").contains(classe)) {
                response.sendRedirect(request.getContextPath() + "/admin/avions?error=invalid_class");
                return;
            }

            if (nombrePlaces <= 0) {
                response.sendRedirect(request.getContextPath() + "/admin/avions?error=invalid_seats");
                return;
            }

            if (frais < 0) {
                response.sendRedirect(request.getContextPath() + "/admin/avions?error=invalid_fees");
                return;
            }

            Avion avion = new Avion();
            avion.setModele(modele);
            avion.setClasse(classe);
            avion.setNombrePlaces(nombrePlaces);
            avion.setFrais(frais);

            boolean success;

            if ("add".equals(action)) {
                success = avionDAO.addAvion(avion);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/avions?success=added");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/avions?error=add_failed");
                }
            } else if ("edit".equals(action) && avionId != null && !avionId.isEmpty()) {
                avion.setIdAvion(Integer.parseInt(avionId));
                success = avionDAO.updateAvion(avion);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/avions?success=updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/avions?error=update_failed");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/avions?error=invalid_action");
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/avions?error=invalid_number");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/avions?error=" + e.getMessage());
        }
    }
}