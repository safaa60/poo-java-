package booking.users;

import booking.model.CollectionHebergements;
import booking.model.Hebergement;

public class Administrateur extends Personne {

    public Administrateur(String nom, String prenom, String email) {
        super(nom, prenom, email);
    }

    @Override
    public String getTypePersonne() { return "Administrateur"; }

    public void ajouterHebergement(CollectionHebergements col, Hebergement h) {
        col.ajouter(h);
        System.out.println("Admin a ajouté: " + h);
    }

    public void supprimerHebergement(CollectionHebergements col, int id) {
        col.supprimerParId(id);
        System.out.println("Admin a supprimé l'hébergement id=" + id);
    }

    public void modifierPrix(Hebergement h, double nouveauPrix) {
        h.setPrixParNuit(nouveauPrix);
        System.out.println("Admin a modifié le prix: " + h);
    }
}
