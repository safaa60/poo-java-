package booking.users;

public class AncienClient extends Client {
    private String motDePasse;

    public AncienClient(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email);
        this.motDePasse = motDePasse;
    }

    @Override
    public String getTypePersonne() { return "AncienClient"; }

    public boolean connexion(String email, String mdp) {
        boolean ok = this.email.equals(email) && this.motDePasse.equals(mdp);
        System.out.println(ok ? "Connexion OK" : "Connexion refusée");
        return ok;
    }

    @Override
    public boolean aReduction() {
        // règle simple : si au moins 2 réservations passées -> réduction
        return reservations.size() >= 2;
    }

    @Override
    public double getTauxReduction() {
        // règle simple : 10% si ancien client “actif”
        return aReduction() ? 0.10 : 0.0;
    }
}
