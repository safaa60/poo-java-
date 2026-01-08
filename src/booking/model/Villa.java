package booking.model;

public class Villa extends Hebergement {
    private boolean piscine;

    public Villa(int id, String nom, String adresse, int capacite, double prixParNuit, String description, boolean piscine) {
        super(id, nom, adresse, "VILLA", capacite, prixParNuit, description);
        this.piscine = piscine;
    }

    public boolean hasPiscine() { return piscine; }

    @Override
    public void afficherDetails() {
        super.afficherDetails();
        System.out.println("Piscine: " + (piscine ? "Oui" : "Non"));
    }
}
