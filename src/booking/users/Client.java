package booking.users;

import booking.model.CollectionHebergements;
import booking.model.Hebergement;
import booking.model.Reservation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Client extends Personne {

    protected final List<Reservation> reservations = new ArrayList<>();

    protected Client(String nom, String prenom, String email) {
        super(nom, prenom, email);
    }

    @Override
    public String getTypePersonne() { return "Client"; }

    public void ajouterReservation(Reservation r) {
        reservations.add(r);
    }

    public List<Reservation> getReservations() {
        return new ArrayList<>(reservations);
    }

    // Recherche simple (exemples)
    public List<Hebergement> rechercherParPrix(CollectionHebergements col, double prixMax) {
        return col.rechercherPrixMax(prixMax);
    }

    public boolean aReduction() { return false; }
    public double getTauxReduction() { return 0.0; }

    public void afficherFacture(Reservation r) {
        System.out.println("Facture pour " + this);
        System.out.println(r);
    }

    public void reserver(Hebergement h, LocalDate debut, LocalDate fin, int nbPersonnes) {
        h.reserver(this, debut, fin, nbPersonnes);
        // si réduction, on l’applique sur la dernière réservation
        Reservation last = reservations.get(reservations.size() - 1);
        if (aReduction()) {
            last.appliquerReduction(getTauxReduction());
        }
    }
}

