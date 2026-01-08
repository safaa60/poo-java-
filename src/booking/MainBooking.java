package booking;

import booking.model.*;
import booking.users.*;

import java.time.LocalDate;
import java.util.List;

public class MainBooking {
    public static void main(String[] args) {

        System.out.println("=== MINI BOOKING (console) ===\n");

        CollectionHebergements col = new CollectionHebergements();

        ChambreHotel h1 = new ChambreHotel(1, "Hotel Paris Centre", "10 rue de Paris", 2, 120, "Chambre cosy", 3);
        h1.ajouterEquipement("WiFi");
        h1.ajouterEquipement("TV");
        h1.ajouterPeriodeDisponible(LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 31));
        h1.ajouterNote(4);
        h1.ajouterNote(5);

        Appartement h2 = new Appartement(2, "Appart Lyon", "2 place Bellecour", 4, 90, "Appartement pratique", 2);
        h2.ajouterEquipement("Cuisine");
        h2.ajouterPeriodeDisponible(LocalDate.of(2026, 1, 5), LocalDate.of(2026, 2, 5));
        h2.ajouterNote(3);

        Villa h3 = new Villa(3, "Villa Nice", "1 avenue de la Mer", 6, 200, "Vue mer", true);
        h3.ajouterEquipement("Piscine");
        h3.ajouterPeriodeDisponible(LocalDate.of(2026, 1, 15), LocalDate.of(2026, 2, 15));
        h3.ajouterNote(5);

        col.ajouter(h1);
        col.ajouter(h2);
        col.ajouter(h3);

        System.out.println("----- Scénario 1 -----");
        NouveauClient nc = new NouveauClient("Dupont", "Lina", "lina@mail.com");
        nc.inscription("1234");

        System.out.println("\nRecherche hébergements à max 130€/nuit :");
        List<Hebergement> resPrix = nc.rechercherParPrix(col, 130);
        for (Hebergement h : resPrix) System.out.println(" - " + h);

        System.out.println("\nDétails d’un hébergement (id=1) :");
        Hebergement choisi = col.trouverParId(1);
        if (choisi != null) {
            choisi.afficherDetails();

            System.out.println("\nTest dispo du 2026-01-12 au 2026-01-15 : "
                    + choisi.estDisponible(LocalDate.of(2026,1,12), LocalDate.of(2026,1,15)));

            System.out.println("\nRéservation...");
            nc.reserver(choisi, LocalDate.of(2026,1,12), LocalDate.of(2026,1,15), 2);

            System.out.println("Réservations du client :");
            for (Reservation r : nc.getReservations()) System.out.println(" - " + r);
        }


        System.out.println("\n----- Scénario 2 -----");
        AncienClient ac = new AncienClient("Martin", "Alex", "alex@mail.com", "pass");
        ac.connexion("alex@mail.com", "pass");

        // on lui ajoute 2 "anciennes réservations" juste pour activer la réduction
        ac.ajouterReservation(new Reservation(Reservation.nextId(), ac, h2,
                LocalDate.of(2025,10,1), LocalDate.of(2025,10,3), 180));
        ac.ajouterReservation(new Reservation(Reservation.nextId(), ac, h2,
                LocalDate.of(2025,11,1), LocalDate.of(2025,11,4), 270));

        System.out.println("Ancien client a réduction ? " + ac.aReduction() + " (" + (int)(ac.getTauxReduction()*100) + "%)");

        System.out.println("\nNouvelle réservation (avec réduction) sur l’appartement id=2 :");
        Hebergement appart = col.trouverParId(2);
        ac.reserver(appart, LocalDate.of(2026,1,20), LocalDate.of(2026,1,23), 2);

        Reservation last = ac.getReservations().get(ac.getReservations().size()-1);
        ac.afficherFacture(last);

        System.out.println("\nAnnulation (date d'annulation avant le début) :");
        appart.annulerReservation(ac, LocalDate.of(2026,1,10));
        System.out.println("Réservations du client après annulation :");
        for (Reservation r : ac.getReservations()) System.out.println(" - " + r);

       
        System.out.println("\n----- Scénario 3 -----");
        Administrateur admin = new Administrateur("Admin", "Super", "admin@mail.com");

        System.out.println("\nListe AVANT tri :");
        for (Hebergement h : col.lister()) System.out.println(" - " + h);

        System.out.println("\nTri par prix :");
        col.trierParPrix();
        for (Hebergement h : col.lister()) System.out.println(" - " + h);

        System.out.println("\nAdmin ajoute un hébergement :");
        Villa v2 = new Villa(4, "Villa Bordeaux", "Rue des Vignes", 8, 180, "Grande villa", false);
        v2.ajouterPeriodeDisponible(LocalDate.of(2026,2,1), LocalDate.of(2026,3,1));
        admin.ajouterHebergement(col, v2);

        System.out.println("\nAdmin modifie le prix de l’hôtel id=1 :");
        admin.modifierPrix(h1, 110);

        System.out.println("\nAdmin supprime l’hébergement id=3 :");
        admin.supprimerHebergement(col, 3);

        System.out.println("\nListe FINALE :");
        for (Hebergement h : col.lister()) System.out.println(" - " + h);

        System.out.println("\n--- Polymorphisme (Reservable) ---");
        Reservable r = h1; 
        System.out.println("Disponible le 2026-01-12 -> 2026-01-13 ? " + r.estDisponible(LocalDate.of(2026,1,12), LocalDate.of(2026,1,13)));
    }
}
