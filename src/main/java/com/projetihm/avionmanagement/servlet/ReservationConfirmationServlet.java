package com.projetihm.avionmanagement.servlet;

import com.projetihm.avionmanagement.model.Client;
import com.projetihm.avionmanagement.model.Reservation;
import com.projetihm.avionmanagement.model.Vol;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/reservation/confirmation")
public class ReservationConfirmationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Reservation reservation = (Reservation) session.getAttribute("lastReservation");
        Vol vol = (Vol) session.getAttribute("lastVol");
        Client client = (Client) session.getAttribute("lastClient");

        if (reservation == null || vol == null || client == null) {
            response.sendRedirect(request.getContextPath() + "/reservation");
            return;
        }

        request.setAttribute("reservation", reservation);
        request.setAttribute("vol", vol);
        request.setAttribute("client", client);

        // Nettoyer la session
        session.removeAttribute("lastReservation");
        session.removeAttribute("lastVol");
        session.removeAttribute("lastClient");

        request.getRequestDispatcher("/reservation/confirmation.jsp").forward(request, response);
    }
}