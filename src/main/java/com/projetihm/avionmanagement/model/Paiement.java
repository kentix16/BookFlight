package com.projetihm.avionmanagement.model;

import java.sql.Timestamp;

public class Paiement {
    private int idPaiement;
    private int idReservation;
    private double montant;
    private Timestamp datePaiement;
    private String modePaiement;
    private String reference;
    private String statutPaiement;

    public Paiement() {}

    // Getters
    public int getIdPaiement() { return idPaiement; }
    public int getIdReservation() { return idReservation; }
    public double getMontant() { return montant; }
    public Timestamp getDatePaiement() { return datePaiement; }
    public String getModePaiement() { return modePaiement; }
    public String getReference() { return reference; }
    public String getStatutPaiement() { return statutPaiement; }

    // Setters
    public void setIdPaiement(int idPaiement) { this.idPaiement = idPaiement; }
    public void setIdReservation(int idReservation) { this.idReservation = idReservation; }
    public void setMontant(double montant) { this.montant = montant; }
    public void setDatePaiement(Timestamp datePaiement) { this.datePaiement = datePaiement; }
    public void setModePaiement(String modePaiement) { this.modePaiement = modePaiement; }
    public void setReference(String reference) { this.reference = reference; }
    public void setStatutPaiement(String statutPaiement) { this.statutPaiement = statutPaiement; }
}