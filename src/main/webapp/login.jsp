<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>BookingApp - Connexion & Inscription</title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="container">
  <div class="tabs">
    <button class="tab active" onclick="showTab('login')">Connexion</button>
    <button class="tab" onclick="showTab('register')">Inscription</button>
  </div>

  <div class="form-container">
    <!-- Alert Messages -->
    <div id="alert" class="alert"></div>

    <!-- Login Form -->
    <div id="loginForm" class="form active">
      <h2>Connexion</h2>
      <form action="<%= request.getContextPath() %>/LoginServlet" method="POST">
        <div class="input-group">
          <label>Email ou Login</label>
          <input type="text" name="username" id="login_username" required>
          <span class="error-message" id="login_username_error"></span>
        </div>

        <div class="input-group">
          <label>Mot de passe</label>
          <input type="password" name="password" id="login_password" required>
          <span class="error-message" id="login_password_error"></span>
        </div>

        <button type="submit" class="btn-submit">Se connecter</button>

        <div class="info-text">
          Pas encore de compte ? <a href="#" onclick="showTab('register')">S'inscrire</a>
        </div>
      </form>
    </div>

    <!-- Register Form -->
    <div id="registerForm" class="form">
      <h2>Inscription</h2>
      <form action="<%= request.getContextPath() %>/RegisterServlet" method="POST">
        <div class="input-group">
          <label>Nom *</label>
          <input type="text" name="nom" id="reg_nom" required>
          <span class="error-message" id="reg_nom_error"></span>
        </div>

        <div class="input-group">
          <label>Prénoms *</label>
          <input type="text" name="prenoms" id="reg_prenoms" required>
          <span class="error-message" id="reg_prenoms_error"></span>
        </div>

        <div class="input-group">
          <label>Email *</label>
          <input type="email" name="email" id="reg_email" required>
          <span class="error-message" id="reg_email_error"></span>
        </div>

        <div class="input-group">
          <label>Téléphone *</label>
          <input type="tel" name="contact" id="reg_contact" required>
          <span class="error-message" id="reg_contact_error"></span>
        </div>

        <div class="input-group">
          <label>Login *</label>
          <input type="text" name="login" id="reg_login" required>
          <span class="error-message" id="reg_login_error"></span>
        </div>

        <div class="input-group">
          <label>Mot de passe *</label>
          <input type="password" name="password" id="reg_password" required>
          <div class="password-strength">
            <div class="password-strength-bar" id="passwordStrength"></div>
          </div>
          <span class="error-message" id="reg_password_error"></span>
        </div>

        <div class="input-group">
          <label>Confirmer mot de passe *</label>
          <input type="password" name="confirm_password" id="reg_confirm_password" required>
          <span class="error-message" id="reg_confirm_error"></span>
        </div>

        <button type="submit" class="btn-submit">S'inscrire</button>

        <div class="info-text">
          Déjà un compte ? <a href="#" onclick="showTab('login')">Se connecter</a>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="js/validation.js"></script>
</body>
</html>