package com.projetihm.avionmanagement.model;

import java.sql.Timestamp;

public class Avion {
    private int idAvion;
    private String modele;
    private String classe;
    private int nombrePlaces;
    private double frais;
    private Timestamp dateAjout;  // Nouveau champ

    // Constructeurs
    public Avion() {}

    public Avion(int idAvion, String modele, String classe, int nombrePlaces, double frais) {
        this.idAvion = idAvion;
        this.modele = modele;
        this.classe = classe;
        this.nombrePlaces = nombrePlaces;
        this.frais = frais;
    }

    // Getters et Setters
    public int getIdAvion() { return idAvion; }
    public void setIdAvion(int idAvion) { this.idAvion = idAvion; }

    public String getModele() { return modele; }
    public void setModele(String modele) { this.modele = modele; }

    public String getClasse() { return classe; }
    public void setClasse(String classe) { this.classe = classe; }

    public int getNombrePlaces() { return nombrePlaces; }
    public void setNombrePlaces(int nombrePlaces) { this.nombrePlaces = nombrePlaces; }

    public double getFrais() { return frais; }
    public void setFrais(double frais) { this.frais = frais; }

    public Timestamp getDateAjout() { return dateAjout; }
    public void setDateAjout(Timestamp dateAjout) { this.dateAjout = dateAjout; }

    @Override
    public String toString() {
        return modele + " (" + classe + ") - " + nombrePlaces + " places";
    }
}