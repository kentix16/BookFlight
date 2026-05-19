<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Reservation" %>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Client client = (Client) request.getAttribute("client");
    List<Reservation> reservations = (List<Reservation>) request.getAttribute("reservations");

    Boolean isAdminObj = (Boolean) request.getAttribute("isAdmin");
    boolean isAdmin = isAdminObj != null && isAdminObj;

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    SimpleDateFormat sdfShort = new SimpleDateFormat("dd/MM/yyyy");
    String success = request.getParameter("success");
    String error = request.getParameter("error");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes réservations</title>
    <style>
        /* RESET & BASE */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: #5a67d8;
            min-height: 100vh;
        }

        /* CONTENEUR PRINCIPAL */
        .reservations-container {
            max-width: 1400px;
            margin: 100px auto 2rem auto;
            padding: 0 2rem;
            animation: fadeInUp 0.5s ease-out;
        }

        @keyframes fadeInUp {
            from {
                opacity: 0;
                transform: translateY(30px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        /* TITRE */
        .page-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2rem;
            flex-wrap: wrap;
            gap: 1rem;
        }

        .page-title {
            color: white;
            font-size: 1.8rem;
            font-weight: 700;
            display: flex;
            align-items: center;
            gap: 0.75rem;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
        }

        .page-title:before {
            content: "📋";
            font-size: 1.8rem;
        }

        .btn-new {
            background: white;
            color: #5a67d8;
            padding: 0.75rem 1.5rem;
            border-radius: 12px;
            text-decoration: none;
            font-weight: 600;
            transition: all 0.3s;
            display: inline-flex;
            align-items: center;
            gap: 0.5rem;
            box-shadow: 0 4px 10px rgba(0,0,0,0.1);
        }

        .btn-new:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 15px rgba(0,0,0,0.15);
        }

        /* STATISTIQUES */
        .stats-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
            gap: 1.5rem;
            margin-bottom: 2rem;
        }

        .stat-card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 16px;
            padding: 1.25rem;
            text-align: center;
            transition: transform 0.3s;
        }

        .stat-card:hover {
            transform: translateY(-3px);
        }

        .stat-icon {
            font-size: 2rem;
            margin-bottom: 0.5rem;
        }

        .stat-value {
            font-size: 1.8rem;
            font-weight: bold;
            color: #5a67d8;
        }

        .stat-label {
            color: #666;
            font-size: 0.85rem;
            margin-top: 0.25rem;
        }

        /* CARTE PRINCIPALE */
        .card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 24px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }

        .card-header {
            background: linear-gradient(135deg, #5a67d8 0%, #434190 100%);
            color: white;
            padding: 1.25rem 1.5rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 1rem;
        }

        .card-header h2 {
            margin: 0;
            font-size: 1.3rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .reservation-count {
            background: rgba(255,255,255,0.2);
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.85rem;
        }

        .card-body {
            padding: 1.5rem;
        }

        /* TABLEAU */
        .table-wrapper {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 1rem;
            text-align: left;
            border-bottom: 1px solid #e1e5e9;
        }

        th {
            background: #f8f9fa;
            font-weight: 600;
            color: #555;
            font-size: 0.85rem;
            text-transform: uppercase;
            letter-spacing: 0.5px;
        }

        tr {
            transition: background 0.2s;
        }

        tr:hover {
            background: #f8f9fa;
        }

        /* BADGES DE STATUT */
        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.75rem;
            font-weight: 600;
        }

        .status-confirmed {
            background: #d4edda;
            color: #27ae60;
        }

        .status-confirmed:before {
            content: "✓";
        }

        .status-cancelled {
            background: #f8d7da;
            color: #e74c3c;
        }

        .status-cancelled:before {
            content: "✗";
        }

        .status-pending {
            background: #fff3cd;
            color: #f39c12;
        }

        .status-pending:before {
            content: "⏳";
        }

        .status-paid {
            background: #d1ecf1;
            color: #3498db;
        }

        .status-paid:before {
            content: "💰";
        }

        /* BOUTONS D'ACTION */
        .btn-cancel {
            background: #e74c3c;
            color: white;
            border: none;
            padding: 0.4rem 0.8rem;
            border-radius: 8px;
            cursor: pointer;
            font-size: 0.75rem;
            font-weight: 600;
            transition: all 0.3s;
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
        }

        .btn-cancel:hover {
            background: #c0392b;
            transform: scale(1.05);
        }

        .btn-view {
            background: #5a67d8;
            color: white;
            border: none;
            padding: 0.4rem 0.8rem;
            border-radius: 8px;
            cursor: pointer;
            font-size: 0.75rem;
            font-weight: 600;
            transition: all 0.3s;
            display: inline-flex;
            align-items: center;
            gap: 0.25rem;
            text-decoration: none;
        }

        .btn-view:hover {
            background: #434190;
            transform: scale(1.05);
        }

        /* MONTANT */
        .amount {
            font-weight: bold;
            color: #27ae60;
        }

        /* ALERTES */
        .alert {
            padding: 1rem;
            border-radius: 12px;
            margin-bottom: 1.5rem;
            font-size: 0.9rem;
            backdrop-filter: blur(10px);
        }

        .alert-success {
            background: rgba(212, 237, 218, 0.95);
            color: #27ae60;
            border-left: 4px solid #27ae60;
        }

        .alert-error {
            background: rgba(248, 215, 218, 0.95);
            color: #e74c3c;
            border-left: 4px solid #e74c3c;
        }

        /* MESSAGE AUCUNE RÉSERVATION */
        .empty-state {
            text-align: center;
            padding: 3rem;
        }

        .empty-icon {
            font-size: 4rem;
            margin-bottom: 1rem;
            opacity: 0.5;
        }

        .empty-title {
            font-size: 1.2rem;
            color: #555;
            margin-bottom: 0.5rem;
        }

        .empty-text {
            color: #888;
            margin-bottom: 1.5rem;
        }

        /* FILTRES */
        .filters {
            display: flex;
            gap: 1rem;
            flex-wrap: wrap;
            margin-bottom: 1.5rem;
        }

        .filter-btn {
            background: #f0f3f8;
            border: none;
            padding: 0.5rem 1rem;
            border-radius: 20px;
            cursor: pointer;
            font-size: 0.85rem;
            transition: all 0.3s;
        }

        .filter-btn:hover, .filter-btn.active {
            background: #5a67d8;
            color: white;
        }

        /* RESPONSIVE */
        @media (max-width: 768px) {
            .reservations-container {
                margin-top: 80px;
                padding: 0 1rem;
            }

            .page-header {
                flex-direction: column;
                align-items: flex-start;
            }

            th, td {
                padding: 0.75rem;
                font-size: 0.85rem;
            }

            .stats-grid {
                grid-template-columns: repeat(2, 1fr);
            }
        }

        @media (max-width: 480px) {
            .stats-grid {
                grid-template-columns: 1fr;
            }

            .card-header {
                flex-direction: column;
                text-align: center;
            }
        }
    </style>
</head>
<body>

<jsp:include page="/includes/menu.jsp" />

<div class="reservations-container">
    <div class="page-header">
        <div class="page-title">Mes réservations</div>
        <a href="${pageContext.request.contextPath}/reservation" class="btn-new">+ Nouvelle réservation</a>
    </div>

    <% if (success != null && "cancelled".equals(success)) { %>
    <div class="alert alert-success">✅ Réservation annulée avec succès !</div>
    <% } %>
    <% if (error != null) { %>
    <div class="alert alert-error">❌ Erreur : <%= error %></div>
    <% } %>

    <!-- Statistiques -->
    <div class="stats-grid">
        <div class="stat-card">
            <div class="stat-icon">📊</div>
            <div class="stat-value"><%= reservations != null ? reservations.size() : 0 %></div>
            <div class="stat-label">Total réservations</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">✅</div>
            <div class="stat-value">
                <%
                    int confirmed = 0;
                    if (reservations != null) {
                        for (Reservation r : reservations) {
                            if ("confirmed".equals(r.getStatut())) confirmed++;
                        }
                    }
                %>
                <%= confirmed %>
            </div>
            <div class="stat-label">Confirmées</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">💰</div>
            <div class="stat-value">
                <%
                    double total = 0;
                    if (reservations != null) {
                        for (Reservation r : reservations) {
                            if ("paid".equals(r.getStatut()) || "confirmed".equals(r.getStatut())) {
                                total += r.getMontantTotal();
                            }
                        }
                    }
                %>
                <%= String.format("%,.0f", total) %>
            </div>
            <div class="stat-label">Total dépensé (FCFA)</div>
        </div>
        <div class="stat-card">
            <div class="stat-icon">🎟️</div>
            <div class="stat-value">
                <%
                    int places = 0;
                    if (reservations != null) {
                        for (Reservation r : reservations) {
                            places += r.getNombrePlaces();
                        }
                    }
                %>
                <%= places %>
            </div>
            <div class="stat-label">Places réservées</div>
        </div>
    </div>

    <!-- Filtres -->
    <div class="filters">
        <button class="filter-btn active" onclick="filterReservations('all')">Toutes</button>
        <button class="filter-btn" onclick="filterReservations('confirmed')">Confirmées</button>
        <button class="filter-btn" onclick="filterReservations('paid')">Payées</button>
        <button class="filter-btn" onclick="filterReservations('pending')">En attente</button>
        <button class="filter-btn" onclick="filterReservations('cancelled')">Annulées</button>
    </div>

    <!-- Liste des réservations -->
    <div class="card">
        <div class="card-header">
            <h2>
                <span>📋</span>
                <%= isAdmin ? "Toutes les réservations" : "Mes réservations" %>
            </h2>
            <span class="reservation-count"><%= reservations != null ? reservations.size() : 0 %> réservation(s)</span>
        </div>
        <div class="card-body">
            <% if (reservations == null || reservations.isEmpty()) { %>
            <div class="empty-state">
                <div class="empty-icon">📭</div>
                <div class="empty-title">Aucune réservation trouvée</div>
                <div class="empty-text">Vous n'avez pas encore effectué de réservation.</div>
                <a href="${pageContext.request.contextPath}/reservation" class="btn-new" style="background: #5a67d8; color: white;">+ Faire une réservation</a>
            </div>
            <% } else { %>
            <div class="table-wrapper">
                <table id="reservationsTable">
                    <thead>
                    <tr>
                        <% if (isAdmin) { %>
                        <th>ID</th>
                        <th>Client</th>
                        <% } %>
                        <th>Vol</th>
                        <th>Date réservation</th>
                        <th>Date départ</th>
                        <th>Places</th>
                        <th>Montant</th>
                        <th>Statut</th>
                        <th>Action</th>
                    </tr>
                    </thead>
                    <tbody>
                    <% for (Reservation r : reservations) { %>
                    <tr data-status="<%= r.getStatut() %>">
                        <% if (isAdmin) { %>
                        <td><%= r.getIdReservation() %></td>
                        <td><strong><%= r.getClientPrenoms() %> <%= r.getClientNom() %></strong></td>
                        <% } %>
                        <td><%= r.getVolInfo() %></td>
                        <td><%= r.getDateReservation() != null ? sdf.format(r.getDateReservation()) : "" %></td>
                        <td><%= r.getVolInfo().contains("(") ? r.getVolInfo().substring(r.getVolInfo().indexOf("(")) : "" %></td>
                        <td><%= r.getNombrePlaces() %></td>
                        <td class="amount"><%= String.format("%,.0f", r.getMontantTotal()) %> FCFA</td>
                        <td>
                                            <span class="status-badge status-<%= r.getStatut() %>">
                                                <% if ("confirmed".equals(r.getStatut())) { %>Confirmée
                                                <% } else if ("cancelled".equals(r.getStatut())) { %>Annulée
                                                <% } else if ("pending".equals(r.getStatut())) { %>En attente
                                                <% } else if ("paid".equals(r.getStatut())) { %>Payée
                                                <% } else { %><%= r.getStatut() %><% } %>
                                            </span>
                        </td>
                        <td>
                            <% if ("confirmed".equals(r.getStatut()) || "pending".equals(r.getStatut())) { %>
                            <button onclick="cancelReservation(<%= r.getIdReservation() %>)" class="btn-cancel">
                                🗑️ Annuler
                            </button>
                            <% } else { %>
                            <span style="color: #999; font-size: 0.75rem;">-</span>
                            <% } %>
                        </td>
                    </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
            <% } %>
        </div>
    </div>
</div>

<form id="cancelForm" method="POST" action="${pageContext.request.contextPath}/reservations">
    <input type="hidden" name="action" value="cancel">
    <input type="hidden" name="id" id="cancelId">
</form>

<script>
    // Annulation d'une réservation
    function cancelReservation(id) {
        if (confirm('Êtes-vous sûr de vouloir annuler cette réservation ?')) {
            document.getElementById('cancelId').value = id;
            document.getElementById('cancelForm').submit();
        }
    }

    // Filtrage des réservations
    function filterReservations(status) {
        const rows = document.querySelectorAll('#reservationsTable tbody tr');
        const buttons = document.querySelectorAll('.filter-btn');

        // Mettre à jour l'état actif des boutons
        buttons.forEach(btn => {
            btn.classList.remove('active');
            if (btn.textContent.toLowerCase().includes(status === 'all' ? 'toutes' : status)) {
                btn.classList.add('active');
            }
        });

        // Filtrer les lignes
        rows.forEach(row => {
            if (status === 'all') {
                row.style.display = '';
            } else {
                const rowStatus = row.getAttribute('data-status');
                if (rowStatus === status) {
                    row.style.display = '';
                } else {
                    row.style.display = 'none';
                }
            }
        });
    }
</script>
</body>
</html>