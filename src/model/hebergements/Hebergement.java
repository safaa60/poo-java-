package model.hebergements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import model.personnes.Client;
import model.reservation.Periode;
import model.reservation.Reservation;
import model.reservation.StatutReservation;

public abstract class Hebergement implements Reservable, Comparable<Hebergement> {

    protected long id;
    protected String nom;
    protected String adressePostale;
    protected String type;
    protected int capaciteMax;
    protected double prixParNuit;
    protected String description;

    protected final List<String> equipements = new ArrayList<>();
    protected final List<Periode> periodesDisponibles = new ArrayList<>();
    protected final List<Integer> notes = new ArrayList<>();
    protected double noteMoyenne = 0.0;

    protected final List<Reservation> reservations = new ArrayList<>();

    protected Hebergement(long id, String nom, String adressePostale, String type,
                          int capaciteMax, double prixParNuit, String description) {
        this.id = id;
        this.nom = nom;
        this.adressePostale = adressePostale;
        this.type = type;
        this.capaciteMax = capaciteMax;
        this.prixParNuit = prixParNuit;
        this.description = description;
    }

    // ===== Getters =====
    @Override public long getId() { return id; }
    public String getNom() { return nom; }
    public String getAdressePostale() { return adressePostale; }
    @Override public String getType() { return type; }
    @Override public int getCapaciteMax() { return capaciteMax; }
    @Override public double getPrixParNuit() { return prixParNuit; }
    @Override public double getNoteMoyenne() { return noteMoyenne; }
    public String getDescription() { return description; }

    public void setPrixParNuit(double prixParNuit) { this.prixParNuit = prixParNuit; }

    public List<Periode> getPeriodesDisponibles() {
        return Collections.unmodifiableList(periodesDisponibles);
    }

    public List<Reservation> getReservationsHebergement() {
        return Collections.unmodifiableList(reservations);
    }

    // ===== Méthodes demandées =====

    @Override
    public boolean estDisponible(Date debut, Date fin) {
        Periode demande = new Periode(debut, fin);

        // 1) Inclus dans une période disponible
        boolean inclus = periodesDisponibles.stream().anyMatch(p -> p.couvre(demande));
        if (!inclus) return false;

        // 2) Pas de chevauchement avec une réservation confirmée
        for (Reservation r : reservations) {
            if (r.getStatut() == StatutReservation.CONFIRMEE && r.getPeriode().chevauche(demande)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public double calculerPrix(Date debut, Date fin, int nbPersonnes) {
        long nuits = new Periode(debut, fin).nbNuits();
        return nuits * prixParNuit;
    }

    /**
     * Signature PDF (void). IMPORTANT: ne pas "double réserver".
     * On délègue à creerReservation(..., 1) pour créer UNE seule réservation.
     */
    @Override
    public void reserver(Client c, Date debut, Date fin) {
        creerReservation(c, debut, fin, 1);
    }

    /**
     * Signature PDF (void). On annule une réservation confirmée du client
     * (celle qui contient la date, ou la première si date == null),
     * et on remet la période dans les dispos.
     */
    @Override
    public void annulerReservation(Client c, Date date) {
        if (c == null) return;

        Reservation cible = null;
        for (Reservation r : reservations) {
            if (r.getStatut() != StatutReservation.CONFIRMEE) continue;
            if (!r.getClient().getEmail().equalsIgnoreCase(c.getEmail())) continue;

            if (date == null || r.getPeriode().contient(date)) {
                cible = r;
                break;
            }
        }
        if (cible == null) return;

        // on annule sans faire une 2e mise à jour de dispo
        cible.annulerInterneSansDoubleMajDispo();

        // remise dispo (fusion automatique via normaliserPeriodes)
        ajouterPeriodeDisponible(cible.getArrivee(), cible.getDepart());
    }

    @Override
    public boolean estReservee(Date date) {
        if (date == null) return false;

        for (Reservation r : reservations) {
            if (r.getStatut() == StatutReservation.CONFIRMEE && r.getPeriode().contient(date)) {
                return true;
            }
        }
        return false;
    }

    public void ajouterPeriodeDisponible(Date debut, Date fin) {
        periodesDisponibles.add(new Periode(debut, fin));
        normaliserPeriodes();
    }

    public void supprimerPeriodeDisponible(Date debut, Date fin) {
        Periode cible = new Periode(debut, fin);
        periodesDisponibles.removeIf(p ->
                p.getDebut().equals(cible.getDebut()) && p.getFin().equals(cible.getFin()));
        normaliserPeriodes();
    }

    public void ajouterNote(int note) {
        if (note < 0 || note > 5) return;
        notes.add(note);
        noteMoyenne = notes.stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    @Override
    public void afficherDetails() {
        System.out.println(toString());
        System.out.println("Adresse: " + adressePostale);
        System.out.println("Description: " + description);
        System.out.println("Équipements: " + (equipements.isEmpty() ? "(aucun)" : equipements));
        System.out.println("Disponibilités: " + (periodesDisponibles.isEmpty() ? "(aucune)" : periodesDisponibles));
    }

    /**
     * Création "propre" d'une réservation (avec nbPersonnes).
     * C'est celle qu'utilise ton menu.
     */
    public Reservation creerReservation(Client c, Date debut, Date fin, int nbPersonnes) {
        if (c == null) return null;
        if (nbPersonnes <= 0 || nbPersonnes > capaciteMax) return null;
        if (!estDisponible(debut, fin)) return null;

        Reservation r = new Reservation(c, this, debut, fin, nbPersonnes);
        reservations.add(r);

        // consomme la période de disponibilité
        retirerPeriodeDemandee(new Periode(debut, fin));
        return r;
    }

    // appelée par Reservation.annuler()
    public void remettreDispoDepuisReservation(Reservation r) {
        if (r == null) return;
        ajouterPeriodeDisponible(r.getArrivee(), r.getDepart());
    }

    // ===== Comparable : prix puis note =====
    @Override
    public int compareTo(Hebergement other) {
        int c = Double.compare(this.prixParNuit, other.prixParNuit);
        if (c != 0) return c;
        return Double.compare(other.noteMoyenne, this.noteMoyenne);
    }

    @Override
    public String toString() {
        return String.format("#%d %s (%s) cap=%d %.2f€/nuit note=%.1f",
                id, nom, type, capaciteMax, prixParNuit, noteMoyenne);
    }

    // ===== Gestion périodes =====
    private void retirerPeriodeDemandee(Periode demande) {
        for (int i = 0; i < periodesDisponibles.size(); i++) {
            Periode p = periodesDisponibles.get(i);
            if (p.couvre(demande)) {
                periodesDisponibles.remove(i);

                if (p.getDebut().before(demande.getDebut())) {
                    periodesDisponibles.add(new Periode(p.getDebut(), demande.getDebut()));
                }
                if (demande.getFin().before(p.getFin())) {
                    periodesDisponibles.add(new Periode(demande.getFin(), p.getFin()));
                }

                normaliserPeriodes();
                return;
            }
        }
    }

    private void normaliserPeriodes() {
        if (periodesDisponibles.isEmpty()) return;

        periodesDisponibles.sort((a, b) -> a.getDebut().compareTo(b.getDebut()));
        List<Periode> merged = new ArrayList<>();

        Periode current = periodesDisponibles.get(0);
        for (int i = 1; i < periodesDisponibles.size(); i++) {
            Periode next = periodesDisponibles.get(i);
            if (current.toucheOuChevauche(next)) current = current.fusion(next);
            else {
                merged.add(current);
                current = next;
            }
        }
        merged.add(current);

        periodesDisponibles.clear();
        periodesDisponibles.addAll(merged);
    }
}
