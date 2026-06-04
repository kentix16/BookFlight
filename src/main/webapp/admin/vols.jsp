<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>
<%@ page import="com.projetihm.avionmanagement.model.Vol" %>
<%@ page import="java.util.List" %>

<%!
    String getStatutFrancais(String statutAnglais) {
        if (statutAnglais == null) return "Inconnu";
        switch(statutAnglais) {
            case "scheduled": return "Planifié";
            case "delayed": return "En retard";
            case "cancelled": return "Annulé";
            case "completed": return "Terminé";
            default: return statutAnglais;
        }
    }

    String getStatutClass(String statutAnglais) {
        if (statutAnglais == null) return "status-unknown";
        switch(statutAnglais) {
            case "scheduled": return "status-scheduled";
            case "delayed": return "status-delayed";
            case "cancelled": return "status-cancelled";
            case "completed": return "status-completed";
            default: return "status-unknown";
        }
    }
%>

<%
    Client client = (Client) session.getAttribute("client");
    if (client == null || !client.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<Vol> vols = (List<Vol>) request.getAttribute("vols");
    String success = request.getParameter("success");
    String error = request.getParameter("error");
%>

<!DOCTYPE html>
<!-- reste du HTML... -->

<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administration - Gestion des vols</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .admin-container {
            max-width: 1400px;
            margin: 2rem auto;
            padding: 0 2rem;
        }

        .header-actions {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
        }

        .btn-add {
            background: #27ae60;
            color: white;
            padding: 10px 20px;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
            transition: background 0.3s;
        }

        .btn-add:hover {
            background: #219a52;
        }

        .btn-edit {
            background: #3498db;
            color: white;
            padding: 5px 10px;
            border-radius: 5px;
            text-decoration: none;
            font-size: 12px;
            margin-right: 5px;
        }

        .btn-delete {
            background: #e74c3c;
            color: white;
            border: none;
            padding: 5px 10px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 12px;
        }

        .btn-delete:hover {
            background: #c0392b;
        }

        .status {
            display: inline-block;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        .status-scheduled { background: #3498db; color: white; }
        .status-delayed { background: #f39c12; color: white; }
        .status-cancelled { background: #e74c3c; color: white; }
        .status-completed { background: #27ae60; color: white; }
        .status-unknown { background: #95a5a6; color: white; }

        .table-container {
            overflow-x: auto;
            background: white;
            border-radius: 15px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.05);
            padding: 1rem;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #e1e5e9;
        }

        th {
            background: #f8f9fa;
            font-weight: 600;
            color: #555;
        }

        tr:hover {
            background: #f8f9fa;
        }

        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .alert-error {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
            padding: 12px;
            border-radius: 8px;
            margin-bottom: 20px;
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

        .nav-links {
            display: flex;
            gap: 15px;
            margin-bottom: 20px;
            flex-wrap: wrap;
        }

        .nav-link {
            padding: 8px 16px;
            background: #f0f3f8;
            border-radius: 8px;
            text-decoration: none;
            color: #333;
            font-weight: 500;
        }

        .nav-link:hover {
            background: #667eea;
            color: white;
        }

        .nav-link.active {
            background: #667eea;
            color: white;
        }
    </style>
</head>
<body class="dashboard-body">

<jsp:include page="/includes/menu.jsp" />

<div class="admin-container">
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="nav-link">📊 Dashboard</a>
        <a href="${pageContext.request.contextPath}/admin/vols" class="nav-link active">✈️ Vols</a>
        <a href="${pageContext.request.contextPath}/admin/avions" class="nav-link">🛩️ Avions</a>
    </div>

    <div class="header-actions">
        <h2>✈️ Gestion des vols</h2>
        <a href="${pageContext.request.contextPath}/admin/vol-edit?action=add" class="btn-add">+ Nouveau vol</a>
    </div>

    <% if (success != null) { %>
    <div class="alert-success">
        <% if ("added".equals(success)) { %>
        ✅ Vol ajouté avec succès !
        <% } else if ("updated".equals(success)) { %>
        ✅ Vol modifié avec succès !
        <% } else if ("deleted".equals(success)) { %>
        ✅ Vol supprimé avec succès !
        <% } %>
    </div>
    <% } %>

    <% if (error != null) { %>
    <div class="alert-error">
        ❌ Erreur :
        <% if ("delete_failed".equals(error)) { %>
        Échec de la suppression
        <% } else if ("add_failed".equals(error)) { %>
        Échec de l'ajout
        <% } else if ("update_failed".equals(error)) { %>
        Échec de la modification
        <% } else { %>
        <%= error %>
        <% } %>
    </div>
    <% } %>

    <div class="table-container">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Avion</th>
                <th>Départ</th>
                <th>Arrivée</th>
                <th>Date départ</th>
                <th>Date arrivée</th>
                <th>Statut</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <% if (vols != null && !vols.isEmpty()) { %>
            <% for (Vol vol : vols) { %>
            <tr>
                <td><%= vol.getIdVol() %></td>
                <td><%= vol.getModeleAvion() %></td>
                <td><%= vol.getLieuDepart() %></td>
                <td><%= vol.getLieuArrivee() %></td>
                <td><%= vol.getDateDepart() != null ? vol.getDateDepart().toString().substring(0, 16) : "" %></td>
                <td><%= vol.getDateArrivee() != null ? vol.getDateArrivee().toString().substring(0, 16) : "" %></td>
                <td>
                    <span class="status <%= getStatutClass(vol.getStatutVol()) %>">
                        <%= getStatutFrancais(vol.getStatutVol()) %>
                    </span>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/vol-edit?action=edit&id=<%= vol.getIdVol() %>"
                       class="btn-edit">✏️ Modifier</a>
                    <button onclick="confirmDelete(<%= vol.getIdVol() %>)"
                            class="btn-delete">🗑️ Supprimer</button>
                </td>
            </tr>
            <% } %>
            <% } else { %>
            <tr>
                <td colspan="8" style="text-align: center;">Aucun vol trouvé</td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>

<form id="deleteForm" method="POST" action="${pageContext.request.contextPath}/admin/vols">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script>
    function confirmDelete(id) {
        if (confirm('Êtes-vous sûr de vouloir supprimer ce vol ? Cette action est irréversible.')) {
            document.getElementById('deleteId').value = id;
            document.getElementById('deleteForm').submit();
        }
    }
</script>
</body>
</html>