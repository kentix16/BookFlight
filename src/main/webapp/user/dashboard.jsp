<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>
<%@ page import="com.projetihm.avionmanagement.model.Reservation" %>
<%@ page import="com.projetihm.avionmanagement.dao.UserDashboardDAO" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
  Client client = (Client) session.getAttribute("client");
  if (client == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }

  // Rediriger si c'est un admin (ne devrait pas arriver normalement)
  if (client.isAdmin()) {
    response.sendRedirect(request.getContextPath() + "/admin/dashboard.jsp");
    return;
  }

  UserDashboardDAO userDAO = new UserDashboardDAO();
  List<Reservation> reservations = userDAO.getReservationsByClient(client.getIdClient());
  int reservationsCount = reservations.size();
  double totalDepense = userDAO.getTotalDepense(client.getIdClient());

  SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Mon espace - BookingApp</title>
  <style>
    /* Styles spécifiques à l'interface utilisateur */
    * {
      margin: 0;
      padding: 0;
      box-sizing: border-box;
    }

    body {
      font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
      background: #f0f2f5;
    }

    /* Header */
    .user-header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 1rem 2rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }

    .user-header h1 {
      font-size: 1.5rem;
    }

    .user-header h1 a {
      color: white;
      text-decoration: none;
    }

    .user-info {
      display: flex;
      align-items: center;
      gap: 1rem;
    }

    .user-name {
      font-weight: 600;
    }

    .logout-btn {
      background: rgba(255,255,255,0.2);
      color: white;
      padding: 0.5rem 1rem;
      border-radius: 8px;
      text-decoration: none;
      transition: background 0.3s;
    }

    .logout-btn:hover {
      background: rgba(255,255,255,0.3);
    }

    /* Container principal */
    .user-container {
      max-width: 1200px;
      margin: 2rem auto;
      padding: 0 2rem;
    }

    /* Cartes de bienvenue */
    .welcome-card {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      border-radius: 15px;
      padding: 2rem;
      margin-bottom: 2rem;
      color: white;
    }

    .welcome-card h2 {
      margin-bottom: 0.5rem;
      font-size: 1.8rem;
    }

    .welcome-card p {
      opacity: 0.9;
    }

    /* Grille de stats */
    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1.5rem;
      margin-bottom: 2rem;
    }

    .stat-card {
      background: white;
      border-radius: 15px;
      padding: 1.5rem;
      text-align: center;
      box-shadow: 0 2px 10px rgba(0,0,0,0.05);
      transition: transform 0.2s;
    }

    .stat-card:hover {
      transform: translateY(-5px);
    }

    .stat-card h3 {
      color: #667eea;
      margin-bottom: 0.5rem;
    }

    .stat-number {
      font-size: 2rem;
      font-weight: bold;
      color: #333;
    }

    .stat-label {
      color: #666;
      font-size: 0.85rem;
      margin-top: 0.25rem;
    }

    /* Bouton réservation */
    .btn-reservation {
      display: inline-block;
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 1rem 2rem;
      border-radius: 50px;
      text-decoration: none;
      font-weight: bold;
      text-align: center;
      transition: transform 0.2s, box-shadow 0.2s;
      margin-bottom: 2rem;
      width: 100%;
      font-size: 1.1rem;
    }

    .btn-reservation:hover {
      transform: translateY(-2px);
      box-shadow: 0 5px 15px rgba(102,126,234,0.4);
    }

    /* Tableau des réservations */
    .reservations-section {
      background: white;
      border-radius: 15px;
      padding: 1.5rem;
      box-shadow: 0 2px 10px rgba(0,0,0,0.05);
    }

    .reservations-section h3 {
      margin-bottom: 1rem;
      color: #333;
      border-bottom: 2px solid #667eea;
      padding-bottom: 0.5rem;
    }

    .reservations-table {
      overflow-x: auto;
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

    .status-confirmed {
      background: #27ae60;
      color: white;
      padding: 3px 8px;
      border-radius: 20px;
      font-size: 12px;
      display: inline-block;
    }

    .status-cancelled {
      background: #e74c3c;
      color: white;
      padding: 3px 8px;
      border-radius: 20px;
      font-size: 12px;
      display: inline-block;
    }

    .status-pending {
      background: #f39c12;
      color: white;
      padding: 3px 8px;
      border-radius: 20px;
      font-size: 12px;
      display: inline-block;
    }

    .status-paid {
      background: #3498db;
      color: white;
      padding: 3px 8px;
      border-radius: 20px;
      font-size: 12px;
      display: inline-block;
    }

    .empty-message {
      text-align: center;
      padding: 2rem;
      color: #666;
    }

    @media (max-width: 768px) {
      .user-container {
        padding: 0 1rem;
      }
      th, td {
        padding: 8px;
        font-size: 12px;
      }
    }
  </style>
</head>
<body>
<header class="user-header">
  <h1><a href="${pageContext.request.contextPath}/user/dashboard.jsp">✈️ BookingApp</a></h1>
  <div class="user-info">
    <span class="user-name">👤 <%= client.getPrenoms() %> <%= client.getNom() %></span>
    <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-btn">🚪 Déconnexion</a>
  </div>
</header>

<div class="user-container">
  <div class="welcome-card">
    <h2>Bonjour, <%= client.getPrenoms() %> !</h2>
    <p>Bienvenue sur votre espace personnel. Gérez vos réservations et planifiez vos prochains voyages.</p>
  </div>

  <div class="stats-grid">
    <div class="stat-card">
      <h3>📅 Mes réservations</h3>
      <div class="stat-number"><%= reservationsCount %></div>
      <div class="stat-label">réservation(s)</div>
    </div>
    <div class="stat-card">
      <h3>💰 Total dépensé</h3>
      <div class="stat-number"><%= String.format("%,.0f", totalDepense) %></div>
      <div class="stat-label">FCFA</div>
    </div>
  </div>

  <a href="${pageContext.request.contextPath}/reservation" class="btn-reservation">✈️ Effectuer une nouvelle réservation</a>

  <div class="reservations-section">
    <h3>📋 Mes réservations récentes</h3>

    <% if (reservations != null && !reservations.isEmpty()) { %>
    <div class="reservations-table">
      <table>
        <thead>
        <tr>
          <th>N°</th>
          <th>Vol</th>
          <th>Date réservation</th>
          <th>Places</th>
          <th>Montant</th>
          <th>Statut</th>
        </tr>
        </thead>
        <tbody>
        <% for (Reservation r : reservations) { %>
        <tr>
          <td><%= r.getIdReservation() %></td>
          <td><%= r.getVolInfo() %></td>
          <td><%= sdf.format(r.getDateReservation()) %></td>
          <td><%= r.getNombrePlaces() %></td>
          <td><%= String.format("%,.0f", r.getMontantTotal()) %> FCFA</td>
          <td>
                                        <span class="status-<%= r.getStatut() %>">
                                            <% if ("confirmed".equals(r.getStatut())) { %>Confirmée
                                            <% } else if ("cancelled".equals(r.getStatut())) { %>Annulée
                                            <% } else if ("pending".equals(r.getStatut())) { %>En attente
                                            <% } else if ("paid".equals(r.getStatut())) { %>Payée
                                            <% } else { %><%= r.getStatut() %><% } %>
                                        </span>
          </td>
        </tr>
        <% } %>
        </tbody>
      </table>
    </div>
    <% } else { %>
    <div class="empty-message">
      <p>Vous n'avez pas encore de réservation.</p>
      <p><a href="${pageContext.request.contextPath}/reservation">📅 Réservez votre premier vol maintenant !</a></p>
    </div>
    <% } %>
  </div>
</div>
</body>
</html>