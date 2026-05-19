<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>
<%@ page import="com.projetihm.avionmanagement.model.Avion" %>
<%@ page import="java.util.List" %>
<%
    Client client = (Client) session.getAttribute("client");
    if (client == null || !client.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    List<Avion> avions = (List<Avion>) request.getAttribute("avions");
    String success = request.getParameter("success");
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Administration - Gestion des avions</title>
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

        .badge {
            display: inline-block;
            padding: 3px 8px;
            border-radius: 15px;
            font-size: 11px;
            font-weight: 600;
        }

        .badge-simple { background: #3498db; color: white; }
        .badge-premium { background: #f39c12; color: white; }
        .badge-vip { background: #9b59b6; color: white; }

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
        }

        .nav-link {
            padding: 10px 15px;
            background: #f8f9fa;
            border-radius: 8px;
            text-decoration: none;
            color: #333;
            font-weight: 500;
        }

        .nav-link.active {
            background: #667eea;
            color: white;
        }
    </style>
</head>
<body class="dashboard-body">
<jsp:include page="/includes/menu.jsp" />

    <!-- Reste du contenu -->
    <div class="header-actions">
        <h2>🛩️ Gestion des avions</h2>
        <a href="${pageContext.request.contextPath}/admin/avion-edit?action=add" class="btn-add">+ Nouvel avion</a>
    </div>

    <% if (success != null) { %>
    <div class="alert-success">
        <% if ("added".equals(success)) { %>
        ✅ Avion ajouté avec succès !
        <% } else if ("updated".equals(success)) { %>
        ✅ Avion modifié avec succès !
        <% } else if ("deleted".equals(success)) { %>
        ✅ Avion supprimé avec succès !
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
        <% } else if ("invalid_model".equals(error)) { %>
        Modèle invalide
        <% } else if ("invalid_class".equals(error)) { %>
        Classe invalide (simple, premium, VIP)
        <% } else if ("invalid_seats".equals(error)) { %>
        Nombre de places invalide
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
                <th>Modèle</th>
                <th>Classe</th>
                <th>Nombre de places</th>
                <th>Frais (CFA)</th>
                <th>Date d'ajout</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <% if (avions != null && !avions.isEmpty()) { %>
            <% for (Avion avion : avions) { %>
            <tr>
                <td><%= avion.getIdAvion() %></td>
                <td><strong><%= avion.getModele() %></strong></td>
                <td>
                                    <span class="badge badge-<%= avion.getClasse().toLowerCase() %>">
                                        <%= avion.getClasse().toUpperCase() %>
                                    </span>
                </td>
                <td><%= avion.getNombrePlaces() %></td>
                <td><%= String.format("%,.0f", avion.getFrais()) %> FCFA</td>
                <td><%= avion.getDateAjout() != null ? avion.getDateAjout().toString().substring(0, 10) : "" %></td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/avion-edit?action=edit&id=<%= avion.getIdAvion() %>"
                       class="btn-edit">✏️ Modifier</a>
                    <button onclick="confirmDelete(<%= avion.getIdAvion() %>)"
                            class="btn-delete">🗑️ Supprimer</button>
                </td>
            </tr>
            <% } %>
            <% } else { %>
            <tr>
                <td colspan="7" style="text-align: center;">Aucun avion trouvé</td>
            </tr>
            <% } %>
            </tbody>
        </table>
    </div>
</div>

<form id="deleteForm" method="POST" action="${pageContext.request.contextPath}/admin/avions">
    <input type="hidden" name="action" value="delete">
    <input type="hidden" name="id" id="deleteId">
</form>

<script>
    function confirmDelete(id) {
        if (confirm('Êtes-vous sûr de vouloir supprimer cet avion ? Cette action est irréversible et ne sera possible que si aucun vol n\'est associé.')) {
            document.getElementById('deleteId').value = id;
            document.getElementById('deleteForm').submit();
        }
    }
</script>
</body>
</html>