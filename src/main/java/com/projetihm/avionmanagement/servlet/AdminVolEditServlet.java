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
        System.out.println("✅ AdminVolEditServlet initialisé");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Client client = (Client) session.getAttribute("client");

        if (client == null || !client.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String idParam = request.getParameter("id");

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

        System.out.println("🔵 AdminVolEditServlet - doPost appelé");

        String volId = request.getParameter("vol_id");
        String action = request.getParameter("action");

        System.out.println("Action: " + action);
        System.out.println("VolId: " + volId);

        try {
            int idAvion = Integer.parseInt(request.getParameter("id_avion"));
            String dateDepartStr = request.getParameter("date_depart");
            String dateArriveeStr = request.getParameter("date_arrivee");
            String lieuDepart = request.getParameter("lieu_depart");
            String lieuArrivee = request.getParameter("lieu_arrivee");
            String statutVol = request.getParameter("statut_vol");

            System.out.println("IdAvion: " + idAvion);
            System.out.println("DateDepart: " + dateDepartStr);
            System.out.println("LieuDepart: " + lieuDepart);
            System.out.println("StatutVol recu: " + statutVol);

            // Convertir le statut français en anglais
            statutVol = STATUS_MAP.get(statutVol);
            if (statutVol == null) {
                statutVol = "scheduled";
            }

            System.out.println("StatutVol apres conversion: " + statutVol);

            // Conversion des dates
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            java.util.Date dateDepartUtil = sdf.parse(dateDepartStr);
            java.util.Date dateArriveeUtil = sdf.parse(dateArriveeStr);
            Timestamp dateDepart = new Timestamp(dateDepartUtil.getTime());
            Timestamp dateArrivee = new Timestamp(dateArriveeUtil.getTime());

            // Validation de base
            Timestamp today = new Timestamp(System.currentTimeMillis());
            if (dateDepart.before(today)) {
                System.out.println("❌ Erreur: Date dans le passé");
                response.sendRedirect(request.getContextPath() + "/admin/vols?error=date_passee");
                return;
            }

            if (dateArrivee.before(dateDepart)) {
                System.out.println("❌ Erreur: Date arrivée avant date départ");
                response.sendRedirect(request.getContextPath() + "/admin/vols?error=date_arrivee_invalide");
                return;
            }

            Vol vol = new Vol();
            vol.setIdAvion(idAvion);
            vol.setDateDepart(dateDepart);
            vol.setDateArrivee(dateArrivee);
            vol.setLieuDepart(lieuDepart);
            vol.setLieuArrivee(lieuArrivee);
            vol.setStatutVol(statutVol);

            boolean success = false;

            if ("add".equals(action)) {
                System.out.println("🔵 Tentative d'ajout de vol...");
                success = volDAO.addVol(vol);
                System.out.println("Résultat addVol: " + success);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/vols?success=added");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/vols?error=add_failed");
                }

            } else if ("edit".equals(action) && volId != null && !volId.isEmpty()) {
                vol.setIdVol(Integer.parseInt(volId));
                System.out.println("🔵 Tentative de modification du vol ID: " + volId);
                success = volDAO.updateVol(vol);
                System.out.println("Résultat updateVol: " + success);

                if (success) {
                    response.sendRedirect(request.getContextPath() + "/admin/vols?success=updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/vols?error=update_failed");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/admin/vols?error=invalid_action");
            }

        } catch (Exception e) {
            System.err.println("❌ Exception dans AdminVolEditServlet: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/vols?error=" + e.getMessage());
        }
    }
}