package model.personnes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.hebergements.Hebergement;
import model.hebergements.Reservable;
import model.reservation.Reservation;
import model.service.CollectionHebergements;

public class Client extends Personne {

    protected String adresse;
    protected Date dateInscription = new Date();
    protected final List<Reservation> reservations = new ArrayList<>();

    // ✅ Constructeur attendu par AncienClient / NouveauClient
    protected Client(String nom, String prenom, String email, String adresse) {
        super(nom, prenom, email);
        this.adresse = adresse;
    }

    public String getAdresse() { return adresse; }
    public Date getDateInscription() { return dateInscription; }
    public List<Reservation> getReservations() { return reservations; }

    // ✅ Réduction par défaut (0%). AncienClient override.
    public double getTauxReduction() { return 0.0; }

    // ✅ Recherche dans le catalogue (avec filtres combinés)
    public List<Hebergement> rechercher(CollectionHebergements col,
                                       Double prixMax, Integer capaciteMin,
                                       String type, Double noteMin,
                                       Date debut, Date fin) {
        if (col == null) return new ArrayList<>();
        return col.rechercher(debut, fin, capaciteMin, prixMax, type, noteMin);
    }

    /**
     * ✅ Réserver un hébergement
     * Important : on ne fait PAS une "double réservation".
     * On passe uniquement par creerReservation(...) qui consomme la période dispo.
     */
    public Reservation reserver(Reservable r, Date debut, Date fin, int nbPersonnes) {
        if (r == null) return null;

        // On veut récupérer un objet Reservation -> il faut un Hebergement
        if (r instanceof Hebergement) {
            Hebergement h = (Hebergement) r;

            Reservation created = h.creerReservation(this, debut, fin, nbPersonnes);
            if (created != null) {
                created.appliquerReduction(getTauxReduction());
                reservations.add(created);
            }
            return created;
        }

        // Fallback si ce n'est pas un Hebergement (rare)
        r.reserver(this, debut, fin);
        return null;
    }

    /**
     * ✅ Annuler une réservation (par objet)
     */
    public boolean annuler(Reservation reservation) {
        if (reservation == null) return false;
        if (!reservations.contains(reservation)) return false;

        reservation.annuler(); // remet les dispos côté hébergement
        return true;
    }

    /**
     * ✅ Afficher facture (utilisé par ton menu)
     */
    public void afficherFacture(Reservation r) {
        if (r == null) return;

        System.out.println("----- FACTURE -----");
        System.out.println("Client: " + this);
        System.out.println("Hébergement: " + r.getHebergement().getNom() + " (" + r.getHebergement().getType() + ")");
        System.out.println("Période: " + r.getArrivee() + " -> " + r.getDepart());
        System.out.println("Nombre de personnes: " + r.getNbPersonnes());
        System.out.println("Réduction: " + (int)(r.getTauxReduction() * 100) + "%");
        System.out.println("Prix total: " + String.format("%.2f", r.getPrixTotal()) + "€");
        System.out.println("-------------------");
    }

    @Override
    public String getTypePersonne() {
        return "CLIENT";
    }
}
