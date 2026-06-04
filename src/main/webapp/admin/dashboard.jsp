<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>
<%@ page import="com.projetihm.avionmanagement.dao.StatDAO" %>
<%
    Client client = (Client) session.getAttribute("client");
    if (client == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    StatDAO statDAO = new StatDAO();
    int reservationsCount = statDAO.getReservationsCount(client.getIdClient(), client.isAdmin());
    int volsCount = statDAO.getAvailableFlightsCount();
    double totalDepense = statDAO.getTotalDepense(client.getIdClient(), client.isAdmin());
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - BookingApp</title>
    <style>
        /* Styles spécifiques au dashboard uniquement - Thème Bleu */
        .dashboard-stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .dashboard-stat-card {
            background: linear-gradient(135deg, #ffffff 0%, #f0f7ff 100%);
            border-radius: 15px;
            padding: 1.5rem;
            text-align: center;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            transition: transform 0.2s, box-shadow 0.2s;
            border: 1px solid rgba(52, 152, 219, 0.2);
        }

        .dashboard-stat-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(52, 152, 219, 0.2);
            border-color: #3498db;
        }

        .dashboard-stat-card h3 {
            color: #2980b9;
            margin-bottom: 0.5rem;
            font-size: 1.1rem;
        }

        .dashboard-stat-number {
            font-size: 2rem;
            font-weight: bold;
            color: #2c3e50;
        }

        .dashboard-stat-label {
            color: #7f8c8d;
            font-size: 0.85rem;
            margin-top: 0.25rem;
        }

        .dashboard-stat-card a {
            color: #3498db;
            text-decoration: none;
            font-size: 1rem;
            font-weight: 500;
        }

        .dashboard-stat-card a:hover {
            color: #2980b9;
            text-decoration: underline;
        }

        .dashboard-welcome-card {
            background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
            border-radius: 15px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
            color: white;
        }

        .dashboard-welcome-card h2 {
            margin: 0 0 0.5rem 0;
            color: white;
            font-size: 1.8rem;
        }

        .dashboard-welcome-card p {
            margin: 0;
            color: rgba(255, 255, 255, 0.9);
            font-size: 1rem;
        }

        .dashboard-admin-section {
            margin-top: 1rem;
        }

        /* Style pour le bouton admin */
        .dashboard-admin-section .dashboard-stat-card {
            background: linear-gradient(135deg, #ffffff 0%, #e8f4fd 100%);
        }

        .dashboard-admin-section .dashboard-stat-card h3 {
            color: #e67e22;
        }

        .dashboard-admin-section .dashboard-stat-card a {
            color: #e67e22;
        }

        .dashboard-admin-section .dashboard-stat-card a:hover {
            color: #d35400;
        }
    </style>
</head>
<body>

<!-- Inclusion du menu existant -->
<jsp:include page="/includes/menu.jsp" />

<!-- Dashboard content -->
<div class="main-container">
    <div class="dashboard-welcome-card">
        <h2>Bienvenue, <%= client.getPrenoms() %> !</h2>
        <p>Gérez vos réservations et découvrez nos offres de vols</p>
    </div>

    <div class="dashboard-stats-grid">
        <div class="dashboard-stat-card">
            <h3>📅 Mes réservations</h3>
            <div class="dashboard-stat-number"><%= reservationsCount %></div>
            <div class="dashboard-stat-label">réservation(s)</div>
        </div>

        <div class="dashboard-stat-card">
            <h3>✈️ Vols disponibles</h3>
            <div class="dashboard-stat-number"><%= volsCount %></div>
            <div class="dashboard-stat-label">vol(s) au départ</div>
        </div>

        <div class="dashboard-stat-card">
            <h3>💰 Total dépensé</h3>
            <div class="dashboard-stat-number"><%= String.format("%,.0f", totalDepense) %></div>
            <div class="dashboard-stat-label">FCFA</div>
        </div>
    </div>

    <div class="dashboard-stats-grid">
        <div class="dashboard-stat-card">
            <h3>📅 Nouvelle réservation</h3>
            <div class="dashboard-stat-number">
                <a href="${pageContext.request.contextPath}/reservation">✈️ Réserver un vol</a>
            </div>
        </div>

        <div class="dashboard-stat-card">
            <h3>📋 Mes réservations</h3>
            <div class="dashboard-stat-number">
                <a href="${pageContext.request.contextPath}/reservations">📋 Voir mes réservations</a>
            </div>
        </div>
    </div>

    <% if(client.isAdmin()) { %>
    <div class="dashboard-admin-section">
        <div class="dashboard-stats-grid">
            <div class="dashboard-stat-card">
                <h3>🔧 Administration</h3>
                <div class="dashboard-stat-number">
                    <a href="${pageContext.request.contextPath}/admin/vols">✈️ Gérer les vols</a>
                </div>
            </div>
            <div class="dashboard-stat-card">
                <h3>🛩️ Avions</h3>
                <div class="dashboard-stat-number">
                    <a href="${pageContext.request.contextPath}/admin/avions">🛩️ Gérer les avions</a>
                </div>
            </div>
        </div>
    </div>
    <% } %>
</div>

</body>
</html>