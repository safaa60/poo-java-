package model.personnes;

public abstract class Personne {
    protected String nom;
    protected String prenom;
    protected String email;
    protected String motDePasse;


    protected Personne(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    public String getNom() { return nom; }
    public String getPrenom() { return prenom; }
    public String getEmail() { return email; }

    public abstract String getTypePersonne();

    @Override
    public String toString() {
        return prenom + " " + nom + " <" + email + ">";
    }
}
