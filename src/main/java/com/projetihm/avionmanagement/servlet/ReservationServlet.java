package com.projetihm.avionmanagement.servlet;

import com.projetihm.avionmanagement.dao.PaiementDAO;
import com.projetihm.avionmanagement.dao.ReservationDAO;
import com.projetihm.avionmanagement.model.Client;
import com.projetihm.avionmanagement.model.Vol;
import com.projetihm.avionmanagement.model.Reservation;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/reservation")
public class ReservationServlet extends HttpServlet {

    private ReservationDAO reservationDAO;

    @Override
    public void init() {
        reservationDAO = new ReservationDAO();
        reservationDAO.updateVolStatuses();
        System.out.println("✅ ReservationServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("🔵 GET ReservationServlet appelé");

        HttpSession session = request.getSession();
        Client loggedInClient = (Client) session.getAttribute("client");

        // Si l'utilisateur est connecté, le sélectionner automatiquement
        if (loggedInClient != null) {
            session.setAttribute("selectedClient", loggedInClient);
            System.out.println("Client automatiquement sélectionné: " + loggedInClient.getPrenoms() + " " + loggedInClient.getNom());
        }

        // Nettoyer l'ancienne sélection si nécessaire (pour admin qui veut changer)
        String reset = request.getParameter("reset");
        if ("true".equals(reset)) {
            session.removeAttribute("selectedClient");
            session.setAttribute("selectedClient", loggedInClient);
        }

        // Récupérer les vols disponibles
        List<Vol> vols = reservationDAO.getAvailableVols();
        System.out.println("Nombre de vols disponibles: " + (vols != null ? vols.size() : 0));

        request.setAttribute("vols", vols);

        // Si client déjà sélectionné, l'envoyer à la JSP
        if (loggedInClient != null) {
            request.setAttribute("selectedClient", loggedInClient);
        }

        request.getRequestDispatcher("/reservation/reservation.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        System.out.println("🔵 POST ReservationServlet - Action: " + action);

        if ("searchClient".equals(action)) {
            // Rechercher un client
            String searchTerm = request.getParameter("searchTerm");
            System.out.println("Recherche client: " + searchTerm);

            List<Client> clients = reservationDAO.searchClients(searchTerm);
            List<Vol> vols = reservationDAO.getAvailableVols();

            request.setAttribute("searchResults", clients);
            request.setAttribute("searchTerm", searchTerm);
            request.setAttribute("vols", vols);
            request.getRequestDispatcher("/reservation/reservation.jsp").forward(request, response);

        } else if ("selectClient".equals(action)) {
            // Sélectionner un client existant
            int clientId = Integer.parseInt(request.getParameter("clientId"));
            System.out.println("Sélection client ID: " + clientId);

            Client client = reservationDAO.getClientById(clientId);
            HttpSession session = request.getSession();
            session.setAttribute("selectedClient", client);

            List<Vol> vols = reservationDAO.getAvailableVols();
            request.setAttribute("vols", vols);
            request.setAttribute("selectedClient", client);
            request.getRequestDispatcher("/reservation/reservation.jsp").forward(request, response);

        } else if ("createClient".equals(action)) {
            // Créer un nouveau client
            String nom = request.getParameter("nom");
            String prenoms = request.getParameter("prenoms");
            String email = request.getParameter("email");
            String contact = request.getParameter("contact");

            System.out.println("Création client: " + nom + " " + prenoms);

            Client newClient = new Client();
            newClient.setNom(nom);
            newClient.setPrenoms(prenoms);
            newClient.setEmail(email);
            newClient.setContact(contact);

            int clientId = reservationDAO.createClient(newClient);

            List<Vol> vols = reservationDAO.getAvailableVols();

            if (clientId > 0) {
                Client client = reservationDAO.getClientById(clientId);
                HttpSession session = request.getSession();
                session.setAttribute("selectedClient", client);
                request.setAttribute("vols", vols);
                request.setAttribute("selectedClient", client);
                request.setAttribute("successMessage", "Client créé avec succès !");
            } else {
                request.setAttribute("vols", vols);
                request.setAttribute("errorMessage", "Erreur lors de la création du client");
            }

            request.getRequestDispatcher("/reservation/reservation.jsp").forward(request, response);

        } else if ("book".equals(action)) {
            try {
                System.out.println("=== CRÉATION RÉSERVATION ===");

                HttpSession session = request.getSession();
                Client client = (Client) session.getAttribute("selectedClient");

                if (client == null) {
                    System.out.println("❌ Aucun client sélectionné");
                    response.sendRedirect(request.getContextPath() + "/reservation");
                    return;
                }

                int idVol = Integer.parseInt(request.getParameter("idVol"));
                int nombrePlaces = Integer.parseInt(request.getParameter("nombrePlaces"));
                String modePaiement = request.getParameter("modePaiement");

                // Vérifier les places disponibles
                int placesDisponibles = reservationDAO.getAvailableSeats(idVol);

                if (nombrePlaces > placesDisponibles) {
                    List<Vol> vols = reservationDAO.getAvailableVols();
                    request.setAttribute("vols", vols);
                    request.setAttribute("errorMessage", "Désolé, il ne reste que " + placesDisponibles + " place(s) disponible(s)");
                    request.getRequestDispatcher("/reservation/reservation.jsp").forward(request, response);
                    return;
                }

                // Récupérer le vol
                Vol vol = reservationDAO.getVolById(idVol);
                if (vol == null) {
                    throw new Exception("Vol non trouvé");
                }

                double montantTotal = nombrePlaces * vol.getFrais();

                // Créer la réservation
                Reservation reservation = new Reservation();
                reservation.setIdClient(client.getIdClient());
                reservation.setIdVol(idVol);
                reservation.setNombrePlaces(nombrePlaces);
                reservation.setStatut("confirmed");
                reservation.setMontantTotal(montantTotal);

                int idReservation = reservationDAO.createReservation(reservation);
                System.out.println("ID Réservation créée: " + idReservation);

                if (idReservation > 0) {
                    // Créer le paiement
                    if (modePaiement != null && !modePaiement.isEmpty()) {
                        PaiementDAO paiementDAO = new PaiementDAO();
                        paiementDAO.createPaiement(idReservation, montantTotal, modePaiement);
                    }

                    System.out.println("✅ Réservation créée avec succès");

                    // Stocker le message de succès en session pour le popup
                    session.setAttribute("toastMessage", "Réservation créée avec succès !");
                    session.setAttribute("toastType", "success");
                    session.setAttribute("toastDetails", "Vol " + vol.getLieuDepart() + " → " + vol.getLieuArrivee() + " | " + nombrePlaces + " place(s) | " + String.format("%,.0f", montantTotal) + " FCFA");

                    // Nettoyer la session client
                    session.removeAttribute("selectedClient");

                    // Rediriger vers la liste des réservations
                    response.sendRedirect(request.getContextPath() + "/reservations");

                } else {
                    throw new Exception("Échec de la création de la réservation");
                }

            } catch (Exception e) {
                System.err.println("❌ Erreur: " + e.getMessage());
                e.printStackTrace();

                // Stocker le message d'erreur en session
                HttpSession session = request.getSession();
                session.setAttribute("toastMessage", "Erreur lors de la réservation");
                session.setAttribute("toastType", "error");
                session.setAttribute("toastDetails", e.getMessage());

                Client client =  (Client) session.getAttribute("selectedClient");
                if (client.isAdmin()) {
                    response.sendRedirect(request.getContextPath() + "/reservations");
                }
                else{
                    System.out.println("il n'est pas admin. va dans /user/dashboard.jsp");
                    response.sendRedirect(request.getContextPath() + "/user/dashboard.jsp");
                }
            }
        }
    }
}