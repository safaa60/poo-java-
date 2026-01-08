package booking.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionHebergements {
    private final ArrayList<Hebergement> hebergements = new ArrayList<>();

    public void ajouter(Hebergement h) { hebergements.add(h); }

    public void supprimerParId(int id) {
        hebergements.removeIf(h -> h.getId() == id);
    }

    public Hebergement trouverParId(int id) {
        for (Hebergement h : hebergements) if (h.getId() == id) return h;
        return null;
    }

    public List<Hebergement> rechercherPrixMax(double prixMax) {
        List<Hebergement> res = new ArrayList<>();
        for (Hebergement h : hebergements) if (h.getPrixParNuit() <= prixMax) res.add(h);
        return res;
    }

    public List<Hebergement> rechercherCapaciteMin(int capaciteMin) {
        List<Hebergement> res = new ArrayList<>();
        for (Hebergement h : hebergements) if (h.getCapacite() >= capaciteMin) res.add(h);
        return res;
    }

    public List<Hebergement> rechercherType(String type) {
        List<Hebergement> res = new ArrayList<>();
        for (Hebergement h : hebergements) if (h.getType().equalsIgnoreCase(type)) res.add(h);
        return res;
    }

    public List<Hebergement> lister() {
        return new ArrayList<>(hebergements);
    }

    public void trierParPrix() {
        Collections.sort(hebergements); // Comparable -> prix
    }
}

