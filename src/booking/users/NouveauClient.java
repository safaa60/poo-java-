package booking.users;

public class NouveauClient extends Client {

    private String motDePasse;

    // ✅ Constructeur 3 paramètres (si tu l’utilises quelque part)
    public NouveauClient(String nom, String prenom, String email) {
        super(nom, prenom, email);
    }

    // ✅ Constructeur 4 paramètres (CELUI qu’attend ton BookingApp)
    public NouveauClient(String nom, String prenom, String email, String motDePasse) {
        super(nom, prenom, email);
        this.motDePasse = motDePasse;
    }

    // ✅ méthode inscription si tu préfères séparer création / mot de passe
    public void inscription(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    @Override
    public String getTypePersonne() {
        return "NouveauClient";
    }
}
