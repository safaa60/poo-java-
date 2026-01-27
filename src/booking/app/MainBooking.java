package booking.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import model.hebergements.Appartement;
import model.hebergements.ChambreHotel;
import model.hebergements.Hebergement;
import model.hebergements.Reservable;
import model.hebergements.Villa;
import model.personnes.Administrateur;
import model.personnes.AncienClient;
import model.personnes.Client;
import model.personnes.NouveauClient;
import model.reservation.Reservation;
import model.service.CollectionHebergements;

public class MainBooking {

    // "Base" en mémoire
    private static final List<Client> clients = new ArrayList<>();
    private static final List<Administrateur> admins = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("=== MINI BOOKING (console) ===");

        // Catalogue + comptes de base
        CollectionHebergements catalogue = new CollectionHebergements();
        seedCatalogue(catalogue);
        seedComptes();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== ACCUEIL =====");
            System.out.println("1) Inscription client");
            System.out.println("2) Connexion client");
            System.out.println("3) Connexion admin");
            System.out.println("0) Quitter");
            System.out.print("Choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1": {
                    Client c = inscriptionClient(sc);
                    if (c != null) {
                        clients.add(c);
                        System.out.println("Compte créé. Connecté.");
                        menuClient(sc, catalogue, c);
                    }
                    break;
                }
                case "2": {
                    Client c = connexionClient(sc);
                    if (c != null) menuClient(sc, catalogue, c);
                    break;
                }
                case "3": {
                    Administrateur a = connexionAdmin(sc);
                    if (a != null) menuAdmin(sc, catalogue, a);
                    break;
                }
                case "0":
                    System.out.println("Bye !");
                    sc.close();
                    return;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    // ===================== MENU CLIENT =====================
    private static void menuClient(Scanner sc, CollectionHebergements catalogue, Client client) {
        while (true) {
            System.out.println("\n===== MENU CLIENT (" + client.getEmail() + ") =====");
            System.out.println("1) Voir catalogue (trié)");
            System.out.println("2) Rechercher disponibles (date + filtres)");
            System.out.println("3) Détails d'un hébergement");
            System.out.println("4) Réserver");
            System.out.println("5) Voir mes réservations");
            System.out.println("6) Annuler une réservation");
            System.out.println("7) Facture d'une réservation");
            System.out.println("8) Noter un hébergement");
            System.out.println("9) Déconnexion");
            System.out.print("Choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1":
                    System.out.println("\n--- Catalogue (trié prix puis note) ---");
                    catalogue.trier().forEach(h -> System.out.println(" - " + h));
                    break;

                case "2": {
                    System.out.println("\n--- Recherche ---");
                    Date debut = lireDate(sc, "Date début (YYYY-MM-DD): ");
                    Date fin = lireDate(sc, "Date fin (YYYY-MM-DD): ");

                    Integer capMin = lireIntNullable(sc, "Capacité min (vide = ignore): ");
                    Double prixMax = lireDoubleNullable(sc, "Prix max/nuit (vide = ignore): ");
                    String type = lireTexte(sc, "Type (VILLA/APPARTEMENT/CHAMBRE_HOTEL ou vide): ");
                    if (type.isBlank()) type = null;
                    Double noteMin = lireDoubleNullable(sc, "Note min (0-5) (vide = ignore): ");

                    List<Hebergement> res = catalogue.rechercher(debut, fin, capMin, prixMax, type, noteMin);
                    if (res.isEmpty()) System.out.println("Aucun résultat.");
                    else res.forEach(h -> System.out.println(" * " + h));
                    break;
                }

                case "3": {
                    long id = lireLong(sc, "Id hébergement: ");
                    Hebergement h = catalogue.trouverParId(id);
                    if (h == null) System.out.println("Id introuvable.");
                    else {
                        System.out.println("\n--- Détails ---");
                        h.afficherDetails();
                    }
                    break;
                }

                case "4": {
                    System.out.println("\n--- Réservation ---");
                    long id = lireLong(sc, "Id hébergement: ");
                    Hebergement h = catalogue.trouverParId(id);
                    if (h == null) {
                        System.out.println("Id introuvable.");
                        break;
                    }

                    Date debut = lireDate(sc, "Arrivée (YYYY-MM-DD): ");
                    Date fin = lireDate(sc, "Départ  (YYYY-MM-DD): ");
                    int nbPers = lireInt(sc, "Nombre de personnes: ");

                    Reservable r = h; // polymorphisme
                    Reservation res = client.reserver(r, debut, fin, nbPers);

                    if (res == null) System.out.println("Échec réservation (dates indisponibles/capacité).");
                    else {
                        System.out.println("Réservation OK:");
                        System.out.println(res);
                    }
                    break;
                }

                case "5":
                    System.out.println("\n--- Mes réservations ---");
                    afficherReservations(client);
                    break;

                case "6": {
                    System.out.println("\n--- Annulation ---");
                    afficherReservations(client);
                    long idRes = lireLong(sc, "Id réservation à annuler: ");
                    Reservation r = trouverReservationParId(client, idRes);
                    if (r == null) System.out.println("Id réservation introuvable.");
                    else {
                        boolean ok = client.annuler(r);
                        System.out.println(ok ? "Annulation OK" : "Annulation KO");
                    }
                    break;
                }

                case "7": {
                    System.out.println("\n--- Facture ---");
                    afficherReservations(client);
                    long idRes = lireLong(sc, "Id réservation: ");
                    Reservation r = trouverReservationParId(client, idRes);
                    if (r == null) System.out.println("Id réservation introuvable.");
                    else client.afficherFacture(r);
                    break;
                }

                case "8": {
                    System.out.println("\n--- Noter ---");
                    long idHeb = lireLong(sc, "Id hébergement: ");
                    Hebergement h = catalogue.trouverParId(idHeb);
                    if (h == null) {
                        System.out.println("Id introuvable.");
                        break;
                    }
                    int note = lireInt(sc, "Note (0 à 5): ");
                    h.ajouterNote(note);
                    System.out.println("OK. Nouvelle moyenne: " + String.format("%.1f", h.getNoteMoyenne()));
                    break;
                }

                case "9":
                    System.out.println("Déconnecté.");
                    return;

                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    // ===================== MENU ADMIN =====================
    private static void menuAdmin(Scanner sc, CollectionHebergements catalogue, Administrateur admin) {
        while (true) {
            System.out.println("\n===== MENU ADMIN (" + admin.getEmail() + ") =====");
            System.out.println("1) Voir catalogue");
            System.out.println("2) Ajouter villa");
            System.out.println("3) Supprimer hébergement");
            System.out.println("4) Modifier prix hébergement");
            System.out.println("5) Forcer réduction ancien client");
            System.out.println("6) Consulter réservations d'un client");
            System.out.println("9) Déconnexion");
            System.out.print("Choix: ");
            String choix = sc.nextLine().trim();

            switch (choix) {
                case "1":
                    catalogue.getTous().forEach(h -> System.out.println(" - " + h));
                    break;

                case "2": {
                    long id = lireLong(sc, "Id: ");
                    String nom = lireTexte(sc, "Nom: ");
                    String adresse = lireTexte(sc, "Adresse: ");
                    int cap = lireInt(sc, "Capacité max: ");
                    double prix = lireDouble(sc, "Prix/nuit: ");
                    boolean piscine = lireTexte(sc, "Piscine (oui/non): ").equalsIgnoreCase("oui");

                    Villa v = new Villa(id, nom, adresse, cap, prix, "Ajout admin", piscine);

                    Date deb = lireDate(sc, "Dispo début (YYYY-MM-DD): ");
                    Date fin = lireDate(sc, "Dispo fin   (YYYY-MM-DD): ");
                    v.ajouterPeriodeDisponible(deb, fin);

                    admin.ajouterHebergement(catalogue, v);
                    System.out.println("Ajout OK.");
                    break;
                }

                case "3": {
                    long id = lireLong(sc, "Id à supprimer: ");
                    System.out.println(admin.supprimerHebergement(catalogue, id) ? "Supprimé." : "Id introuvable.");
                    break;
                }

                case "4": {
                    long id = lireLong(sc, "Id: ");
                    double prix = lireDouble(sc, "Nouveau prix/nuit: ");
                    System.out.println(admin.modifierPrix(catalogue, id, prix) ? "Modifié." : "Id introuvable.");
                    break;
                }

                case "5": {
                    String email = lireTexte(sc, "Email AncienClient: ");
                    AncienClient ac = trouverAncienClientParEmail(email);
                    if (ac == null) {
                        System.out.println("AncienClient introuvable.");
                        break;
                    }
                    double taux = lireDouble(sc, "Taux réduction (ex: 0.15): ");
                    admin.gererReduction(ac, taux);
                    System.out.println("OK.");
                    break;
                }

                case "6": {
                    String email = lireTexte(sc, "Email client: ");
                    Client c = trouverClientParEmail(email);
                    if (c == null) System.out.println("Client introuvable.");
                    else admin.consulterReservationsClient(c);
                    break;
                }

                case "9":
                    System.out.println("Déconnecté.");
                    return;

                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    // ===================== AUTH =====================
    private static Client inscriptionClient(Scanner sc) {
        System.out.println("\n--- Inscription client ---");
        String nom = lireTexte(sc, "Nom: ");
        String prenom = lireTexte(sc, "Prénom: ");
        String email = lireTexte(sc, "Email: ");
        String adresse = lireTexte(sc, "Adresse: ");
        String mdp = lireTexte(sc, "Mot de passe: ");

        if (trouverClientParEmail(email) != null) {
            System.out.println("Email déjà utilisé.");
            return null;
        }

        NouveauClient nc = new NouveauClient(nom, prenom, email, adresse, mdp);
        nc.inscrire();
        return nc;
    }

    private static Client connexionClient(Scanner sc) {
        System.out.println("\n--- Connexion client ---");
        String email = lireTexte(sc, "Email: ");
        String mdp = lireTexte(sc, "Mot de passe: ");

        // on accepte NouveauClient et AncienClient :
        // - AncienClient : seConnecter(email, mdp)
        // - NouveauClient : (motDePasse stocké mais pas utilisé) -> on accepte juste par email pour console
        Client c = trouverClientParEmail(email);
        if (c == null) {
            System.out.println("Identifiants invalides.");
            return null;
        }
        if (c instanceof AncienClient) {
            if (!((AncienClient) c).seConnecter(email, mdp)) {
                System.out.println("Identifiants invalides.");
                return null;
            }
        }
        System.out.println("Connexion OK.");
        return c;
    }

    private static Administrateur connexionAdmin(Scanner sc) {
        System.out.println("\n--- Connexion admin ---");
        String email = lireTexte(sc, "Email: ");
        String mdp = lireTexte(sc, "Mot de passe: ");

        for (Administrateur a : admins) {
            if (a.seConnecter(email, mdp)) {
                System.out.println("Connexion admin OK.");
                return a;
            }
        }
        System.out.println("Identifiants admin invalides.");
        return null;
    }

    // ===================== SEED =====================
    private static void seedCatalogue(CollectionHebergements catalogue) {
        ChambreHotel ch1 = new ChambreHotel(1, "Chambre Cosy", "10 rue Hotel, Paris", 2, 79.0,
                "Chambre calme proche centre", 3);
        Appartement ap1 = new Appartement(2, "Appart T2", "25 avenue République, Lyon", 4, 110.0,
                "T2 lumineux, proche métro", 2);
        Villa v1 = new Villa(3, "Villa Soleil", "Chemin des Pins, Nice", 6, 240.0,
                "Villa avec jardin", true);

        ch1.ajouterPeriodeDisponible(date(2026, 2, 1), date(2026, 3, 1));
        ap1.ajouterPeriodeDisponible(date(2026, 2, 1), date(2026, 2, 20));
        v1.ajouterPeriodeDisponible(date(2026, 2, 5), date(2026, 3, 10));

        catalogue.ajouter(ch1);
        catalogue.ajouter(ap1);
        catalogue.ajouter(v1);
    }

    private static void seedComptes() {
        // ancien client (login)
        AncienClient ac = new AncienClient("Martin", "Yanis", "yanis@mail.com", "8 rue B, Lille", "secret");
        ac.ajouterReservationsPassees(3);
        clients.add(ac);

        // admin
        admins.add(new Administrateur("Admin", "Root", "admin@mail.com", "admin"));
    }

    // ===================== OUTILS =====================
    private static Client trouverClientParEmail(String email) {
        for (Client c : clients) if (c.getEmail().equalsIgnoreCase(email)) return c;
        return null;
    }

    private static AncienClient trouverAncienClientParEmail(String email) {
        Client c = trouverClientParEmail(email);
        return (c instanceof AncienClient) ? (AncienClient) c : null;
    }

    private static void afficherReservations(Client c) {
        if (c.getReservations().isEmpty()) {
            System.out.println("(aucune réservation)");
            return;
        }
        for (Reservation r : c.getReservations()) {
            System.out.println(" - " + r);
        }
    }

    private static Reservation trouverReservationParId(Client c, long id) {
        for (Reservation r : c.getReservations()) {
            if (r.getId() == id) return r;
        }
        return null;
    }

    private static String lireTexte(Scanner sc, String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }

    private static int lireInt(Scanner sc, String msg) {
        while (true) {
            System.out.print(msg);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (Exception e) { System.out.println("Nombre invalide."); }
        }
    }

    private static Integer lireIntNullable(Scanner sc, String msg) {
        System.out.print(msg);
        String s = sc.nextLine().trim();
        if (s.isEmpty()) return null;
        try { return Integer.parseInt(s); }
        catch (Exception e) { System.out.println("Invalide -> ignoré"); return null; }
    }

    private static long lireLong(Scanner sc, String msg) {
        while (true) {
            System.out.print(msg);
            try { return Long.parseLong(sc.nextLine().trim()); }
            catch (Exception e) { System.out.println("Nombre invalide."); }
        }
    }

    private static double lireDouble(Scanner sc, String msg) {
        while (true) {
            System.out.print(msg);
            try { return Double.parseDouble(sc.nextLine().trim().replace(",", ".")); }
            catch (Exception e) { System.out.println("Nombre invalide."); }
        }
    }

    private static Double lireDoubleNullable(Scanner sc, String msg) {
        System.out.print(msg);
        String s = sc.nextLine().trim();
        if (s.isEmpty()) return null;
        try { return Double.parseDouble(s.replace(",", ".")); }
        catch (Exception e) { System.out.println("Invalide -> ignoré"); return null; }
    }

    private static Date lireDate(Scanner sc, String msg) {
        while (true) {
            System.out.print(msg);
            String s = sc.nextLine().trim();
            try {
                String[] p = s.split("-");
                int y = Integer.parseInt(p[0]);
                int m = Integer.parseInt(p[1]);
                int d = Integer.parseInt(p[2]);
                return date(y, m, d);
            } catch (Exception e) {
                System.out.println("Format invalide (ex: 2026-02-05).");
            }
        }
    }

    private static Date date(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}