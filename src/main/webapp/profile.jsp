<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    Client client = (Client) request.getAttribute("client");
    if (client == null) {
        client = (Client) session.getAttribute("client");
    }
    if (client == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    String toastMessage = (String) session.getAttribute("toastMessage");
    String toastType = (String) session.getAttribute("toastType");

    // Nettoyer les messages après affichage
    session.removeAttribute("toastMessage");
    session.removeAttribute("toastType");
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mon profil - BookingApp</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            background: linear-gradient(135deg, #5a67d8 0%, #4c51bf 100%);
        }

        /* CONTENEUR PRINCIPAL */
        .profile-container {
            max-width: 1000px;
            margin: 100px auto 2rem auto;
            padding: 0 2rem;
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
            content: "👤";
            font-size: 2rem;
        }

        /* GRILLE 2 COLONNES */
        .profile-grid {
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
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 30px 50px rgba(0, 0, 0, 0.2);
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

        .form-group input {
            width: 100%;
            padding: 0.75rem 1rem;
            border: 2px solid #e1e5e9;
            border-radius: 12px;
            font-size: 1rem;
            transition: all 0.3s;
            font-family: inherit;
        }

        .form-group input:focus {
            outline: none;
            border-color: #667eea;
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.2);
        }

        .form-group input.readonly {
            background: #f0f3f8;
            color: #666;
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
        }

        .btn-success:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(39, 174, 96, 0.4);
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
        }

        /* SECTION INFOS */
        .info-row {
            display: flex;
            padding: 0.75rem 0;
            border-bottom: 1px solid #e1e5e9;
        }

        .info-label {
            font-weight: 600;
            width: 120px;
            color: #555;
        }

        .info-value {
            flex: 1;
            color: #333;
        }

        .admin-badge {
            display: inline-block;
            background: #ff9800;
            color: white;
            padding: 0.2rem 0.6rem;
            border-radius: 20px;
            font-size: 0.7rem;
            margin-left: 0.5rem;
        }

        /* ALERTES */
        .alert {
            padding: 1rem;
            border-radius: 12px;
            margin-bottom: 1.5rem;
            font-size: 0.95rem;
        }

        .alert-success {
            background: rgba(212, 237, 218, 0.95);
            color: #27ae60;
            border-left: 4px solid #27ae60;
        }

        .alert-error {
            background: rgba(254, 226, 226, 0.95);
            color: #c33;
            border-left: 4px solid #e74c3c;
        }

        .flex {
            display: flex;
            gap: 1rem;
            margin-top: 1rem;
        }

        .flex .btn {
            flex: 1;
        }

        .mt-2 {
            margin-top: 1rem;
        }

        .text-center {
            text-align: center;
        }

        hr {
            margin: 1rem 0;
            border: none;
            border-top: 1px solid #e1e5e9;
        }

        @media (max-width: 768px) {
            .profile-grid {
                grid-template-columns: 1fr;
                gap: 1.5rem;
            }
            .profile-container {
                margin-top: 80px;
                padding: 0 1rem;
            }
            .page-title {
                font-size: 1.5rem;
            }
        }
    </style>
</head>
<body>

<jsp:include page="/includes/menu.jsp" />

<div class="profile-container">
    <div class="page-title">Mon profil</div>

    <% if (toastMessage != null && !toastMessage.isEmpty()) { %>
    <div class="alert alert-<%= toastType %>"><%= toastMessage %></div>
    <% } %>

    <div class="profile-grid">
        <!-- COLONNE GAUCHE : INFORMATIONS -->
        <div class="card">
            <div class="card-header">
                <h3>📋 Informations personnelles</h3>
            </div>
            <div class="card-body">
                <div class="info-row">
                    <div class="info-label">ID client :</div>
                    <div class="info-value"><%= client.getIdClient() %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Nom :</div>
                    <div class="info-value"><%= client.getNom() %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Prénoms :</div>
                    <div class="info-value"><%= client.getPrenoms() %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Email :</div>
                    <div class="info-value"><%= client.getEmail() %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Téléphone :</div>
                    <div class="info-value"><%= client.getContact() %></div>
                </div>
                <div class="info-row">
                    <div class="info-label">Compte créé le :</div>
                    <div class="info-value"><%= client.getDateCreation() != null ? sdf.format(client.getDateCreation()) : "N/A" %></div>
                </div>
                <% if (client.getLogin() != null) { %>
                <div class="info-row">
                    <div class="info-label">Login :</div>
                    <div class="info-value">
                        <%= client.getLogin() %>
                        <% if (client.isAdmin()) { %>
                        <span class="admin-badge">Administrateur</span>
                        <% } %>
                    </div>
                </div>
                <% } %>
            </div>
        </div>

        <!-- COLONNE DROITE : MODIFICATION -->
        <div class="card">
            <div class="card-header">
                <h3>✏️ Modifier mon profil</h3>
            </div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/profile">
                    <input type="hidden" name="action" value="update">

                    <div class="form-group">
                        <label>Nom *</label>
                        <input type="text" name="nom" value="<%= client.getNom() %>" required>
                    </div>

                    <div class="form-group">
                        <label>Prénoms *</label>
                        <input type="text" name="prenoms" value="<%= client.getPrenoms() %>" required>
                    </div>

                    <div class="form-group">
                        <label>Email *</label>
                        <input type="email" name="email" value="<%= client.getEmail() %>" required>
                    </div>

                    <div class="form-group">
                        <label>Téléphone *</label>
                        <input type="tel" name="contact" value="<%= client.getContact() %>" required>
                    </div>

                    <button type="submit" class="btn btn-primary">💾 Enregistrer les modifications</button>
                </form>
            </div>
        </div>

        <!-- CARTE CHANGEMENT DE MOT DE PASSE (si client en ligne) -->
        <% if (client.getLogin() != null) { %>
        <div class="card">
            <div class="card-header">
                <h3>🔐 Changer mon mot de passe</h3>
            </div>
            <div class="card-body">
                <form method="post" action="${pageContext.request.contextPath}/profile">
                    <input type="hidden" name="action" value="changePassword">

                    <div class="form-group">
                        <label>Mot de passe actuel *</label>
                        <input type="password" name="ancien_mot_de_passe" required>
                    </div>

                    <div class="form-group">
                        <label>Nouveau mot de passe *</label>
                        <input type="password" name="nouveau_mot_de_passe" required minlength="4">
                        <small style="color: #666;">Minimum 4 caractères</small>
                    </div>

                    <div class="form-group">
                        <label>Confirmer le nouveau mot de passe *</label>
                        <input type="password" name="confirmation_mot_de_passe" required>
                    </div>

                    <button type="submit" class="btn btn-primary">🔑 Changer le mot de passe</button>
                </form>
            </div>
        </div>
        <% } %>
    </div>
</div>

</body>
</html>