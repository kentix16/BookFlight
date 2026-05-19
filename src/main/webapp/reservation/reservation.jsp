<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Vol" %>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    List<Vol> vols = (List<Vol>) request.getAttribute("vols");
    List<Client> searchResults = (List<Client>) request.getAttribute("searchResults");
    Client selectedClient = (Client) session.getAttribute("selectedClient");
    String searchTerm = (String) request.getAttribute("searchTerm");
    String errorMessage = (String) request.getAttribute("errorMessage");
    String successMessage = (String) request.getAttribute("successMessage");

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Réservation de vol</title>
    <style>
        /* RESET & BASE */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            position: relative;
        }

        /* BACKGROUND AVEC IMAGE D'AVION ET SUPERPOSITION */
        body::before {
            content: "";
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-image: url('https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=1600');
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            background-attachment: fixed;
            filter: brightness(0.7);
            z-index: -2;
        }

        body::after {
            content: "";
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.85) 0%, rgba(118, 75, 162, 0.85) 100%);
            z-index: -1;
        }

        /* CONTENEUR PRINCIPAL */
        .reservation-container {
            max-width: 1400px;
            margin: 100px auto 2rem auto;
            padding: 0 2rem;
            animation: fadeInUp 0.6s ease-out;
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
        .page-title {
            color: white;
            margin-bottom: 2rem;
            font-size: 2rem;
            font-weight: 700;
            display: flex;
            align-items: center;
            gap: 0.75rem;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.2);
        }

        .page-title:before {
            content: "📅";
            font-size: 2rem;
            filter: drop-shadow(2px 2px 4px rgba(0,0,0,0.2));
        }

        /* GRILLE 2 COLONNES */
        .booking-grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 2rem;
        }

        /* CARTES */
        .card {
            background: rgba(255, 255, 255, 0.95);
            backdrop-filter: blur(10px);
            border-radius: 24px;
            box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
            overflow: hidden;
            transition: transform 0.3s, box-shadow 0.3s;
            border: 1px solid rgba(255, 255, 255, 0.3);
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 30px 50px rgba(0, 0, 0, 0.2);
            background: rgba(255, 255, 255, 0.98);
        }

        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1.25rem 1.5rem;
        }

        .card-header h3 {
            margin: 0;
            font-size: 1.2rem;
            font-weight: 600;
            display: flex;
            align-items: center;
            gap: 0.5rem;
        }

        .card-body {
            padding: 1.5rem;
        }

        /* FORMULAIRES */
        .form-group {
            margin-bottom: 1.25rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #333;
            font-size: 0.9rem;
        }

        .form-group input,
        .form-group select {
            width: 100%;
            padding: 0.75rem 1rem;
            border: 2px solid #e1e5e9;
            border-radius: 12px;
            font-size: 1rem;
            transition: all 0.3s;
            font-family: inherit;
            background: white;
        }

        .form-group input:focus,
        .form-group select:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
        }

        /* BOUTONS */
        .btn {
            padding: 0.75rem 1.5rem;
            border: none;
            border-radius: 12px;
            font-weight: 600;
            font-size: 0.95rem;
            cursor: pointer;
            transition: all 0.3s;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 0.5rem;
            font-family: inherit;
        }

        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            width: 100%;
        }

        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }

        .btn-success {
            background: linear-gradient(135deg, #27ae60 0%, #219a52 100%);
            color: white;
            width: 100%;
        }

        .btn-success:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(39, 174, 96, 0.4);
        }

        .btn-danger {
            background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);
            color: white;
        }

        .btn-danger:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(231, 76, 60, 0.4);
        }

        .btn-secondary {
            background: #95a5a6;
            color: white;
        }

        .btn-secondary:hover {
            background: #7f8c8d;
        }

        .btn-outline {
            background: transparent;
            border: 2px solid #667eea;
            color: #667eea;
        }

        .btn-outline:hover {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-color: transparent;
        }

        /* CARTE CLIENT SÉLECTIONNÉ */
        .client-card {
            background: linear-gradient(135deg, #e8f4f8 0%, #d4eaf3 100%);
            padding: 1.25rem;
            border-radius: 16px;
            margin-bottom: 1rem;
            border: 2px solid #667eea;
        }

        .client-card .client-name {
            font-size: 1.1rem;
            font-weight: bold;
            color: #333;
            margin-bottom: 0.5rem;
        }

        .client-card .client-detail {
            color: #555;
            font-size: 0.9rem;
            margin: 0.25rem 0;
        }

        /* RÉSULTATS DE RECHERCHE */
        .search-results {
            max-height: 300px;
            overflow-y: auto;
            margin-top: 1rem;
            border: 1px solid #e1e5e9;
            border-radius: 12px;
            background: white;
        }

        .result-item {
            padding: 0.875rem 1rem;
            border-bottom: 1px solid #e1e5e9;
            cursor: pointer;
            transition: background 0.2s;
        }

        .result-item:last-child {
            border-bottom: none;
        }

        .result-item:hover {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
        }

        .result-item strong {
            display: block;
            margin-bottom: 0.25rem;
            color: #333;
        }

        .result-item small {
            color: #888;
            font-size: 0.8rem;
        }

        /* CARTES VOL */
        .flight-card {
            border: 2px solid #e1e5e9;
            border-radius: 14px;
            padding: 1rem;
            margin-bottom: 1rem;
            cursor: pointer;
            transition: all 0.3s;
            background: white;
        }

        .flight-card:hover {
            border-color: #667eea;
            background: linear-gradient(135deg, #f8f9ff 0%, #f0f2ff 100%);
            transform: translateX(5px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.2);
        }

        .flight-card.selected {
            border-color: #27ae60;
            background: linear-gradient(135deg, #f0fff4 0%, #e8f8f0 100%);
        }

        .flight-route {
            font-weight: bold;
            font-size: 1.1rem;
            margin-bottom: 0.5rem;
            color: #333;
        }

        .flight-details {
            display: flex;
            justify-content: space-between;
            color: #666;
            font-size: 0.85rem;
            flex-wrap: wrap;
            gap: 0.5rem;
        }

        .flight-price {
            color: #27ae60;
            font-weight: bold;
        }

        /* RÉSUMÉ DE RÉSERVATION */
        .booking-summary {
            background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
            padding: 1rem;
            border-radius: 12px;
            margin-top: 1rem;
            border-left: 4px solid #667eea;
        }

        .booking-summary p {
            margin: 0.5rem 0;
            font-size: 0.95rem;
        }

        .booking-summary strong {
            color: #333;
        }

        .summary-total {
            font-size: 1.1rem;
            font-weight: bold;
            color: #27ae60;
            margin-top: 0.5rem;
            padding-top: 0.5rem;
            border-top: 1px dashed #ddd;
        }

        /* ALERTES */
        .alert {
            padding: 1rem;
            border-radius: 12px;
            margin-bottom: 1.5rem;
            font-size: 0.95rem;
            backdrop-filter: blur(10px);
        }

        .alert-error {
            background: rgba(254, 226, 226, 0.95);
            color: #c33;
            border-left: 4px solid #e74c3c;
        }

        .alert-success {
            background: rgba(212, 237, 218, 0.95);
            color: #27ae60;
            border-left: 4px solid #27ae60;
        }

        /* FORMULAIRE NOUVEAU CLIENT */
        #newClientForm {
            margin-top: 1rem;
            padding-top: 1rem;
            border-top: 1px solid #e1e5e9;
        }

        #newClientForm h4 {
            margin-bottom: 1rem;
            color: #333;
        }

        .flex {
            display: flex;
            gap: 1rem;
        }

        .flex .btn {
            flex: 1;
        }

        .mt-2 {
            margin-top: 1rem;
        }

        /* RESPONSIVE */
        @media (max-width: 968px) {
            .booking-grid {
                grid-template-columns: 1fr;
                gap: 1.5rem;
            }

            .reservation-container {
                margin-top: 80px;
                padding: 0 1rem;
            }

            .page-title {
                font-size: 1.5rem;
            }
        }

        @media (max-width: 480px) {
            .card-body {
                padding: 1rem;
            }

            .btn {
                padding: 0.6rem 1rem;
            }

            .flight-details {
                flex-direction: column;
                gap: 0.25rem;
            }

            .page-title {
                font-size: 1.3rem;
            }
        }

        /* Animation de chargement des cartes */
        .card {
            animation: fadeInScale 0.5s ease-out backwards;
        }

        .card:first-child {
            animation-delay: 0.1s;
        }

        .card:last-child {
            animation-delay: 0.2s;
        }

        @keyframes fadeInScale {
            from {
                opacity: 0;
                transform: scale(0.95);
            }
            to {
                opacity: 1;
                transform: scale(1);
            }
        }
        /* BACKGROUND UNI COULEUR #5a67d8 */
        body {
            background: #5a67d8;
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        /* Version avec dégradé de la même couleur */
        body {
            background: linear-gradient(135deg, #5a67d8 0%, #4c51bf 100%);
            min-height: 100vh;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }
    </style>
</head>
<body>

<jsp:include page="/includes/menu.jsp" />

<div class="reservation-container">
    <div class="page-title">Nouvelle réservation</div>

    <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
    <div class="alert alert-error"><%= errorMessage %></div>
    <% } %>
    <% if (successMessage != null && !successMessage.isEmpty()) { %>
    <div class="alert alert-success"><%= successMessage %></div>
    <% } %>

    <div class="booking-grid">
        <!-- COLONNE GAUCHE : GESTION CLIENT -->
        <div>
            <div class="card">
                <div class="card-header">
                    <h3>👤 Client</h3>
                </div>
                <div class="card-body">
                    <% if (selectedClient == null) { %>
                    <!-- Formulaire de recherche -->
                    <form method="post" action="${pageContext.request.contextPath}/reservation">
                        <input type="hidden" name="action" value="searchClient">
                        <div class="form-group">
                            <label>🔍 Rechercher un client</label>
                            <input type="text" name="searchTerm" placeholder="Email, nom ou téléphone..."
                                   value="<%= searchTerm != null ? searchTerm : "" %>" required>
                        </div>
                        <button type="submit" class="btn btn-primary">Rechercher</button>
                    </form>

                    <!-- Résultats de recherche -->
                    <% if (searchResults != null && !searchResults.isEmpty()) { %>
                    <div class="search-results">
                        <div style="padding: 0.75rem; background: #f8f9fa; font-weight: bold; border-bottom: 1px solid #e1e5e9;">
                            Résultats trouvés :
                        </div>
                        <% for (Client c : searchResults) { %>
                        <div class="result-item" onclick="selectClient(<%= c.getIdClient() %>)">
                            <strong><%= c.getPrenoms() %> <%= c.getNom() %></strong>
                            <small>📧 <%= c.getEmail() %> | 📞 <%= c.getContact() %></small>
                        </div>
                        <% } %>
                    </div>
                    <form id="selectClientForm" method="post" action="${pageContext.request.contextPath}/reservation">
                        <input type="hidden" name="action" value="selectClient">
                        <input type="hidden" name="clientId" id="selectedClientId">
                    </form>
                    <% } else if (searchTerm != null && !searchTerm.isEmpty()) { %>
                    <div class="alert alert-error mt-2">Aucun client trouvé. Vous pouvez en créer un nouveau.</div>
                    <% } %>

                    <!-- Bouton nouveau client -->
                    <div class="mt-2">
                        <button class="btn btn-outline" onclick="showNewClientForm()" style="width: 100%;">➕ Nouveau client</button>
                    </div>

                    <!-- Formulaire nouveau client -->
                    <div id="newClientForm" style="display: none;">
                        <h4>📝 Nouveau client</h4>
                        <form method="post" action="${pageContext.request.contextPath}/reservation">
                            <input type="hidden" name="action" value="createClient">
                            <div class="form-group">
                                <input type="text" name="nom" placeholder="Nom *" required>
                            </div>
                            <div class="form-group">
                                <input type="text" name="prenoms" placeholder="Prénoms *" required>
                            </div>
                            <div class="form-group">
                                <input type="email" name="email" placeholder="Email *" required>
                            </div>
                            <div class="form-group">
                                <input type="tel" name="contact" placeholder="Téléphone *" required>
                            </div>
                            <div class="flex">
                                <button type="submit" class="btn btn-success">Créer</button>
                                <button type="button" class="btn btn-secondary" onclick="hideNewClientForm()">Annuler</button>
                            </div>
                        </form>
                    </div>

                    <% } else { %>
                    <!-- Client sélectionné -->
                    <div class="client-card">
                        <p class="client-name">👤 <%= selectedClient.getPrenoms() %> <%= selectedClient.getNom() %></p>
                        <p class="client-detail">📧 <%= selectedClient.getEmail() %></p>
                        <p class="client-detail">📞 <%= selectedClient.getContact() %></p>
                    </div>
                    <form method="get" action="${pageContext.request.contextPath}/reservation">
                        <button type="submit" class="btn btn-danger" style="width: 100%;">🔄 Changer de client</button>
                    </form>
                    <% } %>
                </div>
            </div>
        </div>

        <!-- COLONNE DROITE : SÉLECTION DU VOL -->
        <div>
            <div class="card">
                <div class="card-header">
                    <h3>✈️ Sélection du vol</h3>
                </div>
                <div class="card-body">
                    <% if (selectedClient == null) { %>
                    <div class="alert alert-error">Veuillez d'abord sélectionner ou créer un client</div>
                    <% } else if (vols == null || vols.isEmpty()) { %>
                    <div class="alert alert-error">Aucun vol disponible pour le moment</div>
                    <a href="${pageContext.request.contextPath}/admin/vols" class="btn btn-primary" style="display: block; text-align: center; text-decoration: none;">➕ Ajouter un vol</a>
                    <% } else { %>
                    <form id="bookingForm" method="post" action="${pageContext.request.contextPath}/reservation">
                        <input type="hidden" name="action" value="book">
                        <input type="hidden" name="idVol" id="selectedVolId">

                        <div class="form-group">
                            <label>🎟️ Nombre de places</label>
                            <input type="number" name="nombrePlaces" id="nombrePlaces" min="1" max="10" value="1" required>
                        </div>

                        <div class="form-group">
                            <label>💳 Mode de paiement</label>
                            <select name="modePaiement" id="modePaiement" required>
                                <option value="">Sélectionnez un mode de paiement</option>
                                <option value="carte">💳 Carte bancaire</option>
                                <option value="mobile_money">📱 Mobile Money (Orange Money, Wave)</option>
                                <option value="virement">🏦 Virement bancaire</option>
                                <option value="especes">💵 Espèces (en agence)</option>
                            </select>
                        </div>

                        <div id="volsList">
                            <% for (Vol vol : vols) { %>
                            <div class="flight-card" onclick="selectVol(<%= vol.getIdVol() %>, '<%= vol.getLieuDepart() %>', '<%= vol.getLieuArrivee() %>', <%= vol.getFrais() %>)">
                                <div class="flight-route">
                                    ✈️ <%= vol.getLieuDepart() %> → <%= vol.getLieuArrivee() %>
                                </div>
                                <div class="flight-details">
                                    <span>🛩️ <%= vol.getModeleAvion() %></span>
                                    <span>📅 <%= sdf.format(vol.getDateDepart()) %></span>
                                    <span class="flight-price">💰 <%= String.format("%,.0f", vol.getFrais()) %> FCFA/place</span>
                                </div>
                            </div>
                            <% } %>
                        </div>

                        <!-- Résumé de la réservation -->
                        <div class="booking-summary" id="bookingSummary">
                            <p style="color: #888;">Sélectionnez un vol ci-dessus</p>
                        </div>

                        <button type="submit" class="btn btn-success" id="bookButton" disabled>✅ Confirmer la réservation</button>
                    </form>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    let selectedVolId = null;
    let selectedVolPrice = 0;
    let selectedVolDepart = '';
    let selectedVolArrivee = '';

    function selectClient(clientId) {
        document.getElementById('selectedClientId').value = clientId;
        document.getElementById('selectClientForm').submit();
    }

    function selectVol(id, depart, arrivee, price) {
        // Retirer la sélection précédente
        document.querySelectorAll('.flight-card').forEach(card => {
            card.classList.remove('selected');
        });

        // Sélectionner le nouveau vol
        event.currentTarget.classList.add('selected');
        selectedVolId = id;
        selectedVolPrice = price;
        selectedVolDepart = depart;
        selectedVolArrivee = arrivee;

        document.getElementById('selectedVolId').value = id;

        // Mettre à jour le résumé
        updateSummary();
        checkFormComplete();
    }

    function updateSummary() {
        const nombrePlaces = document.getElementById('nombrePlaces').value;
        const modePaiementSelect = document.getElementById('modePaiement');
        const modeSelected = modePaiementSelect.options[modePaiementSelect.selectedIndex]?.text;
        const summaryDiv = document.getElementById('bookingSummary');

        if (selectedVolId && nombrePlaces && nombrePlaces > 0) {
            const total = selectedVolPrice * nombrePlaces;
            let html = `
                    <p><strong>✈️ Vol sélectionné :</strong> ${selectedVolDepart} → ${selectedVolArrivee}</p>
                    <p><strong>🎟️ Nombre de places :</strong> ${nombrePlaces}</p>
                    <p><strong>💰 Prix unitaire :</strong> ${selectedVolPrice.toLocaleString()} FCFA</p>
                    <div class="summary-total">💵 Montant total : ${total.toLocaleString()} FCFA</div>
                `;
            if (modeSelected && modeSelected !== 'Sélectionnez un mode de paiement') {
                html += `<p><strong>💳 Paiement :</strong> ${modeSelected}</p>`;
            }
            summaryDiv.innerHTML = html;
        } else {
            summaryDiv.innerHTML = '<p style="color: #888;">Sélectionnez un vol ci-dessus</p>';
        }
    }

    function checkFormComplete() {
        const bookButton = document.getElementById('bookButton');
        const nombrePlaces = document.getElementById('nombrePlaces').value;
        const modePaiement = document.getElementById('modePaiement').value;

        if (selectedVolId && nombrePlaces > 0 && modePaiement) {
            bookButton.disabled = false;
        } else {
            bookButton.disabled = true;
        }
    }

    // Écouteurs d'événements
    document.getElementById('nombrePlaces').addEventListener('input', function() {
        updateSummary();
        checkFormComplete();
    });

    document.getElementById('modePaiement').addEventListener('change', function() {
        updateSummary();
        checkFormComplete();
    });

    function showNewClientForm() {
        document.getElementById('newClientForm').style.display = 'block';
    }

    function hideNewClientForm() {
        document.getElementById('newClientForm').style.display = 'none';
    }
</script>
</body>
</html>