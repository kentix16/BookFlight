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
        //reservationDAO.testConnection(28);

        HttpSession session = request.getSession();
        Client loggedInClient = (Client) session.getAttribute("client");

        // Récupérer le paramètre reset pour savoir si l'admin veut changer de client
        String reset = request.getParameter("reset");

        // Si reset=true, on supprime le client sélectionné
        if ("true".equals(reset)) {
            session.removeAttribute("selectedClient");
            System.out.println("Reset client sélectionné");
        }

        // Récupérer le client sélectionné (peut être null après reset)
        Client selectedClient = (Client) session.getAttribute("selectedClient");

        // Si aucun client n'est sélectionné et que l'utilisateur n'est PAS admin, on auto-sélectionne l'utilisateur connecté
        if (selectedClient == null && loggedInClient != null && !loggedInClient.isAdmin()) {
            session.setAttribute("selectedClient", loggedInClient);
            System.out.println("Client automatiquement sélectionné pour utilisateur normal: " + loggedInClient.getPrenoms() + " " + loggedInClient.getNom());
        }

        // Récupérer les vols disponibles
        List<Vol> vols = reservationDAO.getAvailableVols();

        // Calculer les places disponibles pour chaque vol
        for (Vol vol : vols) {
            int placesDisponibles = reservationDAO.getAvailableSeats(vol.getIdVol());
            vol.setPlacesDisponibles(placesDisponibles);
            System.out.println("Vol " + vol.getIdVol() + " - Places disponibles: " + placesDisponibles);
        }

        System.out.println("Nombre de vols disponibles: " + (vols != null ? vols.size() : 0));

        request.setAttribute("vols", vols);

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

        // 🔥 AJOUTE CETTE PARTIE - Calculer les places disponibles
        for (Vol vol : vols) {
            int placesDisponibles = reservationDAO.getAvailableSeats(vol.getIdVol());
            vol.setPlacesDisponibles(placesDisponibles);
        }

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

            // Récupérer les vols disponibles et calculer les places disponibles
            List<Vol> vols = reservationDAO.getAvailableVols();

            // 🔥 AJOUTE CETTE PARTIE - Recalculer les places disponibles
            for (Vol vol : vols) {
                int placesDisponibles = reservationDAO.getAvailableSeats(vol.getIdVol());
                vol.setPlacesDisponibles(placesDisponibles);
                System.out.println("Vol " + vol.getIdVol() + " - Places disponibles: " + placesDisponibles);
            }

            request.setAttribute("vols", vols);
            request.setAttribute("selectedClient", client);
            request.getRequestDispatcher("/reservation/reservation.jsp").forward(request, response);


        }
        else if ("createClient".equals(action)) {
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

            // 🔥 AJOUTE CETTE PARTIE - Calculer les places disponibles
            for (Vol vol : vols) {
                int placesDisponibles = reservationDAO.getAvailableSeats(vol.getIdVol());
                vol.setPlacesDisponibles(placesDisponibles);
            }

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
                int nombrePlacesDemandees = Integer.parseInt(request.getParameter("nombrePlaces"));
                String modePaiement = request.getParameter("modePaiement");

                // Utiliser la méthode getAvailableSeats() existante
                int placesDisponibles = reservationDAO.getAvailableSeats(idVol);
                System.out.println("Places disponibles selon DAO: " + placesDisponibles);

                // Vérifier si la demande est possible
                if (nombrePlacesDemandees <= 0) {
                    List<Vol> vols = reservationDAO.getAvailableVols();
                    request.setAttribute("vols", vols);
                    request.setAttribute("errorMessage", "Veuillez sélectionner au moins 1 place.");
                    request.getRequestDispatcher("/reservation/reservation.jsp").forward(request, response);
                    return;
                }

                if (nombrePlacesDemandees > placesDisponibles) {
                    List<Vol> vols = reservationDAO.getAvailableVols();
                    request.setAttribute("vols", vols);
                    request.setAttribute("errorMessage", "Désolé, il ne reste que " + placesDisponibles + " place(s) disponible(s) sur ce vol. Vous avez demandé " + nombrePlacesDemandees + " place(s).");
                    request.getRequestDispatcher("/reservation/reservation.jsp").forward(request, response);
                    request.removeAttribute("SelectedClient");
                    return;
                }

                // Récupérer le vol
                Vol vol = reservationDAO.getVolById(idVol);
                if (vol == null) {
                    throw new Exception("Vol non trouvé");
                }

                double montantTotal = nombrePlacesDemandees * vol.getFrais();

                // Créer la réservation
                Reservation reservation = new Reservation();
                reservation.setIdClient(client.getIdClient());
                reservation.setIdVol(idVol);
                reservation.setNombrePlaces(nombrePlacesDemandees);
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

                    // Stocker le message de succès en session
                    session.setAttribute("toastMessage", "Réservation créée avec succès !");
                    session.setAttribute("toastType", "success");
                    session.setAttribute("toastDetails", "Vol " + vol.getLieuDepart() + " → " + vol.getLieuArrivee() + " | " + nombrePlacesDemandees + " place(s) | " + String.format("%,.0f", montantTotal) + " FCFA");

                    // Nettoyer la session client seulement pour l'admin
                    Client loggedUser = (Client) session.getAttribute("client");
                    if (loggedUser != null && loggedUser.isAdmin()) {
                        session.removeAttribute("selectedClient");
                    }

                    // Rediriger vers la liste des réservations
                    response.sendRedirect(request.getContextPath() + "/reservations");

                } else {
                    session.removeAttribute("selectedClient");
                    throw new Exception("Échec de la création de la réservation");
                }

            } catch (Exception e) {
                System.err.println("❌ Erreur: " + e.getMessage());
                e.printStackTrace();

                HttpSession session = request.getSession();
                session.setAttribute("toastMessage", "Erreur lors de la réservation");
                session.setAttribute("toastType", "error");
                session.setAttribute("toastDetails", e.getMessage());

                response.sendRedirect(request.getContextPath() + "/reservations");
            }
        }
    }
}