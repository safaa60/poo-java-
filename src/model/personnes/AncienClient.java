package model.personnes;

public class AncienClient extends Client {

    private String motDePasse;
    private int nbReservationsPassees = 0;

    // réduction fixée par admin (optionnel)
    private Double reductionForcee = null;

    public AncienClient(String nom, String prenom, String email, String adresse, String motDePasse) {
        super(nom, prenom, email, adresse);
        this.motDePasse = motDePasse;
    }

    public boolean seConnecter(String email, String mdp) {
        return this.email.equalsIgnoreCase(email) && this.motDePasse.equals(mdp);
    }

    public void ajouterReservationsPassees(int n) {
        nbReservationsPassees += Math.max(0, n);
    }

    public int getTotalReservations() {
        return nbReservationsPassees + this.reservations.size();
    }

    public void setReductionForcee(Double taux) {
        this.reductionForcee = taux;
    }

    @Override
    public double getTauxReduction() {
        if (reductionForcee != null) return reductionForcee;

        int total = getTotalReservations();
        if (total >= 6) return 0.10;
        if (total >= 3) return 0.05;
        return 0.0;
    }

    @Override
    public String getTypePersonne() {
        return "ANCIEN_CLIENT";
    }
}
