package model.hebergements;

public class ChambreHotel extends Hebergement {

    private int etoiles;

    public ChambreHotel(long id, String nom, String adressePostale, int capaciteMax,
                        double prixParNuit, String description, int etoiles) {
        super(id, nom, adressePostale, "CHAMBRE_HOTEL", capaciteMax, prixParNuit, description);
        this.etoiles = etoiles;
        this.equipements.add("WiFi");
        this.equipements.add("TV");
    }

    @Override
    public void afficherDetails() {
        super.afficherDetails();
        System.out.println("Étoiles: " + etoiles);
    }
}
