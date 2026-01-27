package model.hebergements;

public class Appartement extends Hebergement {

    private int etage;

    public Appartement(long id, String nom, String adressePostale, int capaciteMax,
                       double prixParNuit, String description, int etage) {
        super(id, nom, adressePostale, "APPARTEMENT", capaciteMax, prixParNuit, description);
        this.etage = etage;
        this.equipements.add("Cuisine");
        this.equipements.add("WiFi");
    }

    @Override
    public void afficherDetails() {
        super.afficherDetails();
        System.out.println("Étage: " + etage);
    }
}
