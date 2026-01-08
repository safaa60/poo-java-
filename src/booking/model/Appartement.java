package booking.model;

public class Appartement extends Hebergement {
    private int etage;

    public Appartement(int id, String nom, String adresse, int capacite, double prixParNuit, String description, int etage) {
        super(id, nom, adresse, "APPARTEMENT", capacite, prixParNuit, description);
        this.etage = etage;
    }

    public int getEtage() { return etage; }

    @Override
    public void afficherDetails() {
        super.afficherDetails();
        System.out.println("Etage: " + etage);
    }
}
