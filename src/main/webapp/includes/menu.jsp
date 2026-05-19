<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.projetihm.avionmanagement.model.Client" %>
<%
    Client menuClient = (Client) session.getAttribute("client");
    String currentPage = request.getRequestURI();
%>
<!DOCTYPE html>
<html>
<head>
    <style>
        .navbar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 1rem 2rem;
            display: flex;
            justify-content: space-between;
            align-items: center;
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            z-index: 1000;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }

        .navbar h1 {
            margin: 0;
            font-size: 1.5rem;
        }

        .navbar h1 a {
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

        .admin-badge {
            background: #ff9800;
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.8rem;
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

        /* Styles pour les liens de navigation */
        .nav-links {
            display: flex;
            gap: 10px;
            background: white;
            padding: 0.75rem 2rem;
            border-radius: 0;
            box-shadow: 0 2px 5px rgba(0,0,0,0.05);
            margin-top: 70px;
            flex-wrap: wrap;
        }

        .nav-link {
            padding: 8px 16px;
            background: #f0f3f8;
            border-radius: 8px;
            text-decoration: none;
            color: #333;
            font-weight: 500;
            transition: all 0.3s;
            font-size: 0.9rem;
        }

        .nav-link:hover {
            background: #667eea;
            color: white;
        }

        .nav-link.active {
            background: #667eea;
            color: white;
        }

        .container {
            max-width: 1200px;
            margin: 140px auto 2rem auto;
            padding: 0 2rem;
        }

        @media (max-width: 768px) {
            .navbar {
                flex-direction: column;
                gap: 0.5rem;
                padding: 0.75rem;
            }
            .nav-links {
                margin-top: 110px;
                flex-direction: column;
            }
            .container {
                margin-top: 200px;
            }
        }
    </style>
</head>
<body>
<nav class="navbar">
    <h1><a href="${pageContext.request.contextPath}/dashboard.jsp">✈️ BookingApp</a></h1>
    <div class="user-info">
        <span class="user-name">👤 <%= menuClient != null ? menuClient.getPrenoms() + " " + menuClient.getNom() : "Invité" %></span>
        <% if (menuClient != null && menuClient.isAdmin()) { %>
        <span class="admin-badge">Admin</span>
        <% } %>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="logout-btn">🚪 Déconnexion</a>
    </div>
</nav>

<div class="nav-links">
    <a href="${pageContext.request.contextPath}/dashboard.jsp" class="nav-link <%= currentPage.contains("dashboard") ? "active" : "" %>">📊 Dashboard</a>
    <a href="${pageContext.request.contextPath}/reservation" class="nav-link <%= currentPage.contains("reservation") && !currentPage.contains("reservations") ? "active" : "" %>">✈️ Nouvelle réservation</a>
    <a href="${pageContext.request.contextPath}/reservations" class="nav-link <%= currentPage.contains("reservations") ? "active" : "" %>">📋 Mes réservations</a>
    <% if (menuClient != null && menuClient.isAdmin()) { %>
    <a href="${pageContext.request.contextPath}/admin/vols" class="nav-link <%= currentPage.contains("admin/vols") ? "active" : "" %>">🛩️ Gérer vols</a>
    <a href="${pageContext.request.contextPath}/admin/avions" class="nav-link <%= currentPage.contains("admin/avions") ? "active" : "" %>">✈️ Gérer avions</a>
    <% } %>
</div>