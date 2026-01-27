package model.personnes;

public class NouveauClient extends Client {

    private String motDePasse;

    public NouveauClient(String nom, String prenom, String email, String adresse, String motDePasse) {
        super(nom, prenom, email, adresse);
        this.motDePasse = motDePasse;
    }

    public void inscrire() {
        System.out.println("Inscription NouveauClient : " + this);
    }

    @Override
    public double getTauxReduction() {
        return 0.0;
    }

    @Override
    public String getTypePersonne() {
        return "NOUVEAU_CLIENT";
    }
}
