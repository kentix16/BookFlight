<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>  <!-- ← MODIFIÉ -->
<%@ page import="java.util.*" %>
<%
    Client client = (Client) session.getAttribute("client");
    if (client == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - BookingApp</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="dashboard-body">
<jsp:include page="/includes/menu.jsp" />


<div class="dashboard-container">
    <div class="welcome-card">
        <h2>Bienvenue, <%= client.getPrenoms() %> !</h2>
        <p>Gérez vos réservations et découvrez nos offres de vols</p>
    </div>

    <div class="stats-grid">
        <div class="stat-card">
            <h3>Mes réservations</h3>
            <div class="stat-number" id="reservationsCount">0</div>
        </div>
        <div class="stat-card">
            <h3>Vols disponibles</h3>
            <div class="stat-number" id="volsCount">0</div>
        </div>
        <div class="stat-card">
            <h3>Total dépensé</h3>
            <div class="stat-number" id="totalDepense">0 CFA</div>
        </div>
    </div>

    <div class="reservations-table">
        <h3>📋 Mes dernières réservations</h3>
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Date réservation</th>
                <th>Vol</th>
                <th>Places</th>
                <th>Montant</th>
                <th>Statut</th>
            </tr>
            </thead>
            <tbody id="reservationsList">
            <tr>
                <td colspan="6" style="text-align: center;">Chargement...</td>
            </tr>
            </tbody>
        </table>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        document.getElementById('reservationsCount').textContent = '3';
        document.getElementById('volsCount').textContent = '12';
        document.getElementById('totalDepense').textContent = '125 000 CFA';

        const tbody = document.getElementById('reservationsList');
        tbody.innerHTML = `
                <tr>
                    <td>RES001</td>
                    <td>15/01/2025</td>
                    <td>Dakar → Paris</td>
                    <td>2</td>
                    <td>10 000 CFA</td>
                    <td>Confirmée</td>
                </tr>
                <tr>
                    <td>RES002</td>
                    <td>20/01/2025</td>
                    <td>Paris → Dakar</td>
                    <td>1</td>
                    <td>8 000 CFA</td>
                    <td>Payée</td>
                </tr>
                <tr>
                    <td>RES003</td>
                    <td>01/02/2025</td>
                    <td>Abidjan → Bamako</td>
                    <td>3</td>
                    <td>16 500 CFA</td>
                    <td>En attente</td>
                </tr>
            `;
    });

</script>
</body>
</html>