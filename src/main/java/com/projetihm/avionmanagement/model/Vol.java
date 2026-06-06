package com.projetihm.avionmanagement.model;

import java.sql.Timestamp;

public class Vol {
    private int idVol;
    private int idAvion;
    private String modeleAvion;  // Pour affichage
    private Timestamp dateDepart;
    private Timestamp dateArrivee;
    private String lieuDepart;
    private String lieuArrivee;
    private String statutVol;
    private double frais;
    private int placesDisponibles;// ← AJOUTER CETTE LIGNE

    // Constructeurs
    public Vol() {}

    public Vol(int idVol, int idAvion, Timestamp dateDepart, Timestamp dateArrivee,
               String lieuDepart, String lieuArrivee, String statutVol) {
        this.idVol = idVol;
        this.idAvion = idAvion;
        this.dateDepart = dateDepart;
        this.dateArrivee = dateArrivee;
        this.lieuDepart = lieuDepart;
        this.lieuArrivee = lieuArrivee;
        this.statutVol = statutVol;
    }

    // Getters et Setters
    public int getIdVol() { return idVol; }
    public void setIdVol(int idVol) { this.idVol = idVol; }

    public int getIdAvion() { return idAvion; }
    public void setIdAvion(int idAvion) { this.idAvion = idAvion; }

    public String getModeleAvion() { return modeleAvion; }
    public void setModeleAvion(String modeleAvion) { this.modeleAvion = modeleAvion; }

    public Timestamp getDateDepart() { return dateDepart; }
    public void setDateDepart(Timestamp dateDepart) { this.dateDepart = dateDepart; }

    public Timestamp getDateArrivee() { return dateArrivee; }
    public void setDateArrivee(Timestamp dateArrivee) { this.dateArrivee = dateArrivee; }

    public String getLieuDepart() { return lieuDepart; }
    public void setLieuDepart(String lieuDepart) { this.lieuDepart = lieuDepart; }

    public String getLieuArrivee() { return lieuArrivee; }
    public void setLieuArrivee(String lieuArrivee) { this.lieuArrivee = lieuArrivee; }

    public String getStatutVol() { return statutVol; }
    public void setStatutVol(String statutVol) { this.statutVol = statutVol; }

    // ← AJOUTER CES DEUX MÉTHODES
    public double getFrais() { return frais; }
    public void setFrais(double frais) { this.frais = frais; }

    public int getPlacesDisponibles() { return placesDisponibles; }
    public void setPlacesDisponibles(int placesDisponibles) { this.placesDisponibles = placesDisponibles; }
}