package model.hebergements;

public class Villa extends Hebergement {

    private boolean piscine;

    public Villa(long id, String nom, String adressePostale, int capaciteMax,
                 double prixParNuit, String description, boolean piscine) {
        super(id, nom, adressePostale, "VILLA", capaciteMax, prixParNuit, description);
        this.piscine = piscine;
        this.equipements.add("Jardin");
        this.equipements.add("Cuisine");
        this.equipements.add("WiFi");
    }

    @Override
    public void afficherDetails() {
        super.afficherDetails();
        System.out.println("Piscine: " + (piscine ? "oui" : "non"));
    }
}
