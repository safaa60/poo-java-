package model.reservation;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import model.hebergements.Hebergement;
import model.personnes.Client;

public class Reservation {

    private static final AtomicLong SEQ = new AtomicLong(1);

    private final long id;
    private StatutReservation statut;

    private final Client client;
    private final Hebergement hebergement;

    private final Date arrivee;
    private final Date depart;

    private final int nbPersonnes;

    private double prixTotal;
    private double tauxReduction; // ex: 0.05
    private final Date dateCreation;

    public Reservation(Client client, Hebergement hebergement, Date arrivee, Date depart, int nbPersonnes) {
        this.id = SEQ.getAndIncrement();
        this.client = client;
        this.hebergement = hebergement;
        this.arrivee = arrivee;
        this.depart = depart;
        this.nbPersonnes = nbPersonnes;
        this.dateCreation = new Date();
        this.statut = StatutReservation.CONFIRMEE;

        calculerPrixTotal();
    }

    public long getId() { return id; }
    public StatutReservation getStatut() { return statut; }
    public Client getClient() { return client; }
    public Hebergement getHebergement() { return hebergement; }
    public Date getArrivee() { return arrivee; }
    public Date getDepart() { return depart; }
    public int getNbPersonnes() { return nbPersonnes; }
    public double getPrixTotal() { return prixTotal; }
    public double getTauxReduction() { return tauxReduction; }
    public Date getDateCreation() { return dateCreation; }

    public Periode getPeriode() {
        return new Periode(arrivee, depart);
    }

    // --- Méthodes minimum PDF ---

    public void calculerPrixTotal() {
        double brut = hebergement.calculerPrix(arrivee, depart, nbPersonnes);
        this.prixTotal = brut * (1.0 - tauxReduction);
    }

    public void appliquerReduction(double taux) {
        if (taux < 0) taux = 0;
        if (taux > 0.80) taux = 0.80;
        this.tauxReduction = taux;
        calculerPrixTotal();
    }

    public boolean estEnCours() {
        if (statut != StatutReservation.CONFIRMEE) return false;
        Date now = new Date();
        // en cours si now ∈ [arrivee, depart)
        return (!now.before(arrivee)) && now.before(depart);
    }

    public void annuler() {
        if (statut == StatutReservation.ANNULEE) return;
        statut = StatutReservation.ANNULEE;
        // remet la disponibilité côté hébergement
        hebergement.remettreDispoDepuisReservation(this);
    }

    // utilisé par Hebergement (annulation via interface)
    public void annulerInterneSansDoubleMajDispo() {
        this.statut = StatutReservation.ANNULEE;
    }

    @Override
    public String toString() {
        return "Reservation#" + id +
                " statut=" + statut +
                " hebergement=" + hebergement.getNom() +
                " client=" + client.getEmail() +
                " [" + arrivee + " -> " + depart + ")" +
                " nbPers=" + nbPersonnes +
                " reduc=" + (int)(tauxReduction * 100) + "%" +
                " prix=" + String.format("%.2f", prixTotal) + "€";
    }
}
