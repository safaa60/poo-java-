package booking.model;

public class ChambreHotel extends Hebergement {
    private int etoiles;

    public ChambreHotel(int id, String nom, String adresse, int capacite, double prixParNuit, String description, int etoiles) {
        super(id, nom, adresse, "HOTEL", capacite, prixParNuit, description);
        this.etoiles = etoiles;
    }

    public int getEtoiles() { return etoiles; }

    @Override
    public void afficherDetails() {
        super.afficherDetails();
        System.out.println("Etoiles: " + etoiles);
    }
}

