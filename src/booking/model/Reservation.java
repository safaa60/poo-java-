package booking.model;

import booking.users.Client;
import java.time.LocalDate;

public class Reservation {
    private static int SEQ = 1;

    private final int id;
    private StatutReservation statut;
    private final Client client;
    private final Reservable hebergement;
    private final Periode periode;
    private double prixTotal;
    private double tauxReduction;

    public static int nextId() {
        return SEQ++;
    }

    public Reservation(int id, Client client, Reservable hebergement, LocalDate debut, LocalDate fin, double prixTotal) {
        this.id = id;
        this.client = client;
        this.hebergement = hebergement;
        this.periode = new Periode(debut, fin);
        this.prixTotal = prixTotal;
        this.tauxReduction = 0.0;
        this.statut = StatutReservation.EN_ATTENTE;
    }

    public void appliquerReduction(double pourcentage) {
        if (pourcentage < 0 || pourcentage > 1) {
            throw new IllegalArgumentException("Réduction entre 0 et 1.");
        }
        this.tauxReduction = pourcentage;
        this.prixTotal = this.prixTotal * (1 - tauxReduction);
    }

    public void confirmer() { this.statut = StatutReservation.CONFIRMEE; }
    public void annuler() { this.statut = StatutReservation.ANNULEE; }

    public int getId() { return id; }
    public StatutReservation getStatut() { return statut; }
    public Client getClient() { return client; }
    public Reservable getHebergement() { return hebergement; }
    public Periode getPeriode() { return periode; }
    public LocalDate getDebut() { return periode.getDebut(); }
    public LocalDate getFin() { return periode.getFin(); }
    public double getPrixTotal() { return prixTotal; }
    public double getTauxReduction() { return tauxReduction; }

    @Override
    public String toString() {
        return "Reservation #" + id + " [" + statut + "] " +
                getDebut() + " -> " + getFin() +
                " | prix=" + String.format("%.2f", prixTotal) + "€ | reduc=" + (int)(tauxReduction*100) + "%";
    }
}
