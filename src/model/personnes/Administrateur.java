package model.personnes;

import model.hebergements.Hebergement;
import model.service.CollectionHebergements;

public class Administrateur extends Personne {

    private String motDePasse;

    public Administrateur(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email);
        this.motDePasse = motDePasse;
    }

    public boolean seConnecter(String email, String mdp) {
        return this.email.equalsIgnoreCase(email) && this.motDePasse.equals(mdp);
    }

    // ajouter / supprimer / modifier hébergement
    public void ajouterHebergement(CollectionHebergements col, Hebergement h) {
        if (col != null && h != null) col.ajouter(h);
    }

    public boolean supprimerHebergement(CollectionHebergements col, long id) {
        if (col == null) return false;
        return col.supprimerParId(id);
    }

    public boolean modifierPrix(CollectionHebergements col, long id, double nouveauPrix) {
        Hebergement h = col != null ? col.trouverParId(id) : null;
        if (h == null) return false;
        h.setPrixParNuit(nouveauPrix);
        return true;
    }

    // gérer réductions (admin force un taux sur un ancien client)
    public void gererReduction(AncienClient c, double taux) {
        if (c == null) return;
        c.setReductionForcee(taux);
    }

    // consulter dossiers clients / réservations
    public void consulterReservationsClient(Client c) {
        if (c == null) return;
        System.out.println("Dossier client: " + c);
        if (c.getReservations().isEmpty()) {
            System.out.println("  (aucune réservation)");
            return;
        }
        for (var r : c.getReservations()) {
            System.out.println("  - " + r);
        }
    }

    @Override
    public String getTypePersonne() {
        return "ADMINISTRATEUR";
    }
}
