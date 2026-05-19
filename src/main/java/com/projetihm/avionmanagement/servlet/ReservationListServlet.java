package com.projetihm.avionmanagement.servlet;

import com.projetihm.avionmanagement.dao.ReservationListDAO;
import com.projetihm.avionmanagement.model.Reservation;
import com.projetihm.avionmanagement.model.Client;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/reservations")
public class ReservationListServlet extends HttpServlet {

    private ReservationListDAO reservationListDAO;

    @Override
    public void init() {
        reservationListDAO = new ReservationListDAO();
        System.out.println("✅ ReservationListServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔵 GET ReservationListServlet appelé");

        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");

        // Vérifier si l'utilisateur est connecté
        if (client == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Récupérer le message toast de la session
        String toastMessage = (String) session.getAttribute("toastMessage");
        String toastType = (String) session.getAttribute("toastType");
        String toastDetails = (String) session.getAttribute("toastDetails");

        // Supprimer les attributs de session après les avoir récupérés
        session.removeAttribute("toastMessage");
        session.removeAttribute("toastType");
        session.removeAttribute("toastDetails");

        List<Reservation> reservations;
        boolean isAdmin = false;

        if (client.isAdmin()) {
            reservations = reservationListDAO.getAllReservations();
            isAdmin = true;
        } else {
            reservations = reservationListDAO.getReservationsByClient(client.getIdClient());
            isAdmin = false;
        }

        request.setAttribute("reservations", reservations);
        request.setAttribute("client", client);
        request.setAttribute("isAdmin", isAdmin);
        request.setAttribute("toastMessage", toastMessage);
        request.setAttribute("toastType", toastType);
        request.setAttribute("toastDetails", toastDetails);

        request.getRequestDispatcher("/reservations/list.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔵 POST ReservationListServlet appelé");

        String action = request.getParameter("action");

        if ("cancel".equals(action)) {
            int idReservation = Integer.parseInt(request.getParameter("id"));
            System.out.println("Annulation réservation ID: " + idReservation);

            boolean cancelled = reservationListDAO.cancelReservation(idReservation);

            if (cancelled) {
                System.out.println("✅ Réservation annulée avec succès");
                response.sendRedirect(request.getContextPath() + "/reservations?success=cancelled");
            } else {
                System.out.println("❌ Échec de l'annulation");
                response.sendRedirect(request.getContextPath() + "/reservations?error=cancel_failed");
            }
        }
    }
}