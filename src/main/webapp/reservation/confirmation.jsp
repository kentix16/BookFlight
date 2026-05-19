<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Reservation" %>
<%@ page import="com.projetihm.avionmanagement.model.Vol" %>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Reservation reservation = (Reservation) session.getAttribute("lastReservation");
    Vol vol = (Vol) session.getAttribute("lastVol");
    Client client = (Client) session.getAttribute("lastClient");
    Double montantTotal = (Double) session.getAttribute("montantTotal");
    String modePaiement = (String) session.getAttribute("modePaiement");

    if (reservation == null || vol == null || client == null) {
        response.sendRedirect(request.getContextPath() + "/reservation");
        return;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    // Nettoyer la session
    session.removeAttribute("lastReservation");
    session.removeAttribute("lastVol");
    session.removeAttribute("lastClient");
    session.removeAttribute("montantTotal");
    session.removeAttribute("modePaiement");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Confirmation - Paiement effectué</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .container { max-width: 600px; margin: 2rem auto; padding: 2rem; background: white; border-radius: 15px; text-align: center; box-shadow: 0 10px 30px rgba(0,0,0,0.1); }
        .success-icon { font-size: 80px; color: #27ae60; margin-bottom: 20px; }
        .details { text-align: left; margin: 2rem 0; padding: 1rem; background: #f8f9fa; border-radius: 10px; }
        .btn { display: inline-block; padding: 0.75rem 1.5rem; margin: 0.5rem; background: #667eea; color: white; text-decoration: none; border-radius: 8px; }
        .btn-print { background: #3498db; }
        .payment-info { background: #e8f4f8; padding: 1rem; border-radius: 10px; margin-top: 1rem; }
    </style>
</head>
<body>
<div class="container">
    <div class="success-icon">✓</div>
    <h1>Paiement effectué avec succès !</h1>
    <p>Votre réservation est maintenant confirmée.</p>

    <div class="details">
        <h3>📋 Récapitulatif de la réservation</h3>
        <p><strong>Numéro de réservation :</strong> <%= reservation.getIdReservation() %></p>
        <p><strong>Passager :</strong> <%= client.getPrenoms() %> <%= client.getNom() %></p>
        <p><strong>Vol :</strong> <%= vol.getLieuDepart() %> → <%= vol.getLieuArrivee() %></p>
        <p><strong>Date de départ :</strong> <%= sdf.format(vol.getDateDepart()) %></p>
        <p><strong>Nombre de places :</strong> <%= reservation.getNombrePlaces() %></p>

        <div class="payment-info">
            <h3>💰 Détails du paiement</h3>
            <p><strong>Montant total :</strong> <%= String.format("%,.0f", montantTotal) %> FCFA</p>
            <p><strong>Mode de paiement :</strong>
                <% if ("carte".equals(modePaiement)) { %>💳 Carte bancaire
                <% } else if ("mobile_money".equals(modePaiement)) { %>📱 Mobile Money
                <% } else if ("virement".equals(modePaiement)) { %>🏦 Virement bancaire
                <% } else { %>💵 Espèces
                <% } %>
            </p>
            <p><strong>Statut :</strong> <span style="color: #27ae60;">Payé</span></p>
        </div>
    </div>

    <div>
        <button onclick="window.print()" class="btn btn-print">🖨️ Imprimer</button>
        <a href="${pageContext.request.contextPath}/reservation" class="btn">📅 Nouvelle réservation</a>
        <a href="${pageContext.request.contextPath}/dashboard.jsp" class="btn">🏠 Accueil</a>
    </div>
</div>
</body>
</html>