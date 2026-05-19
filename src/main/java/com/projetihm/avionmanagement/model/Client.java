package com.projetihm.avionmanagement.model;  // ← MODIFIÉ

import java.io.Serializable;
import java.sql.Timestamp;

public class Client implements Serializable {

    private int idClient;
    private String nom;
    private String prenoms;
    private String contact;
    private String email;
    private boolean estClientEnLigne;
    private Timestamp dateCreation;
    private String login;
    private String motDePasse;
    private boolean isAdmin;
    private Timestamp dernierLogin;

    // Constructeurs
    public Client() {}

    public Client(int idClient, String nom, String prenoms, String contact,
                  String email, String login, boolean isAdmin) {
        this.idClient = idClient;
        this.nom = nom;
        this.prenoms = prenoms;
        this.contact = contact;
        this.email = email;
        this.login = login;
        this.isAdmin = isAdmin;
    }

    // Getters et Setters
    public int getIdClient() { return idClient; }
    public void setIdClient(int idClient) { this.idClient = idClient; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenoms() { return prenoms; }
    public void setPrenoms(String prenoms) { this.prenoms = prenoms; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isEstClientEnLigne() { return estClientEnLigne; }
    public void setEstClientEnLigne(boolean estClientEnLigne) { this.estClientEnLigne = estClientEnLigne; }

    public Timestamp getDateCreation() { return dateCreation; }
    public void setDateCreation(Timestamp dateCreation) { this.dateCreation = dateCreation; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public boolean isAdmin() { return isAdmin; }
    public void setAdmin(boolean admin) { isAdmin = admin; }

    public Timestamp getDernierLogin() { return dernierLogin; }
    public void setDernierLogin(Timestamp dernierLogin) { this.dernierLogin = dernierLogin; }

    @Override
    public String toString() {
        return nom + " " + prenoms + " (" + login + ")";
    }
}