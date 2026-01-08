package booking.model;

import booking.users.Client;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public abstract class Hebergement implements Reservable, Comparable<Hebergement> {

    private final int id;
    private String nom;
    private String adresse;
    private String type;
    private int capacite;
    private double prixParNuit;
    private String description;

    private final List<String> equipements = new ArrayList<>();
    private final List<Periode> disponibilites = new ArrayList<>();
    private final List<Integer> notes = new ArrayList<>();
    private double moyenneNotes = 0.0;

    private final List<Reservation> reservations = new ArrayList<>();

    protected Hebergement(int id, String nom, String adresse, String type, int capacite, double prixParNuit, String description) {
        this.id = id;
        this.nom = nom;
        this.adresse = adresse;
        this.type = type;
        this.capacite = capacite;
        this.prixParNuit = prixParNuit;
        this.description = description;
    }

    public void ajouterPeriodeDisponible(LocalDate debut, LocalDate fin) {
        disponibilites.add(new Periode(debut, fin));
    }

    public void supprimerPeriodeDisponible(LocalDate debut, LocalDate fin) {
        disponibilites.removeIf(p -> p.getDebut().equals(debut) && p.getFin().equals(fin));
    }

    // vérifie si la période demandée est dans une période dispo et non réservée
    @Override
    public boolean estDisponible(LocalDate debut, LocalDate fin) {
        if (debut == null || fin == null || !debut.isBefore(fin)) return false;

        boolean dansDispo = disponibilites.stream().anyMatch(p -> p.couvre(debut, fin));
        if (!dansDispo) return false;

        // pas de chevauchement avec une réservation confirmée
        return reservations.stream()
                .filter(r -> r.getStatut() == StatutReservation.CONFIRMEE)
                .noneMatch(r -> r.getPeriode().chevauche(debut, fin));
    }

    @Override
    public boolean estReservee(LocalDate date) {
        return reservations.stream()
                .filter(r -> r.getStatut() == StatutReservation.CONFIRMEE)
                .anyMatch(r -> r.getPeriode().contient(date));
    }

    //Prix total pour une période
    public double calculerPrixTotal(LocalDate debut, LocalDate fin) {
        long nuits = ChronoUnit.DAYS.between(debut, fin);
        return nuits * prixParNuit;
    }

    @Override
    public double calculerPrix(LocalDate debut, LocalDate fin, int nbPersonnes) {
        if (nbPersonnes > capacite) {
            throw new IllegalArgumentException("Trop de personnes (capacité = " + capacite + ")");
        }
        return calculerPrixTotal(debut, fin);
    }

    public void ajouterNote(int noteSur5) {
        if (noteSur5 < 1 || noteSur5 > 5) throw new IllegalArgumentException("Note entre 1 et 5.");
        notes.add(noteSur5);
        double somme = 0;
        for (int n : notes) somme += n;
        moyenneNotes = somme / notes.size();
    }

    public double getMoyenneNotes() { return moyenneNotes; }

    //Reservations
    @Override
    public void reserver(Client c, LocalDate debut, LocalDate fin, int nbPersonnes) {
        if (!estDisponible(debut, fin)) {
            throw new IllegalStateException("Période non disponible pour " + nom);
        }
        double prix = calculerPrix(debut, fin, nbPersonnes);

        Reservation r = new Reservation(Reservation.nextId(), c, this, debut, fin, prix);
        r.confirmer();
        reservations.add(r);
        c.ajouterReservation(r);
    }

    @Override
    public void annulerReservation(Client c, LocalDate dateAnnulation) {
        for (Reservation r : reservations) {
            if (r.getClient().equals(c) && r.getStatut() == StatutReservation.CONFIRMEE) {
                if (dateAnnulation.isBefore(r.getDebut())) {
                    r.annuler();
                    return;
                }
            }
        }
        throw new IllegalStateException("Aucune réservation future à annuler pour ce client.");
    }

    //affichage
    @Override
    public void afficherDetails() {
        System.out.println("=== Hébergement #" + id + " (" + type + ") ===");
        System.out.println("Nom: " + nom);
        System.out.println("Adresse: " + adresse);
        System.out.println("Capacité: " + capacite);
        System.out.println("Prix/nuit: " + prixParNuit + " €");
        System.out.println("Note moyenne: " + String.format("%.2f", moyenneNotes) + "/5");
        System.out.println("Description: " + description);
        System.out.println("Equipements: " + (equipements.isEmpty() ? "-" : equipements));
        System.out.println("Disponibilités: " + (disponibilites.isEmpty() ? "-" : disponibilites));
    }

    @Override
    public int compareTo(Hebergement other) {
        return Double.compare(this.prixParNuit, other.prixParNuit);
    }

    //GETTERS
    @Override public int getId() { return id; }
    @Override public String getType() { return type; }
    @Override public int getCapacite() { return capacite; }
    @Override public double getPrixParNuit() { return prixParNuit; }

    public String getNom() { return nom; }
    public String getAdresse() { return adresse; }

    //SETTERS
    public void setNom(String nom) { this.nom = nom; }
    public void setAdresse(String adresse) { this.adresse = adresse; }
    public void setCapacite(int capacite) { this.capacite = capacite; }
    public void setPrixParNuit(double prixParNuit) { this.prixParNuit = prixParNuit; }
    public void setDescription(String description) { this.description = description; }
    public void setType(String type) { this.type = type; }

    public void ajouterEquipement(String e) { equipements.add(e); }

    @Override
    public String toString() {
        return "#" + id + " " + nom + " (" + type + ") - " + prixParNuit + " €/nuit - note " + String.format("%.2f", moyenneNotes);
    }
}

