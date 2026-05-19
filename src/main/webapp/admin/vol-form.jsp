<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>
<%@ page import="com.projetihm.avionmanagement.model.Vol" %>
<%@ page import="com.projetihm.avionmanagement.model.Avion" %>
<%@ page import="java.util.List" %>
<%
    Client client = (Client) session.getAttribute("client");
    if (client == null || !client.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    Vol vol = (Vol) request.getAttribute("vol");
    List<Avion> avions = (List<Avion>) request.getAttribute("avions");
    String title = (String) request.getAttribute("title");
    boolean isEdit = (vol != null);
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administration - <%= title %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .admin-container {
            max-width: 800px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .form-card {
            background: white;
            border-radius: 15px;
            padding: 2rem;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #555;
        }

        .form-group input, .form-group select {
            width: 100%;
            padding: 10px;
            border: 2px solid #e1e5e9;
            border-radius: 8px;
            font-size: 14px;
        }

        .form-group input:focus, .form-group select:focus {
            outline: none;
            border-color: #667eea;
        }

        .form-actions {
            display: flex;
            gap: 10px;
            margin-top: 30px;
        }

        .btn-submit {
            background: #27ae60;
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            font-weight: 600;
        }

        .btn-cancel {
            background: #95a5a6;
            color: white;
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            text-align: center;
        }

        .btn-submit:hover {
            background: #219a52;
        }

        .btn-cancel:hover {
            background: #7f8c8d;
        }

        .back-link {
            display: inline-block;
            margin-bottom: 20px;
            color: #667eea;
            text-decoration: none;
        }

        .back-link:hover {
            text-decoration: underline;
        }

        .help-text {
            font-size: 12px;
            color: #999;
            margin-top: 5px;
        }
    </style>
</head>
<body class="dashboard-body">
<nav class="navbar">
    <h1>✈️ BookingApp - Administration</h1>
    <div class="user-info">
        <span class="user-name">👤 <%= client.getPrenoms() %> <%= client.getNom() %></span>
        <span class="admin-badge">Admin</span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-btn">🚪 Déconnexion</a>
    </div>
</nav>

<div class="admin-container">
    <a href="${pageContext.request.contextPath}/admin/vols" class="back-link">← Retour à la liste des vols</a>

    <div class="form-card">
        <h2><%= title %></h2>

        <form method="POST" action="${pageContext.request.contextPath}/admin/vol-edit">
            <input type="hidden" name="action" value="<%= isEdit ? "edit" : "add" %>">
            <% if (isEdit) { %>
            <input type="hidden" name="vol_id" value="<%= vol.getIdVol() %>">
            <% } %>

            <div class="form-group">
                <label>Avion *</label>
                <select name="id_avion" required>
                    <option value="">Sélectionnez un avion</option>
                    <% for (Avion avion : avions) { %>
                    <option value="<%= avion.getIdAvion() %>"
                            <%= isEdit && vol.getIdAvion() == avion.getIdAvion() ? "selected" : "" %>>
                        <%= avion.getModele() %> - <%= avion.getClasse() %> - <%= avion.getNombrePlaces() %> places
                    </option>
                    <% } %>
                </select>
            </div>

            <div class="form-group">
                <label>Lieu de départ *</label>
                <input type="text" name="lieu_depart" required
                       value="<%= isEdit ? vol.getLieuDepart() : "" %>"
                       placeholder="Ex: Dakar (DSS)">
            </div>

            <div class="form-group">
                <label>Lieu d'arrivée *</label>
                <input type="text" name="lieu_arrivee" required
                       value="<%= isEdit ? vol.getLieuArrivee() : "" %>"
                       placeholder="Ex: Paris (CDG)">
            </div>

            <div class="form-group">
                <label>Date et heure de départ *</label>
                <input type="datetime-local" name="date_depart" required
                       value="<%= isEdit && vol.getDateDepart() != null ? vol.getDateDepart().toString().substring(0, 16) : "" %>">
                <div class="help-text">Format: AAAA-MM-JJ HH:MM</div>
            </div>

            <div class="form-group">
                <label>Date et heure d'arrivée *</label>
                <input type="datetime-local" name="date_arrivee" required
                       value="<%= isEdit && vol.getDateArrivee() != null ? vol.getDateArrivee().toString().substring(0, 16) : "" %>">
                <div class="help-text">Doit être après la date de départ</div>
            </div>

            <div class="form-group">
                <label>Statut du vol *</label>
                <select name="statut_vol" required>
                    <option value="planifié" <%= isEdit && "planifié".equals(vol.getStatutVol()) ? "selected" : "" %>>Planifié</option>
                    <option value="en_retard" <%= isEdit && "en_retard".equals(vol.getStatutVol()) ? "selected" : "" %>>En retard</option>
                    <option value="annulé" <%= isEdit && "annulé".equals(vol.getStatutVol()) ? "selected" : "" %>>Annulé</option>
                    <option value="terminé" <%= isEdit && "terminé".equals(vol.getStatutVol()) ? "selected" : "" %>>Terminé</option>
                </select>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-submit">
                    <%= isEdit ? "Modifier le vol" : "Ajouter le vol" %>
                </button>
                <a href="${pageContext.request.contextPath}/admin/vols" class="btn-cancel">Annuler</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>