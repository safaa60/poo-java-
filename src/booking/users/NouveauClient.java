package booking.users;

public class NouveauClient extends Client {
    private String motDePasse;

    public NouveauClient(String nom, String prenom, String email) {
        super(nom, prenom, email);
    }

    @Override
    public String getTypePersonne() { return "NouveauClient"; }

    public void inscription(String motDePasse) {
        this.motDePasse = motDePasse;
        System.out.println("Inscription OK pour " + this.email);
    }
}

