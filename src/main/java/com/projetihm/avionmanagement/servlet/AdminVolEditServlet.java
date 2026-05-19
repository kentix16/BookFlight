package com.projetihm.avionmanagement.servlet;

import com.projetihm.avionmanagement.dao.VolDAO;
import com.projetihm.avionmanagement.dao.AvionDAO;
import com.projetihm.avionmanagement.model.Vol;
import com.projetihm.avionmanagement.model.Avion;
import com.projetihm.avionmanagement.model.Client;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/admin/vol-edit")
public class AdminVolEditServlet extends HttpServlet {
    private static final Map<String, String> STATUS_MAP = new HashMap<>();
    static {
        STATUS_MAP.put("planifié", "scheduled");
        STATUS_MAP.put("planifie", "scheduled");
        STATUS_MAP.put("en_retard", "delayed");
        STATUS_MAP.put("en retard", "delayed");
        STATUS_MAP.put("annulé", "cancelled");
        STATUS_MAP.put("annule", "cancelled");
        STATUS_MAP.put("terminé", "completed");
        STATUS_MAP.put("termine", "completed");
    }

    private VolDAO volDAO;
    private AvionDAO avionDAO;

    @Override
    public void init() {
        volDAO = new VolDAO();
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

        // Récupérer la liste des avions pour le formulaire
        List<Avion> avions = avionDAO.getAllAvions();
        request.setAttribute("avions", avions);

        if ("edit".equals(action) && idParam != null) {
            int idVol = Integer.parseInt(idParam);
            Vol vol = volDAO.getVolById(idVol);
            request.setAttribute("vol", vol);
            request.setAttribute("title", "Modifier un vol");

        } else if ("add".equals(action)) {
            request.setAttribute("title", "Ajouter un vol");

        } else {
            response.sendRedirect(request.getContextPath() + "/admin/vols");
            return;
        }

        request.getRequestDispatcher("/admin/vol-form.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String volId = request.getParameter("vol_id");
        String action = request.getParameter("action");


        try {
            int idAvion = Integer.parseInt(request.getParameter("id_avion"));
            String dateDepartStr = request.getParameter("date_depart");
            String dateArriveeStr = request.getParameter("date_arrivee");
            String lieuDepart = request.getParameter("lieu_depart");
            String lieuArrivee = request.getParameter("lieu_arrivee");
            String statutVol = request.getParameter("statut_vol");
            statutVol = STATUS_MAP.get(statutVol);
            // Dans AdminVolEditServlet.java
            /*String statutVol = request.getParameter("statut_vol");

// Nettoyer et normaliser la valeur, puis convertir en anglais
            if (statutVol != null) {
                statutVol = statutVol.trim();

                // Convertir les valeurs françaises vers l'anglais
                if (statutVol.equals("planifié") || statutVol.equals("planifie") ||
                        statutVol.equals("planifi�") || statutVol.equals("planifiÃ©")) {
                    statutVol = "scheduled";
                } else if (statutVol.equals("en_retard") || statutVol.equals("en retard")) {
                    statutVol = "delayed";
                } else if (statutVol.equals("annulé") || statutVol.equals("annule") ||
                        statutVol.equals("annul�") || statutVol.equals("annulÃ©")) {
                    statutVol = "cancelled";
                } else if (statutVol.equals("terminé") || statutVol.equals("termine") ||
                        statutVol.equals("termin�") || statutVol.equals("terminÃ©")) {
                    statutVol = "completed";
                }
            }

// Validation
            if (statutVol == null || (!statutVol.equals("scheduled") &&
                    !statutVol.equals("delayed") &&
                    !statutVol.equals("cancelled") &&
                    !statutVol.equals("completed"))) {
                response.sendRedirect(request.getContextPath() + "/admin/vols?error=invalid_status");
                return;
            }*/
// Normaliser la valeur
            // Conversion des dates
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            java.util.Date dateDepartUtil = sdf.parse(dateDepartStr);
            java.util.Date dateArriveeUtil = sdf.parse(dateArriveeStr);
            Timestamp dateDepart = new Timestamp(dateDepartUtil.getTime());
            Timestamp dateArrivee = new Timestamp(dateArriveeUtil.getTime());

            Vol vol = new Vol();
            vol.setIdAvion(idAvion);
            vol.setDateDepart(dateDepart);
            vol.setDateArrivee(dateArrivee);
            vol.setLieuDepart(lieuDepart);
            vol.setLieuArrivee(lieuArrivee);
            vol.setStatutVol(statutVol);

            boolean success;

            if ("add".equals(action)) {
                success = volDAO.addVol(vol);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/vols?success=added");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/vols?error=add_failed");
                }
            } else if ("edit".equals(action) && volId != null && !volId.isEmpty()) {
                vol.setIdVol(Integer.parseInt(volId));
                success = volDAO.updateVol(vol);
                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/vols?success=updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/vols?error=update_failed");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/vols?error=invalid_action");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/vols?error=" + e.getMessage());
        }
    }
}