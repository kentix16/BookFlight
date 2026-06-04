<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>
<%@ page import="com.projetihm.avionmanagement.model.Avion" %>
<%
    Client client = (Client) session.getAttribute("client");
    if (client == null || !client.isAdmin()) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    Avion avion = (Avion) request.getAttribute("avion");
    String title = (String) request.getAttribute("title");
    boolean isEdit = (avion != null);
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
<nav class="navbar">
    <h1>✈️ BookingApp - Administration</h1>
    <div class="user-info">
        <span class="user-name">👤 <%= client.getPrenoms() %> <%= client.getNom() %></span>
        <span class="admin-badge">Admin</span>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-btn">🚪 Déconnexion</a>
    </div>
</nav>

<div class="admin-container">
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/admin/dashboard.jsp" class="nav-link">📊 Dashboard</a>
        <a href="${pageContext.request.contextPath}/admin/vols" class="nav-link">✈️ Vols</a>
        <a href="${pageContext.request.contextPath}/admin/avions" class="nav-link active">🛩️ Avions</a>
    </div>

    <a href="${pageContext.request.contextPath}/admin/avions" class="back-link">← Retour à la liste des avions</a>

    <div class="form-card">
        <h2><%= title %></h2>

        <form method="POST" action="${pageContext.request.contextPath}/admin/avion-edit">
            <input type="hidden" name="action" value="<%= isEdit ? "edit" : "add" %>">
            <% if (isEdit) { %>
            <input type="hidden" name="avion_id" value="<%= avion.getIdAvion() %>">
            <% } %>

            <div class="form-group">
                <label>Modèle *</label>
                <input type="text" name="modele" required
                       value="<%= isEdit ? avion.getModele() : "" %>"
                       placeholder="Ex: Airbus A320, Boeing 737, Embraer E190">
                <div class="help-text">Le modèle commercial de l'avion</div>
            </div>

            <div class="form-group">
                <label>Classe *</label>
                <select name="classe" required>
                    <option value="">Sélectionnez une classe</option>
                    <option value="simple" <%= isEdit && "simple".equals(avion.getClasse()) ? "selected" : "" %>>Simple (Économique)</option>
                    <option value="premium" <%= isEdit && "premium".equals(avion.getClasse()) ? "selected" : "" %>>Premium (Affaires)</option>
                    <option value="VIP" <%= isEdit && "VIP".equals(avion.getClasse()) ? "selected" : "" %>>VIP (Luxe)</option>
                </select>
                <div class="help-text">La classe de l'avion détermine le confort et le prix</div>
            </div>

            <div class="form-group">
                <label>Nombre de places *</label>
                <input type="number" name="nombre_places" required min="1" max="500"
                       value="<%= isEdit ? avion.getNombrePlaces() : "" %>"
                       placeholder="Ex: 180">
                <div class="help-text">Capacité totale de l'avion en passagers</div>
            </div>

            <div class="form-group">
                <label>Frais (CFA) *</label>
                <input type="number" name="frais" required min="0" step="1000"
                       value="<%= isEdit ? avion.getFrais() : "" %>"
                       placeholder="Ex: 5000000">
                <div class="help-text">Frais de base par vol en Francs CFA</div>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn-submit">
                    <%= isEdit ? "Modifier l'avion" : "Ajouter l'avion" %>
                </button>
                <a href="${pageContext.request.contextPath}/admin/avions" class="btn-cancel">Annuler</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>