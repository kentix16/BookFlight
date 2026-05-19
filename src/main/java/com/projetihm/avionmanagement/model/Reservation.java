package com.projetihm.avionmanagement.model;

import java.sql.Timestamp;

public class Reservation {
    private int idReservation;
    private int idClient;
    private String clientNom;
    private String clientPrenoms;
    private int idVol;
    private String volInfo;
    private Timestamp dateReservation;
    private int nombrePlaces;
    private String statut;
    private double montantTotal;

    public Reservation() {}

    // Getters
    public int getIdReservation() { return idReservation; }
    public int getIdClient() { return idClient; }
    public String getClientNom() { return clientNom; }
    public String getClientPrenoms() { return clientPrenoms; }
    public int getIdVol() { return idVol; }
    public String getVolInfo() { return volInfo; }
    public Timestamp getDateReservation() { return dateReservation; }
    public int getNombrePlaces() { return nombrePlaces; }
    public String getStatut() { return statut; }
    public double getMontantTotal() { return montantTotal; }

    // Setters
    public void setIdReservation(int idReservation) { this.idReservation = idReservation; }
    public void setIdClient(int idClient) { this.idClient = idClient; }
    public void setClientNom(String clientNom) { this.clientNom = clientNom; }
    public void setClientPrenoms(String clientPrenoms) { this.clientPrenoms = clientPrenoms; }
    public void setIdVol(int idVol) { this.idVol = idVol; }
    public void setVolInfo(String volInfo) { this.volInfo = volInfo; }
    public void setDateReservation(Timestamp dateReservation) { this.dateReservation = dateReservation; }
    public void setNombrePlaces(int nombrePlaces) { this.nombrePlaces = nombrePlaces; }
    public void setStatut(String statut) { this.statut = statut; }
    public void setMontantTotal(double montantTotal) { this.montantTotal = montantTotal; }
}